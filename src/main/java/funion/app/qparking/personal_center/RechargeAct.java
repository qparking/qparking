package funion.app.qparking.personal_center;

import funion.app.qparking.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Administrator
 * @包名： funion.app.qparking.personal_center
 * @类名：RechargeAct
 * @时间：2015年5月25日下午4:42:14
 * @要做的事情：TODO 充值的页面实现类
 */
public class RechargeAct extends Activity {

	private ImageView back_iv;
	private TextView title_tv;
	private EditText price_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_recharge);

		init();

	}

	@SuppressWarnings("deprecation")
	private void init() {
		back_iv = (ImageView) findViewById(R.id.include_iv_left);
		title_tv = (TextView) findViewById(R.id.include_tv_title);
		price_et = (EditText) findViewById(R.id.recharge_price);

		back_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_back_btn));
		title_tv.setText("充值");
		title_tv.setTextColor(getResources().getColor(R.color.app_white));
		
		price_et.setOnFocusChangeListener(new MyFocusListener());
		back_iv.setOnClickListener(new MyListener());
	}

	private class MyListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.include_iv_left:
				finish();
				break;

			default:
				break;
			}
		}
	}
	
	private class MyFocusListener implements OnFocusChangeListener{
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				View view = getWindow().peekDecorView();
		        if (view != null) {
		            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		        }
			}
		}
	}
}
