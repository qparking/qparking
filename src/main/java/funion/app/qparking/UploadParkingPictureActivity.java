package funion.app.qparking;
/**
 * 上传停车场图片页面
 * Created by yunze on 2015/12/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import funion.app.qparking.popWindow.UploadParkingPicturePop;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.HttpUtil;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;


public class UploadParkingPictureActivity extends Activity implements View.OnClickListener {
    private UploadParkingPicturePop uploadParkingPicturePop;
    private Context context = UploadParkingPictureActivity.this;
    private ImageView select_im;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    ProgressDialog progressDialog;
    SharedPreferences sp;
    private File tempFile = new File(Environment.getExternalStorageDirectory()
            + "/qpking/tempImage.jpg");
    private File tempCoreFile = new File(
            Environment.getExternalStorageDirectory()
                    + "/qpking/tempCoreImage.jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_upload_parking_picture);
        initTitle();
        initView();
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
    }

    private void initView() {
        findViewById(R.id.upload_picture_ly).setOnClickListener(this);
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("上传停车场照片");
        select_im=(ImageView)findViewById(R.id.selectphone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.upload_picture_ly:
                uploadParkingPicturePop = new UploadParkingPicturePop(context,onClickListener);
                uploadParkingPicturePop.setAnimationStyle(R.style.PopupAnimation);
                uploadParkingPicturePop.showAtLocation(findViewById(R.id.upload_picture_parent_ly), Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    public View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.make_photo:
                    uploadParkingPicturePop.dismiss();
                    File temp = new File(Environment.getExternalStorageDirectory()
                            + "/qpking");
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
                    if (!tempCoreFile.exists()) {
                        try {
                            tempCoreFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                    cameraintent.putExtra("outputFormat", "JPEG"); // 输入文件格式
                    startActivityForResult(cameraintent, PHOTO_REQUEST_TAKEPHOTO);
                    break;
                case R.id.select_photoalbum:
                    uploadParkingPicturePop.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");//相片类型
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    break;
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
                startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
                    // 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    } else {
                        QParkingApp.ToastTip(context, "读取失败，请重试", -100);
                    }
                    break;
                case PHOTO_REQUEST_CUT:// 返回的结果
                if (data != null) {
                   Bitmap bmp=data.getParcelableExtra("data");
//                    select_im.setImageBitmap(bmp);
                    new UploadImgTask(this).execute();

                }else{
                  QParkingApp.ToastTip(context,"读取失败，请重试",-100);
                }
                    break;
            }
        }
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(tempFile));
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private class UploadImgTask extends AsyncTask<String,Integer,String>{
        private Context context_;
        public UploadImgTask(Context context){
            this.context_=context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(context_);
            progressDialog.setTitle("请稍后");
            progressDialog.setMessage("正在上传");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
                    Map<String,String> parm=new HashMap<String,String>();
                    parm.put("parkid",sp.getString("parkId",null));
                    parm.put("type", "1");
            return HttpUtil.uploadFile("uploadparkimg", parm, tempCoreFile,
                    "file");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Log.e("result", result);

            try {
                JSONObject jsonObject=new JSONObject(result);
                String code= (String) jsonObject.get("code");
                if(code.equals("0")){
                    String msg= (String) jsonObject.get("msg");
                    QParkingApp.ToastTip(context, TransCoding.trans(msg),-100);
                    ActivityTools.switchActivity(context,PersonalCenterActivity.class,null);
                    finish();
                }else{
                    QParkingApp.ToastTip(context,getResources().getString(R.string.add_file),-100);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }



}
