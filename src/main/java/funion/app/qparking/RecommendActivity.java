package funion.app.qparking;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendActivity extends Activity implements OnClickListener {

	private ImageView back_iv;
	private TextView title_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend_activity);

		init();
	}

	private void init() {
		back_iv = (ImageView) findViewById(R.id.include_iv_left);
		title_tv = (TextView) findViewById(R.id.include_tv_title);

		back_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_back_btn));
		title_tv.setText("推荐收费员");

		findViewById(R.id.include_iv_left).setOnClickListener(this);
	}

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
