package funion.app.qparking;
/**
 * 搜索页面
 * Created by yunze on 2016/1/4.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;

import funion.app.adapter.SearchHistoryAdapter;
import funion.app.adapter.SearchResultAdapter;
import funion.app.common.T;
import funion.app.qparking.vo.TagParkingItem1;

public class NearSearchActivity extends Activity implements View.OnClickListener, OnGetPoiSearchResultListener {
    private Context context = NearSearchActivity.this;
    private EditText input;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Set<String> set = new HashSet<>();
    private PullToRefreshListView mPullToRefreshListView;
    private PoiSearch m_poiSearch = null;
    private int num = 0;
    private final static String STATION = "加油站";
    private final static String HOTEL = "酒店";
    private final static String MARKET = "商场";
    private final static String TRAVEL = "景点";
    private ArrayList<TagParkingItem1> list = new ArrayList<>();
    private QParkingApp appQParking;
    private SearchResultAdapter adapter;
    private SearchHistoryAdapter mAdapter;
    private ListView listView;
    private long lastUp;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_search);
        sp = getSharedPreferences("searchResult", 0);
        editor = sp.edit();
        if (sp.getStringSet("searchSet", null) != null) {
            set.addAll(sp.getStringSet("searchSet", null));
        }
        initDate();
        initView();
        setListener();

    }

    private void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String result = ((TextView) view.findViewById(R.id.history_tv)).getText().toString();
                openPullToRefreshListView();
                input.setText(result);
                searchType(input.getText().toString().trim(), 0);
            }
        });
    }

    private void initDate() {
        appQParking = (QParkingApp) getApplicationContext();
        // POI搜索
        m_poiSearch = PoiSearch.newInstance();
        m_poiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initView() {
        input = (EditText) findViewById(R.id.input_et);
        listView = (ListView) findViewById(R.id.listview_search);
        mAdapter = new SearchHistoryAdapter(context, set);
        listView.setAdapter(mAdapter);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_listview);
        ILoadingLayout bottomLayout = mPullToRefreshListView
                .getLoadingLayoutProxy(false, true);
        bottomLayout.setPullLabel("上拉加载下10条数据");
        ILoadingLayout frontLayout = mPullToRefreshListView
                .getLoadingLayoutProxy(true, false);
        frontLayout.setPullLabel("下拉刷新");
        mPullToRefreshListView.setVisibility(View.GONE);
        findViewById(R.id.station_tv).setOnClickListener(this);
        findViewById(R.id.hotel_tv).setOnClickListener(this);
        findViewById(R.id.market_tv).setOnClickListener(this);
        findViewById(R.id.travel_tv).setOnClickListener(this);
        findViewById(R.id.clear_tv).setOnClickListener(this);
        findViewById(R.id.come_back_iv).setOnClickListener(this);
        findViewById(R.id.search_tv).setOnClickListener(this);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                ILoadingLayout frontLayout = mPullToRefreshListView
                        .getLoadingLayoutProxy(true, false);
                lastUp = System.currentTimeMillis();
                String label = "上一次刷新时间："
                        + DateUtils.formatDateTime(context
                                .getApplicationContext(), lastUp,
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                frontLayout.setLastUpdatedLabel(label);
                num = 0;
                searchType(input.getText().toString().trim(), num);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                num++;
                searchType(input.getText().toString().trim(), num);

            }
        });
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                          @Override
                                                          public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                                              Intent intent = new Intent();
                                                              intent.putExtra("lat", list.get(i).getM_llParking().latitude);
                                                              intent.putExtra("lon", list.get(i).getM_llParking().longitude);
                                                              setResult(RESULT_OK, intent);
                                                              finish();
                                                          }
                                                      }

        );
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.come_back_iv:
                finish();
                break;
            case R.id.search_tv:
                if (TextUtils.isEmpty(input.getText().toString().trim())) {
                    return;
                }
                set.add(input.getText().toString().trim());
                editor.putStringSet("searchSet", set);
                editor.commit();
                openPullToRefreshListView();
                searchType(input.getText().toString().trim(), 0);
                break;
            case R.id.station_tv:
                openPullToRefreshListView();
                input.setText(STATION);
                searchType(input.getText().toString().trim(), 0);
                break;
            case R.id.hotel_tv:
                openPullToRefreshListView();
                input.setText(HOTEL);
                searchType(input.getText().toString().trim(), 0);
                break;
            case R.id.market_tv:
                openPullToRefreshListView();
                input.setText(MARKET);
                searchType(input.getText().toString().trim(), 0);
                break;
            case R.id.travel_tv:
                openPullToRefreshListView();
                input.setText(TRAVEL);
                searchType(input.getText().toString().trim(), 0);
                break;
            case R.id.clear_tv:
                set.clear();
                editor.putStringSet("searchSet", set);
                editor.commit();
                listView.setVisibility(View.GONE);
//                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void searchType(String name, int number) {
        if (getIntent().getStringExtra("cityName").equals("")) {
            return;
        }
        if (number == 0) {
            list.clear();
        }
        PoiNearbySearchOption optionNearBy = new PoiNearbySearchOption();
        optionNearBy.keyword(name);
        optionNearBy.keyword(input.getText().toString());
        optionNearBy.pageNum(number);
        optionNearBy.pageCapacity(10);
        optionNearBy.radius(10000);
        optionNearBy.location(appQParking.m_llMe);
        m_poiSearch.searchNearby(optionNearBy);
    }

    private void openPullToRefreshListView() {
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        findViewById(R.id.head_ly).setVisibility(View.GONE);
        findViewById(R.id.footer_ly).setVisibility(View.GONE);
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            QParkingApp.ToastTip(context, "未找到相关搜索条件的结果！", -100);
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            int iCount = result.getAllPoi().size();
            for (int i = 0; i < iCount; i++) {
                TagParkingItem1 tagParkingItem1 = new TagParkingItem1();
                PoiInfo infoPoi = result.getAllPoi().get(i);
                tagParkingItem1.setM_strName(infoPoi.name);
                tagParkingItem1.setM_llParking(infoPoi.location);
                tagParkingItem1.setM_strAddress(infoPoi.address);
                tagParkingItem1.setM_iDistance(((int)
                        DistanceUtil.getDistance(appQParking.m_llMe, tagParkingItem1.getM_llParking())));
                list.add(tagParkingItem1);
            }
            adapter = new SearchResultAdapter(context, list);
            mPullToRefreshListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            mPullToRefreshListView.onRefreshComplete();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }
}
