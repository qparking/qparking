package funion.app.qparking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class GetBackPwdActivity extends Activity implements OnClickListener {
	private ProgressDialog m_dlgProgress = null;
	private Handler m_hNotify = null;
	private Button m_btDone;
	private LinearLayout m_llVerifyPhone;
	private LinearLayout m_llResetPassword;
	private String m_strPhoneNum = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_back_password_activity);

		// 获取控件
		m_llVerifyPhone = (LinearLayout) findViewById(R.id.llVerifyPhone);
		m_llResetPassword = (LinearLayout) findViewById(R.id.llResetPassword);
		m_btDone = (Button) findViewById(R.id.btGetBackPwdDone);

		m_btDone.setOnClickListener(this);

		// 链接按钮消息
		Button btTemp;
		btTemp = (Button) findViewById(R.id.btGetBackPwdBack);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btGetSmsCodePwd);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btPasswordNext);
		btTemp.setOnClickListener(this);

		// 设置通信句柄
		m_hNotify = new Handler() {
			public void handleMessage(Message msg) {
				m_dlgProgress.dismiss();
				switch (msg.what) {
				case 0:
					QParkingApp.ToastTip(GetBackPwdActivity.this, msg
							.peekData().getString("info"), -100);
					break;
				case 1:
					QParkingApp.ToastTip(GetBackPwdActivity.this,
							"发送成功，请等待接收！", -100);
					break;
				case 2: {
					m_btDone.setVisibility(View.VISIBLE);

					m_llResetPassword.setVisibility(View.VISIBLE);
					m_llVerifyPhone.setVisibility(View.INVISIBLE);
				}
					break;
				case 3: {
					QParkingApp.ToastTip(GetBackPwdActivity.this, msg
							.peekData().getString("info"), -100);
					finish();
				}
					break;
				}
			}
		};
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
		case R.id.btGetBackPwdBack:
			finish();
			break;
		case R.id.btGetSmsCodePwd: {
			m_strPhoneNum = ((EditText) findViewById(R.id.etYourPhoneNum))
					.getText().toString();
			if (m_strPhoneNum.length() == 0) {
				QParkingApp.ToastTip(GetBackPwdActivity.this, "请填写手机号！", -100);
				break;
			}
			// 显示等待对话框
			m_dlgProgress = ProgressDialog.show(GetBackPwdActivity.this, null,
					"获取验证码，请稍后... ", true, false);

			// 接口调用线程
			Thread InterfaceThread = new Thread(new Runnable() {
				public void run() {
					try {
						String strParams = String.format("smsType=2&mobile=%s",
								URLEncoder.encode(m_strPhoneNum));
						URL urlInterface = new URL(
								"http://app.qutingche.cn/Mobile/Common/sendCode");
						HttpURLConnection hucInterface = (HttpURLConnection) urlInterface
								.openConnection();

						hucInterface.setDoInput(true);
						hucInterface.setDoOutput(true);
						hucInterface.setUseCaches(false);

						hucInterface.setRequestMethod("POST");
						// 设置DataOutputStream
						DataOutputStream dosInterface = new DataOutputStream(
								hucInterface.getOutputStream());

						dosInterface.writeBytes(strParams);
						dosInterface.flush();
						dosInterface.close();

						BufferedReader readerBuff = new BufferedReader(
								new InputStreamReader(hucInterface
										.getInputStream()));
						String strData = readerBuff.readLine();

						JSONObject jsonData = new JSONObject(strData);
						if (jsonData.getInt("status") == 1)
							m_hNotify.sendEmptyMessage(1);
						else {
							Message msg = new Message();
							Bundle bundleData = new Bundle();

							bundleData.putString("info",
									jsonData.getString("info"));
							msg.setData(bundleData);

							msg.what = 0;
							m_hNotify.sendMessage(msg);
						}

					} catch (Exception e) {
					}
				}
			});
			InterfaceThread.start();
		}
			break;
		case R.id.btPasswordNext: {
			m_strPhoneNum = ((EditText) findViewById(R.id.etYourPhoneNum))
					.getText().toString();
			if (m_strPhoneNum.length() == 0) {
				QParkingApp.ToastTip(GetBackPwdActivity.this, "请填写手机号！", -100);
				break;
			}
			// 获取验证码
			EditText etSmsCode = (EditText) findViewById(R.id.etSmsCodePwdBack);
			final String strSmsCode = etSmsCode.getText().toString();
			if (strSmsCode.length() == 0) {
				QParkingApp.ToastTip(GetBackPwdActivity.this, "请输入验证码", -100);
				return;
			}
			// 显示等待对话框
			m_dlgProgress = ProgressDialog.show(GetBackPwdActivity.this, null,
					"验证验证码，请稍后... ", true, false);

			// 接口调用线程
			Thread InterfaceThread = new Thread(new Runnable() {
				public void run() {
					try {
						// 获取应用程序对象
						QParkingApp appQParking = (QParkingApp) getApplicationContext();

						String strParams = String.format(
								"mobile=%s&smsCode=%s", m_strPhoneNum,
								strSmsCode);
						URL urlInterface = new URL(
								"http://app.qutingche.cn/Mobile/Common/checkSmsCode");
						HttpURLConnection hucInterface = (HttpURLConnection) urlInterface
								.openConnection();

						hucInterface.setDoInput(true);
						hucInterface.setDoOutput(true);
						hucInterface.setUseCaches(false);

						hucInterface.setRequestMethod("POST");
						// 设置DataOutputStream
						DataOutputStream dosInterface = new DataOutputStream(
								hucInterface.getOutputStream());

						dosInterface.writeBytes(strParams);
						dosInterface.flush();
						dosInterface.close();

						BufferedReader readerBuff = new BufferedReader(
								new InputStreamReader(hucInterface
										.getInputStream()));
						String strData = readerBuff.readLine();

						JSONObject jsonData = new JSONObject(strData);
						if (jsonData.getInt("status") == 1)
							m_hNotify.sendEmptyMessage(2);
						else {
							Message msg = new Message();
							Bundle bundleData = new Bundle();

							bundleData.putString("info",
									jsonData.getString("info"));
							msg.setData(bundleData);

							msg.what = 0;
							m_hNotify.sendMessage(msg);
						}

					} catch (Exception e) {
					}
				}
			});
			InterfaceThread.start();
		}
			break;
		case R.id.btGetBackPwdDone: {
			EditText edPasswordNew = (EditText) findViewById(R.id.etPasswordNewSet);
			if (edPasswordNew.getText().toString().length() == 0) {
				QParkingApp.ToastTip(this, "请填写新密码！", -100);
				break;
			}
			EditText edPasswordAffirm = (EditText) findViewById(R.id.etPasswordAffirmSet);
			if (edPasswordAffirm.getText().toString().length() == 0) {
				QParkingApp.ToastTip(this, "请确认新密码！", -100);
				break;
			}
			final String strPasswordNew = edPasswordNew.getText().toString();
			String strPasswordAffirm = edPasswordAffirm.getText().toString();
			if (!strPasswordNew.equals(strPasswordAffirm)) {
				QParkingApp.ToastTip(this, "新密码与确认密码不一致，请重新填写！", -100);
				break;
			}
			// 显示等待对话框
			m_dlgProgress = ProgressDialog.show(GetBackPwdActivity.this, null,
					"重置密码，请稍后... ", true, false);
			// 调用接口线程
			Thread InterfaceThread = new Thread(new Runnable() {
				public void run() {
					try {
						String strParams = String.format(
								"mobile=%s&password=%s", m_strPhoneNum,
								strPasswordNew);
						URL urlInterface = new URL(
								"http://app.qutingche.cn/Mobile/MemberLogin/setNewPwd");
						HttpURLConnection hucInterface = (HttpURLConnection) urlInterface
								.openConnection();

						hucInterface.setDoInput(true);
						hucInterface.setDoOutput(true);
						hucInterface.setUseCaches(false);

						hucInterface.setRequestMethod("POST");
						// 设置DataOutputStream
						DataOutputStream dosInterface = new DataOutputStream(
								hucInterface.getOutputStream());

						dosInterface.writeBytes(strParams);
						dosInterface.flush();
						dosInterface.close();

						BufferedReader readerBuff = new BufferedReader(
								new InputStreamReader(hucInterface
										.getInputStream()));
						String strData = readerBuff.readLine();

						JSONObject jsonRet = new JSONObject(strData);

						Message msg = new Message();
						Bundle bundleData = new Bundle();

						bundleData.putString("info", jsonRet.getString("info"));
						msg.setData(bundleData);

						msg.what = 0;
						m_hNotify.sendMessage(msg);

					} catch (Exception e) {
					}
				}
			});
			InterfaceThread.start();
		}
			break;
		}

	}
}
