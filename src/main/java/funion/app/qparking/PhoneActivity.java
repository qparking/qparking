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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：PhoneActivity
 * @时间：2015下午2:56:04
 * @要做的事情：TODO 修改手机号码页面实现类
 */
public class PhoneActivity extends Activity implements OnClickListener {
	private ProgressDialog m_dlgProgress = null;
	private Handler m_hNotify = null;
	private Button m_btDone;
	private LinearLayout m_llVerifyPhonePre;
	private LinearLayout m_llBindPhoneNew;
	private String m_strPhoneNumNew;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_activity);

		// 获取应用程序对象
		QParkingApp appQParking = (QParkingApp) getApplicationContext();
		TextView tvPhoneNum = (TextView) findViewById(R.id.tvPhoneNumPre);

		// 获取控件
		m_llVerifyPhonePre = (LinearLayout) findViewById(R.id.llVerifyPhonePre);
		m_llBindPhoneNew = (LinearLayout) findViewById(R.id.llBindPhoneNew);
		m_btDone = (Button) findViewById(R.id.btPhoneDone);

		m_btDone.setOnClickListener(this);
		if (appQParking.m_strMobile.length() > 0) {
			tvPhoneNum.setText(appQParking.m_strMobile.substring(0, 3)
					+ "****"
					+ appQParking.m_strMobile.substring(
							appQParking.m_strMobile.length() - 4,
							appQParking.m_strMobile.length()));
			m_btDone.setVisibility(View.INVISIBLE);

			m_llBindPhoneNew.setVisibility(View.INVISIBLE);
			m_llVerifyPhonePre.setVisibility(View.VISIBLE);
		}

		// 链接按钮消息
		Button btTemp;
		btTemp = (Button) findViewById(R.id.btPhoneBack);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btGetSmsCodePre);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btPhoneNext);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btGetSmsCodeNew);
		btTemp.setOnClickListener(this);
		// 设置通信句柄
		m_hNotify = new Handler() {
			public void handleMessage(Message msg) {
				m_dlgProgress.dismiss();
				switch (msg.what) {
				case 0:
					QParkingApp.ToastTip(PhoneActivity.this, msg.peekData()
							.getString("info"), -100);
					break;
				case 1:
					QParkingApp.ToastTip(PhoneActivity.this, "短信验证码发送成功，请等待接收！",
							-100);
					break;
				case 2: {
					m_btDone.setVisibility(View.VISIBLE);

					m_llBindPhoneNew.setVisibility(View.VISIBLE);
					m_llVerifyPhonePre.setVisibility(View.INVISIBLE);
				}
					break;
				case 3: {
					Intent intentRetData = new Intent();

					// 设置用户名
					intentRetData.putExtra("NewPhoneNum", m_strPhoneNumNew);

					setResult(1, intentRetData);
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
		case R.id.btPhoneBack:
			finish();
			break;
		case R.id.btGetSmsCodePre: {
			// 显示等待对话框
			m_dlgProgress = ProgressDialog.show(PhoneActivity.this, null,
					"获取验证码，请稍后... ", true, false);

			// 接口调用线程
			Thread InterfaceThread = new Thread(new Runnable() {
				public void run() {
					try {
						// 获取应用程序对象
						QParkingApp appQParking = (QParkingApp) getApplicationContext();

						String strParams = String.format("smsType=4&mobile=%s",
								URLEncoder.encode(appQParking.m_strMobile));
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
		case R.id.btPhoneNext: {
			// 获取验证码
			EditText etSmsCode = (EditText) findViewById(R.id.etSmsCodePre);
			final String strSmsCode = etSmsCode.getText().toString();
			if (strSmsCode.length() == 0) {
				QParkingApp.ToastTip(PhoneActivity.this, "请输入验证码", -100);
				return;
			}
			// 显示等待对话框
			m_dlgProgress = ProgressDialog.show(PhoneActivity.this, null,
					"验证验证码，请稍后... ", true, false);

			// 接口调用线程
			Thread InterfaceThread = new Thread(new Runnable() {
				public void run() {
					try {
						// 获取应用程序对象
						QParkingApp appQParking = (QParkingApp) getApplicationContext();

						String strParams = String.format(
								"mobile=%s&smsCode=%s",
								URLEncoder.encode(appQParking.m_strMobile),
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
		case R.id.btGetSmsCodeNew: {
			// 设置用户名
			EditText etPhoneNum = (EditText) findViewById(R.id.etPhoneNew);

			m_strPhoneNumNew = etPhoneNum.getText().toString();
			if (m_strPhoneNumNew.length() == 0) {
				QParkingApp.ToastTip(PhoneActivity.this, "请输入手机号", -100);
				return;
			}
			// 显示等待对话框
			m_dlgProgress = ProgressDialog.show(PhoneActivity.this, null,
					"获取验证码，请稍后... ", true, false);

			// 接口调用线程
			Thread InterfaceThread = new Thread(new Runnable() {
				public void run() {
					try {
						// 获取应用程序对象
						QParkingApp appQParking = (QParkingApp) getApplicationContext();

						String strParams = String.format("smsType=3&mobile=%s",
								URLEncoder.encode(m_strPhoneNumNew));
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
						e.printStackTrace();
					}
				}
			});
			InterfaceThread.start();
		}
			break;
		case R.id.btPhoneDone: {
			// 获取验证码
			EditText etSmsCode = (EditText) findViewById(R.id.etSmsCodeNew);
			final String strSmsCode = etSmsCode.getText().toString();
			if (m_strPhoneNumNew == null || m_strPhoneNumNew.length() == 0) {
				QParkingApp.ToastTip(PhoneActivity.this, "请输入手机号", -100);
				return;
			}
			if (strSmsCode.length() == 0) {
				QParkingApp.ToastTip(PhoneActivity.this, "请输入验证码", -100);
				return;
			}
			// 显示等待对话框
			m_dlgProgress = ProgressDialog.show(PhoneActivity.this, null,
					"验证验证码，请稍后... ", true, false);

			// 接口调用线程
			Thread InterfaceThread = new Thread(new Runnable() {
				public void run() {
					try {
						String strParams = String.format(
								"mobile=%s&smsCode=%s",
								URLEncoder.encode(m_strPhoneNumNew), strSmsCode);
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
							m_hNotify.sendEmptyMessage(3);
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
		}

	}
}
