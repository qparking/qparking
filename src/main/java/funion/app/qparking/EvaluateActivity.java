package funion.app.qparking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：EvaluateActivity
 * @时间：2015下午4:34:13
 * @要做的事情：TODO 评价页面的实现类
 */
public class EvaluateActivity extends Activity implements OnClickListener {
	final int BAR_BASE_COUNT = 1000;

	private int m_iEvaluateImageID;
	private int m_iEvaluateViewID;
	private int m_iPayTypeNameID;
	private int m_iPayTypeViewID;

	private ProgressDialog m_dlgProgress = null;
	private Handler m_hNotify = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluate_activity);

		// 初始化状态
		m_iEvaluateImageID = R.id.ivGivePraise;
		m_iEvaluateViewID = R.id.viewEvaGivePraise;
		m_iPayTypeNameID = R.id.tvEvaFreeBtn;
		m_iPayTypeViewID = R.id.viewEvaFree;

		// 设置默认shape
		((GradientDrawable) findViewById(R.id.viewEvaGivePraise).getBackground()).setColor(0xFF00c1a0);
		((GradientDrawable) findViewById(R.id.viewEvaGiveDesprise).getBackground()).setColor(0xFFFFFFFF);

		((GradientDrawable) findViewById(R.id.viewEvaFree).getBackground()).setColor(0xFF00c1a0);
		((GradientDrawable) findViewById(R.id.viewEvaCharge).getBackground()).setColor(0xFFFFFFFF);

		// 按钮消息
		Button btTemp;

		btTemp = (Button) findViewById(R.id.btEvaBack);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btSubmitEvaluae);
		btTemp.setOnClickListener(this);

		// 获取应用程序对象
		QParkingApp appQParking = (QParkingApp) getApplicationContext();

		// 设置停车场信息
		TextView tvParkName = (TextView) findViewById(R.id.tvEvaParkName);
		tvParkName.setText(appQParking.m_itemParking.m_strName);
		TextView tvPraisedCount = (TextView) findViewById(R.id.tvEvaPraisedCount);
		tvPraisedCount.setText(String.format("%d", appQParking.m_itemParking.m_iPraiseNum));
		TextView tvDesprisedCount = (TextView) findViewById(R.id.tvEvaDesprisedCount);
		tvDesprisedCount.setText(String.format("%d", appQParking.m_itemParking.m_iDespiseNum));
		TextView tvFreeCount = (TextView) findViewById(R.id.tvEvaFreeCount);
		tvFreeCount.setText(String.format("%d", appQParking.m_itemParking.m_iFreeNum));
		TextView tvChargeCount = (TextView) findViewById(R.id.tvEvaChargeCount);
		tvChargeCount.setText(String.format("%d", appQParking.m_itemParking.m_iChargeNum));

		// 设置进度条
		RelativeLayout rlPraisedBar = (RelativeLayout) findViewById(R.id.rlEvaPraised);
		View viewPraisedBar = findViewById(R.id.viewPraisedBar);
		ViewGroup.LayoutParams rllpPraisedBar = viewPraisedBar.getLayoutParams();

		rllpPraisedBar.width = rlPraisedBar.getWidth() * appQParking.m_itemParking.m_iPraiseNum / BAR_BASE_COUNT;
		if (appQParking.m_itemParking.m_iPraiseNum > 0 && rllpPraisedBar.width == 0)
			rllpPraisedBar.width = 1;
		viewPraisedBar.setLayoutParams(rllpPraisedBar);

		RelativeLayout rlDesprisedBar = (RelativeLayout) findViewById(R.id.rlEvaDesprised);
		View viewDesprisedBar = findViewById(R.id.viewDesprisedBar);
		ViewGroup.LayoutParams rllpDesprisedBar = viewDesprisedBar.getLayoutParams();

		rllpDesprisedBar.width = rlDesprisedBar.getWidth() * appQParking.m_itemParking.m_iDespiseNum / BAR_BASE_COUNT;
		if (appQParking.m_itemParking.m_iDespiseNum > 0 && rllpDesprisedBar.width == 0)
			rllpDesprisedBar.width = 1;
		viewDesprisedBar.setLayoutParams(rllpDesprisedBar);

		RelativeLayout rlFreeBar = (RelativeLayout) findViewById(R.id.rlEvaFree);
		View viewFreeBar = findViewById(R.id.viewFreeBar);
		ViewGroup.LayoutParams rllpFreeBar = viewFreeBar.getLayoutParams();

		rllpFreeBar.width = rlFreeBar.getWidth() * appQParking.m_itemParking.m_iFreeNum / BAR_BASE_COUNT;
		if (appQParking.m_itemParking.m_iFreeNum > 0 && rllpFreeBar.width == 0)
			rllpFreeBar.width = 1;
		viewFreeBar.setLayoutParams(rllpFreeBar);

		RelativeLayout rlChargeBar = (RelativeLayout) findViewById(R.id.rlEvaCharge);
		View viewChargeBar = findViewById(R.id.viewChargeBar);
		ViewGroup.LayoutParams rllpChargeBar = viewChargeBar.getLayoutParams();

		rllpChargeBar.width = rlChargeBar.getWidth() * appQParking.m_itemParking.m_iChargeNum / BAR_BASE_COUNT;
		if (appQParking.m_itemParking.m_iChargeNum > 0 && rllpChargeBar.width == 0)
			rllpChargeBar.width = 1;
		viewChargeBar.setLayoutParams(rllpChargeBar);
		// 连接消息
		View viewTemp;

		viewTemp = findViewById(R.id.viewEvaGivePraise);
		viewTemp.setOnClickListener(this);
		viewTemp = findViewById(R.id.viewEvaGiveDesprise);
		viewTemp.setOnClickListener(this);
		viewTemp = findViewById(R.id.viewEvaFree);
		viewTemp.setOnClickListener(this);
		viewTemp = findViewById(R.id.viewEvaCharge);
		viewTemp.setOnClickListener(this);
		// 设置通信句柄
		m_hNotify = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0: {
					m_dlgProgress.dismiss();
					QParkingApp.ToastTip(EvaluateActivity.this, msg.peekData().getString("info"), -100);
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
		if (m_iEvaluateViewID == v.getId() || m_iPayTypeViewID == v.getId())
			return;

		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btEvaBack:
			finish();
			break;
		case R.id.viewEvaFree:
		case R.id.viewEvaCharge: {
			TextView tvTypeName;
			View viewType;
			// 复位原始设置
			tvTypeName = (TextView) findViewById(m_iPayTypeNameID);
			viewType = findViewById(m_iPayTypeViewID);

			tvTypeName.setTextColor(0xFF4d4d4d);
			((GradientDrawable) viewType.getBackground()).setColor(0xFFFFFFFF);
			// 设置当前项
			m_iPayTypeViewID = v.getId();
			switch (m_iPayTypeViewID) {
			case R.id.viewEvaFree:
				m_iPayTypeNameID = R.id.tvEvaFreeBtn;
				break;
			case R.id.viewEvaCharge:
				m_iPayTypeNameID = R.id.tvEvaChargeBtn;
				break;
			}
			tvTypeName = (TextView) findViewById(m_iPayTypeNameID);
			viewType = findViewById(m_iPayTypeViewID);

			tvTypeName.setTextColor(0xFFFFFFFF);
			((GradientDrawable) viewType.getBackground()).setColor(0xFF00c1a0);
		}
			break;
		case R.id.viewEvaGivePraise:
		case R.id.viewEvaGiveDesprise: {
			ImageView ivIcon;
			View viewType;
			// 复位原始设置
			ivIcon = (ImageView) findViewById(m_iEvaluateImageID);
			viewType = findViewById(m_iEvaluateViewID);

			switch (m_iEvaluateViewID) {
			case R.id.viewEvaGivePraise:
				ivIcon.setImageResource(R.drawable.eva_praise_n);
				break;
			case R.id.viewEvaGiveDesprise:
				ivIcon.setImageResource(R.drawable.eva_desprise_n);
				break;
			}

			((GradientDrawable) viewType.getBackground()).setColor(0xFFFFFFFF);
			// 设置当前项
			m_iEvaluateViewID = v.getId();
			switch (m_iEvaluateViewID) {
			case R.id.viewEvaGivePraise: {
				m_iEvaluateImageID = R.id.ivGivePraise;
				ivIcon = (ImageView) findViewById(m_iEvaluateImageID);
				ivIcon.setImageResource(R.drawable.eva_praise_s);
			}
				break;
			case R.id.viewEvaGiveDesprise: {
				m_iEvaluateImageID = R.id.ivGiveDesprise;
				ivIcon = (ImageView) findViewById(m_iEvaluateImageID);
				ivIcon.setImageResource(R.drawable.eva_desprise_s);
			}
				break;
			}
			viewType = findViewById(m_iEvaluateViewID);

			((GradientDrawable) viewType.getBackground()).setColor(0xFF00c1a0);
		}
			break;
		case R.id.btSubmitEvaluae: {
			QParkingApp appQParking = (QParkingApp) getApplicationContext();
			if (appQParking.m_strUserID.length() == 0) {
				AlertDialog.Builder adLogonTip = new Builder(EvaluateActivity.this);
				adLogonTip.setMessage("登录之后才能评价，是否立即登录？");
				adLogonTip.setTitle("登录提示");
				adLogonTip.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						QParkingApp appQParking = (QParkingApp) getApplicationContext();
						// 界面跳转
						Intent intentNewActivity = new Intent();

						appQParking.m_bIsGoCenter = false;
						intentNewActivity.setClass(EvaluateActivity.this, LoginActivity.class);
						startActivity(intentNewActivity);
					}
				});
				adLogonTip.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				adLogonTip.show();
				return;
			}
			// 显示等待对话框
			m_dlgProgress = ProgressDialog.show(EvaluateActivity.this, null, "提交信息，请稍后... ", true, false);

			// 调用接口线程
			Thread threadInterface = new Thread(new Runnable() {
				public void run() {
					try {
						QParkingApp appQParking = (QParkingApp) getApplicationContext();

						String strShell = String.format("shell=%s", appQParking.m_strUserID);
						String strPid = String.format("pid=%s", appQParking.m_itemParking.m_strPid);
						String strSourceType;
						if (appQParking.m_itemParking.m_strShareName.equals("百度"))
							strSourceType = "sourceType=0";
						else
							strSourceType = "sourceType=1";
						String strChargeType = null;
						switch (m_iPayTypeViewID) {
						case R.id.viewEvaFree:
							strChargeType = "chargeType=0";
							break;
						case R.id.viewEvaCharge:
							strChargeType = "chargeType=1";
							break;
						}
						String strEvaluate = null;
						switch (m_iEvaluateViewID) {
						case R.id.viewEvaGivePraise:
							strEvaluate = "evaluate=0";
							break;
						case R.id.viewEvaGiveDesprise:
							strEvaluate = "evaluate=1";
							break;
						}

						String strParams = strShell + "&" + strPid + "&" + strSourceType + "&" + strChargeType + "&"
								+ strEvaluate;
						URL urlInterface = new URL("http://app.qutingche.cn/Mobile/Depot/addComment");
						HttpURLConnection hucInterface = (HttpURLConnection) urlInterface.openConnection();

						hucInterface.setDoInput(true);
						hucInterface.setDoOutput(true);
						hucInterface.setUseCaches(false);

						hucInterface.setRequestMethod("POST");
						// 设置DataOutputStream
						DataOutputStream dosInterface = new DataOutputStream(hucInterface.getOutputStream());

						dosInterface.writeBytes(strParams);
						dosInterface.flush();
						dosInterface.close();

						BufferedReader readerBuff = new BufferedReader(new InputStreamReader(
								hucInterface.getInputStream()));
						String strData = readerBuff.readLine();

						JSONObject jsonRet = new JSONObject(strData);

						Message msg = new Message();
						Bundle bundleData = new Bundle();

						bundleData.putString("info", jsonRet.getString("info"));
						msg.setData(bundleData);

						msg.what = 0;
						m_hNotify.sendMessage(msg);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			threadInterface.start();
		}
			break;
		}
	}
}
