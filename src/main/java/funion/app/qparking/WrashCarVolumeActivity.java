package funion.app.qparking;
/**
 * 洗车卷页面
 * Created by yunze on 2015/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import funion.app.adapter.ParkingVolumeAdapter;

public class WrashCarVolumeActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    private ParkingVolumeAdapter adapter;
    private Context context = WrashCarVolumeActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_parking_volume);
        initTitle();
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.parking_volume_lv);
        adapter = new ParkingVolumeAdapter(context);
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
