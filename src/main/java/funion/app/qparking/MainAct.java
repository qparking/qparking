package funion.app.qparking;
/**
 * Created by yunze on 2015/12/22.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.LeftMenuAdapter;
import funion.app.adapter.ParkingInfoAdapter;
import funion.app.common.T;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.tools.HttpUtil;
import funion.app.qparking.tools.JsonParser;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.view.SildMenuView;
import funion.app.qparking.vo.LeftMenuIconBean;
import funion.app.qparking.vo.LocationPlace;
import funion.app.qparking.vo.MyOrderRecharge;
import funion.app.qparking.vo.MyServiceParkingInfo;
import funion.app.qparking.vo.TagParkingItem1;
import funion.app.qparking.vo.ToolBarBean;

import static com.baidu.mapapi.map.MapStatus.*;

public class MainAct extends Activity implements View.OnClickListener, MKOfflineMapListener, OnGetGeoCoderResultListener, OnGetPoiSearchResultListener {
    private Context context = MainAct.this;
    private QParkingApp appQParking;

    //百度地图view参数及视图
    private Boolean m_bIsRoadStateOn = false;
    private Button m_btRoadState;
    private MapView mapView;
    private View myLocationView = null;//我的位置显示试图
    private LocationClient myLocation;
    private int locationCount;// 定位次数标记，逻辑上小于3时将重新定位
    private GeoCoder m_coderGeo = null;//反向geo
    private ImageView lessee_arrow;
    private ImageView ivCar;
    private ReverseGeoCodeResult.AddressComponent m_addressCur = null;
    // 离线地图服务
    private MKOfflineMap mapOffline = null;
    // 离线城市列表
    private ArrayList<MKOLSearchRecord> arrayMapList = null;

    //离线下载地图布局
    private RelativeLayout downloadOfflineMapRl;
    private TextView downloadOfflineMapTv1;
    private TextView downloadOfflineMapTv2;
    private int y;
    private int viewBottom, viewTop;
    //
    private boolean m_bIsMainMenu;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    /**
     * 搜索条件设置
     */
    // 搜索关键字
    final String POI_SEARCH_KEYWORD = "停车场";
    // 设置显示数量，默认10
    final int POI_SEARCH_PAGE_CAPACITY = 10;
    // 搜索半径，单位：米
    final int POI_SEARCH_RADIUS = 5000;
    final int DISTANCE_PARKING = 30;
    private PoiSearch m_poiSearch = null;

    //百度定位覆盖物图片资源
    private BitmapDescriptor m_bmpParkFree;
    private BitmapDescriptor m_bmpParCharge;
    private BitmapDescriptor m_bmpSearchPos;
    private BitmapDescriptor m_bmpMePoint;

    private ViewPager viewPager;
    ParkingInfoAdapter parkingInfoAdapter;
    private ArrayList<TagParkingItem1> tagParkingItem1ArrayList = new ArrayList<>();
    private ArrayList<TagParkingItem1> tagParkingItem1s = new ArrayList<>();
    private ArrayList<MyServiceParkingInfo> myServiceParkingInfoList = new ArrayList<>();
    private String cityName;
    private String cityCode;
    LocationPlace locationPlace;
    // 语音识别
    private SpeechRecognizer speechRecognizer;
    // 语音听写UI
    private RecognizerDialog dlgRecognizer;
    //viewPager 按下Y坐标点
    private int startY = -1;
    private int selectPosition = 0;
    private boolean isScroll = false;
    /**
     * 侧滑布局参数以及控件
     */
    private SildMenuView menu;
    //动画处理handler
    private Handler m_hAnimate = new Handler();
    // 最近按下系统返回时间
    private long m_lPressBackKeyTime;
    List<Bitmap> leftmenulist;
    List<Bitmap> toolbarlist;
    List<Bitmap> barItemlist;
    List<Bitmap> mapItemlist;
    List<Bitmap> actionItemlist;
    private List<LeftMenuIconBean> leftMenuIconBeanList;
    private List<ToolBarBean> toolBarBeanList;
    //延迟显示菜单
    private Runnable m_raShowMenu = new Runnable() {
        @Override
        public void run() {
            m_hNotify.sendEmptyMessage(5);
        }
    };
    private Runnable m_raHideMenu = new Runnable() {
        @Override
        public void run() {
            m_hNotify.sendEmptyMessage(6);
        }
    };

    private BNaviEngineManager.NaviEngineInitListener m_listenerNaviEngineInit = new BNaviEngineManager.NaviEngineInitListener() {
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

    //消息处理
    private Handler m_hNotify = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    locationCount = 0;
                    QParkingApp.ToastTip(context, "定位失败，请稍后重试！",
                            Toast.LENGTH_SHORT);
                    myLocation.stop();
                    break;
                case 2:
                    QParkingApp appQParking = (QParkingApp) getApplicationContext();
                    for (int i = 0; i < appQParking.m_listParking.size(); i++) {
                        TagParkingItem itemInfo = appQParking.m_listParking
                                .get(i);
                        if (itemInfo.m_iDistance < DISTANCE_PARKING)
                            continue;
                        OverlayOptions optionOverlay = null;
                        if (i != 0) {
                            optionOverlay = new MarkerOptions()
                                    .position(itemInfo.m_llParking)
                                    .icon(m_bmpParkFree).zIndex(i);
                        } else {
                            optionOverlay = new MarkerOptions()
                                    .position(itemInfo.m_llParking)
                                    .icon(m_bmpParCharge).zIndex(11);
                        }
                        mapView.getMap().addOverlay(optionOverlay);
                    }
                    break;
                case 10:
                    // 未下载离线地图
                    String strInfo = msg.peekData().getString("info");
                    downloadOfflineMapTv1.setText(String.format("请下载%s离线地图",
                            strInfo));
                    downloadOfflineMapRl.setVisibility(View.VISIBLE);
                    break;

            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (m_bIsMainMenu && ev.getRawX() > m_iMainMenuLen) {
//            RestoreMainUI();
//            return true;
//        }
        DisplayImageOptions options;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Rect rect, rect1;
                if (tagParkingItem1ArrayList.size() == 0) {
                    return super.dispatchTouchEvent(ev);
                }
                if (tagParkingItem1s.get(selectPosition).getM_iFreeNum() == -1) {
                    ((TextView) findViewById(R.id.number_tv)).setText("未知");
                } else {
                    ((TextView) findViewById(R.id.number_tv)).setText(tagParkingItem1s.get(selectPosition).getM_iFreeNum() + "");
                }
                ((TextView) findViewById(R.id.parking_title)).setText(tagParkingItem1s.get(selectPosition).getM_strName());
                ((TextView) findViewById(R.id.address_tv)).
                        setText(AppTools.distance(tagParkingItem1s.get(selectPosition).getM_iDistance())
                                + "|" + tagParkingItem1s.get(selectPosition).getM_strAddress());
                options = AppTools.confirgImgInfo(R.drawable.no_picture_img, R.drawable.no_picture_img);
                ImageLoader.getInstance().displayImage(tagParkingItem1s.get(selectPosition)
                        .getParking_img(), (ImageView) findViewById(R.id.parking_img_iv), options);
                rect1 = new Rect(mapView.getLeft(), mapView.getTop(), mapView.getRight(), mapView.getBottom() - viewPager.getHeight());
                if (rect1.contains((int) ev.getX(), (int) ev.getY())) {
                    isScroll = false;
                }
                if (viewPager.getVisibility() == View.VISIBLE) {
                    rect = new Rect(viewPager.getLeft(), mapView.getHeight() - viewPager.getHeight(), viewPager.getRight(), viewPager.getBottom());
                    if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                        startY = (int) ev.getY();
                    }

                    break;
                }
                if (findViewById(R.id.out_rl).getVisibility() == View.VISIBLE) {
                    rect = new Rect(findViewById(R.id.out_rl).getLeft(), findViewById(R.id.out_rl).getTop(),
                            findViewById(R.id.out_rl).getRight(), findViewById(R.id.out_rl).getBottom());
                    rect1 = new Rect(mapView.getLeft(), mapView.getTop(), mapView.getRight(),
                            mapView.getBottom() - findViewById(R.id.out_rl).getHeight());
                    if (rect1.contains((int) ev.getX(), (int) ev.getY())) {
                        isScroll = false;
                    }
                    if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                        startY = (int) ev.getY();
                    }
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {
                    break;
                }
                int current = (int) ev.getY() - startY;
                if (current < -100) {
                    startY = -1;
                    viewPager.setVisibility(View.GONE);
                    findViewById(R.id.out_rl).setVisibility(View.VISIBLE);
                    TranslateAnimation animation = new TranslateAnimation(0, 0, findViewById(R.id.out_rl).getTop() / 2,
                            0);
                    // True:图片停在动画结束位置
                    animation.setFillAfter(true);
                    animation.setDuration(1000);
                    findViewById(R.id.out_rl).startAnimation(animation);
//                    findViewById(R.id.out_rl).
//                            startAnimation(AnimationUtils.loadAnimation(context, R.anim.main_push_bottom_in));
                    return true;
                }
                if (current > 100) {
                    if (viewPager.getVisibility() == View.VISIBLE) {
                        return super.dispatchTouchEvent(ev);
                    }
                    startY = -1;
                    TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
                            findViewById(R.id.out_rl).getTop());
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            findViewById(R.id.out_rl).setVisibility(View.GONE);
                            viewPager.setVisibility(View.VISIBLE);
                            if (findViewById(R.id.out_rl).getVisibility() == View.VISIBLE) {
                                findViewById(R.id.out_rl).setFocusable(true);
                                findViewById(R.id.out_rl).setClickable(true);
//                    mapView.setFocusable(true);
                            } else {
                                findViewById(R.id.out_rl).setFocusable(false);
                                findViewById(R.id.out_rl).setClickable(false);
                                mapView.setFocusable(true);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    // True:图片停在动画结束位置
                    animation.setFillAfter(true);
                    animation.setDuration(1000);
                    findViewById(R.id.out_rl).startAnimation(animation);
//                    findViewById(R.id.out_rl).
//                            startAnimation(AnimationUtils.loadAnimation(context, R.anim.main_bottom_out));
                    return true;
                }
            case MotionEvent.ACTION_UP:
                if (findViewById(R.id.out_rl).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.out_rl).setFocusable(true);
                    findViewById(R.id.out_rl).setClickable(true);
                    mapView.setFocusable(true);
                } else {
                    findViewById(R.id.out_rl).setFocusable(false);
                    findViewById(R.id.out_rl).setClickable(false);
                    mapView.setFocusable(true);
                }
                if(tagParkingItem1s.size()!=0){
                if (tagParkingItem1s.get(selectPosition).getM_iFreeNum() == -1) {
                    ((TextView) findViewById(R.id.number_tv)).setText("未知");
                } else {
                    ((TextView) findViewById(R.id.number_tv)).setText(tagParkingItem1s.get(selectPosition).getM_iFreeNum() + "");
                }
                ((TextView) findViewById(R.id.parking_title)).setText(tagParkingItem1s.get(selectPosition).getM_strName());
                ((TextView) findViewById(R.id.address_tv)).
                        setText(AppTools.distance(tagParkingItem1s.get(selectPosition).getM_iDistance())
                                + "|" + tagParkingItem1s.get(selectPosition).getM_strAddress());
                options = AppTools.confirgImgInfo(R.drawable.no_picture_img, R.drawable.no_picture_img);
                ImageLoader.getInstance().displayImage(tagParkingItem1s.get(selectPosition)
                        .getParking_img(), (ImageView) findViewById(R.id.parking_img_iv), options);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        String sign = sp.getString("sign", null);
        switch (view.getId()) {
            //关闭下载地图
            case R.id.ivOfflineMapClose:
                // 关闭离线下载提示
                downloadOfflineMapRl.setVisibility(View.GONE);
                break;
            //下载地图
            case R.id.tvOfflineMapDownload:
                intent = new Intent();
                intent.setClass(context,
                        OfflineMapActivity.class);
                startActivity(intent);
                break;
            //是否弹出侧滑布局
            case R.id.btHomeMenu: {
                if(sign==null){
                    ActivityTools.switchActivity(context, LoginActivity.class, null);
                }else{
                    menu.openMenu();
                }
            }
            break;
            //登陆状态
            case R.id.rlMenuMe:
                if (sign != null) {
                    ActivityTools.switchActivity(context, PersonalCenterActivity.class, null);
                } else {
                    menu.openMenu();
                    ActivityTools.switchActivity(context, LoginActivity.class, null);
                }
                break;
                //设置
            case R.id.rlMenuSet:
                ActivityTools.switchActivity(context, SetActivity.class, null);
                break;
            // 意见
            case R.id.rlSuggestion:
                ActivityTools.switchActivity(context, SuggestionActivity.class, null);
                break;
            case R.id.offline_rl:
                ActivityTools.switchActivity(context, OfflineMapActivity.class, null);
                break;
            //路况
            case R.id.btMainRoad:
                m_bIsRoadStateOn = !m_bIsRoadStateOn;
                if (m_bIsRoadStateOn)
                    m_btRoadState.setBackgroundResource(R.drawable.main_road_on);
                else
                    m_btRoadState.setBackgroundResource(R.drawable.main_road_off);
                mapView.getMap().setTrafficEnabled(m_bIsRoadStateOn);
                break;
            //发布
            case R.id.btAddparking:
                appQParking.m_addressAdd = m_addressCur;
                appQParking.m_llSubmit = appQParking.m_llMe;
                intent = new Intent();
                intent.setClass(context,
                        PublishParkInfoActivity.class);
                startActivity(intent);
                break;
            //清除地图覆盖物
            case R.id.btMainLocation:
                mapView.getMap().clear();
                break;
            case R.id.ivParkingList:
                if (sign != null) {
                    ActivityTools.switchActivity(context, SelectPayActivity.class, null);
                } else {
                    T.show(context, R.string.login_first, Toast.LENGTH_SHORT);
                    ActivityTools.switchActivity(context, LoginActivity.class, null);
                }
                break;
            //搜索
            case R.id.etAddress:
                intent = new Intent();
                intent.putExtra("cityName", cityName);
                intent.setClass(context, NearSearchActivity.class);
                //intent.setClass(context, NearParkingActivity.class);
                startActivityForResult(intent, 1);
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
            //打电话
            case R.id.call_tv:
//                T.show(context, tagParkingItem1ArrayList.get(selectPosition).getPhone() + selectPosition, 2000);
                if (tagParkingItem1s.get(selectPosition).getPhone().length() < 3) {
                    T.show(context, "当前停车场暂无电话!", 2000);
                    return;
                }
//                try {
                intent = new Intent(Intent.ACTION_DIAL, Uri
                        .parse("tel:"
                                + tagParkingItem1s.get(selectPosition).getPhone()));
                startActivity(intent);
                break;
            //入住
            case R.id.add_tv:
                intent = new Intent();
                intent.setClass(context,
                        AddParkingLotActivity.class);
                startActivity(intent);
                break;
            //抢车位
            case R.id.rush_parking_rl:
                intent = new Intent(context, OrderParkingActivity.class);
                intent.putExtra("title", tagParkingItem1s.get(selectPosition).getM_strName());
                intent.putExtra("address", tagParkingItem1s.get(selectPosition).getM_strAddress());
                startActivity(intent);
                break;
            //去停车
            case R.id.go_parking_rl:
                if (DistanceUtil.getDistance(appQParking.m_llMe, tagParkingItem1s.get(selectPosition).getM_llParking()) < 50) {
                    T.showShort(context, "起始位置离终点太近，请重新定位");
                    return;
                }
                intent = new Intent(context, NavigationAct.class);
//                appQParking.m_itemParking.m_llParking = tagParkingItem1ArrayList.get(selectPosition).getM_llParking();
                intent.putExtra("lat", tagParkingItem1s.get(selectPosition).getM_llParking().latitude);
                intent.putExtra("lon", tagParkingItem1s.get(selectPosition).getM_llParking().longitude);
                intent.putExtra("pid", tagParkingItem1s.get(selectPosition).getM_strPid());
                intent.putExtra("name", tagParkingItem1s.get(selectPosition).getM_strName());
                intent.putExtra("address", tagParkingItem1s.get(selectPosition).getM_strAddress());
                intent.putExtra("phone", tagParkingItem1s.get(selectPosition).getPhone());
                intent.putExtra("img", tagParkingItem1s.get(selectPosition).getParking_img());
                intent.putExtra("freeNum", tagParkingItem1s.get(selectPosition).getM_iFreeNum());
                intent.putExtra("chargeNum", tagParkingItem1s.get(selectPosition).getM_iChargeNum());
                intent.putExtra("praiseNum", tagParkingItem1s.get(selectPosition).getM_iPraiseNum());
                intent.putExtra("despiseNum", tagParkingItem1s.get(selectPosition).getM_iDespiseNum());//ShareName
                intent.putExtra("shareName", tagParkingItem1s.get(selectPosition).getM_strShareName());
                intent.putExtra("locationType", tagParkingItem1s.get(selectPosition).getM_iLocationType());
                intent.putExtra("distance", tagParkingItem1s.get(selectPosition).getM_iDistance());
                startActivity(intent);
                break;
            case R.id.update_tv:
                intent = new Intent(context, UpdateParkingInfoActivity.class);
                intent.putExtra("address", tagParkingItem1s.get(selectPosition).getM_strAddress());
                intent.putExtra("name", tagParkingItem1s.get(selectPosition).getM_strName());
                intent.putExtra("phone", tagParkingItem1s.get(selectPosition).getPhone());
                intent.putExtra("money", "未知");
                intent.putExtra("freeNum", "未知");
                intent.putExtra("longitude", tagParkingItem1s.get(selectPosition).getM_llParking().longitude + "");
                intent.putExtra("latitude", tagParkingItem1s.get(selectPosition).getM_llParking().latitude + "");
                intent.putExtra("img", tagParkingItem1s.get(selectPosition).getParking_img());
                intent.putExtra("pid", tagParkingItem1s.get(selectPosition).getId());
                startActivity(intent);
                break;
            default:
                mapView.getMap().hideInfoWindow();
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - m_lPressBackKeyTime) > 2000) {
                QParkingApp.ToastTip(this, "再按一次退出", Toast.LENGTH_SHORT);
                m_lPressBackKeyTime = System.currentTimeMillis();

                return true;
            }

            System.exit(0);
        }

        return super.onKeyDown(keyCode, event);
    }

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
            QParkingApp.ToastTip(context, "识别错误", -100);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.act_main);
//        m_dlgProgress = ProgressDialog.show(context, null,
//                "正在定位，请稍后..... ", true, true);
        Intent intent=getIntent();
        leftMenuIconBeanList=intent.getParcelableArrayListExtra("leftmenu");
        toolBarBeanList=intent.getParcelableArrayListExtra("toolbar");
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        editor = sp.edit();
        appQParking = (QParkingApp) getApplicationContext();
        initIcon();
        menu=new SildMenuView();
        menu.initSildingMenu(MainAct.this, leftMenuIconBeanList,toolBarBeanList,leftmenulist,toolbarlist);
        initSDK();
        initView();
        initDate();
        downLoadMap();
        initBaiduLocation();
        initSearch();
        setListener();

    }



    private void initIcon() {
        leftmenulist = bmlist("leftmenu");
        toolbarlist = bmlist("toolbar");
        barItemlist = bmlist("barItem");
        mapItemlist = bmlist("mapItem");
        actionItemlist = bmlist("actionItem");
    }

    private List<Bitmap> bmlist(String name) {
        List<String> list = HttpUtil.getPictures(Environment.getExternalStorageDirectory() + "/" + "qparkingIcon" + "/" + name);
        List<Bitmap> bmIconList = null;
        if (list != null) {
            bmIconList = new ArrayList<Bitmap>();
            for (int i = 0; i < list.size(); i++) {
                Bitmap bm = BitmapFactory.decodeFile(list.get(i));
                bmIconList.add(bm);
            }
        }
        return bmIconList;
    }


    /**
     * 连接服务器
     *
     * @param latitude
     * @param longitude
     */
    private void connectNetWord(double latitude, double longitude) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("raidus", POI_SEARCH_RADIUS + "");
        param.put("lat", latitude + "");
        param.put("lon", longitude + "");
        Log.e("lat", latitude + "");
        Log.e("lon", longitude + "");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String result) {
                Log.e("result", result);
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<MyServiceParkingInfo>>() {
                }.getType();
                ArrayList<MyServiceParkingInfo> tempList = gson.fromJson(result, listType);
                myServiceParkingInfoList.addAll(tempList);
                updateResult(myServiceParkingInfoList, tagParkingItem1ArrayList);
//                m_dlgProgress.dismiss();
//                parkingInfoAdapter.notifyDataSetChanged();
            }
        }, param, "depot");

    }

    private void initSDK() {
        // 导航引擎初始化接口
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
                m_listenerNaviEngineInit, m_listenerLBSAuthManager);
        // 讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5495135c");
        speechRecognizer = SpeechRecognizer.createRecognizer(this, null);
        dlgRecognizer = new RecognizerDialog(this, m_listenerInit);
    }

    /**
     * 初始化语音监听器。
     */
    private InitListener m_listenerInit = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS)
                QParkingApp.ToastTip(context, "语音引擎初始化失败", -100);
        }
    };

    private void initView() {

        viewPager = (ViewPager) findViewById(R.id.parking_info_vp);
        findViewById(R.id.call_tv).setOnClickListener(this);
        findViewById(R.id.add_tv).setOnClickListener(this);
        findViewById(R.id.rush_parking_rl).setOnClickListener(this);
        findViewById(R.id.go_parking_rl).setOnClickListener(this);

        findViewById(R.id.btHomeMenu).setOnClickListener(this);
        findViewById(R.id.rlMenuMe).setOnClickListener(this);
        // 声音检索
        findViewById(R.id.ivVoiceSearch).setOnClickListener(this);

        findViewById(R.id.btAddparking).setOnClickListener(this);
        findViewById(R.id.btMainLocation).setOnClickListener(this);
        findViewById(R.id.ivParkingList).setOnClickListener(this);
        findViewById(R.id.etAddress).setOnClickListener(this);
        findViewById(R.id.rlMenuSet).setOnClickListener(this);
        findViewById(R.id.rlSuggestion).setOnClickListener(this);
        findViewById(R.id.offline_rl).setOnClickListener(this);
//        login_re_.setOnClickListener(this);
        lessee_arrow=(ImageView)findViewById(R.id.lessee_arrow);
        lessee_arrow.setImageBitmap(mapItemlist.get(2));
        m_btRoadState = (Button) findViewById(R.id.btMainRoad);

        m_btRoadState.setOnClickListener(this);
//        m_llMenu.setOnClickListener(this);
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
    }

    /**
     * 下载地图
     */
    private void downLoadMap() {
        mapOffline = new MKOfflineMap();
        // 离线下载提示布局
        downloadOfflineMapRl = (RelativeLayout) findViewById(R.id.llDownloadOfflineMap);
        downloadOfflineMapTv1 = (TextView) findViewById(R.id.tv_OfflineMapHint);
        downloadOfflineMapTv2 = (TextView) findViewById(R.id.tvOfflineMapDownload);
        findViewById(R.id.ivOfflineMapClose).setOnClickListener(this);
        // 下载的下划线
        downloadOfflineMapTv2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        ((TextView) findViewById(R.id.add_tv)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        downloadOfflineMapTv2.setTextColor(getResources().getColor(
                R.color.app_green));
        downloadOfflineMapTv2.setOnClickListener(this);

    }

    /**
     * 初始数据
     */
    private void initDate() {
        locationCount = 0;
        m_bIsMainMenu = false;
        // 变量初始化
//        m_iMainMenuLen = (int) (getWindowManager().getDefaultDisplay()
//                .getWidth() * 0.8);
        // 初始化全局 bitmap 信息，不用时及时 recycle
        m_bmpParkFree = BitmapDescriptorFactory
                .fromResource(R.drawable.park_free);
        m_bmpParCharge = BitmapDescriptorFactory
                .fromResource(R.drawable.park_charge);
        m_bmpSearchPos = BitmapDescriptorFactory
                .fromResource(R.drawable.route_end_icon);
        m_bmpMePoint = BitmapDescriptorFactory
                .fromResource(R.drawable.local_point);
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }

        return null;
    }

    /**
     * 初始化mapView视图控件
     */
    private void initBaiduLocation() {
        //初始化我的位置
        myLocationView = LayoutInflater.from(context).inflate(
                R.layout.location_point, null);
        mapView = (MapView) findViewById(R.id.bmapView);
        // 设置百度mapview显示样式
        mapView.showZoomControls(false);
        mapView.showScaleControl(false);
        mapView.getMap().setMyLocationEnabled(true);
        // 设置我的位置图标
        mapView.getMap().setMyLocationConfigeration(
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true,
                        BitmapDescriptorFactory.fromView(myLocationView)));
        initMyLocation();
    }

    /**
     * 初始化百度定位
     */
    private void initMyLocation() {
        // 定位初始化
        myLocation = new LocationClient(this);
        myLocation.registerLocationListener(new LocationListener());
        LocationClientOption optionLocation = new LocationClientOption();
        // 仅用于获得cityCode
        optionLocation.setIsNeedAddress(true);
        optionLocation.setOpenGps(true);
        // 设置坐标类型为百度坐标
        optionLocation.setCoorType("bd09ll");
        optionLocation.setScanSpan(1000);
        myLocation.setLocOption(optionLocation);
        myLocation.start();
    }


    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }


    /**
     * 注册位置监听
     */
    public class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(0)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mapView.getMap().setMyLocationData(locData);

            appQParking.m_llMe = new LatLng(location.getLatitude(),
                    location.getLongitude());//地理坐标基本数据结构类
//            mapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(appQParking.m_llMe));
            // 容错判断，获得三次（0,0）坐标将停止检索，设置定位最多3次
            if (appQParking.m_llMe.latitude == 0
                    && appQParking.m_llMe.longitude == 0) {
                locationCount++;
                if (locationCount > 3) {
                    Message msg = new Message();
                    msg.what = 0;
                    m_hNotify.sendMessage(msg);
                }
                return;
            }
            // 获得当前城市名和编号
            cityCode = location.getCityCode();
            cityName = location.getCity();
            editor.putString("city_name", cityName);
            editor.commit();
            locationPlace = new LocationPlace();
            locationPlace.setPro(location.getProvince());
            locationPlace.setCity(location.getCity());
            locationPlace.setDis(location.getDistrict());
//            if (cityCode != null && cityName != null) {
//                CheckOfflineMap(cityCode, cityName);
//            }
            // 停止定位
            myLocation.stop();
            // 提示开启GPS
            LocationManager mgrLocation = (LocationManager) getSystemService(MainAct.this.LOCATION_SERVICE);
            if (!mgrLocation.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder adGPSTip = new AlertDialog.Builder(MainAct.this);
                adGPSTip.setMessage("您并未开启GPS，是否前往设置？");
                adGPSTip.setTitle("GPS设置");
                adGPSTip.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
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
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                adGPSTip.show();
            }
//                // 反Geo搜索
//                m_coderGeo.reverseGeoCode(new ReverseGeoCodeOption()
//                        .location(appQParking.m_llMe));
            // 定位动画
            MapStatusUpdate updateStatus = MapStatusUpdateFactory
                    .newLatLngZoom(appQParking.m_llMe, 17);
            mapView.getMap().animateMapStatus(updateStatus);
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

    /**
     * 判断是否存在当前城市地图离线包
     *
     * @param cityCode
     * @param cityName
     */
    private void CheckOfflineMap(String cityCode, String cityName) {
        Log.i("城市编号", cityCode);
        mapOffline.init(MainAct.this);
        arrayMapList = mapOffline.getOfflineCityList();
        // 判断是否已下载离线地图
        for (int i = 0; i < arrayMapList.size(); i++) {
            if (arrayMapList.get(i).childCities == null
                    && (arrayMapList.get(i).cityID + "").equals(cityCode)) {
                MKOLUpdateElement elementUpdate = mapOffline
                        .getUpdateInfo(arrayMapList.get(i).cityID);
                if (elementUpdate == null || elementUpdate.ratio != 100) {
                    Message msg = new Message();
                    Bundle bundleData = new Bundle();
                    bundleData.putString("info", cityName);
                    msg.setData(bundleData);

                    msg.what = 10;
                    m_hNotify.sendMessage(msg);
                    break;
                }
            } else if (arrayMapList.get(i).childCities != null) {
                for (int j = 0; j < arrayMapList.get(i).childCities.size(); j++) {
                    if ((arrayMapList.get(i).childCities.get(j).cityID + "")
                            .equals(cityCode)) {
                        MKOLUpdateElement elementUpdate = mapOffline
                                .getUpdateInfo(arrayMapList.get(i).childCities
                                        .get(j).cityID);
                        if (elementUpdate == null || elementUpdate.ratio != 100) {
                            Message msg = new Message();
                            Bundle bundleData = new Bundle();
//                            T.show(context, cityName + "1", 2000);
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


    @Override
    public void onGetOfflineMapState(int type, int state) {

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            QParkingApp.ToastTip(context, "定位失败，请稍后重试！", -100);
            return;
        }

        m_addressCur = result.getAddressDetail();

    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        // TODO:这样判断会在百度返回空的时候不向服务器取数据
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            QParkingApp.ToastTip(context, "未找到停车场！", -100);
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            // 获取应用程序对象
            QParkingApp appQParking = (QParkingApp) getApplicationContext();
            mapView.getMap().clear();
            tagParkingItem1ArrayList.clear();
            myServiceParkingInfoList.clear();
            tagParkingItem1s.clear();
            appQParking.m_listParking.clear();
            int iCount = result.getAllPoi().size();
            for (int i = 0; i < iCount; i++) {
                TagParkingItem itemInfo = new TagParkingItem();
                TagParkingItem1 tagParkingItem1 = new TagParkingItem1();
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
                tagParkingItem1.setId("0");
                tagParkingItem1.setM_strPid(infoPoi.uid);
                tagParkingItem1.setM_strName(infoPoi.name);
                tagParkingItem1.setM_llParking(infoPoi.location);
                tagParkingItem1.setM_strAddress(infoPoi.address);
                tagParkingItem1.setPhone(infoPoi.phoneNum);
                tagParkingItem1.setM_strShareName("百度");
                tagParkingItem1.setM_iDistance(((int)
                        DistanceUtil.getDistance(appQParking.m_llMe, tagParkingItem1.getM_llParking())));
                tagParkingItem1.setM_iFreeNum(-1);
                tagParkingItem1.setM_iChargeNum(-1);
                tagParkingItem1.setM_iPraiseNum(-1);
                tagParkingItem1.setM_iDespiseNum(-1);
                tagParkingItem1.setParking_img(null);
                tagParkingItem1ArrayList.add(tagParkingItem1);
            }
            connectNetWord(tagParkingItem1ArrayList.get(0).getM_llParking().latitude, tagParkingItem1ArrayList.get(0).getM_llParking().longitude);
        }
    }

    private void updateResult(ArrayList<MyServiceParkingInfo> list, ArrayList<TagParkingItem1> tagList) {

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < tagList.size(); j++) {
                if (list.get(i).getMarker_id().equals(tagList.get(j).getM_strPid())) {
                    tagList.get(j).setId(list.get(i).getId());
                    tagList.get(j).setM_strName(list.get(i).getName());
                    LatLng latLng = new LatLng(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()));
                    tagList.get(j).setM_llParking(latLng);
                    tagList.get(j).setM_strAddress(list.get(i).getAddress());
                    tagList.get(j).setM_iDistance((int) DistanceUtil.getDistance(appQParking.m_llMe, latLng));
                    tagList.get(j).setM_strShareName("百度");
                    tagList.get(j).setPhone(list.get(i).getContactway());
                    tagList.get(j).setM_iFreeNum(Integer.parseInt(list.get(i).getFree_number()));
                    tagList.get(j).setM_iChargeNum(Integer.parseInt(list.get(i).getCharge_number()));
                    tagList.get(j).setM_iPraiseNum(Integer.parseInt(list.get(i).getPraise_number()));
                    tagList.get(j).setM_iDespiseNum(Integer.parseInt(list.get(i).getDespise_number()));
                    tagList.get(j).setParking_img(list.get(i).getAvatar());

                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMarker_id().equals("")) {
                TagParkingItem1 info = new TagParkingItem1();
                info.setM_strName(list.get(i).getName());
                info.setId(list.get(i).getId());
                LatLng latLng = new LatLng(Double.parseDouble(list.get(i).getLatitude()), Double.parseDouble(list.get(i).getLongitude()));
                info.setM_llParking(latLng);
                info.setM_strAddress(list.get(i).getAddress());
                info.setM_iDistance((int) DistanceUtil.getDistance(appQParking.m_llMe, latLng));
                info.setM_strShareName("百度");
                info.setPhone(list.get(i).getContactway());
                info.setM_iFreeNum(Integer.parseInt(list.get(i).getFree_number()));
                info.setM_iChargeNum(Integer.parseInt(list.get(i).getCharge_number()));
                info.setM_iPraiseNum(Integer.parseInt(list.get(i).getPraise_number()));
                info.setM_iDespiseNum(Integer.parseInt(list.get(i).getDespise_number()));
                info.setParking_img(list.get(i).getAvatar());
                tagList.add(info);
            }
        }
        tagParkingItem1s.addAll(tagList);
        parkingInfoAdapter = new ParkingInfoAdapter(context, tagList,actionItemlist);
        viewPager.setAdapter(parkingInfoAdapter);
        parkingInfoAdapter.notifyDataSetChanged();
        addOptionOverlay1(tagList);
    }

    private void addOptionOverlay1(ArrayList<TagParkingItem1> tagList) {
        for (int i = 0; i < tagList.size(); i++) {
            TagParkingItem1 itemInfo = tagList
                    .get(i);
            if (itemInfo.m_iDistance < DISTANCE_PARKING)
                continue;
            OverlayOptions optionOverlay;
            if (i != 0) {
                optionOverlay = new MarkerOptions()
                        .position(itemInfo.m_llParking)
                        .icon(m_bmpParkFree).zIndex(i);
            } else {
                optionOverlay = new MarkerOptions()
                        .position(itemInfo.m_llParking)
                        .icon(m_bmpParCharge).zIndex(11);
            }
            mapView.getMap().addOverlay(optionOverlay);
        }
    }

    private void addOptionOverlay(List<TagParkingItem> list) {
        for (int i = 0; i < appQParking.m_listParking.size(); i++) {
            TagParkingItem itemInfo = list
                    .get(i);
            if (itemInfo.m_iDistance < DISTANCE_PARKING)
                continue;
            OverlayOptions optionOverlay;
            if (i != 0) {
                optionOverlay = new MarkerOptions()
                        .position(itemInfo.m_llParking)
                        .icon(m_bmpParkFree).zIndex(i);
            } else {
                optionOverlay = new MarkerOptions()
                        .position(itemInfo.m_llParking)
                        .icon(m_bmpParCharge).zIndex(11);
            }
            mapView.getMap().addOverlay(optionOverlay);

        }
    }

    /**
     * 监听方法
     */
    private void setListener() {
        // 点击地图关闭停车场详情小窗口
        mapView.getMap().setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                mapView.getMap().hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }
        });
        // 点击marker点弹出停车场详情小窗口
        mapView.getMap().setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                QParkingApp appQParking = (QParkingApp) getApplicationContext();
                View viewInfo = LayoutInflater.from(context)
                        .inflate(R.layout.parking_info, null);
                // 覆盖物的堆叠高度(个人觉得是区分覆盖物的标记位)
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
                viewPager.setCurrentItem(marker.getZIndex());
                selectPosition = marker.getZIndex();
                // 记录目标停车场索引
                appQParking.m_itemParking = itemInfo;
                return true;
            }
        });
        //图状态改变相关接口
        mapView.getMap().setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            /**
             *完成改变
             * @param mapStatus
             */
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
//                if (!isScroll)
//                    mapView.getMap().clear();

            }

            /**
             *正在
             * @param mapStatus
             */
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            /**
             *改变完成
             * @param mapStatus
             */
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (isScroll) {
                    return;
                }
                selectPosition = 0;
                // 创建附近检索参数对象
                PoiNearbySearchOption optionNearBy = new PoiNearbySearchOption();
                optionNearBy.keyword(POI_SEARCH_KEYWORD);
                optionNearBy.pageNum(0);
                optionNearBy.pageCapacity(POI_SEARCH_PAGE_CAPACITY);
                optionNearBy.radius(POI_SEARCH_RADIUS);
                optionNearBy.location(mapStatus.target);
                m_poiSearch.searchNearby(optionNearBy);
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition = position;
                mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(tagParkingItem1ArrayList
                        .get(position).getM_llParking()));
                mapView.getMap().clear();
                for (int i = 0; i < tagParkingItem1ArrayList.size(); i++) {
                    OverlayOptions optionOverlay;
                    if (i == position) {
                        optionOverlay = new MarkerOptions()
                                .position(tagParkingItem1ArrayList.get(i).getM_llParking())
                                .icon(m_bmpParCharge).zIndex(i);
                    } else {
                        optionOverlay = new MarkerOptions()
                                .position(tagParkingItem1ArrayList.get(i).getM_llParking())
                                .icon(m_bmpParkFree).zIndex(i);
                    }
                    mapView.getMap().addOverlay(optionOverlay);
                }
                isScroll = true;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void ShowSearchInfo() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    LatLng latLng;
                    if (data.getDoubleExtra("lat", 0) != 0 && data.getDoubleExtra("lon", 0) != 0) {
                        latLng = new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lon", 0));
                        mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        MobclickAgent.onResume(this);
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPause(this);
        mapView.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        mapOffline.destroy();
        m_poiSearch.destroy();
        // 回收 bitmap 资源
        m_bmpParkFree.recycle();
        m_bmpParCharge.recycle();
        m_bmpSearchPos.recycle();
        m_bmpMePoint.recycle();
        super.onDestroy();
    }
}
