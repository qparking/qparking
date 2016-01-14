package funion.app.qparking;

import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class ParkInfoActivity extends Activity {

	private ViewPager mPager;
	private List<View> listViews;
	private ImageView cursor;
	private TextView latest_tv, history_tv;
	// 动画图片偏移量
	private int offset = 0;
	// 动画图片宽度
	private int bmpW;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_parkinfo);

		init();
		InitImageView();
		InitViewPager();
	}

	private void init() {
		latest_tv = (TextView) findViewById(R.id.park_latest);
		history_tv = (TextView) findViewById(R.id.park_history);

		findViewById(R.id.btParkInfoBack).setOnClickListener(new MyListener());
		latest_tv.setOnClickListener(new MyListener());
		history_tv.setOnClickListener(new MyListener());
	}

	@SuppressLint("InflateParams")
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.view_pager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.pager_park_latest, null));
		listViews.add(mInflater.inflate(R.layout.pager_bill_history, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab_flag).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 2 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	private class MyListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btParkInfoBack:
				finish();
				break;
			case R.id.park_latest:
				mPager.setCurrentItem(0);
				break;
			case R.id.park_history:
				mPager.setCurrentItem(1);
				break;
			default:
				break;
			}
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		// 偏移量
		int one = offset * 2 + bmpW;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				animation = new TranslateAnimation(one, 0, 0, 0);
				latest_tv.setTextColor(getResources().getColor(R.color.app_green));
				history_tv.setTextColor(getResources().getColor(R.color.app_black));
				break;
			case 1:
				latest_tv.setTextColor(getResources().getColor(R.color.app_black));
				history_tv.setTextColor(getResources().getColor(R.color.app_green));
				animation = new TranslateAnimation(0, one, 0, 0);
				break;
			}
			// True:图片停在动画结束位置
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
}
