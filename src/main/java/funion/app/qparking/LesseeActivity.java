package funion.app.qparking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.PlotsReleaseCarPortAdapter;
import funion.app.qparking.popWindow.SortCityPop;
import funion.app.qparking.popWindow.SortPartPop;
import funion.app.qparking.sqlite.MySqliteManager;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.view.PullRefreshList;
import funion.app.qparking.vo.AllReleaseTools;
import funion.app.qparking.vo.LocationPlace;
import funion.app.qparking.vo.PlotBean;
import funion.app.qparking.vo.Searchplotbean;
import funion.app.qparking.vo.SelectPlotId;

public class LesseeActivity extends Activity implements View.OnClickListener, OnGetPoiSearchResultListener {
    private Context context = LesseeActivity.this;
    private static final int LOAD_MORE_DATA_HEADER = 0;
    private static final int LOAD_MORE_DATA_FOOTER = 1;
    private static final int LOAD_MORE_FAILED = 2;
    private static final int LOAD_MESSAGE = 3;
    List<String> dis;
    List<String> select_condition;
    List<SelectPlotId> plots;//存放最后匹配后需要显示的内容
    List<AllReleaseTools> plots_carport;
    List<Searchplotbean> searchplots;//存放poi检索搜到的所有信息
    List<PlotBean> plotbean;//暂时存放小区名字,uid,空闲车位
    PullRefreshList allPlotsCarport;
    //城市排序
    private SortCityPop sortCityPop;
    //车位数量排序
    private SortPartPop sortPartPop;
    QParkingApp appQParking;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private TextView show_dis, sort_park_tv;
    String city_name;
    private Boolean witchBtnPress;
    LocationPlace locationPlace;
    private PoiSearch m_poiSearch = null;
    SelectPlotId selectPlot;
    String address, uid;
    LatLng m_llPos;
    PlotsReleaseCarPortAdapter plotsReleaseCarPortAdapter;
    SelectInfo selectInfo;
    MySqliteManager mySqliteManager;
    int city_code;
    double distance;
    int m_iPageNum = 0;
    PoiCitySearchOption option;
    int n=0;
    int length;
    boolean isshow=true;
    boolean isflush=false;
    private ProgressDialog m_dlgProgress = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lessee_activity);
        appQParking = (QParkingApp) getApplicationContext();
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        city_name = sp.getString("city_name", null);
        mySqliteManager = new MySqliteManager(context);
        mySqliteManager.openDatabase();
        mySqliteManager.select_city(city_name);
        selectInfo = new SelectInfo();
        city_code = selectInfo.getCity_code();
        editor = sp.edit();
        locationPlace = new LocationPlace();
        plots=new ArrayList<SelectPlotId>();
        initView();
        m_dlgProgress = ProgressDialog.show(LesseeActivity.this, null,
                "正在获取小区信息... ", true, true);
        initData();
    }

    private void initData() {
        plotbean=new ArrayList<PlotBean>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sign", sp.getString("sign", null));
        params.put("citycode", city_code + "");
        params.put("ps", "3");
        params.put("pn", m_iPageNum + "");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
//                    String count_ = (String) jsonObject.get("count");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.e("map",result.toString());
                    if (jsonArray.length()!=0) {
                        PlotBean bean;
                        length=jsonArray.length();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            bean=new PlotBean();
                            JSONObject js = jsonArray.getJSONObject(i);
                            String plotname = TransCoding.trans(js.getString("village"));
                            bean.setName(plotname);
                            bean.setCarport(js.getString("freeNum"));
                            String id = js.getString("villageid");
                            if (!id.equals("0")) {
                                uid = id;
                                bean.setUid(uid);
                                plotbean.add(bean);
                                if (!plotname.equals(null)) {
                                    SearchPlot(plotname);
                                }
                            }
                        }
                    } else {
                        m_hNotify.sendEmptyMessage(LOAD_MORE_FAILED);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, params, "finddeportbycode");
    }

    private synchronized void SearchPlot(String name) {
        m_poiSearch = PoiSearch.newInstance();
        m_poiSearch.setOnGetPoiSearchResultListener(this);
        option = new PoiCitySearchOption();
        option.keyword(name);
        option.city(city_name);
        m_poiSearch.searchInCity(option);
    }

    private void initView() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.top_back_btn);
        ((TextView) findViewById(R.id.include_tv_title)).setText("车位出租");
        ((TextView) findViewById(R.id.include_tv_title)).setTextColor(getResources().getColor(R.color.app_white));
        ((TextView) findViewById(R.id.include_tv_right)).setText("发布");
        ((TextView) findViewById(R.id.include_tv_right)).setTextColor(getResources().getColor(R.color.app_white));
        show_dis = (TextView) findViewById(R.id.show_dis);
        sort_park_tv = (TextView) findViewById(R.id.sort_park_tv);
        allPlotsCarport = (PullRefreshList) findViewById(R.id.allPlotsCarport);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        findViewById(R.id.include_tv_right).setOnClickListener(this);
        findViewById(R.id.sort_city_rl).setOnClickListener(this);
        findViewById(R.id.sort_park_rl).setOnClickListener(this);

        searchplots = new ArrayList<Searchplotbean>();

        show_dis.setText(city_name);
        select_condition = new ArrayList<String>();
        select_condition.add("车位最多");
        select_condition.add("离我最近");
        select_condition.add("离我最远");
        allPlotsCarport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int headercount = allPlotsCarport.getHeaderViewsCount();
                String uid;
                uid = plots.get(position - headercount).getUid();
                Map<String, String> param = new HashMap<String, String>();
                param.put("ps", 10 + "");
                param.put("pn", 0 + "");
                param.put("villageid", uid);
                param.put("otype", "1");
                OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String result) {
                        plots_carport = new ArrayList<AllReleaseTools>();
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (result.equals(null) || result.equals("")) {
                                return;
                            }
//                            String count = (String) jsonObject.get("count");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (jsonArray.length()!=0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    AllReleaseTools allReleaseTools = new AllReleaseTools();
                                    JSONObject js = jsonArray.getJSONObject(i);
                                    allReleaseTools.setUser_id(js.getString("user_id"));
                                    allReleaseTools.setName(js.getString("name"));
                                    allReleaseTools.setDtype(js.getString("dtype"));
                                    allReleaseTools.setLongitude(js.getString("longitude"));
                                    allReleaseTools.setLatitude(js.getString("latitude"));
                                    allReleaseTools.setVillage(js.getString("village"));
                                    allReleaseTools.setVillageid(js.getString("villageid"));
                                    allReleaseTools.setAddress(js.getString("address"));
                                    allReleaseTools.setCharge_number(js.getString("charge_number"));
                                    allReleaseTools.setAdd_time(js.getString("add_time"));
                                    allReleaseTools.setStatus(js.getString("status"));
                                    allReleaseTools.setGround(js.getString("ground"));
                                    allReleaseTools.setDuration_start(js.getString("duration_start"));
                                    allReleaseTools.setDuration_end(js.getString("duration_end"));
                                    allReleaseTools.setAvatar(js.getString("avatar"));
                                    allReleaseTools.setLinkman(js.getString("linkman"));
                                    allReleaseTools.setContactway(js.getString("contactway"));
                                    allReleaseTools.setParking_num(js.getString("parking_num"));
                                    plots_carport.add(allReleaseTools);
                                }
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) plots_carport);
                                ActivityTools.switchActivity(context, ShowPoltCarport.class, bundle);
                            } else {
                                m_hNotify.sendEmptyMessage(LOAD_MORE_FAILED);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, param, "personalDeportsByCode");

            }
        });

        allPlotsCarport.setOnHeaderRefreshListener(new PullRefreshList.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh() {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            m_hNotify.sendEmptyMessage(LOAD_MORE_DATA_HEADER);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        allPlotsCarport.setOnFooterRefreshListener(new PullRefreshList.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh() {
                m_iPageNum++;
                isflush=true;
                initData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.include_tv_right:
                ActivityTools.switchActivity(context, PublishParkInfoActivity.class, null);
                break;
            case R.id.sort_city_rl:
                witchBtnPress = true;
                dis = new ArrayList<String>();
                if (city_name != null) {
                    dis = mySqliteManager.select_dis_list(sp.getString("city_name", null), city_code + "");

                } else {
                    QParkingApp.ToastTip(context, "暂未定位到您的信息,请先定位后再试", -100);
                    return;
                }
                sortCityPop = new SortCityPop(context, dis, onItemClickListener);
                sortCityPop.showAsDropDown(v);
                break;
            case R.id.sort_park_rl:
                witchBtnPress = false;
                sortPartPop = new SortPartPop(context, select_condition, onItemClickListener);
                sortPartPop.showAsDropDown(v);
                break;
        }
    }

    public AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if (witchBtnPress) {
                show_dis.setText(dis.get(position).toString());
                sortCityPop.dismiss();
            } else {
                sort_park_tv.setText(select_condition.get(position).toString());
                sortPartPop.dismiss();
            }
        }
    };

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            QParkingApp.ToastTip(context, "未找到相关信息！", -100);
            return;
        }
        int count = poiResult.getAllPoi().size();
        Searchplotbean searchplotbean = null;
        if (count != 1) {
            for (int j = 0; j < count; j++) {
                searchplotbean=new Searchplotbean();
                PoiInfo info = poiResult.getAllPoi().get(j);
                searchplotbean.setUid(info.uid);
                searchplotbean.setAddress(info.address);
                searchplotbean.setLatLng(info.location);
                searchplots.add(searchplotbean);
            }
        } else {
            searchplotbean=new Searchplotbean();
            PoiInfo infos = (PoiInfo) poiResult.getAllPoi();
            searchplotbean.setUid(infos.uid);
            searchplotbean.setAddress(infos.address);
            searchplotbean.setLatLng(infos.location);
            searchplots.add(searchplotbean);
        }
        n++;
        if(isshow) {
            if (n == length-1) {
                m_hNotify.sendEmptyMessage(LOAD_MESSAGE);
                isshow=false;
                n=0;
            }
        }
        if(isflush){
            if(n==length){
                m_hNotify.sendEmptyMessage(LOAD_MORE_DATA_FOOTER);
                n=0;
                isflush=false;
            }
        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    private Handler m_hNotify = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_MORE_DATA_HEADER:
                    allPlotsCarport.onHeaderRefreshComplete();
                    break;
                case LOAD_MORE_DATA_FOOTER:
                    showPlots();
                    plotsReleaseCarPortAdapter.notifyDataSetChanged();
                    allPlotsCarport.onFooterRefreshComplete();
                    Log.e("map", plots.toString());
                    break;
                case LOAD_MORE_FAILED:
                    allPlotsCarport.onFooterRefreshComplete();
                    if (plots.size() > 0) {
                        plotsReleaseCarPortAdapter.notifyDataSetChanged();
                        allPlotsCarport.onFooterRefreshComplete();
                    }
                    break;
                case LOAD_MESSAGE:
                    showPlots();
                    plotsReleaseCarPortAdapter = new PlotsReleaseCarPortAdapter(context, plots);
                    allPlotsCarport.setAdapter(plotsReleaseCarPortAdapter);
                    searchplots.clear();
                    plotbean.clear();
                    m_dlgProgress.dismiss();
                    break;
                default:
                    break;
            }
        }

    };

    private void showPlots() {
        for(int i=0;i<plotbean.size();i++){
            String id=plotbean.get(i).getUid();
            for(int j=0;j<searchplots.size();j++){
                String oid=searchplots.get(j).getUid();
                if(id.equals(oid)){
                    SelectPlotId select = new SelectPlotId();
                    select.setUid(id);
                    select.setName(plotbean.get(i).getName());
                    select.setAdd(searchplots.get(j).getAddress());
                    select.setFreenum(plotbean.get(i).getCarport());
                    double latf = appQParking.m_llMe.latitude;
                    double lonf = appQParking.m_llMe.longitude;
                    double latt=searchplots.get(j).getLatLng().latitude;
                    double lont=searchplots.get(j).getLatLng().longitude;
                    LatLng from = new LatLng(latf, lonf);
                    LatLng to = new LatLng(latt, lont);
                    distance = DistanceUtil.getDistance(from, to);
                    select.setDistance(String.valueOf(distance));
                    plots.add(select);
                }
            }
        }
    }
}
