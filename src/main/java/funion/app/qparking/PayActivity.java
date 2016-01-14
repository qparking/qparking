package funion.app.qparking;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 付款页面
 * 
 * @author Administrator
 */
public class PayActivity extends Activity {

	private ImageView back_iv;
	private TextView title_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_activity);

		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		back_iv = (ImageView) findViewById(R.id.include_iv_left);
		title_tv = (TextView) findViewById(R.id.include_tv_title);

		title_tv.setText("停车信息");

		back_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_back_btn));
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
}
