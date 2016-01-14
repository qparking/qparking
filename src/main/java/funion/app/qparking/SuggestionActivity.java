package funion.app.qparking;

import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.view.SuggestionPopWindow;
import funion.app.qparking.view.SuggestionPopWindow.IOnItemSelectListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SuggestionActivity extends Activity implements IOnItemSelectListener {

	private SuggestionPopWindow myPopWin;
	private TextView popSpinner_tv;
	private TextView complain_tv;
	private TextView suggestion_tv;
	private List<String> list = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestion_activity);

		init();
	}

	private void init() {

		complain_tv = (TextView) findViewById(R.id.tvComplain);
		suggestion_tv = (TextView) findViewById(R.id.tvShare);
		popSpinner_tv = (TextView) findViewById(R.id.tv_select);

		((TextView) findViewById(R.id.include_tv_title)).setText("意见反馈");
		((ImageView) findViewById(R.id.include_iv_left)).setImageDrawable(getResources().getDrawable(
				R.drawable.top_back_btn_n));

		setComplainList();
		myPopWin = new SuggestionPopWindow(this);
		myPopWin.refreshData(list, 0);

		popSpinner_tv.setOnClickListener(new MyClickLisntener());
		findViewById(R.id.include_iv_left).setOnClickListener(new MyClickLisntener());
		findViewById(R.id.btnSuggestion).setOnClickListener(new MyClickLisntener());
		findViewById(R.id.tvComplain).setOnClickListener(new MyClickLisntener());
		findViewById(R.id.tvShare).setOnClickListener(new MyClickLisntener());
	}

	private void setItem(int pos) {
		if (pos >= 0 && pos <= list.size()) {
			String value = list.get(pos);
			popSpinner_tv.setText(value);
		}
	}

	private void setComplainList() {
		list.clear();
		String[] array = getResources().getStringArray(R.array.complain);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		popSpinner_tv.setText(list.get(0));
	}

	private void setSuggestionList() {
		list.clear();
		String[] array = getResources().getStringArray(R.array.suggestion);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		popSpinner_tv.setText(list.get(0));
	}

	private class MyClickLisntener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.include_iv_left:
				finish();
				break;
			case R.id.btnSuggestion:
				// TODO:带上信息类型跳转
				Intent intentNewActivity = new Intent();
				intentNewActivity.setClass(SuggestionActivity.this, SuggestionCompleteActivity.class);
				startActivity(intentNewActivity);
				break;
			// 弹出选择框
			case R.id.tv_select:
				myPopWin.setWidth(popSpinner_tv.getWidth());
				myPopWin.showAsDropDown(popSpinner_tv);
				break;
			case R.id.tvComplain: {
				setComplainList();
				myPopWin.notifyChanged();

				complain_tv.setBackgroundColor(getResources().getColor(R.color.app_green));
				complain_tv.setTextColor(getResources().getColor(R.color.app_white));
				suggestion_tv.setBackgroundColor(getResources().getColor(R.color.app_white));
				suggestion_tv.setTextColor(getResources().getColor(R.color.app_black));
			}
				break;
			case R.id.tvShare: {
				setSuggestionList();
				myPopWin.notifyChanged();

				complain_tv.setBackgroundColor(getResources().getColor(R.color.app_white));
				complain_tv.setTextColor(getResources().getColor(R.color.app_black));
				suggestion_tv.setBackgroundColor(getResources().getColor(R.color.app_green));
				suggestion_tv.setTextColor(getResources().getColor(R.color.app_white));
			}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onItemClick(int pos) {
		setItem(pos);
	}
}
