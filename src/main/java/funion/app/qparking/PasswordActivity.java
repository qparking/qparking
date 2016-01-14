package funion.app.qparking;

import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;

/**
 * 
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：PasswordActivity
 * @时间：2015下午2:56:54
 * @要做的事情：TODO 修改登录密码实现类
 */
public class PasswordActivity extends Activity implements OnClickListener

{
	private Button btTemp;
	private EditText edPasswordPre,edPasswordNew,edPasswordAffirm;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	Context context;
 	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_activity);
		context=this;
		sp=getSharedPreferences("mMessage",MODE_PRIVATE);
		editor=sp.edit();

		// 链接按钮消息
		
		btTemp = (Button) findViewById(R.id.btPasswordBack);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.makesure_modification_btn);
		btTemp.setOnClickListener(this);
	}

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btPasswordBack:
			finish();
			break;
		case R.id.makesure_modification_btn:
			edPasswordPre = (EditText) findViewById(R.id.etPasswordPre);
			if (edPasswordPre.getText().toString().length() == 0) {
				QParkingApp.ToastTip(PasswordActivity.this, "请填写原密码！", Toast.LENGTH_SHORT);
				break;
			}
			 edPasswordNew = (EditText) findViewById(R.id.etPasswordNew);
			if (edPasswordNew.getText().toString().length() == 0) {
				QParkingApp.ToastTip(PasswordActivity.this, "请填写新密码！", Toast.LENGTH_SHORT);
				break;
			}
			 edPasswordAffirm = (EditText) findViewById(R.id.etPasswordAffirm);
			if (edPasswordAffirm.getText().toString().length() == 0) {
				QParkingApp.ToastTip(PasswordActivity.this, "请确认新密码！", Toast.LENGTH_SHORT);
				break;
			}
			String strPasswordNew = edPasswordNew.getText().toString();
			String strPasswordAffirm = edPasswordAffirm.getText().toString();
			if (!strPasswordNew.equals(strPasswordAffirm)) {
				QParkingApp.ToastTip(PasswordActivity.this,
						"两次输入密码不一致，请重新填写！", Toast.LENGTH_SHORT);
				break;
			}
			Map<String,String> parm=new HashMap<String,String>();
			parm.put("sign",sp.getString("sign", null));
			parm.put("password",edPasswordPre.getText().toString());
			parm.put("newpassword",edPasswordNew.getText().toString());
			parm.put("newrepassword", edPasswordNew.getText().toString());

			OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
				@Override
				public void onError(Request request, Exception e) {

				}

				@Override
				public void onResponse(String result) {
					try {
						JSONObject jsonObject = new JSONObject(result);
						String code = jsonObject.getString("code");
						String result_msg = jsonObject.getString("msg");
						if (code.equals("0")) {
							Toast.makeText(context, TransCoding.trans(result_msg), Toast.LENGTH_SHORT).show();
							editor.putString("password", edPasswordNew.getText().toString());
							editor.commit();
							ActivityTools.switchActivity(context,MainActivity.class,null);
							finish();

						} else {
							Toast.makeText(context, TransCoding.trans(result_msg), Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}


				}
			}, parm, "modifypassword");

		break;
		}

	}
}
