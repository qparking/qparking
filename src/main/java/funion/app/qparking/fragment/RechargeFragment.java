package funion.app.qparking.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pingplusplus.android.PaymentActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import funion.app.qparking.RechargeActivity;
import funion.app.qparking.popWindow.SelectPayModePop;
import funion.app.qparking.tools.OkHttpUtils;

/**
 * Created by Administrator on 2016/1/16.
 */
public class RechargeFragment extends Fragment {
    private View rechargefragment;
    private EditText input_et;
    private int value;
    private TextView show_balance;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static String URL = "http://qtc.luopan.net/api/createrecharge";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private String balance;
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    SelectPayModePop selectPayModePop;
    private ProgressDialog m_dlgProgress = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rechargefragment == null) {
            rechargefragment = inflater.inflate(R.layout.recharge_view, null);
            sp = getActivity().getSharedPreferences("mMessage", getActivity().MODE_PRIVATE);
            editor=sp.edit();
            getUserInfo();
            findViewId();

        }
        return rechargefragment;
    }

    public void findViewId() {
        input_et = (EditText) rechargefragment.findViewById(R.id.recharge_price);
        ((TextView) rechargefragment.findViewById(R.id.recharge_50)).setOnClickListener(onclick);
        ((TextView) rechargefragment.findViewById(R.id.recharge_100)).setOnClickListener(onclick);
        ((TextView) rechargefragment.findViewById(R.id.recharge_200)).setOnClickListener(onclick);
        ((TextView) rechargefragment.findViewById(R.id.recharge_800)).setOnClickListener(onclick);
        ((Button) rechargefragment.findViewById(R.id.recharge_btn)).setOnClickListener(onclick);
        value = 1;
    }

    private void getUserInfo() {
        Map<String,String> params=new HashMap<String,String>();
        params.put("sign", sp.getString("sign", null));
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                Log.e("result",result.toString());
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    int code=(int)jsonObject.get("code");
                    if(code==0){
                        editor.putString("phone", jsonObject.getString("phone"));
                        editor.putString("balance", jsonObject.getString("balance"));
                        editor.putString("integral", jsonObject.getString("integral"));
                        editor.putString("washtic", jsonObject.getString("washtic"));
                        editor.putString("parktic", jsonObject.getString("parktic"));
                        balance=jsonObject.getString("balance");
                        ((TextView) rechargefragment.findViewById(R.id.show_balance)).setText(balance);
                        editor.commit();
                    }else{
                        QParkingApp.ToastTip(getActivity(), getResources().getString(R.string.getinfo_fail),-100);
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params,"getuserinfo");
    }

    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch ((view.getId())) {
                case R.id.recharge_50:
                    value = 1;
                    break;
                case R.id.recharge_100:
                    value = 2;
                    break;
                case R.id.recharge_200:
                    value = 3;
                    break;
                case R.id.recharge_800:
                    value = 4;
                    break;
                case R.id.recharge_btn:
                    if(!input_et.getText().toString().equals(null)||!input_et.getText().equals("")) {
                        value=Integer.valueOf(input_et.getText().toString().trim());
                    }
                    recharge(value);
                    break;
            }
        }
    };

    private void recharge(int value) {
        selectPayModePop = new SelectPayModePop(getActivity(), itemsOnClick);
        selectPayModePop.showAtLocation(RechargeFragment.this.rechargefragment.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            selectPayModePop.dismiss();
            switch (v.getId()) {
                case R.id.wechat_btn:
                    initpay(CHANNEL_WECHAT, value);
                    break;
                case R.id.alipay_btn:
                    initpay(CHANNEL_ALIPAY, value);
                    break;
                default:
                    break;
            }
        }
    };

    private void initpay(String channel, int value) {
        m_dlgProgress = ProgressDialog.show(getActivity(), null,
                "加载中... ", true, true);
        new PaymentTask().execute(new PaymentRequest(channel, value, sp.getString("sign", null)));

    }

    class PaymentTask extends AsyncTask<PaymentRequest, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(PaymentRequest... pr) {

            PaymentRequest paymentRequest = pr[0];
            String data = null;
            String json = new Gson().toJson(paymentRequest);
            Log.e("show", json);
            try {
                //向Your Ping++ Server SDK请求数据
                data = postJson(URL, json);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        /**
         * 获得服务端的charge，调用ping++ sdk。
         */
        @Override
        protected void onPostExecute(String data) {
            if (null == data) {
                showMsg("请求出错", "请检查URL", "URL无法获取charge");
                return;
            }
            Log.e("show", data);
            Intent intent = new Intent();
            String packageName = getActivity().getPackageName();
            ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
            intent.setComponent(componentName);
            intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        }
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null != msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null != msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private static String postJson(String url, String json) throws IOException {
        MediaType type = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(type, json);
        Request request = new Request.Builder().url(url).post(body).build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    class PaymentRequest {
        String channel;
        int amount;
        String sign;

        public PaymentRequest(String channel, int amount, String sign) {
            this.channel = channel;
            this.amount = amount;
            this.sign = sign;
        }
    }

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        m_dlgProgress.dismiss();
        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                Log.e("show", result);
                if (result.equals("success")) {
                    result = getResources().getString(R.string.success);
                } else if (result.equals("fail")) {
                    result = getResources().getString(R.string.fail);
                } else if (result.equals("cancel")) {
                    result = getResources().getString(R.string.cancle_pay);
                } else if (result.equals("invalid")) {
                    result = getResources().getString(R.string.invalid);
                }
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }
}
