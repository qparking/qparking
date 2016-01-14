package funion.app.qparking;
/**
 * 我的车位
 * Created by yunze on 2015/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import funion.app.adapter.MyParkingLotAdapter;

public class MyParkingLotActivity extends Activity implements View.OnClickListener {
    private MyParkingLotAdapter adapter;
    private ListView listView;
    private Context context = MyParkingLotActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_parking_lot);
        initTitle();
        initView();
    }

    private void initView() {
//        listView = (ListView) findViewById(R.id.my_parking_lot_lv);
        adapter = new MyParkingLotAdapter(context);
        listView.setAdapter(adapter);
    }


    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("我的停车卷");
        ((TextView) findViewById(R.id.include_tv_right)).setText("发布");
        findViewById(R.id.include_tv_right).setOnClickListener(this);
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
