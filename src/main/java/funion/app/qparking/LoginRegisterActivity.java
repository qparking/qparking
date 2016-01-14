package funion.app.qparking;/**
 * Created by yunze on 2015/12/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;

public class LoginRegisterActivity extends Activity implements View.OnClickListener {
    private Context context = LoginRegisterActivity.this;
    String phonenum,title,tag;
    EditText verfication_code,pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register_password);
        Intent intent=getIntent();
        phonenum=intent.getStringExtra("phonenum");
        title=intent.getStringExtra("title");
        tag=intent.getStringExtra("tag");
        initView();
    }

    private void initView() {
        verfication_code=(EditText)findViewById(R.id.verication_ed);
        pwd=(EditText)findViewById(R.id.password_ed);
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.top_back_btn);
        if(title!=null)
        ((TextView) findViewById(R.id.include_tv_title)).setText(title);
        findViewById(R.id.code_tv).setOnClickListener(this);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        findViewById(R.id.submit_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.code_tv:
                Map<String, String> param=new HashMap<String, String>();
                param.put("mobile", phonenum);
                if(tag.equals("reg")) {
                    param.put("reg", 1 + "");
                }
                OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String result) {
                        try {
                            Log.e("result",result);
                            JSONObject jsonObject=new JSONObject(result);
                            String code=jsonObject.getString("code");
                            String result_msg=jsonObject.getString("msg");
                            if(code.equals("0")){
                                String msg= TransCoding.trans(result_msg);
                              Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                            }else{
                                String msg=TransCoding.trans(result_msg);
                                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, param, "duanxinget.html");
                break;
            case R.id.submit_tv:
                if(verfication_code.getText().toString().equals(null)||verfication_code.getText().toString().equals("")){
                Toast.makeText(context,R.string.enter_verification_code,Toast.LENGTH_SHORT).show();
                    return;
            }
                if(pwd.getText().toString().equals(null)||pwd.getText().toString().equals("")){
                    Toast.makeText(context, R.string.enter_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pwd.getText().toString().length()<6){
                    Toast.makeText(context, R.string.enter_morethan_min_length, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(tag.equals("find_pwd")){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put("mobile",phonenum);
                    map.put("vcode", verfication_code.getText().toString());
                    map.put("password", pwd.getText().toString());
                    OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(String result) {
                            Log.e("result",result);

                        }
                    },map,"resetPassword");
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("phonenum", phonenum);
                    bundle.putString("verificationcode", verfication_code.getText().toString());
                    bundle.putString("password", pwd.getText().toString());
                    bundle.putString("title", getResources().getString(R.string.add_plate_num));
                    ActivityTools.switchActivity(context, PlateNumActivity.class, bundle);
                }
                break;
        }
    }
}
