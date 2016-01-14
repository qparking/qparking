package funion.app.qparking;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;

import funion.app.qparking.tools.DialogManager;
import funion.app.qparking.view.MyPromptDialog;
import funion.app.qparking.view.WheelView;
import funion.app.qparking.view.MyPromptDialog.LeftOnClickListener;
import funion.app.qparking.view.MyPromptDialog.RightOnClickListener;

public class ParkingDetailActivity extends Activity {

	final int NAVIGATOR_FINISH = 0;

	private MyPromptDialog dialog;
	private ImageView back_iv;
	private TextView title_tv;
	private TextView praise_tv;
	private TextView despise_tv;
	private TextView address_tv;
	private WheelView m_wvDate;
	private WheelView m_wvHour;
	private WheelView m_wvMinite;
	private LinearLayout m_llTimerSetting;
	private View m_viewTimerSetBk;
	private Animation m_animShowTimerSetting;
	private Animation m_animHideTimerSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parking_detail_activity);

		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {

		QParkingApp appQParking = (QParkingApp) getApplicationContext();

		back_iv = (ImageView) findViewById(R.id.include_iv_left);
		title_tv = (TextView) findViewById(R.id.include_tv_title);
		praise_tv = (TextView) findViewById(R.id.tvPraiseCount);
		despise_tv = (TextView) findViewById(R.id.tvDespiseCount);
		address_tv = (TextView) findViewById(R.id.tvParkingAddress);
		m_llTimerSetting = (LinearLayout) findViewById(R.id.llTimerSetting);
		m_viewTimerSetBk = findViewById(R.id.viewTimerBack);

		m_animShowTimerSetting = AnimationUtils.loadAnimation(ParkingDetailActivity.this, R.anim.timer_setting_up);
		m_animHideTimerSetting = AnimationUtils.loadAnimation(ParkingDetailActivity.this, R.anim.timer_setting_down);

		// 初始化时间选择器
		m_wvDate = (WheelView) findViewById(R.id.wvDate);
		m_wvHour = (WheelView) findViewById(R.id.wvHour);
		m_wvMinite = (WheelView) findViewById(R.id.wvMinite);

		String[] strWeekDay = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		String[] strDates = new String[21];
		Calendar calendarTemp = Calendar.getInstance();
		Date dateTemp = null;
		for (int i = 0; i < 10; i++) {
			int iDate = calendarTemp.get(Calendar.DATE);
			calendarTemp.set(Calendar.DATE, iDate - 1);
			dateTemp = calendarTemp.getTime();
			strDates[9 - i] = String.format("%d月%d日 %s", dateTemp.getMonth() + 1, dateTemp.getDate(),
					strWeekDay[dateTemp.getDay()]);
		}
		strDates[10] = "今天";
		calendarTemp = Calendar.getInstance();
		for (int i = 0; i < 10; i++) {
			int iDate = calendarTemp.get(Calendar.DATE);
			calendarTemp.set(Calendar.DATE, iDate + 1);
			dateTemp = calendarTemp.getTime();
			strDates[11 + i] = String.format("%d月%d日 %s", dateTemp.getMonth() + 1, dateTemp.getDate(),
					strWeekDay[dateTemp.getDay()]);
		}
		String[] strHours = new String[24];
		for (int i = 0; i < strHours.length; i++)
			strHours[i] = String.format("%02d", i);
		String[] strMinite = new String[60];
		for (int i = 0; i < strMinite.length; i++)
			strMinite[i] = String.format("%02d", i);

		// 设置日期
		StringWheelAdapter adapterStringDate = new StringWheelAdapter();

		adapterStringDate.SetItem(strDates);
		m_wvDate.setAdapter(adapterStringDate);
		m_wvDate.setCurrentItem(10);
		m_wvDate.setCyclic(true);
		// 设置小时
		StringWheelAdapter adapterStringHour = new StringWheelAdapter();

		adapterStringHour.SetItem(strHours);
		m_wvHour.setAdapter(adapterStringHour);
		m_wvHour.setCurrentItem(dateTemp.getHours());
		m_wvHour.setCyclic(true);
		// 设置分钟
		StringWheelAdapter adapterStringMinite = new StringWheelAdapter();

		adapterStringMinite.SetItem(strMinite);
		m_wvMinite.setAdapter(adapterStringMinite);
		m_wvMinite.setCurrentItem(dateTemp.getMinutes());
		m_wvMinite.setCyclic(true);

		title_tv.setText(appQParking.m_itemParking.m_strName);
		praise_tv.setText(appQParking.m_itemParking.m_iPraiseNum + "");
		despise_tv.setText(appQParking.m_itemParking.m_iDespiseNum + "");
		address_tv.setText(appQParking.m_itemParking.m_strAddress + " " + appQParking.m_itemParking.m_strName);
		back_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_back_btn));

		m_animShowTimerSetting.setAnimationListener(new MyAnimationListener());
		m_animHideTimerSetting.setAnimationListener(new MyAnimationListener());

		back_iv.setOnClickListener(new MyListener());
		m_viewTimerSetBk.setOnClickListener(new MyListener());
		findViewById(R.id.llParkingPrice).setOnClickListener(new MyListener());
		findViewById(R.id.llParkingComment).setOnClickListener(new MyListener());
		findViewById(R.id.parking_phone).setOnClickListener(new MyListener());
		findViewById(R.id.order).setOnClickListener(new MyListener());
		findViewById(R.id.navigation).setOnClickListener(new MyListener());
		findViewById(R.id.btSetTimer).setOnClickListener(new MyListener());
	}

	/**
	 * wheelview适配器
	 * 
	 * @author Administrator
	 *
	 */
	public class StringWheelAdapter implements WheelView.WheelAdapter {
		String[] m_strItem;

		void SetItem(String[] strItem) {
			m_strItem = strItem;
		}

		public String getItem(int index) {
			return m_strItem[index];
		}

		// 获取滚动项最大长度
		public int getMaximumLength() {
			int iLength = 0;
			for (int i = 0; i < m_strItem.length; i++) {
				if (iLength < m_strItem[i].length())
					iLength = m_strItem[i].length();
			}

			return iLength;
		}

		@Override
		public int getItemsCount() {
			return m_strItem.length;
		}

	}

	private void ShowPickCarReminder() {
		m_viewTimerSetBk.setVisibility(View.VISIBLE);
		m_llTimerSetting.startAnimation(m_animShowTimerSetting);
	}

	private void HIdePickCarReminder() {
		m_viewTimerSetBk.setVisibility(View.INVISIBLE);
		m_llTimerSetting.startAnimation(m_animHideTimerSetting);
	}

	private class MyListener implements OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			QParkingApp appQParking = (QParkingApp) getApplicationContext();
			Intent intentNewActivity = new Intent();
			switch (v.getId()) {
			case R.id.include_iv_left:
				finish();
				break;
			case R.id.viewTimerBack:
				HIdePickCarReminder();
				break;
			// 价格详细
			case R.id.llParkingPrice:
				ShowPickCarReminder();
				break;
			// 停车场评价
			case R.id.llParkingComment:
				intentNewActivity.setClass(ParkingDetailActivity.this, EvaluateActivity.class);
				startActivity(intentNewActivity);
				break;
			// 拨打电话
			case R.id.parking_phone:
				DialogManager dialogManager = new DialogManager();
				dialog = dialogManager.getPromptDialog(ParkingDetailActivity.this, "提示", "是否拨打电话10086", "是", "否",
						new LeftOnClickListener() {
							@Override
							public void leftButtonlistener() {
								Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:10086"));
								startActivity(intent);
								dialog.dismiss();
							}
						}, new RightOnClickListener() {
							@Override
							public void rightButtonlistener() {
								dialog.dismiss();
							}
						});
				dialog.show();
				break;
			// 预约停车
			case R.id.order:
				intentNewActivity.setClass(ParkingDetailActivity.this, OrderParkingActivity.class);
				startActivity(intentNewActivity);
				break;
			// 开始导航
			case R.id.navigation:

				BNaviPoint ptStart = new BNaviPoint(appQParking.m_llMe.longitude, appQParking.m_llMe.latitude, "我的位置",
						BNaviPoint.CoordinateType.BD09_MC);
				BNaviPoint ptEnd = new BNaviPoint(appQParking.m_itemParking.m_llParking.longitude,
						appQParking.m_itemParking.m_llParking.latitude, appQParking.m_itemParking.m_strName,
						BNaviPoint.CoordinateType.BD09_MC);

				BaiduNaviManager.getInstance().launchNavigator(ParkingDetailActivity.this, ptStart, ptEnd,
						NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
						true, // 真实导航
						BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
						new OnStartNavigationListener() { // 跳转监听

							@Override
							public void onJumpToNavigator(Bundle configParams) {
								Intent intent = new Intent(ParkingDetailActivity.this, BNavigatorActivity.class);
								intent.putExtras(configParams);
								startActivityForResult(intent, NAVIGATOR_FINISH);
							}

							@Override
							public void onJumpToDownloader() {

							}
						});
				break;
			// 设置提醒时间
			case R.id.btSetTimer:
				Calendar calendarTemp = Calendar.getInstance();
				int iDate = calendarTemp.get(Calendar.DATE);
				int iCurSel = m_wvDate.getCurrentItem();

				calendarTemp.set(Calendar.DATE, iDate + iCurSel - 10);
				Date dateSet = calendarTemp.getTime();

				dateSet.setHours(m_wvHour.getCurrentItem());
				dateSet.setMinutes(m_wvMinite.getCurrentItem());
				dateSet.setSeconds(0);
				if (dateSet.before(new Date())) {
					QParkingApp.ToastTip(ParkingDetailActivity.this, "您设置的时间已经逝去了", Toast.LENGTH_SHORT);
					return;
				}

				HIdePickCarReminder();

				// 进行闹铃注册
				Intent intent = new Intent(ParkingDetailActivity.this, ReminderReceiver.class);
				PendingIntent sender = PendingIntent.getBroadcast(ParkingDetailActivity.this, 0, intent, 0);

				// 设置执行时间
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateSet);

				AlarmManager managerAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
				// API19以后，闹铃将有可能延迟5分钟内生效
				managerAlarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

				// 保存停车信息
				SharedPreferences spParkInfo = getSharedPreferences("ParkInfo", Activity.MODE_PRIVATE);
				Editor editorParkInfo = spParkInfo.edit();
				editorParkInfo.clear();

				// 用putString的方法保存数据
				editorParkInfo.putInt("Month", dateSet.getMonth());
				editorParkInfo.putInt("Date", dateSet.getDate());
				editorParkInfo.putInt("Hour", dateSet.getHours());
				editorParkInfo.putInt("Minutes", dateSet.getMinutes());
				editorParkInfo.putString("Pid", appQParking.m_itemParking.m_strPid);
				editorParkInfo.putString("name", appQParking.m_itemParking.m_strName);
				editorParkInfo.putString("address", appQParking.m_itemParking.m_strAddress);
				editorParkInfo.putString("ShareName", appQParking.m_itemParking.m_strShareName);
				editorParkInfo.putFloat("latitude", (float) appQParking.m_itemParking.m_llParking.latitude);
				editorParkInfo.putFloat("longitude", (float) appQParking.m_itemParking.m_llParking.longitude);
				editorParkInfo.putInt("FreeNum", appQParking.m_itemParking.m_iFreeNum);
				editorParkInfo.putInt("ChargeNum", appQParking.m_itemParking.m_iChargeNum);
				editorParkInfo.putInt("PraiseNum", appQParking.m_itemParking.m_iPraiseNum);
				editorParkInfo.putInt("DespiseNum", appQParking.m_itemParking.m_iDespiseNum);
				editorParkInfo.putInt("LocationType", appQParking.m_itemParking.m_iLocationType);
				editorParkInfo.putInt("Distance", appQParking.m_itemParking.m_iDistance);

				editorParkInfo.commit();
				QParkingApp.ToastTip(ParkingDetailActivity.this, "设置成功", -100);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 动画执行相关回调
	 * 
	 * @author Administrator
	 *
	 */
	private class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation.equals(m_animShowTimerSetting)) {
				RelativeLayout.LayoutParams rllpTimerSetting = (RelativeLayout.LayoutParams) m_llTimerSetting
						.getLayoutParams();
				m_llTimerSetting.clearAnimation();

				rllpTimerSetting.bottomMargin = 0;
				m_llTimerSetting.setLayoutParams(rllpTimerSetting);
			} else if (animation.equals(m_animHideTimerSetting)) {
				RelativeLayout.LayoutParams rllpTimerSetting = (RelativeLayout.LayoutParams) m_llTimerSetting
						.getLayoutParams();
				m_llTimerSetting.clearAnimation();

				rllpTimerSetting.bottomMargin = (int) (-m_llTimerSetting.getHeight());
				m_llTimerSetting.setLayoutParams(rllpTimerSetting);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (m_viewTimerSetBk.getVisibility() == View.VISIBLE) {
				m_viewTimerSetBk.setVisibility(View.INVISIBLE);
				m_llTimerSetting.startAnimation(m_animHideTimerSetting);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0)
			return;

		switch (requestCode) {
		case NAVIGATOR_FINISH: {
			DialogManager dialogManager = new DialogManager();
			dialog = dialogManager.getPromptDialog(this, "温馨提示", "导航结束，是否添加取车提醒时间", "添加提醒", "取消",
					new LeftOnClickListener() {
						@Override
						public void leftButtonlistener() {
							ShowPickCarReminder();
							dialog.dismiss();
						}
					}, new RightOnClickListener() {
						@Override
						public void rightButtonlistener() {
							dialog.dismiss();
						}
					});
			dialog.show();
		}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
