package funion.app.qparking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;

public class ParkAuthActivity extends Activity implements OnClickListener {
	private View m_viewGreenBar = null;
	private ImageView m_ivNoAddParkTip = null;
	private ListView m_lvInfoList;
	private ParkAuthAdapter m_adapterInfoList;
	private int m_iTypeNameID;
	private ProgressDialog m_dlgProgress = null;

	// 停车场信息列表
	List<tagParkAuthItem> m_listParkAuth = new ArrayList<tagParkAuthItem>();

	private Handler m_hNotify = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			m_dlgProgress.dismiss();
			switch (msg.what) {
			case 0: {
				if (m_listParkAuth.size() == 0) {
					m_ivNoAddParkTip.setVisibility(View.VISIBLE);
					m_lvInfoList.setVisibility(View.INVISIBLE);
				} else {
					// 实例化自定义适配器
					m_adapterInfoList = new ParkAuthAdapter(ParkAuthActivity.this, m_listParkAuth);
					m_lvInfoList.setAdapter(m_adapterInfoList);
				}
			}
				break;
			case 1: {
				String strInfo = msg.peekData().getString("info");
				if (!strInfo.equals("空数据"))
					QParkingApp.ToastTip(ParkAuthActivity.this, strInfo, -100);
			}
			default:
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parking_auth_activity);

		m_iTypeNameID = R.id.tvAuthAll;
		// 返回按钮
		Button btBack = (Button) findViewById(R.id.btParkingAuthBack);
		btBack.setOnClickListener(this);

		// 链接显示类型
		TextView tvTemp;

		tvTemp = (TextView) findViewById(R.id.tvAuthAll);
		tvTemp.setOnClickListener(this);
		tvTemp = (TextView) findViewById(R.id.tvAuthDoing);
		tvTemp.setOnClickListener(this);
		tvTemp = (TextView) findViewById(R.id.tvAuthFailed);
		tvTemp.setOnClickListener(this);
		tvTemp = (TextView) findViewById(R.id.tvAuthPassed);
		tvTemp.setOnClickListener(this);

		m_viewGreenBar = findViewById(R.id.viewAuthTypeGreenBar);
		// 调整绿条长度
		RelativeLayout.LayoutParams rllpGreenBar = (RelativeLayout.LayoutParams) m_viewGreenBar.getLayoutParams();
		rllpGreenBar.width = getWindowManager().getDefaultDisplay().getWidth() / 4;
		m_viewGreenBar.setLayoutParams(rllpGreenBar);

		m_ivNoAddParkTip = (ImageView) findViewById(R.id.ivNoAddParkTip);
		m_lvInfoList = (ListView) findViewById(R.id.lvParkAuthInfo);

		// 获取用户添加的停车场信息
		m_dlgProgress = ProgressDialog.show(ParkAuthActivity.this, null, "获取数据... ", true, false);
		// 调用接口线程
		Thread InterfaceThread = new Thread(new Runnable() {
			public void run() {
				try {
					// 获取应用程序对象
					QParkingApp appQParking = (QParkingApp) getApplicationContext();

					String strParams = String.format("shell=%s", URLEncoder.encode(appQParking.m_strUserID));
					URL urlInterface = new URL("http://app.qutingche.cn/Mobile/MemberDepot/index");
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

					BufferedReader readerBuff = new BufferedReader(new InputStreamReader(hucInterface.getInputStream()));
					String strData = readerBuff.readLine();

					JSONObject jsonRet = new JSONObject(strData);
					if (jsonRet.getInt("status") == 1) {
						if (jsonRet.isNull("data")) {
							m_hNotify.sendEmptyMessage(0);
							return;
						}
						JSONArray jsonInfos = jsonRet.getJSONArray("data");
						// 获取信息
						for (int i = 0; i < jsonInfos.length(); i++) {
							JSONObject jsonData = jsonInfos.getJSONObject(i);

							tagParkAuthItem itemParkAuth = new tagParkAuthItem();

							itemParkAuth.m_strName = jsonData.getString("name");
							itemParkAuth.m_lAddTime = jsonData.getLong("add_time");
							itemParkAuth.m_strAddress = jsonData.getString("address");
							itemParkAuth.m_bIsFree = jsonData.getInt("free_number") > 0;
							itemParkAuth.m_iPraiseNum = jsonData.getInt("praise_number");
							itemParkAuth.m_iDespiseNum = jsonData.getInt("despise_number");
							itemParkAuth.m_iStatus = jsonData.getInt("status");

							m_listParkAuth.add(itemParkAuth);
						}

						m_hNotify.sendEmptyMessage(0);
					} else {
						Message msg = new Message();
						Bundle bundleData = new Bundle();

						bundleData.putString("info", jsonRet.getString("info"));
						msg.setData(bundleData);

						msg.what = 1;
						m_hNotify.sendMessage(msg);
					}

				} catch (Exception e) {
				}
			}
		});
		InterfaceThread.start();
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
		if (m_iTypeNameID == v.getId())
			return;
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btParkingAuthBack:
			finish();
			break;
		case R.id.tvAuthAll:
		case R.id.tvAuthPassed:
		case R.id.tvAuthDoing:
		case R.id.tvAuthFailed: {
			int iStartPos = 0, iEndPos = 0;
			int iScreenWith = getWindowManager().getDefaultDisplay().getWidth();
			TextView tvTypeName;
			switch (m_iTypeNameID) {
			case R.id.tvAuthAll:
				iStartPos = 0;
				break;
			case R.id.tvAuthPassed:
				iStartPos = iScreenWith / 4;
				break;
			case R.id.tvAuthDoing:
				iStartPos = iScreenWith * 2 / 4;
				break;
			case R.id.tvAuthFailed:
				iStartPos = iScreenWith * 3 / 4;
				break;
			}
			// 复位原始设置
			tvTypeName = (TextView) findViewById(m_iTypeNameID);

			tvTypeName.setTextColor(0xFF4d4d4d);
			// 设置当前项
			m_iTypeNameID = v.getId();
			switch (m_iTypeNameID) {
			case R.id.tvAuthAll: {
				if (m_adapterInfoList != null) {
					m_adapterInfoList.SetShowType(0);
					m_adapterInfoList.notifyDataSetChanged();
				}
				iEndPos = 0;
			}
				break;
			case R.id.tvAuthPassed: {
				if (m_adapterInfoList != null) {
					m_adapterInfoList.SetShowType(1);
					m_adapterInfoList.notifyDataSetChanged();
				}
				iEndPos = iScreenWith / 4;
			}
				break;
			case R.id.tvAuthDoing: {
				if (m_adapterInfoList != null) {
					m_adapterInfoList.SetShowType(2);
					m_adapterInfoList.notifyDataSetChanged();
				}
				iEndPos = iScreenWith * 2 / 4;
			}
				break;
			case R.id.tvAuthFailed: {
				if (m_adapterInfoList != null) {
					m_adapterInfoList.SetShowType(3);
					m_adapterInfoList.notifyDataSetChanged();
				}
				iEndPos = iScreenWith * 3 / 4;
			}
				break;
			}
			tvTypeName = (TextView) findViewById(m_iTypeNameID);

			tvTypeName.setTextColor(0xFF00c1a0);

			TranslateAnimation taMoveGreenBar = new TranslateAnimation(iStartPos, iEndPos, 0, 0);
			taMoveGreenBar.setDuration(30);
			taMoveGreenBar.setFillAfter(true);
			m_viewGreenBar.startAnimation(taMoveGreenBar);
		}
		}
	}

	// 停车场信息节点
	class tagParkAuthItem {
		public String m_strName;
		public long m_lAddTime;
		public String m_strAddress;
		public boolean m_bIsFree;
		public int m_iPraiseNum;
		public int m_iDespiseNum;
		public int m_iStatus;
	};

	// 列表适配器
	class ParkAuthAdapter extends BaseAdapter {
		private int m_iShowType;
		private List<tagParkAuthItem> m_listItemAll;
		private List<tagParkAuthItem> m_listItemPass = new ArrayList<tagParkAuthItem>();
		private List<tagParkAuthItem> m_listItemAuth = new ArrayList<tagParkAuthItem>();
		private List<tagParkAuthItem> m_listItemFail = new ArrayList<tagParkAuthItem>();
		private LayoutInflater m_layoutInflater;

		public ParkAuthAdapter(Context context, List<tagParkAuthItem> list) {
			m_iShowType = 0;
			m_listItemAll = list;
			m_layoutInflater = LayoutInflater.from(context);
			for (int i = 0; i < m_listItemAll.size(); i++) {
				tagParkAuthItem itemParkAuth = m_listItemAll.get(i);
				switch (itemParkAuth.m_iStatus) {
				case 0:
					m_listItemAuth.add(itemParkAuth);
					break;
				case 1:
					m_listItemPass.add(itemParkAuth);
					break;
				case 2:
					m_listItemFail.add(itemParkAuth);
					break;
				}
			}
		}

		public void SetShowType(int iType) {
			m_iShowType = iType;
		}

		@Override
		public int getCount() {
			switch (m_iShowType) {
			case 0:
				return m_listItemAll.size();
			case 1:
				return m_listItemPass.size();
			case 2:
				return m_listItemAuth.size();
			case 3:
				return m_listItemFail.size();
			}
			return 0;
		}

		@Override
		public tagParkAuthItem getItem(int position) {
			switch (m_iShowType) {
			case 0:
				return m_listItemAll.get(position - 1);
			case 1:
				return m_listItemPass.get(position - 1);
			case 2:
				return m_listItemAuth.get(position - 1);
			case 3:
				return m_listItemFail.get(position - 1);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position - 1;
		}

		// 控制ITEM 布局内容
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = m_layoutInflater.inflate(R.layout.park_auth_info, null); // 加载布局

			tagParkAuthItem itemParkAuth = null;

			switch (m_iShowType) {
			case 0:
				itemParkAuth = m_listItemAll.get(position);
				break;
			case 1:
				itemParkAuth = m_listItemPass.get(position);
				break;
			case 2:
				itemParkAuth = m_listItemAuth.get(position);
				break;
			case 3:
				itemParkAuth = m_listItemFail.get(position);
				break;
			}
			// 设置停车场名
			TextView tvName = (TextView) convertView.findViewById(R.id.tvAuthParkName);
			tvName.setText(itemParkAuth.m_strName);
			// 设置审核状态
			ImageView ivAuthStatus = (ImageView) convertView.findViewById(R.id.ivAuthParkStatus);
			TextView tvAuthStatus = (TextView) convertView.findViewById(R.id.tvAuthParkStatus);
			switch (itemParkAuth.m_iStatus) {
			case 0: {
				ivAuthStatus.setImageResource(R.drawable.park_certify_passing);
				tvAuthStatus.setText("认证进行中");
				tvAuthStatus.setTextColor(0xFFffdb29);
			}
				break;
			case 1: {
				ivAuthStatus.setImageResource(R.drawable.park_certify_passed);
				tvAuthStatus.setText("认证已通过");
				tvAuthStatus.setTextColor(0xFF94d62c);
			}
				break;
			case 2: {
				ivAuthStatus.setImageResource(R.drawable.park_certify_unpassed);
				tvAuthStatus.setText("认证未通过");
				tvAuthStatus.setTextColor(0xFFff5e2c);
			}
				break;
			}
			// 设置添加时间
			TextView tvAddTime = (TextView) convertView.findViewById(R.id.tvAddTime);
			Date dateAdd = new Date(itemParkAuth.m_lAddTime * 1000);
			Calendar caledarAdd = Calendar.getInstance();

			caledarAdd.setTime(dateAdd);
			tvAddTime.setText(String.format("添加时间: %d-%d-%d", caledarAdd.get(Calendar.YEAR),
					caledarAdd.get(Calendar.MONTH) + 1, caledarAdd.get(Calendar.DATE)));
			// 设置停车场评价
			TextView tvEvaluate = (TextView) convertView.findViewById(R.id.tvAuthParkEvaluate);
			tvEvaluate
					.setText(String.format("已赞: %d    鄙视: %d", itemParkAuth.m_iPraiseNum, itemParkAuth.m_iDespiseNum));
			// 设置停车场收费状态
			TextView tvPayStatus = (TextView) convertView.findViewById(R.id.tvAuthParkPayStatus);
			if (itemParkAuth.m_bIsFree)
				tvPayStatus.setText("状态: 免费");
			else
				tvPayStatus.setText("状态: 收费");
			// 设置停车场地址
			TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAuthParkAddress);
			tvAddress.setText(String.format("地址: %s", itemParkAuth.m_strAddress));

			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			switch (m_iShowType) {
			case 0:
				return m_listItemAll.size();
			case 1:
				return m_listItemPass.size();
			case 2:
				return m_listItemAuth.size();
			case 3:
				return m_listItemFail.size();
			}
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		// 是否Item监听
		@Override
		public boolean isEmpty() {
			return true;
		}

		// true所有项目可选择可点击
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		// 是否显示分割线
		@Override
		public boolean isEnabled(int position) {
			return true;
		}
	}

}
