package funion.app.qparking;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * 用户昵称输入
 * 
 * @author Administrator
 */
public class UserNameActivity extends Activity implements OnClickListener {
	private Button btTemp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.username_activity);

		// 获取应用程序对象
		QParkingApp appQParking = (QParkingApp) getApplicationContext();

		// 设置用户名
		EditText etUserName = (EditText) findViewById(R.id.etModifyUserName);

		etUserName.setText(appQParking.m_strUserName);
		// 链接按钮消息

		btTemp = (Button) findViewById(R.id.btUserNameBack);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btUserNameDone);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btUserNameBack:
			finish();
			break;
		case R.id.btUserNameDone: {
			Intent intentRetData = new Intent();

			// 设置用户名
			EditText etUserName = (EditText) findViewById(R.id.etModifyUserName);
			intentRetData.putExtra("NewName", etUserName.getText().toString());

			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			setResult(1, intentRetData);
			finish();
		}
		}

	}
}
