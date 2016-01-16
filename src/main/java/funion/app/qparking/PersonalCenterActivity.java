package funion.app.qparking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import funion.app.common.T;
import funion.app.qparking.popWindow.UploadParkingPicturePop;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.tools.HttpUtil;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.view.RoundImageView;

public class PersonalCenterActivity extends Activity implements OnClickListener {
    private TextView myIntegral;
    private TextView parkingCardTv;
    private UploadParkingPicturePop pop;
    SharedPreferences sp;
    Context context;
    SharedPreferences.Editor editor;
    private File tempFile = new File(Environment.getExternalStorageDirectory()
            + "/Qparking/tempImage.jpg");
    private File tempCoreFile = new File(
            Environment.getExternalStorageDirectory()
                    + "/Qparking/tempCoreImage.jpg");
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        context = this;
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        editor = sp.edit();
        init();

    }

    private void init() {
        myIntegral = (TextView) findViewById(R.id.integral_tv);
        parkingCardTv = (TextView) findViewById(R.id.parking_card_tv);
        ((TextView) findViewById(R.id.balance_tv)).setText(sp.getString("balance", null));
        myIntegral.setText(getResources().getString(R.string.integral) + sp.getString("integral", null));
        findViewById(R.id.personalCenter_message_rl).setOnClickListener(this);
        findViewById(R.id.personalCenter_myOrder_rl).setOnClickListener(this);
        findViewById(R.id.personalCenter_myPlate_rl).setOnClickListener(this);
        findViewById(R.id.personalCenter_myCoupon_rl).setOnClickListener(this);
        findViewById(R.id.personalCenter_back_iv).setOnClickListener(this);
        findViewById(R.id.recharge_tv).setOnClickListener(this);
        findViewById(R.id.wash_card_tv).setOnClickListener(this);
        findViewById(R.id.logout_tv).setOnClickListener(this);
        findViewById(R.id.recharge_iv).setOnClickListener(this);
        DisplayImageOptions options = AppTools.confirgImgInfo(R.drawable.headimage_default, R.drawable.headimage_default);
        ImageLoader.getInstance().displayImage(sp.getString("avatar", ""),
                ((ImageView) findViewById(R.id.personalCenter_head_iv)), options);
        findViewById(R.id.personalCenter_head_iv).setOnClickListener(this);
        myIntegral.setOnClickListener(this);
        parkingCardTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personalCenter_back_iv: {
                finish();
            }
            break;
            case R.id.personalCenter_info_iv: {
                Intent intent = new Intent();
                intent.setClass(PersonalCenterActivity.this, MyInfoActivity.class);
                startActivity(intent);
            }
            break;
            //洗车券
            case R.id.wash_card_tv:
                ActivityTools.switchActivity(context, WrashCarVolumeActivity.class, null);
                break;
            case R.id.personalCenter_message_rl: {
                Intent intent = new Intent();
                intent.setClass(PersonalCenterActivity.this, MyOrderActivity.class);
                startActivity(intent);
                QParkingApp.ToastTip(PersonalCenterActivity.this, "我的订单", -100);
            }
            break;
            case R.id.personalCenter_myOrder_rl: {
                //我的车位
                //ps:10 pn:0
                ActivityTools.switchActivity(context, MyReleaseCarPort.class, null);

            }
            break;
            case R.id.personalCenter_myPlate_rl: {
                ActivityTools.switchActivity(context, CarLicenceManagerActivity.class, null);
            }
            break;
            case R.id.personalCenter_myCoupon_rl: {
                ActivityTools.switchActivity(context, PasswordActivity.class, null);
            }
            break;
            case R.id.recharge_tv:
                ActivityTools.switchActivity(context, RechargeActivity.class, null);
                break;
            case R.id.integral_tv:
                ActivityTools.switchActivity(context, ScoreActivity.class, null);
                break;
            case R.id.parking_card_tv:
                ActivityTools.switchActivity(context, ParkingVolumeActivity.class, null);
                break;
            case R.id.logout_tv:
                sp.edit().clear().commit();
                Toast.makeText(context, getResources().getString(R.string.exit_success), Toast.LENGTH_SHORT).show();
                ActivityTools.switchActivity(context, MainAct.class, null);
                finish();
                break;
            case R.id.personalCenter_head_iv:
                pop = new UploadParkingPicturePop(context, new MyOnClickListener());
                pop.showAtLocation(findViewById(R.id.person_parent_rl), Gravity.BOTTOM, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                break;
        }
    }

    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.select_photoalbum:
                    T.showShort(context, "点击上传文件");
                    upLoadingPhoto();
                    break;
                case R.id.make_photo:
                    T.showShort(context, "点击拍照");
                    makePhoto();
                    break;
                case R.id.cancel_tv:
                    pop.dismiss();
                    break;
            }
        }
    }

    private void upLoadingPhoto() {

        File temp = new File(Environment.getExternalStorageDirectory()
                + "/mingren");
        if (!temp.exists()) {
            temp.mkdir();
        }

        if (!tempCoreFile.exists()) {
            try {
                tempCoreFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, 2);
    }

    private void makePhoto() {
        File temp = new File(Environment.getExternalStorageDirectory()
                + "/Qparking");
        if (!temp.exists()) {
            temp.mkdir();
        }
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        intent.putExtra("outputFormat", "JPEG"); // 输入文件格式
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(Uri.fromFile(tempFile), "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 500);
                    intent.putExtra("outputY", 500);
                    intent.putExtra("outputFormat", "JPEG");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(tempCoreFile));
                    startActivityForResult(intent, 1);
                    break;
                case 1:
                    new UploadImgTask(this).execute();
                    break;
                case 2:
                    intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 500);
                    intent.putExtra("outputY", 500);
                    intent.putExtra("outputFormat", "JPEG");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(tempCoreFile));
                    startActivityForResult(intent, 1);
                    break;
            }
        }
    }

    private class UploadImgTask extends AsyncTask<String, Integer, String> {
        private PersonalCenterActivity context;

        public UploadImgTask(PersonalCenterActivity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("请稍候");
            progressDialog.setMessage("正在处理...");
            progressDialog.setIndeterminate(false);
            // 设置滚动条是否按返回键取消
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            pop.dismiss();
            Log.e("result", result);
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                String code = (String) jsonObject.get("code");
                if (code.equals("0")) {
                    String msg = (String) jsonObject.get("msg");
                    editor.putString("avatar", jsonObject.getString("photourl"));
                    editor.commit();
                    DisplayImageOptions options = AppTools.confirgImgInfo(R.drawable.headimage_default, R.drawable.headimage_default);
                    ImageLoader.getInstance().displayImage(jsonObject.getString("photourl"),
                            ((ImageView) findViewById(R.id.personalCenter_head_iv)), options);
                    QParkingApp.ToastTip(context, TransCoding.trans(msg), -100);
                } else {
                    QParkingApp.ToastTip(context, getResources().getString(R.string.add_file), -100);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Map<String, String> param = new HashMap<String, String>();
            param.put("sign", sp.getString("sign", null));
//            parm.put("type", "1");
            return HttpUtil.uploadFile("uploadphoto", param, tempCoreFile,
                    "file");
        }
    }
}
