
package funion.app.qparking.personal_center;

import funion.app.qparking.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Administrator
 * @包名： funion.app.qparking.personalcenter
 * @类名：SuccessPayMoneyAct
 * @时间：2015年5月25日下午1:55:35
 * @要做的事情：TODO 完成缴费页面功能
 */
public class SuccessPayMoneyAct extends Activity{
	private Button back_btn;
    private ImageView back_iv;
    private TextView title_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_success_paymoney);
		initView();
	}
	/**
	 * 
	 * void
	 * TODO
	 * 2015年5月25日下午1:57:08
	 */
	private void initView() {
		// TODO Auto-generated method stub
		back_btn = (Button) findViewById(R.id.center_back_btn);
		back_iv = (ImageView) findViewById(R.id.include_iv_left);
		title_tv = (TextView) findViewById(R.id.include_tv_title);
		
		title_tv.setText("完成缴费");
		title_tv.setTextColor(getResources().getColor(R.color.app_white));
		back_iv.setBackgroundResource(R.drawable.top_back_btn);
		
		back_btn.setOnClickListener(new MyListener());
		back_iv.setOnClickListener(new MyListener());

	}
	class MyListener implements OnClickListener{

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 * 2015年5月25日下午1:58:51
		 * SuccessPayMoneyAct.java
		 * TODO
		 */
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.center_back_btn:
				finish();
				
				break;
			case R.id.include_iv_left:
				finish();
				
				break;

			default:
				break;
			}
			
		}
		
	}
	

}
