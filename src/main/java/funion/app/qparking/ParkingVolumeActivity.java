package funion.app.qparking;
/**
 * 停车卷页面
 * Created by yunze on 2015/12/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class ParkingVolumeActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private List<WashCardBean> list;
    private Context context = ParkingVolumeActivity.this;
    SharedPreferences sp;
    ParkingVolumeAdapter parkingVolumeAdapter;
    private ProgressDialog m_dlgProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_parking_volume);
        sp=getSharedPreferences("mMessage", MODE_PRIVATE);
        m_dlgProgress = ProgressDialog.show(ParkingVolumeActivity.this, null,
                "正在获取信息... ", true, true);
        initView();
        initTitle();
        initData();
    }

    private void initData() {
        list=new ArrayList<WashCardBean>();
        Map<String,String> params=new HashMap<String,String>();
        params.put("sign",sp.getString("sign",null));
        params.put("stype","1");
        params.put("ps","10");
        params.put("pn","0");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    String count=(String)jsonObject.get("count");
                    if(!count.equals(0)){
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
                            washCardBean.setEnd_date(object.getString("end_date"));
                            list.add(washCardBean);
                        }
                        parkingVolumeAdapter=new ParkingVolumeAdapter(context,list);
                        listView.setAdapter(parkingVolumeAdapter);
                        m_dlgProgress.dismiss();
                    }else{
                        m_dlgProgress.dismiss();
                        QParkingApp.ToastTip(ParkingVolumeActivity.this, "暂无数据", -100);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params,"getcoupon");
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.parking_volume_lv);
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.top_back_btn);
        findViewById(R.id.include_back_left_ll).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("我的停车卷");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_back_left_ll:
                finish();
                break;
        }
    }
}
