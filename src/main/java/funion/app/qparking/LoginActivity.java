package funion.app.qparking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import funion.app.common.T;
import funion.app.qparking.receive.ReadSmsContent;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.tools.Countdown;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;

/**
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：LogonActivity
 * @时间：2015年5月28日上午11:37:33
 * @要做的事情：TODO登录与注册页面
 */
public class LoginActivity extends Activity implements OnClickListener {
    private Context context = LoginActivity.this;
    private EditText phone, password;
    private TextView sendMsg;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private String phoneCode = "";
    private SmsContent content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        editor = sp.edit();
        sendReceiver();
    }

    private void sendReceiver() {
        content = new SmsContent(new Handler());
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
    }

    private void initView() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("登陆");
        phone = (EditText) findViewById(R.id.input_phone_et);
        sendMsg = (TextView) findViewById(R.id.send_msg_tv);
        password = (EditText) findViewById(R.id.input_password_et);
        findViewById(R.id.agreement_tv).setOnClickListener(this);
        findViewById(R.id.submit_btn).setOnClickListener(this);
        findViewById(R.id.agreement_tv).setOnClickListener(this);
        sendMsg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.send_msg_tv:
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    T.show(context, "账号不能为空", 1000);
                    return;
                }
                if (!AppTools.chekPhone(phone.getText().toString().trim())) {
                    T.showShort(context, "手机格式不正确，请重新输入");
                    return;
                }
                new Countdown(sendMsg, 60, "秒", false);
                requestSecurityCode();
                break;
            case R.id.submit_btn:
                if (TextUtils.isEmpty(phone.getText().toString().trim())) {
                    T.show(context, "账号不能为空", 1000);
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString().trim())) {
                    T.show(context, "验证码不能为空", 1000);
                    return;
                }
                if (!AppTools.chekPhone(phone.getText().toString().trim())) {
                    T.showShort(context, "手机格式不正确，请重新输入");
                    return;
                }
                if (!phoneCode.equals(password.getText().toString().trim())) {
                    T.showShort(context, "验证码不正确，请重新输入");
                    return;
                }
                T.showShort(context, "phoneCode:" + phoneCode + "password:" + password.getText().toString().trim());
                sendLogin();
                break;
            case R.id.agreement_tv:
                Intent intent = new Intent(context, AgreementActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void logining(String code, String result) {
        switch (code) {
            case "0":
                login(result);
                finish();
                break;
            case "1":
                register();
                break;
            case "2":
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("验证码不正确，请重新输入..");
                builder.setPositiveButton("确定", null).show();
                break;
        }
    }

    private void login(String result) {
        try {
            QParkingApp qParkingApp = (QParkingApp) getApplicationContext();
            JSONObject obj = new JSONObject(result);
            qParkingApp.m_strScore = obj.getString("integral");
            qParkingApp.m_strUserName = phone.getText().toString().trim();
            editor.putString("sign", obj.getString("sign"));
            editor.putString("username", phone.getText().toString().trim());
            editor.putString("avatar", obj.getString("avatar"));
            editor.putString("balance", obj.getString("balance"));//余额
            editor.putString("integral", obj.getString("integral"));//积分
            String plate = obj.getString("plate");
            obj = new JSONObject(plate);
            editor.putString("ID", obj.getString("ID"));
            qParkingApp.m_strUserID = obj.getString("ID");
            editor.putString("PLATETYPE", obj.getString("PLATETYPE"));
            editor.putString("userId", obj.getString("USERID"));
            editor.putString("PLATENUM", obj.getString("PLATENUM"));//车牌号
            editor.putString("ISDEFAULT", obj.getString("ISDEFAULT"));
            editor.commit();
            setResult(RESULT_OK);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void register() {
        if (TextUtils.isEmpty(phoneCode)) {
            T.showShort(context, "验证码不能为空");
            return;
        }
        Intent intent = new Intent(context, PlateNumActivity.class);
        intent.putExtra("mobile", phone.getText().toString().trim());
        intent.putExtra("vcode", phoneCode);
        startActivity(intent);

    }

    private void sendLogin() {
        Map<String, String> param = new HashMap<>();
        param.put("mobile", phone.getText().toString().trim());
        param.put("vcode", password.getText().toString().trim());
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                Log.e("result", result);
                try {
                    JSONObject obj = new JSONObject(result);
                    String code = obj.getString("code");
                    String userMsg = obj.getString("usermsg");
                    logining(code, userMsg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, param, "login");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getContentResolver().unregisterContentObserver(content);
    }

    private void requestSecurityCode() {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phone.getText().toString());
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                Log.e("result", result);
                try {
                    JSONObject obj = new JSONObject(result);
                    String code = obj.getString("code");
                    if (code.equals("0")) {
                        T.showShort(context, TransCoding.trans(obj.getString("msg")));
                        phoneCode = obj.getString("vcode");
                    } else {
                        T.showShort(context, TransCoding.trans(obj.getString("msg")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, map, "duanxinget.html");
    }

    class SmsContent extends ContentObserver {
        private Cursor cursor = null;

        public SmsContent(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // 读取收件箱中指定号码的短信
            cursor = managedQuery(Uri.parse("content://sms/inbox"),
                    new String[]{"_id", "address", "read", "body"},
                    " address=? and read=?",
                    new String[]{"1069064019", "0"}, "_id desc");
            // 按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
            if (cursor != null && cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put("read", "1"); // 修改短信为已读模式
                cursor.moveToNext();
                int smsbodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(smsbodyColumn);
                password.setText(getDynamicPassword(smsBody));
            }
            // 在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
            if (Build.VERSION.SDK_INT < 14) {
                cursor.close();
            }

        }
    }

    private String getDynamicPassword(String str) {
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
                + 4 + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            dynamicPassword = m.group();
        }

        return dynamicPassword;

    }
}
