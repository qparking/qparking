package funion.app.qparking;
/**
 * 我的订单
 * Created by yunze on 2015/12/14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.ConsumptionAdapter;
import funion.app.adapter.MyOrderPagerAdapter;
import funion.app.adapter.OrderAdapter;
import funion.app.adapter.RechargeAdapter;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.vo.MyOrderBooking;
import funion.app.qparking.vo.MyOrderRecharge;
import funion.app.qparking.vo.MyOrderConsumption;

public class MyOrderActivity extends Activity implements View.OnClickListener {
    private Button orderBtn, rechargeBtn, consumptionBtn;//预约，消费,充值
    private ViewPager orderVp;
    private ImageView cursorIv;
    private List<View> listViews;
    private MyOrderPagerAdapter adapter;
    private Context context = MyOrderActivity.this;
    // 动画图片偏移量
    private int offset = 0;
    // 动画图片宽度
    private int bmpW;
    //预约listView
    private PullToRefreshListView orderListView;
    //预约Adapter
    private OrderAdapter orderAdapter;
    //消费ListView
    private PullToRefreshListView consumptionListView;
    //消费Adapter
    private ConsumptionAdapter consumptionAdapter = null;
    //充值ListView
    private PullToRefreshListView rechargeListView;
    //充值Adapter
    private RechargeAdapter rechargeAdapter;
    private Gson gson;
    //预约List
    private List<MyOrderBooking> orderParkingInfos = new ArrayList<MyOrderBooking>();
    //充值list
    private List<MyOrderRecharge> myOrderRecharges = new ArrayList<MyOrderRecharge>();
    // 消费list
    private List<MyOrderConsumption> myOrderConsumptions = new ArrayList<MyOrderConsumption>();
    //当前页-我的订单-预约
    private int myOrderPage = 0;
    //每页总数-我的订单
    private int num = 10;
    //当前页-我的订单-消费
    private int consumptionPage = 0;
    //当前页-我的订单-消费
    private int rechargePage = 0;
    //// 上次刷新时间-我的订单-消费
    private long consumptionLastUpt;
    // 上次刷新时间-我的订单
    private long lastUpt;
    // 上次刷新时间-我的订单
    private long rechargeLastUpt;
    private int current;//偏移量
    private int lastPosition;//viewpager上一次位置
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_order);
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        myOrderRequest(myOrderPage, num);
        myOrderConsumption(consumptionPage, num);
        myOrderRecharge(rechargePage, num);
        initView();
        initViewPager();
        initImageView();

    }

    /**
     * 我的订单-预约
     *
     * @param currentPage 当前页
     * @param num         每页数量
     */
    private void myOrderRequest(int currentPage, int num) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("sign", sp.getString("userId", ""));
        param.put("ps", num + "");
        param.put("pn", currentPage + "");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
                Log.e("Exception", e.toString());
            }

            @Override
            public void onResponse(String result) {
                Log.e("result", result);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("count").equals("0")) {
                        return;
                    }
                    String temp = object.getJSONArray("data").toString();
                    gson = new Gson();
                    Type listType = new TypeToken<ArrayList<MyOrderBooking>>() {
                    }.getType();
                    ArrayList<MyOrderBooking> tempList = gson.fromJson(temp, listType);
                    orderParkingInfos.addAll(tempList);
                    orderAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    orderListView.onRefreshComplete();
                }

            }
        }, param, "reserveLog");
    }

    /**
     * 我的订单-消费
     *
     * @param currentPage
     * @param num
     */
    private void myOrderConsumption(int currentPage, int num) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("sign", sp.getString("userId", ""));
        param.put("ps", num + "");
        param.put("pn", currentPage + "");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                Log.e("result", result);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("count").equals("0")) {
                        return;
                    }
                    String temp = object.getJSONArray("data").toString();
                    gson = new Gson();
                    Type listType = new TypeToken<ArrayList<MyOrderConsumption>>() {
                    }.getType();
                    ArrayList<MyOrderConsumption> tempList = gson.fromJson(temp, listType);
                    myOrderConsumptions.addAll(tempList);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (consumptionAdapter != null) {
                        consumptionAdapter.notifyDataSetChanged();
                        consumptionListView.onRefreshComplete();
                    }
                }

            }
        }, param, "parkinglog");
    }

    /**
     * 我的订单-充值
     *
     * @param currentPage 当前页
     * @param num         每页数量
     */
    private void myOrderRecharge(int currentPage, int num) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("sign", sp.getString("userId", ""));
        param.put("ps", num + "");
        param.put("pn", currentPage + "");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                Log.e("result", result);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("count").equals("0")) {
                        return;
                    }
                    String temp = object.getJSONArray("data").toString();
                    gson = new Gson();
                    Type listType = new TypeToken<ArrayList<MyOrderRecharge>>() {
                    }.getType();
                    ArrayList<MyOrderRecharge> tempList = gson.fromJson(temp, listType);
                    myOrderRecharges.addAll(tempList);

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (rechargeAdapter != null) {
                        rechargeAdapter.notifyDataSetChanged();
                        rechargeListView.onRefreshComplete();
                    }
                }

            }
        }, param, "rechargelog");
    }

    private void initImageView() {
        cursorIv = (ImageView) findViewById(R.id.cursor);
        DisplayMetrics dm = new DisplayMetrics();
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab_flag).getWidth();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (screenW / 3 - bmpW) / 3;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursorIv.setImageMatrix(matrix);
        current = offset * 3 + bmpW;//偏移量
    }


    private void initViewPager() {
        orderVp = (ViewPager) findViewById(R.id.order_vp);
        listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.pager_parking_order, null));
        listViews.add(mInflater.inflate(R.layout.pager_parking_consumption, null));
        listViews.add(mInflater.inflate(R.layout.pager_parking_recharge, null));
        adapter = new MyOrderPagerAdapter(context, listViews);
        orderVp.setAdapter(adapter);
        orderVp.setCurrentItem(0);
        lastPosition = 0;
        orderListView = (PullToRefreshListView) listViews.get(0).findViewById(R.id.order_lv);
        consumptionListView = (PullToRefreshListView) listViews.get(1).findViewById(R.id.consumption_lv);
        rechargeListView = (PullToRefreshListView) listViews.get(2).findViewById(R.id.recharge_lv);
        orderVp.setOnPageChangeListener(new MyOnPagerChangerListener());
        setListViewAdapter();
    }

    /**
     * 初始化listView 适配器
     */
    private void setListViewAdapter() {
        orderAdapter = new OrderAdapter(context, orderParkingInfos);
        setPullToRefreshTitle(0);
        orderListView.setAdapter(orderAdapter);
        orderListView.setOnRefreshListener(new MyOrderRefreshListener());
        consumptionAdapter = new ConsumptionAdapter(context, myOrderConsumptions);
        rechargeAdapter = new RechargeAdapter(context, myOrderRecharges);
    }

    private void setPullToRefreshTitle(int position) {
        ILoadingLayout bottomLayout;
        ILoadingLayout frontLayout;
        switch (position) {
            case 0:
                bottomLayout = orderListView
                        .getLoadingLayoutProxy(false, true);
                bottomLayout.setPullLabel("上拉加载下10条数据");
                frontLayout = orderListView
                        .getLoadingLayoutProxy(true, false);
                frontLayout.setPullLabel("下拉刷新");
                break;
            case 1:
                bottomLayout = consumptionListView
                        .getLoadingLayoutProxy(false, true);
                bottomLayout.setPullLabel("上拉加载下10条数据");
                frontLayout = consumptionListView
                        .getLoadingLayoutProxy(true, false);
                frontLayout.setPullLabel("下拉刷新");
                break;
            case 2:
                bottomLayout = rechargeListView
                        .getLoadingLayoutProxy(false, true);
                bottomLayout.setPullLabel("上拉加载下10条数据");
                frontLayout = rechargeListView
                        .getLoadingLayoutProxy(true, false);
                frontLayout.setPullLabel("下拉刷新");
                break;
        }
    }

    private void initView() {
        orderBtn = (Button) findViewById(R.id.order_btn);
        consumptionBtn = (Button) findViewById(R.id.consumption_btn);
        rechargeBtn = (Button) findViewById(R.id.recharge_btn);
        ((TextView) findViewById(R.id.include_tv_title)).setText("我的订单");
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.top_back_btn);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        orderBtn.setOnClickListener(this);
        consumptionBtn.setOnClickListener(this);
        rechargeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.order_btn:
                orderVp.setCurrentItem(0);
                break;
            case R.id.consumption_btn:
                orderVp.setCurrentItem(1);
                break;
            case R.id.recharge_btn:
                orderVp.setCurrentItem(2);
                break;

        }
    }

    public class MyOnPagerChangerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageSelected(int position) {
//            int current = offset * 3 + bmpW;//偏移量
            Animation animation = null;
            switch (position) {
                case 0:
                    lastPosition = 0;
                    animation = new TranslateAnimation(current, 0, 0, 0);
                    orderBtn.setTextColor(getResources().getColor(R.color.app_green));
                    consumptionBtn.setTextColor(getResources().getColor(R.color.app_black));
                    rechargeBtn.setTextColor(getResources().getColor(R.color.app_black));
                    orderListView.setAdapter(orderAdapter);
                    setPullToRefreshTitle(0);
                    orderListView.setOnRefreshListener(new MyOrderRefreshListener());
                    break;
                case 1:
//                    int temp = current;
//                    current = offset * 3 + bmpW;
                    if (lastPosition == 0) {
                        animation = new TranslateAnimation(0, current, 0, 0);
                    } else if (lastPosition == 2) {
                        animation = new TranslateAnimation(current * 2, current, 0, 0);

                    }
                    lastPosition = 1;
                    orderBtn.setTextColor(getResources().getColor(R.color.app_black));
                    consumptionBtn.setTextColor(getResources().getColor(R.color.app_green));
                    rechargeBtn.setTextColor(getResources().getColor(R.color.app_black));
                    consumptionListView.setAdapter(consumptionAdapter);
                    setPullToRefreshTitle(1);
                    consumptionListView.setOnRefreshListener(new MyOrderConsumptionRefreshListener());
                    break;
                case 2:
                    animation = new TranslateAnimation(current, current * 2, 0, 0);
                    lastPosition = 2;
                    orderBtn.setTextColor(getResources().getColor(R.color.app_black));
                    consumptionBtn.setTextColor(getResources().getColor(R.color.app_black));
                    rechargeBtn.setTextColor(getResources().getColor(R.color.app_green));
                    rechargeListView.setAdapter(rechargeAdapter);
                    setPullToRefreshTitle(2);
                    rechargeListView.setOnRefreshListener(new MyOrderRechargeRefreshListener());
                    break;
            }
            // True:图片停在动画结束位置
            animation.setFillAfter(true);
            animation.setDuration(300);
            cursorIv.startAnimation(animation);
        }
    }

    /**
     * 预约下拉监听
     */
    public class MyOrderRefreshListener implements PullToRefreshBase.OnRefreshListener2<ListView> {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            ILoadingLayout frontLayout = orderListView
                    .getLoadingLayoutProxy(true, false);
            lastUpt = System.currentTimeMillis();
            String label = "上一次刷新时间："
                    + DateUtils.formatDateTime(context
                            .getApplicationContext(), lastUpt,
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            frontLayout.setLastUpdatedLabel(label);
            myOrderPage = 1;
            myOrderRequest(myOrderPage, num);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            myOrderPage++;
            myOrderRequest(myOrderPage, num);
        }
    }

    /**
     * 消费下拉监听
     */
    public class MyOrderConsumptionRefreshListener implements PullToRefreshBase.OnRefreshListener2<ListView> {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            ILoadingLayout frontLayout = rechargeListView
                    .getLoadingLayoutProxy(true, false);
            consumptionLastUpt = System.currentTimeMillis();
            String label = "上一次刷新时间："
                    + DateUtils.formatDateTime(context
                            .getApplicationContext(), consumptionLastUpt,
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            frontLayout.setLastUpdatedLabel(label);
            consumptionPage = 1;
            myOrderConsumption(consumptionPage, num);

        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            consumptionPage++;
            myOrderConsumption(consumptionPage, num);
        }
    }

    /**
     * 充值下拉监听
     */
    public class MyOrderRechargeRefreshListener implements PullToRefreshBase.OnRefreshListener2<ListView> {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            ILoadingLayout frontLayout = rechargeListView
                    .getLoadingLayoutProxy(true, false);
            rechargeLastUpt = System.currentTimeMillis();
            String label = "上一次刷新时间："
                    + DateUtils.formatDateTime(context
                            .getApplicationContext(), rechargeLastUpt,
                    DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            frontLayout.setLastUpdatedLabel(label);
            rechargePage = 1;
            myOrderRecharge(rechargePage, num);

        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            rechargePage++;
            myOrderRecharge(rechargePage, num);
        }
    }
}
