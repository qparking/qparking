package funion.app.qparking;
/**
 * 停车卷页面
 * Created by yunze on 2015/12/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Map;

import funion.app.adapter.ParkingVolumeAdapter;
import funion.app.qparking.tools.OkHttpUtils;

public class ParkingVolumeActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private ParkingVolumeAdapter adapter;
    private Context context = ParkingVolumeActivity.this;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_parking_volume);
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        initData();
        initTitle();
        initView();
    }

    private void initData() {
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
                Log.e("map",result.toString());
            }
        },params,"getcoupon");
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.parking_volume_lv);
//        adapter = new ParkingVolumeAdapter(context);
//        listView.setAdapter(adapter);
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("我的停车卷");
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
