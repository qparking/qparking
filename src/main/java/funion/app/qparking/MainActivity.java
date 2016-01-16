package funion.app.qparking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.umeng.analytics.MobclickAgent;

import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.JsonParser;
import funion.app.qparking.view.MyscrollView;
import funion.app.qparking.view.MyscrollView.MyScrollListener;
import funion.app.qparking.vo.LocationPlace;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements OnClickListener,
        OnEditorActionListener, OnGetGeoCoderResultListener,
        OnGetPoiSearchResultListener, OnGetRoutePlanResultListener,
        MKOfflineMapListener {
    // 搜索关键字
    final String POI_SEARCH_KEYWORD = "停车场";
    // 设置显示数量，默认10
    final int POI_SEARCH_PAGE_CAPACITY = 10;
    // 搜索半径，单位：米
    final int POI_SEARCH_RADIUS = 5000;
    final int DISTANCE_PARKING = 30;
    // 菜单动画延迟
    final int MENU_ANIM_INTERVAL = 20;
    // 定位点动画延迟
    final int LOC_ANIM_INTERVAL = 4000;
    final int MENU_ANIM_LEN = 50;
    final int SEARCH_RET = 0;
    // 自动关闭下载提示时间
    final int CLOSE_OFFLINEMAP_HINT_INTERVAL = 10000;
    Context context;

    private MapView m_viewBaiduMap;
    private Button m_btRoadState = null;
    private EditText m_etAddress = null;
    private View m_viewLocation = null;
    private Boolean m_bIsRoadStateOn = false;
    private int m_iTypeNameID;
    private int m_iMainMenuLen;
    private boolean m_bIsMainMenu;
    private RelativeLayout m_rlMainUI;
    private RelativeLayout m_rlDownloadOfflineMap;
    private TextView downloadOfflineMap_tv1;
    private TextView downloadOfflineMap_tv2;
    private LinearLayout m_llMenuBar, m_llMenu;
    private RelativeLayout login_re_;
    private TextView phonenum_tv, balance;

    private Handler m_hNotify = null;

    private LocationClient m_clientLocation;
    private PoiSearch m_poiSearch = null;
    private GeoCoder m_coderGeo = null;
    private AddressComponent m_addressCur = null;
    private ProgressDialog m_dlgProgress = null;

    // 定位次数标记，逻辑上小于3时将重新定位
    private int m_iLocationCount;
    // 最近停车场编号
    private int m_iNearParkIndex;
    // 最近按下系统返回时间
    private long m_lPressBackKeyTime;
    private String m_strSearchName;
    private String m_strSearchAddress;
    // 搜索目标经纬度坐标
    private LatLng m_llSearchPos;
    // 搜索模块，也可去掉地图模块独立使用
    private RoutePlanSearch m_searchRoute = null;
    // 动画处理handler
    private Handler m_hAnimate = new Handler();
    private Runnable m_raShowMenu = null;
    private Runnable m_raHideMenu = null;
    private Runnable m_raLocPtZoomIn = null;
    private Runnable m_raLocPtZoomOut = null;
    private Runnable m_closeOffline = null;

    // 离线地图服务
    private MKOfflineMap m_mapOffline = null;
    // 离线城市列表
    private ArrayList<MKOLSearchRecord> m_arrayMap = null;

    // 图片资源
    private BitmapDescriptor m_bmpParkFree;
    private BitmapDescriptor m_bmpParCharge;
    private BitmapDescriptor m_bmpSearchPos;
    private BitmapDescriptor m_bmpMePoint;

    // 语音识别
    private SpeechRecognizer speechRecognizer;
    // 语音听写UI
    private RecognizerDialog dlgRecognizer;
    // 自定义滚动控件
    private MyscrollView m_scrollView;
    // 滚动控件初始位置
    private int position = 0;
    QParkingApp appQParking;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    LocationPlace locationPlace;

    private NaviEngineInitListener m_listenerNaviEngineInit = new NaviEngineInitListener() {
        public void engineInitSuccess() {

        }

        public void engineInitStart() {

        }

        public void engineInitFail() {

        }
    };
    private LBSAuthManagerListener m_listenerLBSAuthManager = new LBSAuthManagerListener() {
        @Override
        public void onAuthResult(int status, String msg) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.main_activity);
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        editor = sp.edit();
        context = this;
        initSDK();
        initView();
        initDownloadMap();
        myLocation();
        setListener();
        initBaiduLocation();
        initSearch();
        initDate();
        setHandler();
        getNetWord();
        remindDialog();
        appQParking = (QParkingApp) getApplicationContext();
    }

    /**
     * 取车对话框
     */
    private void remindDialog() {
        // 取车提醒
        if (getIntent().getBooleanExtra("PickCar", false)) {
            AlertDialog.Builder adMakeCall = new Builder(this);
            adMakeCall.setMessage("您设定的取车时间到了");
            adMakeCall.setTitle("温馨提示");
            adMakeCall.setPositiveButton("查看详情",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intentNewActivity = new Intent();
                            intentNewActivity.setClass(MainActivity.this,
                                    ParkInfoActivity.class);
                            startActivity(intentNewActivity);
                        }
                    });
            adMakeCall.setNegativeButton("取消",
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            adMakeCall.show();
        }

    }

    /**
     * 获取应用信息线程
     */
    private void getNetWord() {
        // 获取应用信息
        m_dlgProgress = ProgressDialog.show(MainActivity.this, null,
                "获取应用信息... ", true, true);
        Thread interfaceThread = new Thread(new Runnable() {
            public void run() {
                try {
                    // 获取应用程序对象
                    QParkingApp appQParking = (QParkingApp) getApplicationContext();

                    URL urlInterface = new URL(
                            "http://app.qutingche.cn/Mobile/Common/parkInfo");
                    HttpURLConnection hucInterface = (HttpURLConnection) urlInterface
                            .openConnection();

                    hucInterface.setDoInput(true);
                    hucInterface.setDoOutput(true);
                    hucInterface.setUseCaches(false);

                    hucInterface.setRequestMethod("POST");
                    // 设置DataOutputStream
                    DataOutputStream dosInterface = new DataOutputStream(
                            hucInterface.getOutputStream());

                    dosInterface.flush();
                    dosInterface.close();

                    BufferedReader readerBuff = new BufferedReader(
                            new InputStreamReader(hucInterface.getInputStream()));
                    String strData = readerBuff.readLine();
                    Log.i("获取应用信息", strData);

                    JSONObject jsonRet = new JSONObject(strData);
                    if (jsonRet.getInt("status") == 1) {

                        JSONObject jsonData = jsonRet.getJSONObject("data");
                        // 获取应用信息
                        appQParking.m_strVersion = jsonData
                                .getString("android_version");
                        appQParking.m_strDownloadUrl = jsonData
                                .getString("download_link");
                        appQParking.m_strShareInfo = jsonData
                                .getString("share_info");
                    }

                    // 读取用户信息
                    if (appQParking.m_strUserID.length() == 0) {
                        SharedPreferences spUserInfo = getSharedPreferences(
                                "UserInfo", Activity.MODE_PRIVATE);
                        Log.i("本地是否有userID", spUserInfo.contains("shell") + "");

                        appQParking.m_strUserID = spUserInfo.getString("shell",
                                "");
                    }
                    if (appQParking.m_strUserID.length() == 0) {
                        m_hNotify.sendEmptyMessage(3);
                        return;
                    }

                    m_hNotify.sendEmptyMessage(4);
                } catch (Exception e) {
                    m_hNotify.sendEmptyMessage(9);
                }
            }
        });
        interfaceThread.start();
    }

    /***
     * 消息处理
     */
    private void setHandler() {
        // 设置通信句柄
        m_hNotify = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    // 定位失败
                    case 0: {
                        m_iLocationCount = 0;

                        m_dlgProgress.dismiss();
                        QParkingApp.ToastTip(MainActivity.this, "定位失败，请稍后重试！",
                                Toast.LENGTH_SHORT);
                        m_clientLocation.stop();
                    }
                    break;
                    case 1: {
                        m_dlgProgress.dismiss();

                        String strInfo = msg.peekData().getString("info");
                        if (!strInfo.equals("空数据"))
                            QParkingApp.ToastTip(MainActivity.this, strInfo,
                                    Toast.LENGTH_SHORT);
                    }
                    // 数据获取成功，界面ui填充
                    case 2: {
                        m_dlgProgress.dismiss();
                        m_scrollView.setVisibility(View.VISIBLE);
                        QParkingApp appQParking = (QParkingApp) getApplicationContext();

                        boolean bIsShowInfoWindow = false;
                        for (int i = 0; i < appQParking.m_listParking.size(); i++) {
                            TagParkingItem itemInfo = appQParking.m_listParking
                                    .get(i);
                            if (itemInfo.m_iDistance < DISTANCE_PARKING)
                                continue;

                            OverlayOptions optionOverlay;
                            if (i != 0)
                                optionOverlay = new MarkerOptions()
                                        .position(itemInfo.m_llParking)
                                        .icon(m_bmpParkFree).zIndex(i);
                            else
                                optionOverlay = new MarkerOptions()
                                        .position(itemInfo.m_llParking)
                                        .icon(m_bmpParCharge).zIndex(11);

                            m_viewBaiduMap.getMap().addOverlay(optionOverlay);

                            if (!bIsShowInfoWindow) {
                                bIsShowInfoWindow = true;
                                m_iNearParkIndex = i;

                                // 记录目标停车场索引
                                appQParking.m_itemParking = itemInfo;

                                View viewInfo = LayoutInflater.from(
                                        MainActivity.this).inflate(
                                        R.layout.parking_info, null);

                                ((TextView) viewInfo.findViewById(R.id.tvParkName))
                                        .setText(itemInfo.m_strName);
                                ((TextView) viewInfo.findViewById(R.id.tvDistance))
                                        .setText("距离" + itemInfo.m_iDistance + "m");

                                viewInfo.setTag(i);
                                viewInfo.setOnClickListener(MainActivity.this);

                                // InfoWindow infoWindow = new InfoWindow(viewInfo,
                                // itemInfo.m_llParking, -55);
                                // m_viewBaiduMap.getMap().showInfoWindow(infoWindow);
                            }
                        }

                        if (appQParking.m_listParking.size() >= 3) {
                            System.out.println("3个以上");
                            m_scrollView.setVisibility(View.VISIBLE);
                            setMiddleView(appQParking.m_listParking.get(position));
                            setLeftView(appQParking.m_listParking
                                    .get(appQParking.m_listParking.size() - 1));
                            setRightView(appQParking.m_listParking
                                    .get(position + 1));
                        } else if (appQParking.m_listParking.size() == 2) {
                            m_scrollView.setVisibility(View.VISIBLE);
                            System.out.println("2个");
                            setMiddleView(appQParking.m_listParking.get(position));
                            setLeftView(appQParking.m_listParking.get(position + 1));
                            setRightView(appQParking.m_listParking
                                    .get(position + 1));
                        } else if (appQParking.m_listParking.size() == 1) {
                            m_scrollView.setVisibility(View.VISIBLE);
                            System.out.println("1个");
                            setMiddleView(appQParking.m_listParking.get(position));
                            setLeftView(appQParking.m_listParking.get(position));
                            setRightView(appQParking.m_listParking.get(position));
                        } else if (appQParking.m_listParking.size() == 0) {
                        }

                    }
                    break;
                    // 等待定位开始
                    case 3: {
                        m_clientLocation.start();
                        m_dlgProgress.setMessage("正在定位... ");
                    }
                    break;
                    // 获取登录用户信息
                    case 4: {
                        m_dlgProgress.setMessage("获取用户信息... ");
                        // 调用接口线程
                        Thread interfaceThread = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    // 获取应用程序对象
                                    QParkingApp appQParking = (QParkingApp) getApplicationContext();
                                    String strParams = String.format(
                                            "shell=%s",
                                            URLEncoder
                                                    .encode(appQParking.m_strUserID));
                                    URL urlInterface = new URL(
                                            "http://app.qutingche.cn/Mobile/MemberIndex/Index");
                                    HttpURLConnection hucInterface = (HttpURLConnection) urlInterface
                                            .openConnection();

                                    hucInterface.setDoInput(true);
                                    hucInterface.setDoOutput(true);
                                    hucInterface.setUseCaches(false);

                                    hucInterface.setRequestMethod("POST");
                                    // 设置DataOutputStream
                                    DataOutputStream dosInterface = new DataOutputStream(
                                            hucInterface.getOutputStream());
                                    dosInterface.writeBytes(strParams);

                                    dosInterface.flush();
                                    dosInterface.close();

                                    BufferedReader readerBuff = new BufferedReader(
                                            new InputStreamReader(hucInterface
                                                    .getInputStream()));
                                    String strData = readerBuff.readLine();
                                    Log.i("获取用户信息", strData);

                                    JSONObject jsonRet = new JSONObject(strData);
                                    if (jsonRet.getInt("status") == 1) {
                                        JSONObject jsonData = jsonRet
                                                .getJSONObject("data");
                                        // 获取用户信息
                                        appQParking.m_strUserName = jsonData
                                                .getString("username");
                                        appQParking.m_strMobile = jsonData
                                                .getString("mobile");
                                        appQParking.m_strScore = jsonData
                                                .getString("integral");
                                        appQParking.m_strCarNo = jsonData
                                                .getString("carno");
                                    }
                                    Message msg = new Message();
                                    Bundle bundleData = new Bundle();

                                    bundleData.putString("info",
                                            jsonRet.getString("info"));
                                    msg.setData(bundleData);

                                    msg.what = 3;
                                    m_hNotify.sendMessage(msg);
                                } catch (Exception e) {
                                    m_hNotify.sendEmptyMessage(9);
                                }
                            }
                        });
                        interfaceThread.start();
                    }
                    break;
                    // 显示主菜单逻辑
                    case 5: {
                        RelativeLayout.LayoutParams rllpMainUI = (RelativeLayout.LayoutParams) m_rlMainUI
                                .getLayoutParams();

                        int iChange = m_iMainMenuLen * MENU_ANIM_INTERVAL
                                / MENU_ANIM_LEN;
                        rllpMainUI.leftMargin += iChange;
                        rllpMainUI.rightMargin -= iChange;
                        m_rlMainUI.setLayoutParams(rllpMainUI);
                        if (rllpMainUI.leftMargin < m_iMainMenuLen)
                            m_hAnimate
                                    .postDelayed(m_raShowMenu, MENU_ANIM_INTERVAL);
                        else {
                            rllpMainUI.leftMargin = m_iMainMenuLen;
                            rllpMainUI.rightMargin = -m_iMainMenuLen;
                            m_rlMainUI.setLayoutParams(rllpMainUI);
                        }
                    }
                    break;
                    // 隐藏主菜单逻辑
                    case 6: {
                        RelativeLayout.LayoutParams rllpMainUI = (RelativeLayout.LayoutParams) m_rlMainUI
                                .getLayoutParams();

                        int iChange = m_iMainMenuLen * MENU_ANIM_INTERVAL
                                / MENU_ANIM_LEN;
                        rllpMainUI.leftMargin -= iChange;
                        rllpMainUI.rightMargin += iChange;
                        m_rlMainUI.setLayoutParams(rllpMainUI);
                        if (rllpMainUI.leftMargin > 0)
                            m_hAnimate
                                    .postDelayed(m_raHideMenu, MENU_ANIM_INTERVAL);
                        else {
                            rllpMainUI.leftMargin = 0;
                            rllpMainUI.rightMargin = 0;
                            m_rlMainUI.setLayoutParams(rllpMainUI);
                            m_llMenuBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                    // 我的位置点缩小图标
                    case 7: {
                        ImageView ivBluePoint = (ImageView) m_viewLocation
                                .findViewById(R.id.ivBluePoint);
                        RelativeLayout.LayoutParams rllpBluePoint = (RelativeLayout.LayoutParams) ivBluePoint
                                .getLayoutParams();

                        if (rllpBluePoint.width > 10) {
                            rllpBluePoint.width -= 4;
                            rllpBluePoint.height -= 4;
                            rllpBluePoint.topMargin += 2;
                            ivBluePoint.setLayoutParams(rllpBluePoint);
                            m_viewBaiduMap.getMap().setMyLocationConfigeration(
                                    new MyLocationConfiguration(
                                            LocationMode.NORMAL, true,
                                            BitmapDescriptorFactory
                                                    .fromView(m_viewLocation)));
                            m_hAnimate.postDelayed(m_raLocPtZoomIn,
                                    LOC_ANIM_INTERVAL);
                        } else
                            m_hAnimate.postDelayed(m_raLocPtZoomOut,
                                    LOC_ANIM_INTERVAL);

                    }
                    break;
                    // 我的位置点放大图标
                    case 8: {
                        ImageView ivBluePoint = (ImageView) m_viewLocation
                                .findViewById(R.id.ivBluePoint);
                        RelativeLayout.LayoutParams rllpBluePoint = (RelativeLayout.LayoutParams) ivBluePoint
                                .getLayoutParams();

                        if (rllpBluePoint.width < 16) {
                            rllpBluePoint.width += 4;
                            rllpBluePoint.height += 4;
                            rllpBluePoint.topMargin -= 2;
                            ivBluePoint.setLayoutParams(rllpBluePoint);
                            m_viewBaiduMap.getMap().setMyLocationConfigeration(
                                    new MyLocationConfiguration(
                                            LocationMode.NORMAL, true,
                                            BitmapDescriptorFactory
                                                    .fromView(m_viewLocation)));
                            m_hAnimate.postDelayed(m_raLocPtZoomOut,
                                    LOC_ANIM_INTERVAL);
                        } else
                            m_hAnimate.postDelayed(m_raLocPtZoomIn,
                                    LOC_ANIM_INTERVAL);

                    }
                    break;
                    // 网络异常处理
                    case 9: {
                        m_dlgProgress.dismiss();
                        QParkingApp.ToastTip(MainActivity.this, "网络不给力，请检查相关设置！",
                                -100);
                    }
                    break;
                    // 未下载离线地图
                    case 10: {
                        String strInfo = msg.peekData().getString("info");
                        downloadOfflineMap_tv1.setText(String.format("请下载%s离线地图",
                                strInfo));
                        m_rlDownloadOfflineMap.setVisibility(View.VISIBLE);
                    }
                    break;
                    // 关闭离线下载提示
                    case 11: {
                        if (m_rlDownloadOfflineMap != null) {
                            m_rlDownloadOfflineMap.setVisibility(View.GONE);
                        }
                    }
                    break;
                }
            }
        };
        m_raShowMenu = new Runnable() {
            @Override
            public void run() {
                m_hNotify.sendEmptyMessage(5);
                System.out.println("--------5");
            }
        };
        m_raHideMenu = new Runnable() {
            @Override
            public void run() {
                m_hNotify.sendEmptyMessage(6);
                System.out.println("--------6");
            }
        };
        m_raLocPtZoomIn = new Runnable() {
            @Override
            public void run() {
                m_hNotify.sendEmptyMessage(7);
                System.out.println("--------7");
            }
        };
        m_raLocPtZoomOut = new Runnable() {
            @Override
            public void run() {
                m_hNotify.sendEmptyMessage(8);
                System.out.println("--------8");
            }
        };
        m_closeOffline = new Runnable() {
            @Override
            public void run() {
                m_hNotify.sendEmptyMessage(11);
                System.out.println("--------11");
            }
        };
        // 位置指示标志进行放大缩小循环
        m_hAnimate.postDelayed(m_raLocPtZoomIn, LOC_ANIM_INTERVAL);
        // 下载提示框倒计时关闭
        m_hAnimate.postDelayed(m_closeOffline, CLOSE_OFFLINEMAP_HINT_INTERVAL);
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        // 变量初始化
        m_iMainMenuLen = (int) (getWindowManager().getDefaultDisplay()
                .getWidth() * 0.8);
        m_bIsMainMenu = false;
        m_iLocationCount = 0;
        m_bmpParkFree = BitmapDescriptorFactory
                .fromResource(R.drawable.park_free);
        m_bmpParCharge = BitmapDescriptorFactory
                .fromResource(R.drawable.park_charge);
        m_bmpSearchPos = BitmapDescriptorFactory
                .fromResource(R.drawable.route_end_icon);
        m_bmpMePoint = BitmapDescriptorFactory
                .fromResource(R.drawable.local_point);

        m_lPressBackKeyTime = 0;
    }

    /**
     * 初始化搜索
     */
    private void initSearch() {

        // 初始化搜索模块，注册事件监听
        m_coderGeo = GeoCoder.newInstance();
        m_coderGeo.setOnGetGeoCodeResultListener(this);

        // POI搜索
        m_poiSearch = PoiSearch.newInstance();
        m_poiSearch.setOnGetPoiSearchResultListener(this);

        // 初始化搜索模块，注册事件监听
        m_searchRoute = RoutePlanSearch.newInstance();
        m_searchRoute.setOnGetRoutePlanResultListener(this);
    }

    /**
     * 百度定位
     */
    private void initBaiduLocation() {

        // 离线地图初始化
        m_mapOffline = new MKOfflineMap();
        // 定位初始化
        m_clientLocation = new LocationClient(this);
        m_clientLocation.registerLocationListener(new LocationListener());

        LocationClientOption optionLocation = new LocationClientOption();
        // 仅用于获得cityCode
        optionLocation.setIsNeedAddress(true);
        optionLocation.setOpenGps(true);
        // 设置坐标类型为百度坐标
        optionLocation.setCoorType("bd09ll");
        optionLocation.setScanSpan(1000);
        m_clientLocation.setLocOption(optionLocation);

    }

    /**
     * 设置监听
     */
    private void setListener() {
        // 点击地图关闭停车场详情小窗口
        m_viewBaiduMap.getMap().setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                m_viewBaiduMap.getMap().hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {

                return false;
            }

        });
        // 点击marker点弹出停车场详情小窗口
        m_viewBaiduMap.getMap().setOnMarkerClickListener(
                new OnMarkerClickListener() {
                    public boolean onMarkerClick(final Marker marker) {
                        QParkingApp appQParking = (QParkingApp) getApplicationContext();
                        View viewInfo = LayoutInflater.from(MainActivity.this)
                                .inflate(R.layout.parking_info, null);
                        // 覆盖物的堆叠高度(可以认为是堆叠一起的覆盖物个数)
                        if (marker.getZIndex() == 100) {
                            ShowSearchInfo();
                            return true;
                        }
                        if (marker.getZIndex() < 0)
                            return true;

                        if (marker.getZIndex() >= appQParking.m_listParking
                                .size())
                            return true;

                        TagParkingItem itemInfo = appQParking.m_listParking
                                .get(marker.getZIndex());
                        System.out.println(marker.getZIndex());

                        ((TextView) viewInfo.findViewById(R.id.tvParkName))
                                .setText(itemInfo.m_strName);
                        ((TextView) viewInfo.findViewById(R.id.tvDistance))
                                .setText("距离" + itemInfo.m_iDistance + "m");

                        viewInfo.setTag(marker.getZIndex());
                        viewInfo.setOnClickListener(MainActivity.this);

                        LatLng llPos = marker.getPosition();
                        InfoWindow infoWindow = new InfoWindow(viewInfo, llPos,
                                -55);
                        m_viewBaiduMap.getMap().showInfoWindow(infoWindow);

                        // 记录目标停车场索引
                        appQParking.m_itemParking = itemInfo;
                        return true;
                    }
                });

        m_viewBaiduMap.getMap().setOnMapStatusChangeListener(
                new OnMapStatusChangeListener() {
                    @Override
                    public void onMapStatusChangeStart(MapStatus status) {
                        m_viewBaiduMap.getMap().clear();
                        m_scrollView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onMapStatusChangeFinish(MapStatus status) {

                        // 创建附近检索参数对象
                        PoiNearbySearchOption optionNearBy = new PoiNearbySearchOption();
                        optionNearBy.keyword(POI_SEARCH_KEYWORD);
                        optionNearBy.pageNum(0);
                        optionNearBy.pageCapacity(POI_SEARCH_PAGE_CAPACITY);
                        optionNearBy.radius(POI_SEARCH_RADIUS);
                        optionNearBy.location(status.target);

                        m_poiSearch.searchNearby(optionNearBy);
                        // 移动后将指示器重置为0
                        position = 0;
                    }

                    @Override
                    public void onMapStatusChange(MapStatus status) {

                    }
                });

        m_scrollView.setMyScrollListener(new MyScrollListener() {

            @Override
            public void switchFinish(int flag) {
                QParkingApp appQParking = (QParkingApp) getApplicationContext();
                if (flag == 10) {
                    // 做更新1的事情
                    if (position == 0) {
                        setLeftView(appQParking.m_listParking
                                .get(appQParking.m_listParking.size() - 1));
                    } else {
                        setLeftView(appQParking.m_listParking.get(position - 1));
                    }
                } else if (flag == 20) {
                    // 做更新3的事情
                    if (position == appQParking.m_listParking.size() - 1) {
                        setRightView(appQParking.m_listParking.get(0));
                    } else {
                        setRightView(appQParking.m_listParking
                                .get(position + 1));
                    }
                }

                // 重绘地图overlay
                m_viewBaiduMap.getMap().clear();
                for (int i = 0; i < appQParking.m_listParking.size(); i++) {
                    TagParkingItem itemInfo = appQParking.m_listParking.get(i);
                    if (itemInfo.m_iDistance < DISTANCE_PARKING)
                        continue;

                    OverlayOptions optionOverlay;
                    if (i != position)
                        optionOverlay = new MarkerOptions()
                                .position(itemInfo.m_llParking)
                                .icon(m_bmpParkFree).zIndex(-1);
                    else
                        optionOverlay = new MarkerOptions()
                                .position(itemInfo.m_llParking)
                                .icon(m_bmpParCharge).zIndex(i);

                    m_viewBaiduMap.getMap().addOverlay(optionOverlay);
                }

            }

            @Override
            public void scrollFinish(int flag) {
                QParkingApp appQParking = (QParkingApp) getApplicationContext();
                if (flag == 10) {
                    // 做更新2、3的事情
                    if (position > 0
                            && position < appQParking.m_listParking.size()) {
                        position--;
                        setMiddleView(appQParking.m_listParking.get(position));
                        setRightView(appQParking.m_listParking
                                .get(position + 1));
                    } else if (position == 0) {
                        position = appQParking.m_listParking.size() - 1;
                        setMiddleView(appQParking.m_listParking.get(position));
                        setRightView(appQParking.m_listParking.get(0));
                    }
                    // QParkingApp.ToastTip(MainActivity.this, "向右拖", -100);
                } else if (flag == 20) {
                    // 做更新1、2的事情
                    if (position >= 0
                            && position < appQParking.m_listParking.size() - 1) {
                        position++;
                        setLeftView(appQParking.m_listParking.get(position - 1));
                        setMiddleView(appQParking.m_listParking.get(position));
                    } else if (position == appQParking.m_listParking.size() - 1) {
                        position = 0;
                        setLeftView(appQParking.m_listParking
                                .get(appQParking.m_listParking.size() - 1));
                        setMiddleView(appQParking.m_listParking.get(position));
                    }
                    // QParkingApp.ToastTip(MainActivity.this, "向左拖", -100);
                }
                // 移动控件后将目标停车场改为控件显示的
                appQParking.m_itemParking = appQParking.m_listParking
                        .get(position);
            }
        });
    }

    /**
     * 获取我的位置
     */
    private void myLocation() {
        // 我的位置点
        m_viewLocation = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.location_point, null);
        m_viewBaiduMap = (MapView) findViewById(R.id.bmapView);
        // 设置百度mapview显示样式
        m_viewBaiduMap.showZoomControls(false);
        m_viewBaiduMap.showScaleControl(false);
        m_viewBaiduMap.getMap().setMyLocationEnabled(true);
        // 设置我的位置图标
        m_viewBaiduMap.getMap().setMyLocationConfigeration(
                new MyLocationConfiguration(LocationMode.NORMAL, true,
                        BitmapDescriptorFactory.fromView(m_viewLocation)));

    }

    /**
     * 离线下载百度地图
     */
    private void initDownloadMap() {
        // 离线下载提示布局
        m_rlDownloadOfflineMap = (RelativeLayout) findViewById(R.id.llDownloadOfflineMap);
        downloadOfflineMap_tv1 = (TextView) findViewById(R.id.tv_OfflineMapHint);
        downloadOfflineMap_tv2 = (TextView) findViewById(R.id.tvOfflineMapDownload);
        findViewById(R.id.ivOfflineMapClose).setOnClickListener(this);
        // 下载的下划线
        downloadOfflineMap_tv2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        downloadOfflineMap_tv2.setTextColor(getResources().getColor(
                R.color.app_green));
        downloadOfflineMap_tv2.setOnClickListener(this);
    }

    /**
     * 初始化view
     */
    private void initView() {
        m_rlMainUI = (RelativeLayout) findViewById(R.id.rlMainUI);
        m_llMenuBar = (LinearLayout) findViewById(R.id.llMenuBar);
        m_llMenu = (LinearLayout) findViewById(R.id.llMenuMe);
        m_etAddress = (EditText) findViewById(R.id.etAddress);
        login_re_ = (RelativeLayout) findViewById(R.id.login_re);
        phonenum_tv = (TextView) findViewById(R.id.tvMenuMe);
        balance = (TextView) findViewById(R.id.tvBalance);
        findViewById(R.id.rlMenuMe).setOnClickListener(this);
        findViewById(R.id.rlIntegral_exchange).setOnClickListener(this);
        findViewById(R.id.my_wallet_form).setOnClickListener(this);
        login_re_.setOnClickListener(this);
        // 定位按钮
        findViewById(R.id.btMainLocation).setOnClickListener(this);
        // 声音检索
        findViewById(R.id.ivVoiceSearch).setOnClickListener(this);
        // 键盘检索
        m_etAddress.setOnEditorActionListener(this);

        // 路况按钮
        m_btRoadState = (Button) findViewById(R.id.btMainRoad);
        m_btRoadState.setOnClickListener(this);
        // 添加车位
        findViewById(R.id.btAddparking).setOnClickListener(this);

        // 左侧菜单
        findViewById(R.id.my_order_form).setOnClickListener(this);
//        findViewById(R.id.rlParkingInfo).setOnClickListener(this);
        findViewById(R.id.rlReverseParking).setOnClickListener(this);
        findViewById(R.id.rlLeaseParking).setOnClickListener(this);
        findViewById(R.id.rlShare).setOnClickListener(this);
        findViewById(R.id.rlMenuSet).setOnClickListener(this);
        findViewById(R.id.rlSuggestion).setOnClickListener(this);

        // 顶部按钮栏
        findViewById(R.id.btHomeMenu).setOnClickListener(this);
        findViewById(R.id.ivParkingList).setOnClickListener(this);

        // 链接地图控制消息
        findViewById(R.id.btMainZoomIn).setOnClickListener(this);
        findViewById(R.id.btMainZoomOut).setOnClickListener(this);

        // 链接底部按钮栏消息
        findViewById(R.id.ibtMainLocate).setOnClickListener(this);
        findViewById(R.id.ibtNear).setOnClickListener(this);
        findViewById(R.id.ibtGoParking).setOnClickListener(this);

        // 滚动监听回调
        m_scrollView = (MyscrollView) findViewById(R.id.myscroll_view);

        findViewById(R.id.myScorllView).setOnClickListener(this);
        findViewById(R.id.llOrder).setOnClickListener(this);
        findViewById(R.id.llGoParing).setOnClickListener(this);
    }

    /**
     * 注册讯飞语音,初始化百度定位
     */
    private void initSDK() {
        // 导航引擎初始化接口
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
                m_listenerNaviEngineInit, m_listenerLBSAuthManager);
        // 讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5495135c");
        speechRecognizer = SpeechRecognizer.createRecognizer(this, null);
        dlgRecognizer = new RecognizerDialog(this, m_listenerInit);
    }

    // 动态为滚动控件赋值
    public void setMiddleView(TagParkingItem tagParkingItem) {
        ((TextView) findViewById(R.id.myScorllView_address))
                .setText(tagParkingItem.m_strName);
        ((TextView) findViewById(R.id.myScorllView_distance)).setText("距离:"
                + tagParkingItem.m_iDistance + "m");
    }

    public void setLeftView(TagParkingItem tagParkingItem) {
        ((TextView) findViewById(R.id.myScorllView_address2))
                .setText(tagParkingItem.m_strName);
        ((TextView) findViewById(R.id.myScorllView_distance2)).setText("距离:"
                + tagParkingItem.m_iDistance + "m");
    }

    public void setRightView(TagParkingItem tagParkingItem) {
        ((TextView) findViewById(R.id.myScorllView_address4))
                .setText(tagParkingItem.m_strName);
        ((TextView) findViewById(R.id.myScorllView_distance4)).setText("距离:"
                + tagParkingItem.m_iDistance + "m");
    }

    /**
     * 离线地图是否下载判断
     *
     * @param cityCode
     * @param cityName
     */
    private void CheckOfflineMap(String cityCode, String cityName) {
        Log.i("城市编号", cityCode);
        editor.putString("city_name",cityName);
        editor.commit();
        m_mapOffline.init(MainActivity.this);
        // 获取已下过的离线地图信息
        m_arrayMap = m_mapOffline.getOfflineCityList();
        // 判断是否已下载离线地图
        for (int i = 0; i < m_arrayMap.size(); i++) {
            if (m_arrayMap.get(i).childCities == null
                    && (m_arrayMap.get(i).cityID + "").equals(cityCode)) {
                MKOLUpdateElement elementUpdate = m_mapOffline
                        .getUpdateInfo(m_arrayMap.get(i).cityID);
                if (elementUpdate == null || elementUpdate.ratio != 100) {
                    Message msg = new Message();
                    Bundle bundleData = new Bundle();

                    bundleData.putString("info", cityName);
                    msg.setData(bundleData);

                    msg.what = 10;
                    m_hNotify.sendMessage(msg);
                    break;
                }
            } else if (m_arrayMap.get(i).childCities != null) {
                for (int j = 0; j < m_arrayMap.get(i).childCities.size(); j++) {
                    if ((m_arrayMap.get(i).childCities.get(j).cityID + "")
                            .equals(cityCode)) {
                        MKOLUpdateElement elementUpdate = m_mapOffline
                                .getUpdateInfo(m_arrayMap.get(i).childCities
                                        .get(j).cityID);
                        if (elementUpdate == null || elementUpdate.ratio != 100) {
                            Message msg = new Message();
                            Bundle bundleData = new Bundle();

                            bundleData.putString("info", cityName);
                            msg.setData(bundleData);

                            msg.what = 10;
                            m_hNotify.sendMessage(msg);
                            break;
                        }
                    }
                }
            }
        }
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }

        return null;
    }

    /**
     * 定位SDK监听函数
     */
    public class LocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || m_viewBaiduMap == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(0)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            m_viewBaiduMap.getMap().setMyLocationData(locData);

            appQParking.m_llMe = new LatLng(location.getLatitude(),
                    location.getLongitude());

            // 容错判断，获得三次（0,0）坐标将停止检索
            if (appQParking.m_llMe.latitude == 0
                    && appQParking.m_llMe.longitude == 0) {
                m_iLocationCount++;
                if (m_iLocationCount > 3) {
                    Message msg = new Message();
                    msg.what = 0;
                    m_hNotify.sendMessage(msg);
                }
                return;
            }

            // 获得当前城市名和编号
            String cityCode = location.getCityCode();
            String cityName = location.getCity();
            locationPlace=new LocationPlace();
            locationPlace.setPro(location.getProvince());
            locationPlace.setCity(location.getCity());
            locationPlace.setDis(location.getDistrict());
            Log.e("show",locationPlace.toString());
            if (cityCode != null && cityName != null) {
                CheckOfflineMap(cityCode, cityName);
            }
            // 停止定位
            m_clientLocation.stop();
            // 提示开启GPS
            LocationManager mgrLocation = (LocationManager) getSystemService(MainActivity.this.LOCATION_SERVICE);
            if (!mgrLocation.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder adGPSTip = new Builder(MainActivity.this);
                adGPSTip.setMessage("您并未开启GPS，是否前往设置？");
                adGPSTip.setTitle("GPS设置");
                adGPSTip.setPositiveButton("确定",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intentGPS = new Intent();
                                intentGPS
                                        .setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intentGPS
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                try {
                                    startActivity(intentGPS);
                                } catch (ActivityNotFoundException ex) {
                                    // General settings activity
                                    intentGPS
                                            .setAction(Settings.ACTION_SETTINGS);
                                    try {
                                        startActivity(intentGPS);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                adGPSTip.setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                adGPSTip.show();
            }

            // 反Geo搜索
            m_coderGeo.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(appQParking.m_llMe));
            // 定位动画
            MapStatusUpdate updateStatus = MapStatusUpdateFactory
                    .newLatLngZoom(appQParking.m_llMe, 17);
            m_viewBaiduMap.getMap().animateMapStatus(updateStatus);

            // 创建附近检索参数对象
            PoiNearbySearchOption optionNearBy = new PoiNearbySearchOption();
            optionNearBy.keyword(POI_SEARCH_KEYWORD);
            optionNearBy.pageNum(0);
            optionNearBy.pageCapacity(POI_SEARCH_PAGE_CAPACITY);
            optionNearBy.radius(POI_SEARCH_RADIUS);
            optionNearBy.location(appQParking.m_llMe);
            m_poiSearch.searchNearby(optionNearBy);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (m_bIsMainMenu && ev.getRawX() > m_iMainMenuLen) {
            RestoreMainUI();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        if (m_iTypeNameID == v.getId())
            return;
        switch (v.getId()) {
            // 菜单按钮
            case R.id.btHomeMenu: {
                m_bIsMainMenu = true;
                String userId = sp.getString("userId", null);
                if (userId != null) {
                    login_re_.setVisibility(View.GONE);
                    m_llMenu.setVisibility(View.VISIBLE);
                    phonenum_tv.setText(sp.getString("username", null));
                    balance.setText(sp.getString("balance", null));
                } else {
                    login_re_.setVisibility(View.VISIBLE);
                    m_llMenu.setVisibility(View.GONE);
                }

                m_llMenuBar.setVisibility(View.VISIBLE);
                m_hAnimate.postDelayed(m_raShowMenu, MENU_ANIM_INTERVAL);
            }
            break;
            // 周边车位列表
            case R.id.ivParkingList: {
                if (m_addressCur == null) {
                    QParkingApp.ToastTip(MainActivity.this, "未检测到定位信息，无法实现相关功能！",
                            -100);
                    return;
                }
                ActivityTools.switchActivity(context, NearParkingActivity.class, null);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
            }
            break;
            // 语音搜索
            case R.id.ivVoiceSearch: {
                // 清空参数
                speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
                // 设置听写引擎
                speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE,
                        SpeechConstant.TYPE_CLOUD);
                // 设置返回结果格式
                speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
                // 设置语言
                speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                // 设置语言区域
                speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
                // 设置语音前端点
                speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
                // 设置语音后端点
                speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "1000");
                // 设置标点符号
                speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0");
                // 设置音频保存路径
                speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                        Environment.getExternalStorageDirectory()
                                + "/qparking/wavaudio.pcm");
                dlgRecognizer.setListener(m_listenerRecognizerDialog);
                dlgRecognizer.show();
            }
            break;

            // 个人主页
            case R.id.rlMenuMe:
                String userId = sp.getString("userId", null);
                if (userId != null) {
                    ActivityTools.switchActivity(context, PersonalCenterActivity.class, null);
                } else {
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivityForResult(i, 2);
//                    ActivityTools.switchActivity(context, LoginActivity.class, null);
                }
                break;
            //我的钱包
            case R.id.my_wallet_form:
                ActivityTools.switchActivity(context,MyWalletActivity.class,null);

                break;
            // 付款
            case R.id.my_order_form: {
                if (appQParking.m_strUserID.length() > 0) {
                    ActivityTools.switchActivity(context, MyOrderActivity.class, null);
                } else {
                    Toast.makeText(context, R.string.login_first, Toast.LENGTH_SHORT).show();
                    ActivityTools.switchActivity(context, LoginActivity.class, null);
                }
            }
 break;
            // 反向寻车
            case R.id.rlReverseParking: {

                Intent intentNewActivity = new Intent();
                intentNewActivity.setClass(MainActivity.this,
                        MipcaActivityCapture.class);
                startActivity(intentNewActivity);
            }
            break;
            // 车位求租
            case R.id.rlLeaseParking: {
//                if (m_addressCur == null) {
//                    QParkingApp.ToastTip(MainActivity.this, "未检测到定位信息，无法实现相关功能！",
//                            -100);
//                    return;
//                }
                appQParking = (QParkingApp) getApplicationContext();
                appQParking.m_addressAdd = m_addressCur;
                appQParking.m_llSubmit = appQParking.m_llMe;

                Intent intentNewActivity = new Intent();
                intentNewActivity.setClass(MainActivity.this, LesseeActivity.class);
                startActivity(intentNewActivity);
            }
            break;
            //积分商城
            case R.id.rlIntegral_exchange:
//                String userid=sp.getString("userId",null);
//                if(userid!=null){
//                    ActivityTools.switchActivity(context,IntegralExChange.class,null);
//                }else{
//                    Toast.makeText(context,R.string.login_first,Toast.LENGTH_SHORT).show();
//                    ActivityTools.switchActivity(context, LoginActivity.class, null);
//                }
                ActivityTools.switchActivity(context,SelectPayActivity.class,null);
                break;
            // 分享
            case R.id.rlShare: {
                QParkingApp appQParking = (QParkingApp) getApplicationContext();

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RecommendActivity.class);
                startActivity(intent);

                // Intent intentShare = new Intent(Intent.ACTION_SEND);
                // intentShare.setType("text/plain");
                // intentShare.putExtra(Intent.EXTRA_SUBJECT, "分享");
                // intentShare.putExtra(Intent.EXTRA_TEXT,
                // appQParking.m_strShareInfo);
                //
                // startActivity(Intent.createChooser(intentShare, getTitle()));
            }
            break;
            // 设置
            case R.id.rlMenuSet: {

                Intent intentNewActivity = new Intent();
//                intentNewActivity.setClass(MainActivity.this, SetActivity.class);
                intentNewActivity.setClass(MainActivity.this, MainAct.class);
                startActivity(intentNewActivity);
            }
            break;
            // 意见
            case R.id.rlSuggestion: {

                Intent intentNewActivity = new Intent();
                intentNewActivity.setClass(MainActivity.this,
                        SuggestionActivity.class);
                startActivity(intentNewActivity);
            }
            break;
            // 定位
            case R.id.btMainLocation: {
                m_clientLocation.start();
                m_dlgProgress = ProgressDialog.show(MainActivity.this, null,
                        "正在定位... ", true, true);
            }
            break;
            // 离线地图下载按钮
            case R.id.tvOfflineMapDownload: {
                Intent intentNewActivity = new Intent();
                intentNewActivity.setClass(MainActivity.this,
                        OfflineMapActivity.class);
                startActivity(intentNewActivity);
            }
            break;
            // 关闭离线下载提示
            case R.id.ivOfflineMapClose: {
                m_rlDownloadOfflineMap.setVisibility(View.GONE);
            }
            break;
            // 路况
            case R.id.btMainRoad: {
                m_bIsRoadStateOn = !m_bIsRoadStateOn;
                if (m_bIsRoadStateOn)
                    m_btRoadState.setBackgroundResource(R.drawable.main_road_on);
                else
                    m_btRoadState.setBackgroundResource(R.drawable.main_road_off);

                m_viewBaiduMap.getMap().setTrafficEnabled(m_bIsRoadStateOn);
            }
            break;
            // 添加停车场
            case R.id.btAddparking: {

                if (m_addressCur == null) {
                    QParkingApp.ToastTip(MainActivity.this, "未检测到定位信息，无法实现相关功能！",
                            -100);
                    return;
                }
                appQParking.m_addressAdd = m_addressCur;
                appQParking.m_llSubmit = appQParking.m_llMe;

                Intent intentNewActivity = new Intent();
                intentNewActivity.setClass(MainActivity.this,
                        AddParkingActivity.class);
                startActivity(intentNewActivity);
            }
            break;
            // 地图放大
            case R.id.btMainZoomIn: {
                MapStatusUpdate updateMapStatus = MapStatusUpdateFactory.zoomIn();
                m_viewBaiduMap.getMap().animateMapStatus(updateMapStatus);
            }
            break;
            // 地图缩小
            case R.id.btMainZoomOut: {
                MapStatusUpdate updateMapStatus = MapStatusUpdateFactory.zoomOut();
                m_viewBaiduMap.getMap().animateMapStatus(updateMapStatus);
            }
            break;
            // 停车场详细
            case R.id.myScorllView: {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ParkingDetailActivity.class);
                startActivity(intent);
            }
            break;
            // 预定
            case R.id.llOrder: {
                Intent intentNewActivity = new Intent();
                intentNewActivity.setClass(MainActivity.this,
                        OrderParkingActivity.class);
                startActivity(intentNewActivity);
            }
            break;
            // 停车场导航
            case R.id.llGoParing: {
                QParkingApp appQParking = (QParkingApp) getApplicationContext();

                BNaviPoint ptStart = new BNaviPoint(appQParking.m_llMe.longitude,
                        appQParking.m_llMe.latitude, "我的位置",
                        BNaviPoint.CoordinateType.BD09_MC);
                BNaviPoint ptEnd = new BNaviPoint(
                        appQParking.m_itemParking.m_llParking.longitude,
                        appQParking.m_itemParking.m_llParking.latitude,
                        appQParking.m_itemParking.m_strName,
                        BNaviPoint.CoordinateType.BD09_MC);

                BaiduNaviManager.getInstance().launchNavigator(MainActivity.this,
                        ptStart, ptEnd, NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
                        true, // 真实导航
                        BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
                        new OnStartNavigationListener() { // 跳转监听
                            @Override
                            public void onJumpToNavigator(Bundle configParams) {
                                Intent intent = new Intent(MainActivity.this,
                                        BNavigatorActivity.class);
                                intent.putExtras(configParams);
                                startActivity(intent);
                            }

                            @Override
                            public void onJumpToDownloader() {

                            }
                        });
            }
            break;

            // 以下三个已隐藏
            // 定位按钮
            case R.id.ibtMainLocate: {
                m_clientLocation.start();
                m_dlgProgress = ProgressDialog.show(MainActivity.this, null,
                        "正在定位... ", true, true);
            }
            break;
            // 前往最近停车场
            case R.id.ibtGoParking: {
                QParkingApp appQParking = (QParkingApp) getApplicationContext();
                // 记录目标停车场索引
                TagParkingItem itemInfo = appQParking.m_listParking
                        .get(m_iNearParkIndex);
                appQParking.m_itemParking = itemInfo;

                Intent intentNewActivity = new Intent();
                intentNewActivity.setClass(MainActivity.this,
                        NavigationActivity.class);
                startActivity(intentNewActivity);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
            }
            break;
            // 附近停车场列表
            case R.id.ibtNear: {
                Intent intentNewActivity = new Intent();
                intentNewActivity.setClass(MainActivity.this,
                        NearParkingActivity.class);
                startActivity(intentNewActivity);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
            }
            break;
            default: {
                int iViewTag = Integer.parseInt(v.getTag().toString());
                switch (iViewTag) {
                    case 100: {
                        QParkingApp appQParking = (QParkingApp) getApplicationContext();

                        BNaviPoint ptStart = new BNaviPoint(
                                appQParking.m_llMe.longitude,
                                appQParking.m_llMe.latitude, "我的位置",
                                BNaviPoint.CoordinateType.BD09_MC);
                        BNaviPoint ptEnd = new BNaviPoint(m_llSearchPos.longitude,
                                m_llSearchPos.latitude, m_strSearchName,
                                BNaviPoint.CoordinateType.BD09_MC);

                        BaiduNaviManager.getInstance().launchNavigator(
                                MainActivity.this, ptStart, ptEnd,
                                NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
                                true, // 真实导航
                                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
                                new OnStartNavigationListener() { // 跳转监听
                                    @Override
                                    public void onJumpToNavigator(Bundle configParams) {
                                        Intent intent = new Intent(MainActivity.this,
                                                BNavigatorActivity.class);
                                        intent.putExtras(configParams);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onJumpToDownloader() {
                                    }
                                });

                        m_viewBaiduMap.getMap().hideInfoWindow();
                    }
                    break;
                    // 默认为点击地图上的小窗标记
                    default: {
                        Intent intentNewActivity = new Intent();
                        intentNewActivity.setClass(MainActivity.this,
                                ParkingDetailActivity.class);
                        startActivity(intentNewActivity);

                        m_viewBaiduMap.getMap().hideInfoWindow();
                    }
                    break;
                }
            }
            break;
        }
    }

    /**
     * 初始化语音监听器。
     */
    private InitListener m_listenerInit = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS)
                QParkingApp.ToastTip(MainActivity.this, "语音引擎初始化失败", -100);
        }
    };

    // 听写字符缓冲区，onResult将在一次语音识别中回调多次，初始化不能写在回调函数中
    StringBuffer tempStr = new StringBuffer();
    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener m_listenerRecognizerDialog = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            String strInput = JsonParser.parseIatResult(results
                    .getResultString());
            tempStr = tempStr.append(strInput);
            if (isLast) {
                QParkingApp appQParking = (QParkingApp) getApplicationContext();
                appQParking.m_addressAdd = m_addressCur;
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("info", tempStr.toString());
                startActivity(intent);
                // 清空
                tempStr.setLength(0);
            }
        }

        @Override
        public void onError(SpeechError arg0) {
            QParkingApp.ToastTip(MainActivity.this, "识别错误", -100);
        }
    };

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            QParkingApp.ToastTip(MainActivity.this, "定位失败，请稍后重试！", -100);
            return;
        }

        m_addressCur = result.getAddressDetail();

        Log.i("地理反编码地址", m_addressCur.province + m_addressCur.city
                + m_addressCur.district + m_addressCur.street
                + m_addressCur.streetNumber);
    }

    // 返回详细poi数据
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    /*
     * POI搜索结束回调方法，加载停车场评价
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onGetPoiResult(PoiResult result) {
        m_dlgProgress.dismiss();

        // TODO:这样判断会在百度返回空的时候不向服务器取数据
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            QParkingApp.ToastTip(MainActivity.this, "未找到停车场！", -100);
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            // 获取应用程序对象
            QParkingApp appQParking = (QParkingApp) getApplicationContext();
            m_viewBaiduMap.getMap().clear();
            appQParking.m_listParking.clear();

            int iCount = result.getAllPoi().size();
            for (int i = 0; i < iCount; i++) {
                TagParkingItem itemInfo = new TagParkingItem();
                PoiInfo infoPoi = result.getAllPoi().get(i);

                itemInfo.m_strPid = infoPoi.uid;
                itemInfo.m_strName = infoPoi.name;
                itemInfo.m_llParking = infoPoi.location;
                itemInfo.m_strAddress = infoPoi.address;
                itemInfo.m_strShareName = "百度";
                itemInfo.m_iDistance = (int) DistanceUtil.getDistance(
                        appQParking.m_llMe, itemInfo.m_llParking);

                itemInfo.m_iFreeNum = 0;
                itemInfo.m_iChargeNum = 0;
                itemInfo.m_iPraiseNum = 0;
                itemInfo.m_iDespiseNum = 0;

                appQParking.m_listParking.add(itemInfo);
            }
            // 调用接口线程
            Thread interfaceThread = new Thread(new Runnable() {
                @SuppressLint("DefaultLocale")
                public void run() {
                    try {
                        // 获取应用程序对象
                        QParkingApp appQParking = (QParkingApp) getApplicationContext();
                        String strParams = "pid=";
                        for (int i = 0; i < appQParking.m_listParking.size(); i++) {
                            String strAdded;
                            TagParkingItem itemInfo = appQParking.m_listParking
                                    .get(i);
                            if (i == 0)
                                strAdded = String.format("%s",
                                        URLEncoder.encode(itemInfo.m_strPid));
                            else
                                strAdded = String.format(",%s",
                                        URLEncoder.encode(itemInfo.m_strPid));

                            strParams += strAdded;
                        }
                        URL urlInterface = new URL(
                                "http://app.qutingche.cn/Mobile/DepotFee/getTrapeze");
                        HttpURLConnection hucInterface = (HttpURLConnection) urlInterface
                                .openConnection();

                        hucInterface.setDoInput(true);
                        hucInterface.setDoOutput(true);
                        hucInterface.setUseCaches(false);
                        hucInterface.setRequestMethod("POST");
                        // 设置DataOutputStream
                        DataOutputStream dosInterface = new DataOutputStream(
                                hucInterface.getOutputStream());

                        dosInterface.writeBytes(strParams);
                        dosInterface.flush();
                        dosInterface.close();

                        BufferedReader readerBuff = new BufferedReader(
                                new InputStreamReader(hucInterface
                                        .getInputStream()));
                        String strData = readerBuff.readLine();
                        Log.i("百度获取的停车场信息", strData);

                        JSONObject jsonRet = new JSONObject(strData);
                        if (jsonRet.getInt("status") == 1) {
                            JSONArray jsonInfos = jsonRet.getJSONArray("data");
                            // 获取用户信息
                            for (int i = 0; i < jsonInfos.length(); i++) {
                                JSONObject jsonData = jsonInfos
                                        .getJSONObject(i);
                                for (int j = 0; j < appQParking.m_listParking
                                        .size(); j++) {
                                    TagParkingItem itemInfo = appQParking.m_listParking
                                            .get(j);
                                    if (itemInfo.m_strPid.equals(jsonData
                                            .getString("pid"))) {
                                        itemInfo.m_iFreeNum = jsonData
                                                .getInt("free_number");
                                        itemInfo.m_iChargeNum = jsonData
                                                .getInt("charge_number");
                                        itemInfo.m_iPraiseNum = jsonData
                                                .getInt("praise_number");
                                        itemInfo.m_iDespiseNum = jsonData
                                                .getInt("despise_number");
                                    }
                                }
                            }
                            // 调用接口线程
                            Thread interfaceThread = new Thread(new Runnable() {
                                @SuppressLint("DefaultLocale")
                                public void run() {
                                    try {
                                        // 获取应用程序对象
                                        QParkingApp appQParking = (QParkingApp) getApplicationContext();
                                        String strParams = String
                                                .format("latitude=%f&longitude=%f&distance=%d&pageCapacity=%d&pageIndex=0",
                                                        appQParking.m_llMe.latitude,
                                                        appQParking.m_llMe.longitude,
                                                        POI_SEARCH_RADIUS,
                                                        POI_SEARCH_PAGE_CAPACITY);
                                        URL urlInterface = new URL(
                                                "http://app.qutingche.cn/Mobile/Depot/getAround");
                                        HttpURLConnection hucInterface = (HttpURLConnection) urlInterface
                                                .openConnection();

                                        hucInterface.setDoInput(true);
                                        hucInterface.setDoOutput(true);
                                        hucInterface.setUseCaches(false);

                                        hucInterface.setRequestMethod("POST");
                                        // 设置DataOutputStream
                                        DataOutputStream dosInterface = new DataOutputStream(
                                                hucInterface.getOutputStream());

                                        dosInterface.writeBytes(strParams);
                                        dosInterface.flush();
                                        dosInterface.close();

                                        BufferedReader readerBuff = new BufferedReader(
                                                new InputStreamReader(
                                                        hucInterface
                                                                .getInputStream()));
                                        String strData = readerBuff.readLine();
                                        Log.i("服务器获取手动添加的停车场信息", strData);

                                        JSONObject jsonRet = new JSONObject(
                                                strData);
                                        if (jsonRet.getInt("status") == 1) {
                                            JSONArray jsonInfos = jsonRet
                                                    .getJSONArray("data");
                                            // 获取用户信息
                                            for (int i = 0; i < jsonInfos
                                                    .length(); i++) {
                                                JSONObject jsonData = jsonInfos
                                                        .getJSONObject(i);

                                                TagParkingItem itemInfo = new TagParkingItem();

                                                itemInfo.m_strPid = jsonData
                                                        .getString("id");
                                                itemInfo.m_strName = jsonData
                                                        .getString("name");
                                                itemInfo.m_llParking = new LatLng(
                                                        jsonData.getDouble("latitude"),
                                                        jsonData.getDouble("longitude"));
                                                itemInfo.m_strAddress = jsonData
                                                        .getString("address");
                                                itemInfo.m_strShareName = jsonData
                                                        .getString("username");
                                                itemInfo.m_iDistance = jsonData
                                                        .getInt("distance");

                                                itemInfo.m_iLocationType = jsonData
                                                        .getInt("location_type");
                                                itemInfo.m_iFreeNum = jsonData
                                                        .getInt("free_number");
                                                itemInfo.m_iChargeNum = jsonData
                                                        .getInt("charge_number");
                                                itemInfo.m_iPraiseNum = jsonData
                                                        .getInt("praise_number");
                                                itemInfo.m_iDespiseNum = jsonData
                                                        .getInt("despise_number");

                                                appQParking.m_listParking
                                                        .add(itemInfo);
                                            }

                                            m_hNotify.sendEmptyMessage(2);
                                        } else {
                                            Message msg = new Message();
                                            Bundle bundleData = new Bundle();

                                            bundleData.putString("info",
                                                    jsonRet.getString("info"));
                                            msg.setData(bundleData);

                                            msg.what = 1;
                                            m_hNotify.sendMessage(msg);
                                        }

                                    } catch (Exception e) {
                                        // 接口返回异常导致无法显示数据，暂时先这样处理
                                        m_hNotify.sendEmptyMessage(2);
                                    }
                                }
                            });
                            interfaceThread.start();
                        } else {
                            Message msg = new Message();
                            Bundle bundleData = new Bundle();

                            bundleData.putString("info",
                                    jsonRet.getString("info"));
                            msg.setData(bundleData);

                            msg.what = 1;
                            m_hNotify.sendMessage(msg);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            interfaceThread.start();

            return;
        }
    }

    // 隐藏菜单栏
    void RestoreMainUI() {
        m_bIsMainMenu = false;
        m_hAnimate.postDelayed(m_raHideMenu, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0)
            return;
        switch (requestCode) {
            case SEARCH_RET: {
                m_llSearchPos = new LatLng(data.getDoubleExtra("latitude", 0),
                        data.getDoubleExtra("longitude", 0));
                m_strSearchName = data.getStringExtra("name");
                m_strSearchAddress = data.getStringExtra("address");

                OverlayOptions searchOverlay = new MarkerOptions()
                        .position(m_llSearchPos).icon(m_bmpSearchPos).zIndex(100);
                m_viewBaiduMap.getMap().addOverlay(searchOverlay);
                MapStatusUpdate updateStatus = MapStatusUpdateFactory
                        .newLatLngZoom(m_llSearchPos, 15);
                m_viewBaiduMap.getMap().animateMapStatus(updateStatus);

                ShowSearchInfo();

                // 根据经纬度搜索路线
                QParkingApp appQParking = (QParkingApp) getApplicationContext();

                PlanNode nodeStart = PlanNode.withLocation(appQParking.m_llMe);
                PlanNode nodeTarget = PlanNode.withLocation(m_llSearchPos);

                m_searchRoute.drivingSearch((new DrivingRoutePlanOption()).from(
                        nodeStart).to(nodeTarget));
            }
            break;
            case 2:
                m_bIsMainMenu = true;
                String userId = sp.getString("userId", null);
                if (userId != null) {
                    login_re_.setVisibility(View.GONE);
                    m_llMenu.setVisibility(View.VISIBLE);
                    phonenum_tv.setText(sp.getString("username", null));
                    balance.setText(sp.getString("balance", null));
                }
                break;

        }
    }

    private void ShowSearchInfo() {
        View viewInfo = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.search_info, null);

        ((TextView) viewInfo.findViewById(R.id.tvSearchName))
                .setText(m_strSearchName);
        ((TextView) viewInfo.findViewById(R.id.tvSearchAddress))
                .setText(m_strSearchAddress);
        viewInfo.setTag(100);
        viewInfo.setOnClickListener(MainActivity.this);

        InfoWindow infoWindow = new InfoWindow(viewInfo, m_llSearchPos, -65);

        m_viewBaiduMap.getMap().showInfoWindow(infoWindow);
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            QParkingApp.ToastTip(this, "抱歉，未找到结果！", Toast.LENGTH_SHORT);
            finish();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            QParkingApp.ToastTip(this, "抱歉，未找到结果！", Toast.LENGTH_SHORT);
            finish();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(
                    m_viewBaiduMap.getMap());

            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

            QParkingApp appQParking = (QParkingApp) getApplicationContext();

            LatLng llCenter = new LatLng(
                    (appQParking.m_llMe.latitude + m_llSearchPos.latitude) / 2,
                    (appQParking.m_llMe.longitude + m_llSearchPos.longitude) / 2);
            int iDistance = (int) DistanceUtil.getDistance(appQParking.m_llMe,
                    m_llSearchPos);
            MapStatusUpdate updateStatus = MapStatusUpdateFactory
                    .newLatLngZoom(llCenter,
                            QParkingApp.GetZoomLevel(iDistance));
            m_viewBaiduMap.getMap().animateMapStatus(updateStatus);
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult arg0) {

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult arg0) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (m_bIsMainMenu) {
                RestoreMainUI();

                return true;
            }
            if ((System.currentTimeMillis() - m_lPressBackKeyTime) > 2000) {
                QParkingApp.ToastTip(this, "再按一次退出", Toast.LENGTH_SHORT);
                m_lPressBackKeyTime = System.currentTimeMillis();

                return true;
            }

            System.exit(0);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        MobclickAgent.onResume(this);
        m_viewBaiduMap.onResume();

        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPause(this);
        m_viewBaiduMap.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        m_viewBaiduMap.onDestroy();
        m_mapOffline.destroy();
        m_poiSearch.destroy();
        // 回收 bitmap 资源
        m_bmpParkFree.recycle();
        m_bmpParCharge.recycle();
        m_bmpSearchPos.recycle();
        m_bmpMePoint.recycle();
        super.onDestroy();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            QParkingApp appQParking = (QParkingApp) getApplicationContext();
            appQParking.m_addressAdd = m_addressCur;
            Intent intent = new Intent(context, SearchActivity.class);
            intent.putExtra("info", m_etAddress.getText().toString());
            startActivity(intent);
        }
        return false;
    }

}
