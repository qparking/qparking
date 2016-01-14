package funion.app.qparking.view;

import funion.app.qparking.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyPromptDialog extends Dialog {

	private LinearLayout right_ll;
	private TextView title_tv, message_tv;
	private Button left_bt, right_bt;

	public MyPromptDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_prompt);
		init();
	}

	public MyPromptDialog(Context context, int theme) {
		super(context, theme);
		setContentView(R.layout.dialog_prompt);
		init();
	}

	private void init() {
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.y = -50;
		params.alpha = 0.9f;
		params.gravity = Gravity.CENTER;
		this.getWindow().setAttributes(params);

		title_tv = (TextView) findViewById(R.id.dialog_prompt_title);
		message_tv = (TextView) findViewById(R.id.dialog_prompt_message);
		left_bt = (Button) findViewById(R.id.dialog_prompt_ok);
		right_bt = (Button) findViewById(R.id.dialog_prompt_cancel);
		right_ll = (LinearLayout) findViewById(R.id.dialog_prompt_cancel_ll);
	}

	public void setTitle(String title) {
		title_tv.setText(title);
	}

	public void setMessage(String message) {
		message_tv.setText(message);
	}

	public void setLeftButton(String leftButton, final LeftOnClickListener letfOnClickListener) {
		left_bt.setText(leftButton);
		left_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				letfOnClickListener.leftButtonlistener();
			}
		});
	}

	public void setRightButton(String rightButton, final RightOnClickListener rightOnClickListener) {
		right_bt.setText(rightButton);
		right_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rightOnClickListener.rightButtonlistener();
			}
		});
	}

	public void setRightButtonUnvisble() {
		right_ll.setVisibility(View.GONE);
	}

	public interface LeftOnClickListener {
		public void leftButtonlistener();
	}

	public interface RightOnClickListener {
		public void rightButtonlistener();
	}
}
