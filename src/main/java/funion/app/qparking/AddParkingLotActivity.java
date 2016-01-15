package funion.app.qparking;/**
 * Created by yunze on 2016/1/7.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import funion.app.common.T;
import funion.app.qparking.tools.OkHttpUtils;

public class AddParkingLotActivity extends Activity implements View.OnClickListener {
    private EditText parkingName;
    private EditText parkingAddress;
    private EditText parkingMan;
    private EditText parkingPhone;
    private Context context = AddParkingLotActivity.this;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_parking_lot);
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        initView();
    }

    private void initView() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("申请入住");
        parkingName = (EditText) findViewById(R.id.parking_name_tv);
        parkingAddress = (EditText) findViewById(R.id.address_parking_tv);
        parkingMan = (EditText) findViewById(R.id.man_tv);
        parkingPhone = (EditText) findViewById(R.id.phone_tv);
        findViewById(R.id.submit_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.submit_btn:
                if (parkingName.getText().toString().equals("")) {
                    T.showShort(context, "停车场名不能为空");
                    return;
                }
                if (parkingAddress.getText().toString().equals("")) {
                    T.showShort(context, "停车场地址不能为空");
                    return;
                }
                if (parkingMan.getText().toString().equals("")) {
                    T.showShort(context, "联系人不能为空");
                    return;
                }
                if (parkingPhone.getText().toString().equals("")) {
                    T.showShort(context, "联系电话不能为空");
                    return;
                }
                getResponse();
                break;
        }
    }

    public void getResponse() {
//        String sign = sp.getString("userId", null);
        Map<String, String> param = new HashMap<String, String>();
        param.put("sign", "343b1c4a3ea748121b2d640fc8700db0f36");
        param.put("deportname", parkingName.getText().toString());
        param.put("address", parkingAddress.getText().toString());
        param.put("contactperson", parkingAddress.getText().toString());
        param.put("contactway", parkingAddress.getText().toString());
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                //T.show(context, result, 1000);
                try {
                    JSONObject object = new JSONObject(result);
                    if (!object.getString("code").equals("0")) {
                        T.showShort(context, object.getString("msg"));
                        return;
                    } else {
                        T.showShort(context, object.getString("msg"));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param, "applyforjoin");
    }
}
