package funion.app.qparking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.ABstractSpinnerAdapter;
import funion.app.adapter.CustemSpinnerAdapter;
import funion.app.qparking.popWindow.SelectPayModePop;
import funion.app.qparking.popWindow.SpinnerPopWindows;
import funion.app.qparking.tools.JudgePlateNum;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.vo.PayOrderBean;
import funion.app.qparking.vo.PlateNumBean;

/**
 * Created by Administrator on 2015/12/17 0017.
 * TODO 付款
 */
public class SelectPayActivity extends Activity implements View.OnClickListener,ABstractSpinnerAdapter.IOnItemSelectListener{


    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static String URL ="http://qtc.luopan.net/api/createcharge";
    RelativeLayout show_re;
    ImageView back_im;
    LinearLayout ll_plate_num_,show_order_ll,linearLayout1;
    TextView show_plate_tv,carport_name_tv,show_order_num_tv,enter_time_tv,out_time_tv,carport_cast_tv,privilege_money_tv,should_pay_tv,btn_sure;
    ImageView im_arrow;
    ABstractSpinnerAdapter mAdapter;
    private SpinnerPopWindows mSpinerPopWindow;
    SharedPreferences sp;
    List<PlateNumBean> list;
    List<PayOrderBean> orderinfo;//订单属性
    String selectplatenum;
    Context context;
    private int value;
    SelectPayModePop selectPayModePop;
    private int select_value;
    private String order_no_,carplatname_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectpay_activity);
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        context=this;
        initView();
    }



    private void initView() {
        show_order_ll=(LinearLayout)findViewById(R.id.show_order_ll);
        linearLayout1=(LinearLayout)findViewById(R.id.linearLayout1);
        carport_name_tv=(TextView)findViewById(R.id.carport_name_tv);
        show_order_num_tv=(TextView)findViewById(R.id.show_order_num_tv);
        enter_time_tv=(TextView)findViewById(R.id.enter_time_tv);
        out_time_tv=(TextView)findViewById(R.id.out_time_tv);
        carport_cast_tv=(TextView)findViewById(R.id.carport_cast_tv);
        privilege_money_tv=(TextView)findViewById(R.id.privilege_money_tv);
        should_pay_tv=(TextView)findViewById(R.id.should_pay_tv);
        btn_sure=(TextView)findViewById(R.id.btn_sure);
        ll_plate_num_=(LinearLayout)findViewById(R.id.ll_plate_sel);
        show_plate_tv=(TextView)findViewById(R.id.plate_num_tv);
        im_arrow=(ImageView)findViewById(R.id.im_plate_show);
        show_re = (RelativeLayout)findViewById(R.id.show_re);
        ((TextView)findViewById(R.id.include_tv_title)).setText("付款");
        back_im = (ImageView)findViewById(R.id.include_iv_left);
        back_im.setImageResource(R.drawable.top_back_btn);
        back_im.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
        ll_plate_num_.setOnClickListener(this);
        getData();
        mAdapter=new CustemSpinnerAdapter(this);
        mAdapter.refreshData(list,0);

        mSpinerPopWindow=new SpinnerPopWindows(this);
        mSpinerPopWindow.setAdatper(mAdapter);
        mSpinerPopWindow.setItemListener(this);
    }

    private void getOrder() {
        orderinfo=new ArrayList<PayOrderBean>();
        final Map<String,String> params=new HashMap<String,String>();
        params.put("sign", sp.getString("sign", null));
        params.put("platenum", selectplatenum);
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                Log.e("map",result.toString());
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = (String) jsonObject.get("code");
                    String msg = (String) jsonObject.get("msg");
                    if (code.equals(0+"")) {
                        show_re.setVisibility(View.GONE);
                        linearLayout1.setVisibility(View.VISIBLE);
                        show_order_ll.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            PayOrderBean payOrderBean = new PayOrderBean();
                            JSONObject object = jsonArray.getJSONObject(i);
                            payOrderBean.setId(object.getString("id"));
                            payOrderBean.setPark_order(object.getString("park_order"));
                            payOrderBean.setReserveId(object.getString("reserveId"));
                            payOrderBean.setPlateNum(TransCoding.trans(object.getString("plateNum")));
                            payOrderBean.setDuration(object.getString("duration"));
                            payOrderBean.setPrice(object.getString("price"));
                            payOrderBean.setYingPay(object.getString("yingPay"));
                            payOrderBean.setPay(object.getString("pay"));
                            payOrderBean.setYouhui(object.getString("youhui"));
                            payOrderBean.setName(TransCoding.trans(object.getString("name")));
                            payOrderBean.setRule_name(TransCoding.trans(object.getString("rule_name")));
                            payOrderBean.setRule_content(object.getString("rule_content"));
                            payOrderBean.setCouponId(object.getString("couponId"));
                            payOrderBean.setInTime(TransCoding.strToDetailTime(object.getString("inTime")));
                            payOrderBean.setOutTime(TransCoding.strToDetailTime(object.getString("outTime")));
                            orderinfo.add(payOrderBean);
                            carport_name_tv.setText(TransCoding.trans(object.getString("name")));
                            show_order_num_tv.setText(object.getString("park_order"));
                            enter_time_tv.setText(TransCoding.strToDetailTime(object.getString("inTime")));
                            out_time_tv.setText(TransCoding.strToDetailTime(object.getString("outTime")));
                            carport_cast_tv.setText(object.getString("yingPay")+"元");
                            privilege_money_tv.setText(object.getString("youhui")+"元");
                            should_pay_tv.setText(object.getString("pay")+"元");
                            value=Integer.valueOf(object.getString("pay"));
                            order_no_=object.getString("park_order");
                            carplatname_=TransCoding.trans(object.getString("name"));
                        }
                    } else{
                        QParkingApp.ToastTip(context, TransCoding.trans(msg), -100);
                        show_re.setVisibility(View.VISIBLE);
                        linearLayout1.setVisibility(View.GONE);
                        show_order_ll.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, "dfd_list");
    }

    private List<PlateNumBean> getData() {
        list=new ArrayList<PlateNumBean>();
        Map<String,String> params=new HashMap<String,String>();
        params.put("sign", sp.getString("sign", null));
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    Log.e("map",result.toString());
                    JSONObject jsonObject = new JSONObject(result);
                    int code = (int) jsonObject.get("code");
                    if (code == 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            PlateNumBean plateNumBean = new PlateNumBean();
                            plateNumBean.setPlatenum(TransCoding.trans(object.getString("plateNum")));
                            plateNumBean.setIsDefault(object.getString("isDefault"));
                            plateNumBean.setPlateType(object.getString("plateType"));
                            list.add(plateNumBean);
                            String isDefault = object.getString("isDefault");
                            if (isDefault.equals(1 + "")) {
                                selectplatenum = TransCoding.trans(object.getString("plateNum"));
                                show_plate_tv.setText(TransCoding.trans(object.getString("plateNum")));
                                getOrder();
                            }
                        }

                    } else {
                        show_plate_tv.setHint(getResources().getString(R.string.add_platenum_first));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, "getuserplate");
        return list;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.btn_sure:
                recharge(value);
                break;
            case R.id.ll_plate_sel:
                showSpinWindow();
                break;
        }

    }

    private void recharge(int value){
       selectPayModePop=new SelectPayModePop(context,itemsOnClick);
        selectPayModePop.showAtLocation(SelectPayActivity.this.findViewById(R.id.mains), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
        select_value=value;
    }
    private View.OnClickListener itemsOnClick=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectPayModePop.dismiss();
            switch(view.getId()){
                case R.id.wechat_btn:
                    Log.e("show","here");
                    initpay(CHANNEL_WECHAT, select_value, order_no_, selectplatenum, carplatname_);
                    Log.e("show","selectplatenum:"+selectplatenum);
                    break;
                case R.id.alipay_btn:
                    initpay(CHANNEL_ALIPAY,select_value,order_no_,selectplatenum,carplatname_);
                    break;
            }
        }
    };
//      String order_no;//订单编号,String subject;//标题:名字+订单编号String platenum;
    private void initpay(String channel,int value,String orderno,String platenum,String subejct){
        new PaymentTask().execute(new PaymentRequest(channel,value,sp.getString("sign",null),orderno,platenum,subejct));
    }


    private void showSpinWindow() {
        mSpinerPopWindow.setWidth(ll_plate_num_.getWidth());
        mSpinerPopWindow.showAsDropDown(ll_plate_num_);
    }

    @Override
    public void onItemClick(int pos) {
        setHero(pos);
    }

    private void setHero(int pos) {
        if(pos>=0&&pos<=list.size()){
            show_plate_tv.setText(list.get(pos).getPlatenum());
            selectplatenum=list.get(pos).getPlatenum();
            getOrder();
        }
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
            Log.e("show","show"+json);
            try {
                //向Your Ping++ Server SDK请求数据
                data = postJson(URL, json);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }


        public void showMsg(String title, String msg1, String msg2) {
            String str = title;
            if (null !=msg1 && msg1.length() != 0) {
                str += "\n" + msg1;
            }
            if (null !=msg2 && msg2.length() != 0) {
                str += "\n" + msg2;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(str);
            builder.setTitle("提示");
            builder.setPositiveButton("OK", null);
            builder.create().show();
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
            Log.e("show", data+"+-+-+-+-+-");
//            Intent intent = new Intent();
//            String packageName = getPackageName();
//            ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
//            intent.setComponent(componentName);
//            intent.putExtra(PaymentActivity.EXTRA_CHARGE, data);
//            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
        }

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
        String order_no;//订单编号
        String subject;//标题:名字+订单编号
        String platenum;


        public PaymentRequest(String channel, int amount,String sign,String order_no,String platenum,String subject) {
            this.channel = channel;
            this.amount = amount;
            this.sign=sign;
            this.order_no=order_no;
            this.subject=subject;
            this.platenum=platenum;
        }
    }
}
