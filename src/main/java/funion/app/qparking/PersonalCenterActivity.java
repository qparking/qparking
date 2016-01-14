package funion.app.qparking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import funion.app.qparking.tools.ActivityTools;

public class PersonalCenterActivity extends Activity implements OnClickListener {
    private TextView myIntegral;
    private TextView parkingCardTv;
    SharedPreferences sp;
    Context context;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        context=this;
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        editor=sp.edit();
        init();

    }

    private void init() {
        myIntegral = (TextView) findViewById(R.id.integral_tv);
        parkingCardTv = (TextView) findViewById(R.id.parking_card_tv);
        ((TextView)findViewById(R.id.balance_tv)).setText(sp.getString("balance", null));
        myIntegral.setText(getResources().getString(R.string.integral) + sp.getString("integral", null));
        findViewById(R.id.personalCenter_message_rl).setOnClickListener(this);
        findViewById(R.id.personalCenter_myOrder_rl).setOnClickListener(this);
        findViewById(R.id.personalCenter_myPlate_rl).setOnClickListener(this);
        findViewById(R.id.personalCenter_myCoupon_rl).setOnClickListener(this);
        findViewById(R.id.personalCenter_back_iv).setOnClickListener(this);
        findViewById(R.id.recharge_tv).setOnClickListener(this);
        findViewById(R.id.wash_card_tv).setOnClickListener(this);
        findViewById(R.id.logout_tv).setOnClickListener(this);
        findViewById(R.id.recharge_iv).setOnClickListener(this);
        myIntegral.setOnClickListener(this);
        parkingCardTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personalCenter_back_iv: {
                finish();
            }
            break;
            case R.id.personalCenter_info_iv: {

                Intent intent = new Intent();
                intent.setClass(PersonalCenterActivity.this, MyInfoActivity.class);
                startActivity(intent);
            }
            break;
            //洗车券
            case R.id.wash_card_tv:
                ActivityTools.switchActivity(context,WrashCarVolumeActivity.class,null);
                break;
            case R.id.personalCenter_message_rl: {
                Intent intent = new Intent();
                intent.setClass(PersonalCenterActivity.this, MyOrderActivity.class);
                startActivity(intent);
                QParkingApp.ToastTip(PersonalCenterActivity.this, "我的订单", -100);
            }
            break;
            case R.id.personalCenter_myOrder_rl: {
                //我的车位
                //ps:10 pn:0
                ActivityTools.switchActivity(context,MyReleaseCarPort.class,null);

            }
            break;
//            case R.id.personalCenter_myParking_rl: {
//
////			Intent intent = new Intent();
////			intent.setClass(PersonalCenterActivity.this, MyInfoActivity.class);
////			startActivity(intent);
//                QParkingApp.ToastTip(PersonalCenterActivity.this, "我的车位", -100);
//            }
//            break;
            case R.id.personalCenter_myPlate_rl: {
                ActivityTools.switchActivity(context, CarLicenceManagerActivity.class, null);
            }
            break;
            case R.id.personalCenter_myCoupon_rl: {
                ActivityTools.switchActivity(context,PasswordActivity.class,null);
            }
            break;
            case R.id.recharge_tv:
                ActivityTools.switchActivity(context,RechargeActivity.class,null);
                break;
            case R.id.integral_tv:
                ActivityTools.switchActivity(context, ScoreActivity.class,null);
                break;
            case R.id.parking_card_tv:
                ActivityTools.switchActivity(context,ParkingVolumeActivity.class,null);
                break;
            case R.id.logout_tv:
                sp.edit().clear().commit();
                Toast.makeText(context,getResources().getString(R.string.exit_success),Toast.LENGTH_SHORT).show();
                ActivityTools.switchActivity(context,MainActivity.class,null);
                finish();
                break;

            default:

                break;
        }
    }
}
