package funion.app.qparking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：CenterActivity
 * @时间：2015下午5:10:34
 * @要做的事情：TODO 个人中心的页面实现类
 */
public class MyInfoActivity extends Activity implements OnClickListener {
    final int MODIFY_USERNAME = 0;
    final int MODIFY_PHONENUM = 1;
    final int MODIFY_PASSWORD = 2;
    final int MODIFY_PLATENUM = 3;

    private ProgressDialog m_dlgProgress = null;
    private Handler m_hNotify = null;

    private TextView m_tvUserName;
    private TextView m_tvPhoneNum;
    private TextView m_tvScore;
    private TextView m_tvCarNo;

    String m_strNewUserName;
    String m_strNewPhoneNum;
    String m_strNewPlateNum;

    String m_strParams;
    URL m_urlInterface;
    int m_iMsgID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_activity);
        // 获取应用程序对象
        QParkingApp appQParking = (QParkingApp) getApplicationContext();

        // 设置用户信息
        m_tvUserName = (TextView) findViewById(R.id.tvCenterUserName);
        m_tvPhoneNum = (TextView) findViewById(R.id.tvCenterPhoneNum);
        m_tvScore = (TextView) findViewById(R.id.tvUserScore);
        m_tvCarNo = (TextView) findViewById(R.id.tvUserCarNo);

        if (appQParking.m_strUserName.length() > 0)
            m_tvUserName.setText(appQParking.m_strUserName);
        if (appQParking.m_strMobile.length() > 0)
            m_tvPhoneNum.setText(appQParking.m_strMobile.substring(0, 3)
                    + "****"
                    + appQParking.m_strMobile.substring(
                    appQParking.m_strMobile.length() - 4,
                    appQParking.m_strMobile.length()));

        m_tvScore.setText(appQParking.m_strScore);
        if (appQParking.m_strCarNo.length() > 0)
            m_tvCarNo.setText(appQParking.m_strCarNo);

        // 链接按钮消息
        Button btTemp;
        btTemp = (Button) findViewById(R.id.btCenterBack);
        btTemp.setOnClickListener(this);
        btTemp = (Button) findViewById(R.id.btLogout);
        btTemp.setOnClickListener(this);

        RelativeLayout rlTemp;
        // 连接菜单消息
        rlTemp = (RelativeLayout) findViewById(R.id.rlCenterUserName);
        rlTemp.setOnClickListener(this);
        rlTemp = (RelativeLayout) findViewById(R.id.rlCenterPhone);
        rlTemp.setOnClickListener(this);
        rlTemp = (RelativeLayout) findViewById(R.id.rlCenterPassword);
        rlTemp.setOnClickListener(this);
        rlTemp = (RelativeLayout) findViewById(R.id.rlCenterScore);
        rlTemp.setOnClickListener(this);
        rlTemp = (RelativeLayout) findViewById(R.id.rlCenterCarNo);
        rlTemp.setOnClickListener(this);
        rlTemp = (RelativeLayout) findViewById(R.id.rlCenterParking);
        rlTemp.setOnClickListener(this);
        // 设置通信句柄
        m_hNotify = new Handler() {
            public void handleMessage(Message msg) {
                // 获取应用程序对象
                QParkingApp appQParking = (QParkingApp) getApplicationContext();

                m_dlgProgress.dismiss();
                switch (msg.what) {
                    case 0:
                        QParkingApp.ToastTip(MyInfoActivity.this, msg.peekData()
                                .getString("info"), -100);
                        break;
                    case 1: {
                        QParkingApp.ToastTip(MyInfoActivity.this, "用户名修改成功", -100);

                        appQParking.m_strUserName = m_strNewUserName;
                        m_tvUserName.setText(m_strNewUserName);
                    }
                    break;
                    case 2: {
                        QParkingApp.ToastTip(MyInfoActivity.this, "手机重新绑定成功", -100);

                        appQParking.m_strMobile = m_strNewPhoneNum;
                        m_tvPhoneNum.setText(m_strNewPhoneNum.substring(0, 3)
                                + "****"
                                + m_strNewPhoneNum.substring(
                                m_strNewPhoneNum.length() - 4,
                                m_strNewPhoneNum.length()));
                    }
                    break;
                    case 3: {
                        QParkingApp.ToastTip(MyInfoActivity.this, "密码修改成功", -100);
                    }
                    break;
                    case 4: {
                        QParkingApp.ToastTip(MyInfoActivity.this, "车牌号修改成功", -100);

                        appQParking.m_strCarNo = m_strNewPlateNum;
                        m_tvCarNo.setText(m_strNewPlateNum);
                    }
                    break;
                }
            }
        };
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
        // 界面跳转
        Intent intentNewActivity = new Intent();
        // TODO Auto-generated method stub
        switch (v.getId()) {
            // 退出登录
            case R.id.btLogout: {
                // 获取应用程序对象
                AlertDialog.Builder adMakeWeb = new Builder(this);
                adMakeWeb.setMessage("确定退出当前已登录的账户吗？");
                adMakeWeb.setTitle("提示：");
                adMakeWeb.setPositiveButton("确定",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                QParkingApp appQParking = (QParkingApp) getApplicationContext();
                                // if(appQParking.m_strMobile.length()==0)
                                // {
                                // QParkingApp.ToastTip(this, "请绑定手机", -100);
                                // return;
                                // }
                                appQParking.m_strUserID = "";
                                // 清除用户信息
                                SharedPreferences spParkInfo = getSharedPreferences(
                                        "UserInfo", Activity.MODE_PRIVATE);
                                Editor editorParkInfo = spParkInfo.edit();
                                editorParkInfo.clear();
                                editorParkInfo.commit();
                                Toast.makeText(getApplicationContext(),
                                        "您已成功退出了当前的账户！", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                adMakeWeb.setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                adMakeWeb.show();

                break;

            }
            case R.id.btCenterBack:

                finish();
                break;
            // 用户名/昵称修改
            case R.id.rlCenterUserName: {
                intentNewActivity.setClass(MyInfoActivity.this,
                        UserNameActivity.class);
                startActivityForResult(intentNewActivity, MODIFY_USERNAME);
            }
            break;
            // 更换电话
            case R.id.rlCenterPhone: {
                intentNewActivity
                        .setClass(MyInfoActivity.this, PhoneActivity.class);
                startActivityForResult(intentNewActivity, MODIFY_PHONENUM);
            }
            break;
            // 更改密码

            case R.id.rlCenterPassword: {
                intentNewActivity.setClass(MyInfoActivity.this,
                        PasswordActivity.class);
                startActivityForResult(intentNewActivity, MODIFY_PASSWORD);
            }
            break;
            // 我的积分
            case R.id.rlCenterScore: {
                intentNewActivity
                        .setClass(MyInfoActivity.this, ScoreActivity.class);
                startActivity(intentNewActivity);
            }
            break;
            // 车牌号码
            case R.id.rlCenterCarNo: {
                intentNewActivity.setClass(MyInfoActivity.this,
                        PlateNumActivity.class);
                startActivityForResult(intentNewActivity, MODIFY_PLATENUM);
            }
            break;
            // 添加过的停车场
            case R.id.rlCenterParking: {
                intentNewActivity.setClass(MyInfoActivity.this,
                        ParkAuthActivity.class);
                startActivity(intentNewActivity);
            }
            break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0)
            return;

        // 获取应用程序对象
        QParkingApp appQParking = (QParkingApp) getApplicationContext();

        switch (requestCode) {
            case MODIFY_USERNAME: {
                m_strNewUserName = data.getExtras().getString("NewName");
                if (appQParking.m_strUserName.equals(m_strNewUserName))
                    return;

                // 显示等待对话框
                m_dlgProgress = ProgressDialog.show(MyInfoActivity.this, null,
                        "提交修改用户名申请... ", true, false);

                m_iMsgID = 1;
                m_strParams = String.format("shell=%s&username=%s",
                        URLEncoder.encode(appQParking.m_strUserID),
                        URLEncoder.encode(m_strNewUserName));
                try {
                    m_urlInterface = new URL(
                            "http://app.qutingche.cn/Mobile/MemberIndex/modifyName");
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            break;
            case MODIFY_PHONENUM: {
                m_strNewPhoneNum = data.getExtras().getString("NewPhoneNum");
                if (appQParking.m_strMobile.equals(m_strNewPhoneNum))
                    return;

                // 显示等待对话框
                m_dlgProgress = ProgressDialog.show(MyInfoActivity.this, null,
                        "重新绑定手机... ", true, false);

                m_iMsgID = 2;
                m_strParams = String.format("shell=%s&mobile=%s",
                        appQParking.m_strUserID, m_strNewPhoneNum);
                try {
                    m_urlInterface = new URL(
                            "http://app.qutingche.cn/Mobile/MemberIndex/bindMobile");
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            break;
            case MODIFY_PASSWORD: {
                // 显示等待对话框
                m_dlgProgress = ProgressDialog.show(MyInfoActivity.this, null,
                        "修改密码... ", true, false);

                m_iMsgID = 3;
                m_strParams = String.format("shell=%s&oldPwd=%s&newPwd=%s",
                        URLEncoder.encode(appQParking.m_strUserID), URLEncoder
                                .encode(data.getExtras().getString("PasswordOld")),
                        URLEncoder
                                .encode(data.getExtras().getString("PasswordNew")));
                try {
                    m_urlInterface = new URL(
                            "http://app.qutingche.cn/Mobile/MemberIndex/modifyPwd");
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            break;
            case MODIFY_PLATENUM: {
                m_strNewPlateNum = data.getExtras().getString("NewCarNo");
                if (appQParking.m_strCarNo.equals(m_strNewPlateNum))
                    return;

                // 显示等待对话框
                m_dlgProgress = ProgressDialog.show(MyInfoActivity.this, null,
                        "提交修改车牌号申请... ", true, false);

                m_iMsgID = 4;
                m_strParams = String.format("shell=%s&carNo=%s",
                        URLEncoder.encode(appQParking.m_strUserID),
                        URLEncoder.encode(m_strNewPlateNum));
                try {
                    m_urlInterface = new URL(
                            "http://app.qutingche.cn/Mobile/MemberIndex/modifyCarNo");
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            break;
        }
        // 调用接口线程
        Thread InterfaceThread = new Thread(new Runnable() {
            public void run() {
                try {
                    HttpURLConnection hucInterface = (HttpURLConnection) m_urlInterface
                            .openConnection();

                    hucInterface.setDoInput(true);
                    hucInterface.setDoOutput(true);
                    hucInterface.setUseCaches(false);

                    hucInterface.setRequestMethod("POST");
                    // 设置DataOutputStream
                    DataOutputStream dosInterface = new DataOutputStream(
                            hucInterface.getOutputStream());

                    dosInterface.writeBytes(m_strParams);
                    dosInterface.flush();
                    dosInterface.close();

                    BufferedReader readerBuff = new BufferedReader(
                            new InputStreamReader(hucInterface.getInputStream()));
                    String strData = readerBuff.readLine();

                    JSONObject jsonRet = new JSONObject(strData);

                    Message msg = new Message();
                    Bundle bundleData = new Bundle();
                    if (jsonRet.getInt("status") == 1) {
                        m_hNotify.sendEmptyMessage(m_iMsgID);
                        if (m_iMsgID == 3) {
                            JSONObject jsonData = jsonRet.getJSONObject("data");
                            // 获取应用程序对象
                            QParkingApp appQParking = (QParkingApp) getApplicationContext();
                            appQParking.m_strUserID = jsonData
                                    .getString("shell");
                            // 保存用户信息
                            QParkingApp.SaveUserInfo(MyInfoActivity.this,
                                    appQParking.m_strUserID);
                        }
                    } else {
                        bundleData.putString("info", jsonRet.getString("info"));
                        msg.setData(bundleData);

                        msg.what = 0;
                        m_hNotify.sendMessage(msg);
                    }

                } catch (Exception e) {
                }
            }
        });
        InterfaceThread.start();

        super.onActivityResult(requestCode, resultCode, data);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     * 2015年5月14日
     * CenterActivity.java
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
}
