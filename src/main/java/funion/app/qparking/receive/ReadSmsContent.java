package funion.app.qparking.receive;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yunze on 2016/1/11.
 */
public class ReadSmsContent extends ContentObserver {
    private Cursor cursor = null;
    private Activity mActivity;
    private EditText password;

    public ReadSmsContent(Handler handler, Activity mActivity, EditText password) {
        super(handler);
        this.mActivity = mActivity;
        this.password = password;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        cursor = mActivity.managedQuery(Uri.parse("content://sms/inbox"), new String[]{
                        "_id", "address", "body", "read"
                }, " address=? and read=?",
                new String[]{"1069064019", "0"}, "_id desc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("read", "1");        //修改短信为已读模式
            cursor.moveToNext();
            int smsbodyColumn = cursor.getColumnIndex("body");
            String smsBody = cursor.getString(smsbodyColumn);
            String verifyCode = getDynamicPassword(smsBody);
            if (TextUtils.isEmpty(verifyCode)) {
                return;
            }
            if (password == null) {
                throw new RuntimeException("你传的EditText为空");
            }
            password.setText(verifyCode);
            //EditText获取焦点，3个属性必须同时设置
            password.setFocusable(true);
            password.setFocusableInTouchMode(true);
            password.requestFocus();
            password.setSelection(verifyCode.length());//设置光标位置

        }
        if (Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }

    }

    private String getDynamicPassword(String str) {
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
                + 4 + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            if (m.group().length() == 4) {
                dynamicPassword = m.group();
            }
        }

        return dynamicPassword;

    }
}
