package funion.app.qparking;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import funion.app.qparking.tools.ActivityTools;

public class SetActivity extends Activity implements OnClickListener {
	private Context context = SetActivity.this;
	private Button btTemp;
	private TextView tvVersion;
	private RelativeLayout rlTemp, rlSetShareFriend;
	SharedPreferences sp;
	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set_activity);
		sp=getSharedPreferences("mMessage",MODE_PRIVATE);
		initView();
		sharePlatform();

	}

	/**
	 * 
	 * void TODO 2015下午2:21:53
	 */
	private void initView() {
		// 链接按钮消息
		btTemp = (Button) findViewById(R.id.btSetBack);
		btTemp.setOnClickListener(this);
		// 连接菜单消息
		rlTemp = (RelativeLayout) findViewById(R.id.rlSetOfflineMap);

		rlTemp.setOnClickListener(this);
		rlTemp = (RelativeLayout) findViewById(R.id.rlSetAbout);
		rlTemp.setOnClickListener(this);
		rlTemp = (RelativeLayout) findViewById(R.id.rlFeedBack);
		rlTemp.setOnClickListener(this);
		rlTemp = (RelativeLayout) findViewById(R.id.rlSetGrade);
		rlTemp.setOnClickListener(this);
		rlSetShareFriend = (RelativeLayout) findViewById(R.id.rlSetShareFriend);
		rlSetShareFriend.setOnClickListener(this);
		findViewById(R.id.logout_tv).setOnClickListener(this);

		// 设置版本号
		QParkingApp appQParking = (QParkingApp) getApplicationContext();
		tvVersion = (TextView) findViewById(R.id.tvCurVersion);
		tvVersion.setText(appQParking.m_strVersion);

	}

	/**
	 * 
	 * void TODO 2015年5月28日上午9:42:33 分享到平台
	 */
	private void sharePlatform() {
		// TODO Auto-generated method stub
		UMImage image = new UMImage(context, R.drawable.ic_launcher);
		mController
				.setShareContent("趣停车，一款想停哪儿就停哪儿，任性车位任你选的应用。http://app.qutingche.cn");
		// 设置分享图片, 参数2为图片的url地址
		mController.setShareMedia(new UMImage((Activity) context,
				R.drawable.ic_launcher));

		// 设置分享内容

		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent
				.setShareContent("趣停车，一款想停哪儿就停哪儿，任性车位任你选的应用。http://app.qutingche.cn");
		weixinContent.setTitle("");
		weixinContent.setTargetUrl("http://app.qutingche.cn");
		weixinContent.setShareImage(image);
		mController.setShareMedia(weixinContent);

		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler((Activity) context,
				QParkingApp.APPID, QParkingApp.APPSECRET);

		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler((Activity) context,
				QParkingApp.APPID, QParkingApp.APPSECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		// QQ好友
		QQShareContent shareContent = new QQShareContent();
		shareContent
				.setShareContent("趣停车，一款想停哪儿就停哪儿，任性车位任你选的应用。http://app.qutingche.cn");
		shareContent.setShareImage(image);
		shareContent.setTargetUrl("http://app.qutingche.cn");
		mController.setShareMedia(shareContent);
		// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) context,
				QParkingApp.QQAPPID, QParkingApp.QQAPPSECRET);
		qqSsoHandler.addToSocialSDK();

		// QQ空间
		QZoneShareContent qShareContent = new QZoneShareContent();
		qShareContent
				.setShareContent("趣停车，一款想停哪儿就停哪儿，任性车位任你选的应用。http://app.qutingche.cn");
		qShareContent.setShareImage(image);
		qShareContent.setTargetUrl("http://app.qutingche.cn");
		mController.setShareMedia(shareContent);
		// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
				(Activity) context, QParkingApp.QQAPPID,
				QParkingApp.QQAPPSECRET);
		qZoneSsoHandler.addToSocialSDK();

		// 设置新浪SSO handler免登录
		// mController.getConfig().setSsoHandler(new SinaSsoHandler());

		// 设置腾讯微博SSO handler 免登录
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

	}

	@Override
	public void onClick(View v) {
		// 界面跳转
		Intent intentNewActivity = new Intent();
		switch (v.getId()) {
		case R.id.btSetBack:
			finish();
			break;
		// 界面跳转 离线地图
		case R.id.rlSetOfflineMap: {

			intentNewActivity.setClass(SetActivity.this,
					OfflineMapActivity.class);
			startActivity(intentNewActivity);
		}
			break;
		case R.id.rlSetAbout: {
			intentNewActivity.setClass(SetActivity.this, AboutActivity.class);
			startActivity(intentNewActivity);

		}
			break;
		case R.id.rlFeedBack: {
			ActivityTools.switchActivity(context,FeedBack.class,null);
		}
			break;
		case R.id.rlSetGrade: {
			Toast.makeText(context, "功能建设中，敬请期待...", Toast.LENGTH_SHORT).show();

		}
			break;

		case R.id.rlSetShareFriend:

			mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
					SHARE_MEDIA.DOUBAN);
			mController.openShare((Activity) context, false);

			break;
			case R.id.logout_tv:
				sp.edit().clear().commit();
				Toast.makeText(context, getResources().getString(R.string.exit_success), Toast.LENGTH_SHORT).show();
				ActivityTools.switchActivity(context, MainAct.class, null);
				finish();
				break;
		}

	}

	// 方法的回调
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		if(resultCode==RESULT_OK){
			
			Uri uri = data.getData();
	        // 得到ContentResolver对象
	        ContentResolver cr = getContentResolver();
	        // 取得电话本中开始一项的光标
	        Cursor cursor = cr.query(uri, null, null, null, null);
	        // 向下移动光标
	        while (cursor.moveToNext()) {
	            // 取得联系人名字
	            int nameFieldColumnIndex = cursor
	                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	            String contact = cursor.getString(nameFieldColumnIndex);
	            int nameFieldColumnIndex1 = cursor
	            		.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP);
	            String phone = cursor.getString(nameFieldColumnIndex1);
	            
	            System.out.println("联系人"+contact+phone);
	        }
			  
		}
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
}
