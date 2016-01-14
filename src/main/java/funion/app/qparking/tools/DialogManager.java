package funion.app.qparking.tools;

import funion.app.qparking.R;
import funion.app.qparking.view.MyPromptDialog;
import funion.app.qparking.view.MyPromptDialog.LeftOnClickListener;
import funion.app.qparking.view.MyPromptDialog.RightOnClickListener;
import android.content.Context;

public class DialogManager {

	private MyPromptDialog promptDialog = null;

	public MyPromptDialog getNoTitlePromptDialog(Context context, String message, String leftString,
			String rightString, LeftOnClickListener leftOnClickListener, RightOnClickListener rightOnClickListener) {
		if (promptDialog == null) {
			promptDialog = new MyPromptDialog(context, R.style.MyDialog);
		}
		promptDialog.setMessage(message);
		promptDialog.setLeftButton(leftString, leftOnClickListener);
		promptDialog.setRightButton(rightString, rightOnClickListener);
		promptDialog.setCancelable(true);
		return promptDialog;
	}

	public MyPromptDialog getPromptDialog(Context context, String title, String message, String leftString,
			String rightString, LeftOnClickListener leftOnClickListener, RightOnClickListener rightOnClickListener) {
		if (promptDialog == null) {
			promptDialog = new MyPromptDialog(context, R.style.MyDialog);
		}
		promptDialog.setTitle(title);
		promptDialog.setMessage(message);
		promptDialog.setLeftButton(leftString, leftOnClickListener);
		promptDialog.setRightButton(rightString, rightOnClickListener);
		promptDialog.setCancelable(true);
		return promptDialog;
	}
}
