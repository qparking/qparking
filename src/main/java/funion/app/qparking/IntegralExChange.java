package funion.app.qparking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import funion.app.qparking.tools.OkHttpUtils;

/**
 * Created by 运泽 on 2015/12/28.
 * TODO积分商城,下个版本再上,先不写
 *
 */
public class IntegralExChange extends Activity implements View.OnClickListener{
    private TextView show_integral,crash_ticket,parking_ticket,car_articles,back_tv,title_tv;
    private ImageView show_behind_iv,back_iv,search_iv;
    private ViewPager vp;
    private RelativeLayout exchange_record_re,back_re;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.integral_exchange);
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        getViews();
        initView();
    }

    private void getViews() {
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {

            }
        },null,"goodslist");
    }

    private void initView() {
        show_integral=(TextView)findViewById(R.id.show_integral_tv);
        exchange_record_re=(RelativeLayout)findViewById(R.id.exchange_record_re);
        crash_ticket=(TextView)findViewById(R.id.crash_ticket_tv);
        parking_ticket=(TextView)findViewById(R.id.parking_ticket_tv);
        car_articles=(TextView)findViewById(R.id.car_articles_tv);
        show_behind_iv=(ImageView)findViewById(R.id.show_behind_iv);
        vp=(ViewPager)findViewById(R.id.commodity_list);
        back_iv=(ImageView)findViewById(R.id.include_iv_left);
        back_tv=(TextView)findViewById(R.id.include_tv_cancel);
        title_tv=(TextView)findViewById(R.id.include_tv_title);
        search_iv=(ImageView)findViewById(R.id.include_iv_right);
        back_re=(RelativeLayout)findViewById(R.id.include_back_left_ll);
        show_integral.setText(sp.getString("integral",null));
        title_tv.setText(getResources().getString(R.string.integral_store));
        back_iv.setBackgroundResource(R.drawable.top_back_btn);
        search_iv.setBackgroundResource(R.drawable.search_p);
        crash_ticket.setOnClickListener(this);
        parking_ticket.setOnClickListener(this);
        car_articles.setOnClickListener(this);
        exchange_record_re.setOnClickListener(this);
        back_re.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.include_back_left_ll:
                finish();
                break;
            case R.id.include_iv_right:
                break;
            case R.id.exchange_record_re:
                break;
            case R.id.crash_ticket_tv:
                break;
            case R.id.parking_ticket_tv:
                break;
            case R.id.car_articles_tv:
                break;
        }

    }
}
