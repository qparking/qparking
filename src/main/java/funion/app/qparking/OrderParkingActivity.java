package funion.app.qparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import funion.app.common.T;

/**
 * 预约车位
 */
public class OrderParkingActivity extends Activity implements View.OnClickListener {

//    private ImageView back_iv;
//    private TextView title_tv;
//    private TextView address_tv;
//    private TextView descripe_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_parking_activity);
        initTitle();
        if (getIntent() != null) {
            ((TextView) findViewById(R.id.parking_name_tv)).setText(getIntent().getStringExtra("title"));
            ((TextView) findViewById(R.id.address_tv)).setText(getIntent().getStringExtra("address"));
        }
        findViewById(R.id.submit_tv).setOnClickListener(this);
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        ((TextView) findViewById(R.id.include_tv_title)).setText("抢车位");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.submit_tv:
                T.show(OrderParkingActivity.this, "点击", 2000);
                break;
        }
    }

//    @SuppressWarnings("deprecation")
//    private void init() {
//        QParkingApp appQParking = (QParkingApp) getApplicationContext();
//
//        back_iv = (ImageView) findViewById(R.id.include_iv_left);
//        title_tv = (TextView) findViewById(R.id.include_tv_title);
//        address_tv = (TextView) findViewById(R.id.parking_address);
//        descripe_tv = (TextView) findViewById(R.id.order_parking_des2);
//
//        title_tv.setText("预约车位");
//        address_tv.setText(appQParking.m_itemParking.m_strAddress + " " + appQParking.m_itemParking.m_strName);
//        descripe_tv.setText("1.先支付10元预约车位" + "\n" + "2.15分钟内免费，15分钟后开始计费" + "\n" + "3.超30分钟未到自动取消");
//        back_iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_back_btn));
//
//        back_iv.setOnClickListener(new MyListener());
//        findViewById(R.id.BtOrder).setOnClickListener(new MyListener());
//    }
//
//    private class MyListener implements OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.include_iv_left:
//                    finish();
//                    break;
//                case R.id.BtOrder: {
//                    Intent intent = new Intent();
//                    intent.setClass(OrderParkingActivity.this, OrderSuccessActivity.class);
//                    startActivity(intent);
//                }
//                break;
//
//                default:
//                    break;
//            }
//        }
//    }
}
