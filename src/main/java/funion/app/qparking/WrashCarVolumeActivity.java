package funion.app.qparking;
/**
 * 洗车卷页面
 * Created by yunze on 2015/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.ParkingVolumeAdapter;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.vo.WashCardBean;

public class WrashCarVolumeActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private ParkingVolumeAdapter adapter;
    private List<WashCardBean> list;
    private Context context = WrashCarVolumeActivity.this;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_parking_volume);
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        initTitle();
        initData();
        initView();
    }

    private void initData() {
        list=new ArrayList<WashCardBean>();
        Map<String,String> params=new HashMap<String,String>();
        params.put("sign",sp.getString("sign",null));
        params.put("stype","0");
        params.put("ps","10");
        params.put("pn","0");

        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    Log.e("map",result.toString());
                    JSONObject jsonObject=new JSONObject(result);
                    int count=(int)jsonObject.get("count");
                    if(count!=0){
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){
                            WashCardBean washCardBean=new WashCardBean();
                            JSONObject object= (JSONObject) jsonArray.get(i);
                            washCardBean.setCouponId(object.getString("couponId"));
                            washCardBean.setMerchantId(object.getString("merchantId"));
                            washCardBean.setCouponName(object.getString("couponName"));
                            washCardBean.setCouponAmount(object.getString("couponAmount"));
                            washCardBean.setStatus(object.getString("status"));
                            washCardBean.setCtype(object.getString("ctype"));
                            washCardBean.setUseType(object.getString("utype"));
                            washCardBean.setServType(object.getString("servtype"));
                            washCardBean.setPlatformTicket(object.getString("platformTicket"));
                            washCardBean.setCouponNumber(object.getString("couponNumber"));
                            washCardBean.setMerchantName(object.getString("merchantName"));
                            washCardBean.setQrCode(object.getString("qrCode"));
                            list.add(washCardBean);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params,"getcoupon");
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.parking_volume_lv);
        adapter = new ParkingVolumeAdapter(context,list);
        listView.setAdapter(adapter);
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("我的洗车卷");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
        }
    }
}
