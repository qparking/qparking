package funion.app.qparking;

import java.util.Date;

import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;

import funion.app.qparking.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ParkInfoActivityOld extends Activity implements OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.park_info_activity);

		// 链接按钮消息
		Button btTemp;
		btTemp = (Button) findViewById(R.id.btParkInfoBack);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btParkInfoEvaluate);
		btTemp.setOnClickListener(this);

		RelativeLayout rlTemp = (RelativeLayout) findViewById(R.id.rlParkingAddress);
		rlTemp.setOnClickListener(this);
		// 读取停车信息
		SharedPreferences spParkInfo = getSharedPreferences("ParkInfo", Activity.MODE_PRIVATE);
		if (spParkInfo.getAll().size() == 0) {
			LinearLayout llParkInfo = (LinearLayout) findViewById(R.id.llParkInfo);
			llParkInfo.setVisibility(View.INVISIBLE);
		} else {
			int iMonth = spParkInfo.getInt("Month", 0);
			int iDate = spParkInfo.getInt("Date", 0);
			int iHour = spParkInfo.getInt("Hour", 0);
			int iMunite = spParkInfo.getInt("Minutes", 0);
			Date dateSet = new Date();
			Date dateCur = new Date();

			dateSet.setMonth(iMonth);
			dateSet.setDate(iDate);
			dateSet.setHours(iHour);
			dateSet.setMinutes(iMunite);
			dateSet.setSeconds(0);
			long lDiff = dateSet.getTime() - dateCur.getTime();
			long lDays = lDiff / (1000 * 60 * 60 * 24);
			long lHours = (lDiff - lDays * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
			long lMinutes = (lDiff - lDays * (1000 * 60 * 60 * 24) - lHours * (1000 * 60 * 60)) / (1000 * 60);

			TextView tvHours = (TextView) findViewById(R.id.tvHourCount);
			TextView tvMinutes = (TextView) findViewById(R.id.tvMinuteCount);
			TextView tvTimeSet = (TextView) findViewById(R.id.tvPickCarTime);
			TextView tvParkName = (TextView) findViewById(R.id.tvParkName);
			TextView tvAddress = (TextView) findViewById(R.id.tvParkAddress);

			if (lDiff < 0) {
				lHours = 0;
				lMinutes = 0;
			}
			tvHours.setText(String.format("%d", lHours + lDays * 24));
			tvMinutes.setText(String.format("%d", lMinutes));
			tvTimeSet.setText(String.format("设置时间 %d-%d %d:%d", iMonth + 1, iDate, iHour, iMunite));
			tvParkName.setText(spParkInfo.getString("name", ""));
			tvAddress.setText(spParkInfo.getString("address", ""));

			QParkingApp appQParking = (QParkingApp) getApplicationContext();

			appQParking.m_itemParking = new TagParkingItem();

			appQParking.m_itemParking.m_strPid = spParkInfo.getString("Pid", "");
			appQParking.m_itemParking.m_strName = spParkInfo.getString("name", "");
			appQParking.m_itemParking.m_strAddress = spParkInfo.getString("address", "");
			appQParking.m_itemParking.m_strShareName = spParkInfo.getString("ShareName", "");
			appQParking.m_itemParking.m_iFreeNum = spParkInfo.getInt("FreeNum", 0);
			appQParking.m_itemParking.m_iChargeNum = spParkInfo.getInt("ChargeNum", 0);
			appQParking.m_itemParking.m_iPraiseNum = spParkInfo.getInt("PraiseNum", 0);
			appQParking.m_itemParking.m_iDespiseNum = spParkInfo.getInt("DespiseNum", 0);
			appQParking.m_itemParking.m_iLocationType = spParkInfo.getInt("LocationType", 0);
			appQParking.m_itemParking.m_iDistance = spParkInfo.getInt("Distance", 0);
			appQParking.m_itemParking.m_llParking = new LatLng(spParkInfo.getFloat("latitude", 0), spParkInfo.getFloat(
					"longitude", 0));
		}
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
		case R.id.btParkInfoBack:
			finish();
			break;
		case R.id.rlParkingAddress: {
			// 界面跳转
			Intent intentNewActivity = new Intent();

			intentNewActivity.putExtra("PickCar", true);
			intentNewActivity.setClass(ParkInfoActivityOld.this, NavigationActivity.class);
			startActivity(intentNewActivity);
		}
			break;
		case R.id.btParkInfoEvaluate: {
			LinearLayout llParkInfo = (LinearLayout) findViewById(R.id.llParkInfo);
			if (llParkInfo.getVisibility() == View.INVISIBLE) {
				QParkingApp.ToastTip(ParkInfoActivityOld.this, "无停车信息，无需评价！", -100);
				return;
			}
			// 界面跳转
			Intent intentNewActivity = new Intent();
			intentNewActivity.setClass(ParkInfoActivityOld.this, EvaluateActivity.class);
			startActivity(intentNewActivity);
		}
			break;
		}
	}
}
