package funion.app.qparking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pingplusplus.android.PaymentActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import funion.app.adapter.MyInfoAdapter;
import funion.app.adapter.MyOrderPagerAdapter;
import funion.app.qparking.fragment.IntegralFragment;
import funion.app.qparking.fragment.ParkingFragment;
import funion.app.qparking.fragment.RechargeFragment;
import funion.app.qparking.fragment.WashingFragment;
import funion.app.qparking.popWindow.SelectPayModePop;

/**
 * Created by Administrator on 2016/1/16.
 */
public class MyWalletActivity extends FragmentActivity implements View.OnClickListener{
    private TextView account_balance_tv,integral_tv,parking_vouchers_tv,washing_vouchers_tv,title_tv;
    private ImageView cursor_im,back_im;
    private ViewPager wallet_vp;
    private List<Fragment> listViews;
    private MyInfoAdapter adapter;
    // 动画图片偏移量
    private int offset = 0;
    // 动画图片宽度
    private int bmpW;
    private int current;//偏移量
    private int lastPosition;//viewpager上一次位置
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_wallet);
        context=this;
        initView();
        initViewPager();
        initImageView();
    }


    private void initImageView() {
        DisplayMetrics dm = new DisplayMetrics();
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab_flag).getWidth();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 4 - bmpW) / 4;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor_im.setImageMatrix(matrix);
        current = offset * 4 + bmpW;//偏移量
    }

    private void initViewPager() {
        listViews=new ArrayList<Fragment>();
        listViews.add(new RechargeFragment());
        listViews.add(new IntegralFragment());
        listViews.add(new ParkingFragment());
        listViews.add(new WashingFragment());

        adapter=new MyInfoAdapter(getSupportFragmentManager(),listViews);
        wallet_vp.setAdapter(adapter);
        wallet_vp.setCurrentItem(0);
        lastPosition=0;
        wallet_vp.setOnPageChangeListener(new MyOnPagerChangerListener());
    }

    private void initView() {
        back_im=(ImageView)findViewById(R.id.include_iv_left);
        back_im.setImageResource(R.drawable.top_back_btn);
        back_im.setOnClickListener(this);
        title_tv=(TextView)findViewById(R.id.include_tv_title);
        title_tv.setText(getResources().getString(R.string.my_wallet));
        account_balance_tv=(TextView)findViewById(R.id.account_balance_tv);
        integral_tv=(TextView)findViewById(R.id.integral_tv);
        parking_vouchers_tv=(TextView)findViewById(R.id.parking_vouchers_tv);
        washing_vouchers_tv=(TextView)findViewById(R.id.washing_vouchers_tv);
        wallet_vp=(ViewPager)findViewById(R.id.wallet_vp);
        cursor_im = (ImageView) findViewById(R.id.cursor_im);
        account_balance_tv.setOnClickListener(this);
        integral_tv.setOnClickListener(this);
        parking_vouchers_tv.setOnClickListener(this);
        washing_vouchers_tv.setOnClickListener(this);
    }

    public class MyOnPagerChangerListener implements  ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Animation animation=null;
            switch(position){
                case 0:
                    lastPosition=0;
                    animation=new TranslateAnimation(current,0,0,0);
                    account_balance_tv.setTextColor(getResources().getColor(R.color.red));
                    integral_tv.setTextColor(getResources().getColor(R.color.app_black));
                    parking_vouchers_tv.setTextColor(getResources().getColor(R.color.app_black));
                    washing_vouchers_tv.setTextColor(getResources().getColor(R.color.app_black));
                    break;
                case 1:
                    if(lastPosition==0){
                        animation=new TranslateAnimation(0,current,0,0);
                    }else if(lastPosition==2){
                        animation=new TranslateAnimation(current*2,current,0,0);
                    }
                    lastPosition=1;
                    account_balance_tv.setTextColor(getResources().getColor(R.color.app_black));
                    integral_tv.setTextColor(getResources().getColor(R.color.red));
                    parking_vouchers_tv.setTextColor(getResources().getColor(R.color.app_black));
                    washing_vouchers_tv.setTextColor(getResources().getColor(R.color.app_black));
                    break;
                case 2:
                    if(lastPosition==1){
                        animation=new TranslateAnimation(current,current*2,0,0);
                    }else if(lastPosition==3){
                        animation=new TranslateAnimation(current*3,current*2,0,0);
                    }
                    lastPosition=2;
                    account_balance_tv.setTextColor(getResources().getColor(R.color.app_black));
                    integral_tv.setTextColor(getResources().getColor(R.color.app_black));
                    parking_vouchers_tv.setTextColor(getResources().getColor(R.color.red));
                    washing_vouchers_tv.setTextColor(getResources().getColor(R.color.app_black));
                    break;
                case 3:
                    animation=new TranslateAnimation(current*2,current*3,0,0);
                    lastPosition=3;
                    account_balance_tv.setTextColor(getResources().getColor(R.color.app_black));
                    integral_tv.setTextColor(getResources().getColor(R.color.app_black));
                    parking_vouchers_tv.setTextColor(getResources().getColor(R.color.app_black));
                    washing_vouchers_tv.setTextColor(getResources().getColor(R.color.red));
                    break;
            }
            animation.setFillAfter(true);
            animation.setDuration(300);
            cursor_im.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.account_balance_tv:
                wallet_vp.setCurrentItem(0);
                break;
            case R.id.integral_tv:
                wallet_vp.setCurrentItem(1);
                break;
            case R.id.parking_vouchers_tv:
                wallet_vp.setCurrentItem(2);
                break;
            case R.id.washing_vouchers_tv:
                wallet_vp.setCurrentItem(3);
                break;
            case R.id.include_iv_left:
                finish();
                break;
        }
    }
}
