package funion.app.qparking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.vo.LeftMenuIconBean;
import funion.app.qparking.vo.ToolBarBean;

public class GuideActivity extends Activity {
	private ViewPager m_vpGuide = null;
	private ArrayList<View> arrayGuideViews;
	private ImageView[] ivIndicators;
	private ViewGroup vgGuidePage;
	private ProgressDialog m_dlgProgress = null;
	private Handler m_hNotify = null;
		Context context;
	private List<LeftMenuIconBean> leftMenuIconBeanList;
	private List<ToolBarBean> toolBarBeanList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
	context=this;
		Intent intent=getIntent();
		leftMenuIconBeanList=intent.getParcelableArrayListExtra("leftmenu");
		toolBarBeanList=intent.getParcelableArrayListExtra("toolbar");
		Log.e("map","收到："+leftMenuIconBeanList.toString());
		// 加载引导页
		LayoutInflater inflater = getLayoutInflater();
		arrayGuideViews = new ArrayList<View>();
		arrayGuideViews.add(inflater.inflate(R.layout.guide_page1, null));
		arrayGuideViews.add(inflater.inflate(R.layout.guide_page2, null));
		arrayGuideViews.add(inflater.inflate(R.layout.guide_page3, null));
		arrayGuideViews.add(inflater.inflate(R.layout.guide_page4, null));

		// 指示灯数组
		ivIndicators = new ImageView[arrayGuideViews.size()];
		// 从指定的XML文件加载视图
		vgGuidePage = (ViewGroup) inflater.inflate(R.layout.guide_activity, null);

		// 实例化小圆点的linearLayout和viewpager
		m_vpGuide = (ViewPager) vgGuidePage.findViewById(R.id.vpGuideContent);
		ivIndicators[0] = (ImageView) vgGuidePage.findViewById(R.id.ivIndicator0);
		ivIndicators[1] = (ImageView) vgGuidePage.findViewById(R.id.ivIndicator1);
		ivIndicators[2] = (ImageView) vgGuidePage.findViewById(R.id.ivIndicator2);
		ivIndicators[3] = (ImageView) vgGuidePage.findViewById(R.id.ivIndicator3);

		setContentView(vgGuidePage);
		// 设置viewpager的适配器和监听事件
		m_vpGuide.setAdapter(new GuidePageAdapter());
		m_vpGuide.setOnPageChangeListener(new GuidePageChangeListener());
		// 设置通信句柄
		m_hNotify = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0: {
//					m_dlgProgress.dismiss();
//					QParkingApp.ToastTip(GuideActivity.this, msg.peekData().getString("info"), -100);

					// 跳转
//					Intent intentMain = new Intent();
//					intentMain.setClass(GuideActivity.this, MainActivity.class);
//					GuideActivity.this.startActivity(intentMain);
//					GuideActivity.this.finish();
					Bundle bundle=new Bundle();
					bundle.putParcelableArrayList("leftmenu", (ArrayList<? extends Parcelable>) leftMenuIconBeanList);
					bundle.putParcelableArrayList("toolbar", (ArrayList<? extends Parcelable>) toolBarBeanList);
					ActivityTools.switchActivity(context, MainAct.class, bundle);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					finish();
				}
					break;
				case 1: {
					m_dlgProgress.dismiss();
					QParkingApp.ToastTip(GuideActivity.this, "网络不给力，请检查相关设置后重试！", -100);
					System.exit(0);
				}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
			System.exit(0);

		return super.onKeyDown(keyCode, event);
	}

	private ImageButton.OnClickListener OnClickStart = new ImageButton.OnClickListener() {
		public void onClick(View v) {
			// 设置已经引导
			SharedPreferences settings = getSharedPreferences("qparking_pref", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("guide_activity", "true");
			editor.commit();

			// 显示等待对话框
//			m_dlgProgress = ProgressDialog.show(GuideActivity.this, null, "正在生成账号，请稍后... ", true, false);

			// 调用接口线程
//			Thread InterfaceThread = new Thread(new Runnable() {
//				public void run() {
					try {
//						String strParams = "safeCode=123456";
//						URL urlInterface = new URL("http://app.qutingche.cn/Mobile/Depot/autoRegister");
//						HttpURLConnection hucInterface = (HttpURLConnection) urlInterface.openConnection();
//
//						hucInterface.setDoInput(true);
//						hucInterface.setDoOutput(true);
//						hucInterface.setUseCaches(false);
//
//						hucInterface.setRequestMethod("POST");
//						// 设置DataOutputStream
//						DataOutputStream dosInterface = new DataOutputStream(hucInterface.getOutputStream());
//
//						dosInterface.writeBytes(strParams);
//						dosInterface.flush();
//						dosInterface.close();
//
//						BufferedReader readerBuff = new BufferedReader(new InputStreamReader(
//								hucInterface.getInputStream()));
//						String strData = readerBuff.readLine();

//						JSONObject jsonRet = new JSONObject(strData);


						Message msg = new Message();
//						Bundle bundleData = new Bundle();
//						if (jsonRet.getInt("status") == 1) {
//							JSONObject jsonData = jsonRet.getJSONObject("data");

							// 获取应用程序对象
//							QParkingApp appQParking = (QParkingApp) getApplicationContext();
//							appQParking.m_strUserID = jsonData.getString("shell");


							// 保存用户信息
//							QParkingApp.SaveUserInfo(GuideActivity.this, appQParking.m_strUserID);
//						}
//						bundleData.putString("info", jsonRet.getString("info"));
//						msg.setData(bundleData);


						msg.what = 0;
						m_hNotify.sendMessage(msg);

					} catch (Exception e) {
						m_hNotify.sendEmptyMessage(1);
					}
//				}
//			});
//			InterfaceThread.start();

		}
	};

	class GuidePageAdapter extends PagerAdapter {
		// 销毁position位置的界面
		@Override
		public void destroyItem(View v, int position, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) v).removeView(arrayGuideViews.get(position));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
		}

		// 获取当前窗体界面数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayGuideViews.size();
		}

		// 初始化position位置的界面
		@Override
		public Object instantiateItem(View v, int position) {
			// TODO Auto-generated method stub
			((ViewPager) v).addView(arrayGuideViews.get(position));

			// 第四个导航页内的按钮事件
			if (position == 3) {
				ImageButton btn = (ImageButton) v.findViewById(R.id.ibtEnter);
				btn.setOnClickListener(OnClickStart);
			}

			return arrayGuideViews.get(position);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			// TODO Auto-generated method stub
			return v == arg1;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int position) {
			ivIndicators[position].setImageResource(R.drawable.indicator_s);
			// TODO Auto-generated method stub
			for (int i = 0; i < ivIndicators.length; i++) {
				// 不是当前选中的page，其小圆点设置为未选中的状态
				if (position == i)
					continue;
				ivIndicators[i].setImageResource(R.drawable.indicator_n);
			}
		}
	}
}
