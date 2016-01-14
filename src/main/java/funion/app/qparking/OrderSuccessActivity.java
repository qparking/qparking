/**
 * 
 */
package funion.app.qparking;

import funion.app.qparking.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Administrator
 * @包名： funion.app.qparking.personal_center
 * @类名：AppointmentSuccessAct
 * @时间：2015年5月25日下午3:09:40
 * @要做的事情：TODO 预约成功页面实现类
 */
public class OrderSuccessActivity extends Activity {
	private ImageView back_iv;
	private TextView title_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_success_activity);
		initView();
	}

	private void initView() {
		back_iv = (ImageView) findViewById(R.id.include_iv_left);
		title_tv = (TextView) findViewById(R.id.include_tv_title);

		title_tv.setText("预约成功");
		title_tv.setTextColor(getResources().getColor(R.color.app_white));
		back_iv.setBackgroundResource(R.drawable.top_back_btn);

		back_iv.setOnClickListener(new MyListener());

	}

	class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.include_iv_left:
				finish();

				break;
			case R.id.order_cancel:
				QParkingApp.ToastTip(OrderSuccessActivity.this, "取消", -100);
				break;
			case R.id.order_goParking:
				QParkingApp.ToastTip(OrderSuccessActivity.this, "马上导航", -100);
				break;
			default:
				break;
			}
		}
	}
}
