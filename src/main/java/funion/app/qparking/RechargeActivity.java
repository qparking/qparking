package funion.app.qparking;
/**
 * 充值页面
 * Created by yunze on 2015/12/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.app.AlertDialog.Builder;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import funion.app.qparking.popWindow.SelectPayModePop;
import funion.app.qparking.tools.OkHttpUtils;

public class RechargeActivity extends Activity implements View.OnClickListener{
    private ImageView im_back;
    private TextView title_tv;
    private EditText input_et;
    private int value;
    private int select_value;
    SharedPreferences sp;
    private static String URL ="http://qtc.luopan.net/api/createrecharge";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    Context context;
    SelectPayModePop selectPayModePop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recharge);
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        context=this;
        initView();
    }

    private void initView() {
        input_et=(EditText)findViewById(R.id.recharge_price);
        im_back=(ImageView)findViewById(R.id.include_iv_left);
        title_tv=(TextView)findViewById(R.id.include_tv_title);
        findViewById(R.id.recharge_50).setOnClickListener(this);
        findViewById(R.id.recharge_100).setOnClickListener(this);
        findViewById(R.id.recharge_200).setOnClickListener(this);
        findViewById(R.id.recharge_800).setOnClickListener(this);
        findViewById(R.id.recharge_btn).setOnClickListener(this);
        value=1;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.recharge_50:
                value=1;
                break;
            case R.id.recharge_100:
                value=2;
                break;
            case R.id.recharge_200:
                value=3;
                break;
            case R.id.recharge_800:
                value=4;
                break;
            case R.id.recharge_btn:
                recharge(value);
                break;
        }

    }

    private void recharge(int value) {
        selectPayModePop = new SelectPayModePop(context,itemsOnClick);
        selectPayModePop.showAtLocation(RechargeActivity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
        select_value=value;

    }


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            selectPayModePop.dismiss();
            switch (v.getId()) {
                case R.id.wechat_btn:
                    initpay(CHANNEL_WECHAT,select_value);
                    break;
                case R.id.alipay_btn:
                    initpay(CHANNEL_ALIPAY,select_value);
                    break;
                default:
                    break;
            }


        }

    };

    private void initpay(String channel,int value) {
//        Map<String,String> parm=new HashMap<String,String>();
//        parm.put("sign",sp.getString("sign", null));
//        parm.put("channel", channel);
//        parm.put("amount", value);
//        Log.e("show",parm.toString());
        new PaymentTask().execute(new PaymentRequest(channel, value,sp.getString("sign", null)));

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
            Log.e("show",json);
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
            if(null==data){
                showMsg("请求出错", "请检查URL", "URL无法获取charge");
                return;
            }
            Log.e("show", data);
//            Intent intent = new Intent();
//            String packageName = getPackageName();
//            ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
//            intent.setComponent(componentName);
//            intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
//            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        }

    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null !=msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null !=msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new Builder(context);
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

        public PaymentRequest(String channel, int amount,String sign) {
            this.channel = channel;
            this.amount = amount;
            this.sign=sign;
        }
    }


}
