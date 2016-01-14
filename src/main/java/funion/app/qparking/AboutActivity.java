package funion.app.qparking;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：AboutActivity
 * @时间：2015下午3:42:10
 * @要做的事情：TODO 关于我们页面的实现类
 */
public class AboutActivity extends Activity implements OnClickListener {
	private Button btTemp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);

		// 链接按钮消息
		btTemp = (Button) findViewById(R.id.btAboutBack);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btGoWeb);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btContactUs);
		btTemp.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
    * 
    */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btAboutBack:
			finish();
			break;
		case R.id.btGoWeb:

			{
				AlertDialog.Builder adMakeWeb = new Builder(this);
				adMakeWeb.setMessage("您想进入官网，更多的了解我们吗？");
				adMakeWeb.setTitle("官方网站");
				adMakeWeb.setPositiveButton("立即进入",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Uri uriQParking = Uri.parse("http://www.qutingche.cn");
								Intent intentWeb = new Intent(Intent.ACTION_VIEW, uriQParking);
								startActivity(intentWeb);
							}
						});
				adMakeWeb.setNegativeButton("以后再看",
						new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				adMakeWeb.show();
				
			}
			break;
		case R.id.btContactUs: {
			AlertDialog.Builder adMakeCall = new Builder(this);
			adMakeCall.setMessage("确认拨打电话0579 - 82060205？");
			adMakeCall.setTitle("联系我们");
			adMakeCall.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intentTel = new Intent(Intent.ACTION_CALL,
									Uri.parse("tel:0579 - 82060205"));
							startActivity(intentTel);
						}
					});
			adMakeCall.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			adMakeCall.show();
		}
			break;
		}
	}
}
