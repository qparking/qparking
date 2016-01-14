package funion.app.qparking;
/**
 * 改-去停车页面
 * Created by yunze on 2016/1/8.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;

import java.util.Calendar;
import java.util.Date;

import funion.app.qparking.view.WheelView;

public class NavigationAct extends Activity implements Animation.AnimationListener, View.OnClickListener, OnGetRoutePlanResultListener {
    private MapView mapView;
    private WheelView m_wvDate;
    private WheelView m_wvHour;
    private WheelView m_wvMinite;
    private View m_viewTimerSetBk = null;
    private LinearLayout m_llTimerSetting;
    private Animation m_animShowTimerSetting;
    private Animation m_animHideTimerSetting;
    private Button m_btRoadState = null;
    private View m_viewRemind = null;
    private View m_viewVoice = null;
    private ImageView m_ivRemind = null;
    private ImageView m_ivVoice = null;
    private TextView m_tvRemind = null;
    private TextView m_tvVoice = null;

    // 搜索模块，也可去掉地图模块独立使用
    private RoutePlanSearch m_searchRoute = null;
    private Context context = NavigationAct.this;
    private double lat;
    private double lon;
    private QParkingApp appQParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);
        appQParking = (QParkingApp) getApplicationContext();
        initDate();
        initView();
        initTime();
    }

    private void initDate() {
        // 初始化搜索模块，注册事件监听
        m_searchRoute = RoutePlanSearch.newInstance();
        m_searchRoute.setOnGetRoutePlanResultListener(this);
        // 根据经纬度搜索路线
        PlanNode nodeStart = PlanNode.withLocation(appQParking.m_llMe);
        PlanNode nodeTarget;
        if (getIntent().getDoubleExtra("lat", 0) == 0 && getIntent().getDoubleExtra("lat", 0) == 0) {
            nodeTarget = PlanNode.withLocation(appQParking.m_itemParking.m_llParking);
        } else {
            lat = getIntent().getDoubleExtra("lat", 0);
            lon = getIntent().getDoubleExtra("lon", 0);
            LatLng latLng = new LatLng(lat, lon);
            nodeTarget = PlanNode.withLocation(latLng);
        }
        if (getIntent().getBooleanExtra("PickCar", false))
            m_searchRoute.walkingSearch((new WalkingRoutePlanOption()).from(nodeStart).to(nodeTarget));
        else
            m_searchRoute.drivingSearch((new DrivingRoutePlanOption()).from(nodeStart).to(nodeTarget));
        // 获取控件
        m_viewVoice = findViewById(R.id.viewVoiceNav);
        m_ivRemind = (ImageView) findViewById(R.id.ivReminder);
        m_ivVoice = (ImageView) findViewById(R.id.ivVoiceNav);
        m_tvRemind = (TextView) findViewById(R.id.tvReminder);
        m_tvVoice = (TextView) findViewById(R.id.tvVoiceNav);
    }

    private void initTime() {
        // 设置日期选择器
        String[] strWeekDay = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        String[] strDates = new String[21];
        Calendar calendarTemp = Calendar.getInstance();
        Date dateTemp = null;
        for (int i = 0; i < 10; i++) {
            int iDate = calendarTemp.get(Calendar.DATE);
            calendarTemp.set(Calendar.DATE, iDate - 1);
            dateTemp = calendarTemp.getTime();
            strDates[9 - i] = String.format("%d月%d日 %s", dateTemp.getMonth() + 1, dateTemp.getDate(),
                    strWeekDay[dateTemp.getDay()]);
        }
        strDates[10] = "今天";
        calendarTemp = Calendar.getInstance();
        for (int i = 0; i < 10; i++) {
            int iDate = calendarTemp.get(Calendar.DATE);
            calendarTemp.set(Calendar.DATE, iDate + 1);
            dateTemp = calendarTemp.getTime();
            strDates[11 + i] = String.format("%d月%d日 %s", dateTemp.getMonth() + 1, dateTemp.getDate(),
                    strWeekDay[dateTemp.getDay()]);
        }
        String[] strHours = new String[24];
        for (int i = 0; i < strHours.length; i++)
            strHours[i] = String.format("%02d", i);
        String[] strMinite = new String[60];
        for (int i = 0; i < strMinite.length; i++)
            strMinite[i] = String.format("%02d", i);

        // 设置日期
        StringWheelAdapter adapterStringDate = new StringWheelAdapter();

        adapterStringDate.SetItem(strDates);
        m_wvDate.setAdapter(adapterStringDate);
        m_wvDate.setCurrentItem(10);
        m_wvDate.setCyclic(true);
        // 设置小时
        StringWheelAdapter adapterStringHour = new StringWheelAdapter();

        adapterStringHour.SetItem(strHours);
        m_wvHour.setAdapter(adapterStringHour);
        m_wvHour.setCurrentItem(dateTemp.getHours());
        m_wvHour.setCyclic(true);
        // 设置分钟
        StringWheelAdapter adapterStringMinite = new StringWheelAdapter();

        adapterStringMinite.SetItem(strMinite);
        m_wvMinite.setAdapter(adapterStringMinite);
        m_wvMinite.setCurrentItem(dateTemp.getMinutes());
        m_wvMinite.setCyclic(true);
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.navBaiduMap);
        m_wvDate = (WheelView) findViewById(R.id.wvDate);
        m_wvHour = (WheelView) findViewById(R.id.wvHour);
        m_wvMinite = (WheelView) findViewById(R.id.wvMinite);
        // 时间设定
        m_viewTimerSetBk = findViewById(R.id.viewTimerBack);
        m_llTimerSetting = (LinearLayout) findViewById(R.id.llTimerSetting);
        // 设置动画
        m_animShowTimerSetting = AnimationUtils.loadAnimation(this, R.anim.timer_setting_up);
        m_animHideTimerSetting = AnimationUtils.loadAnimation(this, R.anim.timer_setting_down);
        m_animShowTimerSetting.setAnimationListener(this);
        m_animHideTimerSetting.setAnimationListener(this);

        // 路况按钮
        m_btRoadState = (Button) findViewById(R.id.btNavRoad);
        m_btRoadState.setOnClickListener(this);

        // 底部按钮区消息
        findViewById(R.id.viewLine).setOnClickListener(this); // 路线
        findViewById(R.id.viewVoiceNav).setOnClickListener(this); // 语音导航

        // 取车提醒
        m_viewRemind = findViewById(R.id.viewRemind);
        m_viewRemind.setOnClickListener(this);

        findViewById(R.id.btNavBack).setOnClickListener(this);
        findViewById(R.id.btSetTimer).setOnClickListener(this);
        mapView.showZoomControls(false);
        mapView.showScaleControl(false);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation.equals(m_animShowTimerSetting)) {
            RelativeLayout.LayoutParams rllpTimerSetting = (RelativeLayout.LayoutParams) m_llTimerSetting
                    .getLayoutParams();

            m_llTimerSetting.clearAnimation();

            rllpTimerSetting.bottomMargin = 0;
            m_llTimerSetting.setLayoutParams(rllpTimerSetting);
        } else if (animation.equals(m_animHideTimerSetting)) {
            RelativeLayout.LayoutParams rllpTimerSetting = (RelativeLayout.LayoutParams) m_llTimerSetting
                    .getLayoutParams();

            m_llTimerSetting.clearAnimation();

            rllpTimerSetting.bottomMargin = (int) (-m_llTimerSetting.getHeight() * 0.76);
            m_llTimerSetting.setLayoutParams(rllpTimerSetting);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btNavBack:
                finish();
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                break;
            case R.id.viewLine:
                intent = new Intent();
                boolean bIsPickCar = getIntent().getBooleanExtra("PickCar", false);
                intent.putExtra("PickCar", bIsPickCar);
                intent.setClass(context, RouteActivity.class);
                startActivity(intent);
                break;
            case R.id.viewVoiceNav: {
                BNaviPoint ptStart = new BNaviPoint(appQParking.m_llMe.longitude, appQParking.m_llMe.latitude, "我的位置",
                        BNaviPoint.CoordinateType.BD09_MC);
                BNaviPoint ptEnd = new BNaviPoint(lon,
                        lat, getIntent().getStringExtra("name"),
                        BNaviPoint.CoordinateType.BD09_MC);

                BaiduNaviManager.getInstance().launchNavigator(this, ptStart, ptEnd,
                        RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
                        true, // 真实导航
                        BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
                        new BaiduNaviManager.OnStartNavigationListener() { // 跳转监听

                            @Override
                            public void onJumpToNavigator(Bundle configParams) {
                                Intent intent = new Intent(context, BNavigatorActivity.class);
                                intent.putExtras(configParams);
                                startActivityForResult(intent, 1);
                            }

                            @Override
                            public void onJumpToDownloader() {
                            }
                        });
            }
            break;
            case R.id.viewRemind: // 取车提醒
            {
                if (m_viewTimerSetBk.getVisibility() == View.VISIBLE)
                    break;

                ShowPickCarReminder();
            }
            break;
            case R.id.btSetTimer:
            case R.id.viewTimerBack: // 取消取车提醒
            {
                m_viewTimerSetBk.setVisibility(View.INVISIBLE);
                m_llTimerSetting.startAnimation(m_animHideTimerSetting);

                m_viewRemind.setBackgroundColor(0x00000000);
                m_viewVoice.setBackgroundColor(0xFF00c1a0);
                m_ivRemind.setImageResource(R.drawable.reminder_icon);
                m_ivVoice.setImageResource(R.drawable.voice_navigation_icon_white);
                m_tvRemind.setTextColor(0xFF808080);
                m_tvVoice.setTextColor(0xFFFFFFFF);
                if (view.getId() == R.id.btSetTimer) {
                    Calendar calendarTemp = Calendar.getInstance();
                    int iDate = calendarTemp.get(Calendar.DATE);
                    int iCurSel = m_wvDate.getCurrentItem();

                    calendarTemp.set(Calendar.DATE, iDate + iCurSel - 10);
                    Date dateSet = calendarTemp.getTime();

                    dateSet.setHours(m_wvHour.getCurrentItem());
                    dateSet.setMinutes(m_wvMinite.getCurrentItem());
                    dateSet.setSeconds(0);
                    if (dateSet.before(new Date())) {
                        QParkingApp.ToastTip(this, "您设置的时间已经逝去了", Toast.LENGTH_SHORT);
                        return;
                    }

                    // 进行闹铃注册
                    intent = new Intent(context, ReminderReceiver.class);
                    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

                    // 设置执行时间
                    Calendar calendar = Calendar.getInstance();

                    calendar.setTime(dateSet);

                    AlarmManager managerAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
                    managerAlarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

                    // 保存停车信息
                    QParkingApp appQParking = (QParkingApp) getApplicationContext();

                    SharedPreferences spParkInfo = getSharedPreferences("ParkInfo", Activity.MODE_PRIVATE);

                    SharedPreferences.Editor editorParkInfo = spParkInfo.edit();

                    editorParkInfo.clear();
                    // 用putString的方法保存数据
                    editorParkInfo.putInt("Month", dateSet.getMonth());
                    editorParkInfo.putInt("Date", dateSet.getDate());
                    editorParkInfo.putInt("Hour", dateSet.getHours());
                    editorParkInfo.putInt("Minutes", dateSet.getMinutes());
                    if (getIntent().getStringExtra("pid").equals("")) {
                        editorParkInfo.putString("Pid", appQParking.m_itemParking.m_strPid);
                        editorParkInfo.putString("name", appQParking.m_itemParking.m_strName);
                        editorParkInfo.putString("address", appQParking.m_itemParking.m_strAddress);
                        editorParkInfo.putString("ShareName", appQParking.m_itemParking.m_strShareName);
                        editorParkInfo.putFloat("latitude", (float) appQParking.m_itemParking.m_llParking.latitude);
                        editorParkInfo.putFloat("longitude", (float) appQParking.m_itemParking.m_llParking.longitude);
                        editorParkInfo.putInt("FreeNum", appQParking.m_itemParking.m_iFreeNum);
                        editorParkInfo.putInt("ChargeNum", appQParking.m_itemParking.m_iChargeNum);
                        editorParkInfo.putInt("PraiseNum", appQParking.m_itemParking.m_iPraiseNum);
                        editorParkInfo.putInt("DespiseNum", appQParking.m_itemParking.m_iDespiseNum);
                        editorParkInfo.putInt("LocationType", appQParking.m_itemParking.m_iLocationType);
                        editorParkInfo.putInt("Distance", appQParking.m_itemParking.m_iDistance);
                    } else {
                        editorParkInfo.putString("Pid", getIntent().getStringExtra("pid"));
                        editorParkInfo.putString("name", getIntent().getStringExtra("name"));
                        editorParkInfo.putString("address", getIntent().getStringExtra("address"));
                        editorParkInfo.putString("ShareName", getIntent().getStringExtra("shareName"));
                        editorParkInfo.putFloat("latitude", (float) lat);
                        editorParkInfo.putFloat("longitude", (float) lon);
                        editorParkInfo.putInt("FreeNum", getIntent().getIntExtra("freeNum", -1));
                        editorParkInfo.putInt("ChargeNum", getIntent().getIntExtra("chargeNum", -1));
                        editorParkInfo.putInt("PraiseNum", getIntent().getIntExtra("praiseNum", -1));
                        editorParkInfo.putInt("DespiseNum", getIntent().getIntExtra("despiseNum", -1));
                        editorParkInfo.putInt("LocationType", getIntent().getIntExtra("locationType", -1));
                        editorParkInfo.putInt("Distance", getIntent().getIntExtra("distance", -1));

                    }
                    editorParkInfo.commit();
                    QParkingApp.ToastTip(this, "设置成功", -100);
                }
            }
            break;
        }
    }

    public void ShowPickCarReminder() {
        m_viewRemind.setBackgroundColor(0xFF00c1a0);
        m_viewVoice.setBackgroundColor(0x00000000);
        m_ivRemind.setImageResource(R.drawable.reminder_icon_white);
        m_ivVoice.setImageResource(R.drawable.voice_navigation_icon);
        m_tvRemind.setTextColor(0xFFFFFFFF);
        m_tvVoice.setTextColor(0xFF808080);

        m_viewTimerSetBk.setVisibility(View.VISIBLE);
        m_llTimerSetting.startAnimation(m_animShowTimerSetting);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            QParkingApp.ToastTip(this, "抱歉，未找到结果！", -100);
            finish();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            QParkingApp.ToastTip(this, "抱歉，未找到结果！", -100);
            finish();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            WalkingRouteOverlay overlay = new QWalkingRouteOverlay(mapView.getMap());

            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

            QParkingApp appQParking = (QParkingApp) getApplicationContext();
            LatLng llCenter;
            int iDistance;
            if (lat == 0 && lon == 0) {
                llCenter = new LatLng(
                        (appQParking.m_llMe.latitude + appQParking.m_itemParking.m_llParking.latitude) / 2,
                        (appQParking.m_llMe.longitude + appQParking.m_itemParking.m_llParking.longitude) / 2);
                iDistance = (int) DistanceUtil.getDistance(appQParking.m_llMe, appQParking.m_itemParking.m_llParking);
            } else {
                llCenter = new LatLng(
                        (appQParking.m_llMe.latitude + lat) / 2,
                        (appQParking.m_llMe.longitude + lon) / 2);
                LatLng latLng = new LatLng(lat, lon);
                iDistance = (int) DistanceUtil.getDistance(appQParking.m_llMe, latLng);
            }
            MapStatusUpdate updateStatus = MapStatusUpdateFactory.newLatLngZoom(llCenter,
                    QParkingApp.GetZoomLevel(iDistance));
            mapView.getMap().animateMapStatus(updateStatus);
            appQParking.m_routeLine = result.getRouteLines().get(0);
        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            QParkingApp.ToastTip(this, "抱歉，未找到结果！", -100);
            finish();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            QParkingApp.ToastTip(this, "抱歉，未找到结果！", -100);
            finish();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            DrivingRouteOverlay overlay = new QDrivingRouteOverlay(mapView.getMap());

            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

            LatLng llCenter;
            int iDistance;
            if (lat == 0 && lon == 0) {
                llCenter = new LatLng(
                        (appQParking.m_llMe.latitude + appQParking.m_itemParking.m_llParking.latitude) / 2,
                        (appQParking.m_llMe.longitude + appQParking.m_itemParking.m_llParking.longitude) / 2);
                iDistance = (int) DistanceUtil.getDistance(appQParking.m_llMe, appQParking.m_itemParking.m_llParking);
            } else {
                llCenter = new LatLng(
                        (appQParking.m_llMe.latitude + lat) / 2,
                        (appQParking.m_llMe.longitude + lon) / 2);
                LatLng latLng = new LatLng(lat, lon);
                iDistance = (int) DistanceUtil.getDistance(appQParking.m_llMe, latLng);
            }
            MapStatusUpdate updateStatus = MapStatusUpdateFactory.newLatLngZoom(llCenter,
                    QParkingApp.GetZoomLevel(iDistance));
            mapView.getMap().animateMapStatus(updateStatus);

            appQParking.m_routeLine = result.getRouteLines().get(0);

        }
    }

    private class QWalkingRouteOverlay extends WalkingRouteOverlay {

        public QWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.route_start_icon);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.route_end_icon);
        }
    }


    private class QDrivingRouteOverlay extends DrivingRouteOverlay {

        public QDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.route_start_icon);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.route_end_icon);
        }
    }

    public class StringWheelAdapter implements WheelView.WheelAdapter {
        String[] m_strItem;

        void SetItem(String[] strItem) {
            m_strItem = strItem;
        }

        public String getItem(int index) {
            return m_strItem[index];
        }

        // 获取滚动项最大长度
        public int getMaximumLength() {
            int iLength = 0;
            for (int i = 0; i < m_strItem.length; i++) {
                if (iLength < m_strItem[i].length())
                    iLength = m_strItem[i].length();
            }

            return iLength;
        }

        @Override
        public int getItemsCount() {
            return m_strItem.length;
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (m_viewTimerSetBk.getVisibility() == View.VISIBLE) {
                m_viewTimerSetBk.setVisibility(View.INVISIBLE);
                m_llTimerSetting.startAnimation(m_animHideTimerSetting);

                m_viewRemind.setBackgroundColor(0x00000000);
                m_viewVoice.setBackgroundColor(0xFF00c1a0);
                m_ivRemind.setImageResource(R.drawable.reminder_icon);
                m_ivVoice.setImageResource(R.drawable.voice_navigation_icon_white);
                m_tvRemind.setTextColor(0xFF808080);
                m_tvVoice.setTextColor(0xFFFFFFFF);

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0)
            return;

        switch (requestCode) {
            case 1: {
                AlertDialog.Builder adMakeCall = new AlertDialog.Builder(this);
                adMakeCall.setMessage("导航结束，可以添加取车提醒时间");
                adMakeCall.setTitle("温馨提示");
                adMakeCall.setPositiveButton("添加提醒", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ShowPickCarReminder();
                    }
                });
                adMakeCall.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adMakeCall.show();

            }
            break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
