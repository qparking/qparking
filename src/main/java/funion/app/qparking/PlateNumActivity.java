package funion.app.qparking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import funion.app.common.T;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.JudgePlateNum;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;

public class PlateNumActivity extends Activity implements OnClickListener {
    private EditText etModifyCarNo;
    private TextView submit;
    private Context context = PlateNumActivity.this;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private QParkingApp qParkingApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carno_activity);
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        editor = sp.edit();
        qParkingApp = (QParkingApp) getApplicationContext();
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.include_tv_title)).setText("注册车牌");
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        etModifyCarNo = (EditText) findViewById(R.id.etModifyCarNo);
        submit = (TextView) findViewById(R.id.include_tv_right);
        submit.setText("确定");
        submit.setTextColor(getResources().getColor(R.color.app_white));
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.include_tv_right:
                if (TextUtils.isEmpty(etModifyCarNo.getText().toString().trim())) {
                    T.showShort(context, "请输入车牌");
                    return;
                }
                if (!JudgePlateNum.isPlateNum(etModifyCarNo.getText().toString().trim())) {
                    QParkingApp.ToastTip(this, "您填写的车牌号格式不正确，请重新填写！", -100);
                    return;
                }
                sendRequest();
                break;
        }

    }

    private void sendRequest() {
        Map<String, String> param = new HashMap<>();
        param.put("mobile", getIntent().getStringExtra("mobile"));
        param.put("vcode", getIntent().getStringExtra("vcode"));
        param.put("platenum", etModifyCarNo.getText().toString().trim());
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    Log.e("register", result);
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    String result_msg = jsonObject.getString("msg");
                    if (code.equals("0")) {
                        String msg = TransCoding.trans(result_msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        editor.putString("sign", jsonObject.getString("sign"));
                        editor.putString("username", getIntent().getStringExtra("mobile"));
                        editor.putString("avatar", jsonObject.getString("avatar"));
                        editor.putString("balance", jsonObject.getString("balance"));//余额
                        editor.putString("integral", jsonObject.getString("integral"));//积分
                        String plate = jsonObject.getString("plate");
                        jsonObject = new JSONObject(plate);
                        editor.putString("ID", jsonObject.getString("ID"));
                        qParkingApp.m_strUserID = jsonObject.getString("ID");
                        editor.putString("PLATETYPE", jsonObject.getString("PLATETYPE"));
                        editor.putString("userId", jsonObject.getString("USERID"));
                        editor.putString("PLATENUM", jsonObject.getString("PLATENUM"));//车牌号
                        editor.putString("ISDEFAULT", jsonObject.getString("ISDEFAULT"));
                        editor.commit();
                        Intent intent = new Intent(context, MainAct.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String msg = TransCoding.trans(result_msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param, "useradd");
    }
//	private EditText etCarNo ;
//	private Button btTemp;
//	private String password,phonenum,verfication_code,title_;
//	TextView title_tv,commit_tv;
//	ImageView im_back;
//	Context context;
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.carno_activity);
//		context=this;
//		Intent intent=getIntent();
//		password=intent.getStringExtra("password");
//		phonenum=intent.getStringExtra("phonenum");
//		verfication_code=intent.getStringExtra("verificationcode");
//		title_=intent.getStringExtra("title");
//		initView();
//
//
//	}
//
//	private void initView() {
//		// 获取应用程序对象
//		QParkingApp appQParking = (QParkingApp) getApplicationContext();
//		title_tv=(TextView)findViewById(R.id.include_tv_title);
//		im_back=(ImageView)findViewById(R.id.include_iv_left);
//		im_back.setOnClickListener(this);
//		commit_tv=(TextView)findViewById(R.id.include_tv_right);
//		commit_tv.setOnClickListener(this);
//		if(title_!=null)
//        title_tv.setText(title_);
//		im_back.setBackgroundResource(R.drawable.top_back_btn);
//		commit_tv.setText(R.string.makesure);
//		etCarNo = (EditText) findViewById(R.id.etModifyCarNo);
//		if(appQParking.m_strCarNo.length()!=0){
//			etCarNo.setText(appQParking.m_strCarNo);
//		}
//		// 链接按钮消息
//
//	}

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.include_iv_left:
//			finish();
//			break;
//		case R.id.include_tv_right: {
//			 etCarNo = (EditText) findViewById(R.id.etModifyCarNo);
//			String strCarNo = etCarNo.getText().toString();
//			if(!JudgePlateNum.isPlateNum(strCarNo)){
//				QParkingApp.ToastTip(this, "您填写的车牌号格式不正确，请重新填写！", -100);
//				return;
//			}
//			Map<String, String> param=new HashMap<String, String>();
//			param.put("mobile",phonenum);
//			param.put("vcode", verfication_code);
//			param.put("password", password);
//			param.put("platenum", strCarNo);
//			OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
//				@Override
//				public void onError(Request request, Exception e) {
//                        e.printStackTrace();
//				}
//
//				@Override
//				public void onResponse(String result) {
//					try {
//						JSONObject jsonObject=new JSONObject(result);
//						String code=jsonObject.getString("code");
//						String result_msg=jsonObject.getString("msg");
//						if(code.equals("0")){
//							String msg= TransCoding.trans(result_msg);
//							Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//							ActivityTools.switchActivity(context,MainActivity.class,null);
//						}else{
//							String msg=TransCoding.trans(result_msg);
//							Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//
//				}
//			}, param, "useradd");
//
//		}
//		}
//
//	}
}
