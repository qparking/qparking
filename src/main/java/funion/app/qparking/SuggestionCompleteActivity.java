package funion.app.qparking;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SuggestionCompleteActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggestion_complete);

		init();
	}

	private void init() {

		((TextView) findViewById(R.id.include_tv_title)).setText("意见反馈");
		((ImageView) findViewById(R.id.include_iv_left)).setImageDrawable(getResources().getDrawable(
				R.drawable.top_back_btn_n));

		findViewById(R.id.include_iv_left).setOnClickListener(new MyClickLisntener());
	}

	private class MyClickLisntener implements OnClickListener {

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
