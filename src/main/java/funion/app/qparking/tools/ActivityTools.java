package funion.app.qparking.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import funion.app.qparking.LoginActivity;


public class ActivityTools {

	/**
	 * 切换界面
	 * 
	 * @param context
	 * @param descClass
	 */
	public static void switchActivity(Context context, Class<?> descClass,
			Bundle bundle) {
		Class<?> mClass= context.getClass();
		if (mClass == descClass) {
			return;
		}
		try {
			Intent intent = new Intent();
			intent.setClass(context, descClass);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			((Activity)context).startActivityForResult(intent, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
