package funion.app.qparking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import funion.app.adapter.NearParkingAdapter;
import funion.app.common.T;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.view.PullRefreshList;
import funion.app.qparking.view.PullRefreshList.OnFooterRefreshListener;
import funion.app.qparking.view.PullRefreshList.OnHeaderRefreshListener;
import funion.app.qparking.vo.MyServiceParkingInfo;
import funion.app.qparking.vo.TagParkingItem1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：NearParkingActivity
 * @时间：2015年5月21日上午10:01:10
 * @要做的事情：TODO 附近的停车场列表功能实现类
 */
//, OnGetPoiSearchResultListener
public class NearParkingActivity extends Activity implements OnClickListener, OnGetPoiSearchResultListener {
    private PullToRefreshListView pullToRefreshListView;
    private PoiSearch m_poiSearch = null;
    private int num = 0;
    private Context context = NearParkingActivity.this;
    private ArrayList<TagParkingItem1> tagParkingItem1ArrayList = new ArrayList<>();
    private ArrayList<MyServiceParkingInfo> myServiceParkingInfoList = new ArrayList<>();
    private NearParkingAdapter adapter;
    private QParkingApp appQParking;
    private long lastUp;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_parking_activity);
        appQParking = (QParkingApp) getApplicationContext();
        initDate();
        initTitle();
        searchNearBy();
        initView();
    }

    private void searchNearBy() {
        PoiNearbySearchOption optionNearBy = new PoiNearbySearchOption();
        optionNearBy.keyword("停车场");
        optionNearBy.pageNum(num);
        optionNearBy.pageCapacity(10);
        optionNearBy.radius(10000);
        optionNearBy.location(appQParking.m_llMe);
        m_poiSearch.searchNearby(optionNearBy);
    }

    private void initDate() {
        // POI搜索
        m_poiSearch = PoiSearch.newInstance();
        m_poiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initView() {
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.near_parking_info_lv);
        ILoadingLayout bottomLayout = pullToRefreshListView
                .getLoadingLayoutProxy(false, true);
        bottomLayout.setPullLabel("上拉加载下10条数据");
        ILoadingLayout frontLayout = pullToRefreshListView
                .getLoadingLayoutProxy(true, false);
        frontLayout.setPullLabel("下拉刷新");
        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                             LinearLayout ly = (LinearLayout) view.findViewById(R.id.is_display_ly);
                                                             if (ly.getVisibility() == View.GONE) {
                                                                 ly.setVisibility(View.VISIBLE);
                                                             } else {
                                                                 ly.setVisibility(View.GONE);
                                                             }
                                                         }
                                                     }


        );
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                ILoadingLayout frontLayout = pullToRefreshListView
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
                tagParkingItem1ArrayList.clear();
                searchNearBy();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                num++;
                searchNearBy();
            }
        });
    }

    private void initTitle() {
        ((TextView) findViewById(R.id.include_tv_title)).setText("附近停车场");
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.top_back_btn);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            QParkingApp.ToastTip(context, "未找到停车场！", -100);
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            for (int i = 0; i < result.getAllPoi().size(); i++) {
                TagParkingItem1 tagParkingItem1 = new TagParkingItem1();
                PoiInfo infoPoi = result.getAllPoi().get(i);
                tagParkingItem1.setM_strPid(infoPoi.uid);
                tagParkingItem1.setM_strName(infoPoi.name);
                tagParkingItem1.setM_llParking(infoPoi.location);
                tagParkingItem1.setM_strAddress(infoPoi.address);
                tagParkingItem1.setM_strShareName("百度");
                tagParkingItem1.setM_iDistance(((int)
                        DistanceUtil.getDistance(appQParking.m_llMe, tagParkingItem1.getM_llParking())));
                tagParkingItem1.setM_iFreeNum(0);
                tagParkingItem1.setM_iChargeNum(0);
                tagParkingItem1.setM_iPraiseNum(0);
                tagParkingItem1.setM_iDespiseNum(0);
                tagParkingItem1ArrayList.add(tagParkingItem1);
            }
            connectNetWord(tagParkingItem1ArrayList.get(0).getM_llParking().latitude, tagParkingItem1ArrayList.get(0).getM_llParking().longitude);
        }

    }

    private void connectNetWord(double latitude, double longitude) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("raidus", 5000 + "");
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
            }
        }, param, "depot");
    }

    private void updateResult(ArrayList<MyServiceParkingInfo> list, ArrayList<TagParkingItem1> tagList) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < tagList.size(); j++) {
                if (list.get(i).getMarker_id().equals(tagList.get(j).getM_strPid())) {
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
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMarker_id().equals("")) {
                TagParkingItem1 info = new TagParkingItem1();
                info.setM_strName(list.get(i).getName());
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
        adapter = new NearParkingAdapter(context, tagList);
        pullToRefreshListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        pullToRefreshListView.onRefreshComplete();
    }


    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {

    }
    //	private static final int LOAD_MORE_DATA_HEADER = 0;
//	private static final int LOAD_MORE_DATA_FOOTER = 1;
//	private static final int LOAD_MORE_FAILED = 2;
//
//	final String POI_SEARCH_KEYWORD = "停车场";
//	final int POI_SEARCH_PAGE_CAPACITY = 10;
//	final int POI_SEARCH_RADIUS = 5000;
//
//	private View m_viewGreenBar = null;
//	private PullRefreshList m_lvInfoList;
//	private InfoListAdapter m_adapterInfoList;
//	private int m_iTypeNameID;
//	private PoiSearch m_poiSearch = null;
//	private TextView tvTemp; // 链接显示类型
//
//	// 停车场信息列表
//	List<TagParkingItem> m_listAdd = new ArrayList<TagParkingItem>();
//	int m_iPageNum = 1;
//
//	private Handler m_hNotify = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case LOAD_MORE_DATA_HEADER:
//				m_lvInfoList.onHeaderRefreshComplete();
//				break;
//			case LOAD_MORE_DATA_FOOTER: {
//				m_adapterInfoList.AddItemList(m_listAdd);
//				m_adapterInfoList.notifyDataSetChanged();
//				m_lvInfoList.onFooterRefreshComplete();
//
//				m_iPageNum++;
//			}
//				break;
//			case LOAD_MORE_FAILED: {
//				m_lvInfoList.onFooterRefreshComplete();
//				if (m_listAdd.size() > 0) {
//					m_adapterInfoList.AddItemList(m_listAdd);
//					m_adapterInfoList.notifyDataSetChanged();
//					m_lvInfoList.onFooterRefreshComplete();
//
//					m_iPageNum++;
//				}
//				// QParkingApp.ToastTip(NearParkingActivity.this,
//				// msg.peekData().getString("info"), -100);
//			}
//			default:
//				break;
//			}
//		}
//
//	};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.near_parking_activity);
//
//		// 获取应用程序对象
//		QParkingApp appQParking = (QParkingApp) getApplicationContext();
//
//		m_iTypeNameID = R.id.tvNearShowAll;
//		// 返回按钮
//		Button btBack = (Button) findViewById(R.id.btNearBack);
//		btBack.setOnClickListener(this);
//
//		tvTemp = (TextView) findViewById(R.id.tvNearShowAll);
//		tvTemp.setOnClickListener(this);
//		tvTemp = (TextView) findViewById(R.id.tvNearShowFree);
//		tvTemp.setOnClickListener(this);
//		tvTemp = (TextView) findViewById(R.id.tvNearShowPay);
//		tvTemp.setOnClickListener(this);
//
//		m_viewGreenBar = findViewById(R.id.viewTypeGreenBar);
//		// 调整绿条长度
//		RelativeLayout.LayoutParams rllpGreenBar = (RelativeLayout.LayoutParams) m_viewGreenBar.getLayoutParams();
//		rllpGreenBar.width = getWindowManager().getDefaultDisplay().getWidth() / 3;
//		m_viewGreenBar.setLayoutParams(rllpGreenBar);
//
//		// 设置停车场列表
//		m_lvInfoList = (PullRefreshList) this.findViewById(R.id.lvParking);
//
//		// 实例化自定义适配器
//		m_adapterInfoList = new InfoListAdapter(this, appQParking.m_listParking);
//		m_lvInfoList.setAdapter(m_adapterInfoList);
//		// 连接列表消息
//		m_lvInfoList.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//				// 获取应用程序对象
//				QParkingApp appQParking = (QParkingApp) getApplicationContext();
//				// 记录目标停车场索引
//				appQParking.m_itemParking = m_adapterInfoList.getItem(position);
//
//				// 界面跳转
//				Intent intentNewActivity = new Intent();
//				intentNewActivity.setClass(NearParkingActivity.this, NavigationActivity.class);
//				startActivity(intentNewActivity);
//				overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
//			}
//		});
//		m_lvInfoList.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
//			@Override
//			public void onHeaderRefresh() {
//				// TODO Auto-generated method stub
//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						try {
//							Thread.sleep(2000);
//							m_hNotify.sendEmptyMessage(LOAD_MORE_DATA_HEADER);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//				}).start();
//			}
//		});
//		m_lvInfoList.setOnFooterRefreshListener(new OnFooterRefreshListener() {
//
//			@Override
//			public void onFooterRefresh() {
//				// TODO Auto-generated method stub
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						QParkingApp appQParking = (QParkingApp) getApplicationContext();
//						// 停车场搜索
//						PoiNearbySearchOption optionNearBy = new PoiNearbySearchOption();
//
//						optionNearBy.keyword(POI_SEARCH_KEYWORD);
//						optionNearBy.pageNum(m_iPageNum);
//						optionNearBy.pageCapacity(POI_SEARCH_PAGE_CAPACITY);
//						optionNearBy.radius(POI_SEARCH_RADIUS);
//						optionNearBy.location(appQParking.m_llMe);
//
//						m_poiSearch.searchNearby(optionNearBy);
//					}
//				}).start();
//
//			}
//		});
//		// POI搜索
//		m_poiSearch = PoiSearch.newInstance();
//		m_poiSearch.setOnGetPoiSearchResultListener(this);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		MobclickAgent.onResume(this);
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		MobclickAgent.onPause(this);
//	}
//
//	@Override
//	public void onClick(View v) {
//		if (m_iTypeNameID == v.getId())
//			return;
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.btNearBack:
//			finish();
//			overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
//			break;
//		case R.id.tvNearShowAll:
//		case R.id.tvNearShowFree:
//		case R.id.tvNearShowPay: {
//			int iStartPos = 0, iEndPos = 0;
//			int iScreenWith = getWindowManager().getDefaultDisplay().getWidth();
//			TextView tvTypeName;
//			switch (m_iTypeNameID) {
//			case R.id.tvNearShowAll:
//				iStartPos = 0;
//				break;
//			case R.id.tvNearShowFree:
//				iStartPos = iScreenWith / 3;
//				break;
//			case R.id.tvNearShowPay:
//				iStartPos = iScreenWith * 2 / 3;
//				break;
//			}
//			// 复位原始设置
//			tvTypeName = (TextView) findViewById(m_iTypeNameID);
//
//			tvTypeName.setTextColor(0xFF4d4d4d);
//			// 设置当前项
//			m_iTypeNameID = v.getId();
//			switch (m_iTypeNameID) {
//			case R.id.tvNearShowAll: {
//				m_iTypeNameID = R.id.tvNearShowAll;
//
//				m_adapterInfoList.SetShowType(0);
//				m_adapterInfoList.notifyDataSetChanged();
//				iEndPos = 0;
//			}
//				break;
//			case R.id.tvNearShowFree: {
//				m_iTypeNameID = R.id.tvNearShowFree;
//
//				m_adapterInfoList.SetShowType(1);
//				m_adapterInfoList.notifyDataSetChanged();
//				iEndPos = iScreenWith / 3;
//			}
//				break;
//			case R.id.tvNearShowPay: {
//				m_iTypeNameID = R.id.tvNearShowPay;
//
//				m_adapterInfoList.SetShowType(2);
//				m_adapterInfoList.notifyDataSetChanged();
//				iEndPos = iScreenWith * 2 / 3;
//			}
//				break;
//			}
//			tvTypeName = (TextView) findViewById(m_iTypeNameID);
//
//			tvTypeName.setTextColor(0xFF00c1a0);
//
//			TranslateAnimation taMoveGreenBar = new TranslateAnimation(iStartPos, iEndPos, 0, 0);
//			taMoveGreenBar.setDuration(30);
//			taMoveGreenBar.setFillAfter(true);
//			m_viewGreenBar.startAnimation(taMoveGreenBar);
//		}
//		}
//	}
//
//	// 列表适配器
//	class InfoListAdapter extends BaseAdapter {
//		private int m_iShowType;
//		private List<TagParkingItem> m_listItemAll;
//		private List<TagParkingItem> m_listItemFree = new ArrayList<TagParkingItem>();
//		private List<TagParkingItem> m_listItemCharge = new ArrayList<TagParkingItem>();
//		private LayoutInflater m_layoutInflater;
//
//		public InfoListAdapter(Context context, List<TagParkingItem> list) {
//			m_iShowType = 0;
//			m_listItemAll = list;
//			m_layoutInflater = LayoutInflater.from(context);
//			for (int i = 0; i < m_listItemAll.size(); i++) {
//				TagParkingItem itemInfo = m_listItemAll.get(i);
//				if (itemInfo.m_iFreeNum > 0)
//					m_listItemFree.add(itemInfo);
//				else
//					m_listItemCharge.add(itemInfo);
//			}
//		}
//
//		public void SetShowType(int iType) {
//			m_iShowType = iType;
//		}
//
//		public void AddItemList(List<TagParkingItem> list) {
//			for (int i = 0; i < list.size(); i++) {
//				TagParkingItem itemInfo = list.get(i);
//				m_listItemAll.add(itemInfo);
//				if (itemInfo.m_iFreeNum > 0)
//					m_listItemFree.add(itemInfo);
//				else
//					m_listItemCharge.add(itemInfo);
//			}
//		}
//
//		@Override
//		public int getCount() {
//			switch (m_iShowType) {
//			case 0:
//				return m_listItemAll.size();
//			case 1:
//				return m_listItemFree.size();
//			case 2:
//				return m_listItemCharge.size();
//			}
//			return 0;
//		}
//
//		@Override
//		public TagParkingItem getItem(int position) {
//			switch (m_iShowType) {
//			case 0:
//				return m_listItemAll.get(position - 1);
//			case 1:
//				return m_listItemFree.get(position - 1);
//			case 2:
//				return m_listItemCharge.get(position - 1);
//			}
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position - 1;
//		}
//
//		// 控制ITEM 布局内容
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			if (convertView == null)
//				convertView = m_layoutInflater.inflate(R.layout.parking_info_item, null); // 加载布局
//
//			TagParkingItem itemInfo = null;
//
//			switch (m_iShowType) {
//			case 0:
//				itemInfo = m_listItemAll.get(position);
//				break;
//			case 1:
//				itemInfo = m_listItemFree.get(position);
//				break;
//			case 2:
//				itemInfo = m_listItemCharge.get(position);
//				break;
//			}
//			// 设置索引底图
//			ImageView ivIndexBack = (ImageView) convertView.findViewById(R.id.ivIndexBack);
//			if (itemInfo.m_iFreeNum > 0)
//				ivIndexBack.setImageResource(R.drawable.point_green);
//			else
//				ivIndexBack.setImageResource(R.drawable.point_blue);
//			// 设置索引号
//			TextView tvIndex = (TextView) convertView.findViewById(R.id.tvInfoIndex);
//			tvIndex.setText(String.format("%d", position + 1));
//			// 设置停车场名
//			TextView tvName = (TextView) convertView.findViewById(R.id.tvInfoParkName);
//			tvName.setText(itemInfo.m_strName);
//			if (itemInfo.m_strName.length() > 9) {
//				int iScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
//				tvName.setWidth(iScreenWidth / 2);
//			}
//			// 设置停车场地址
//			TextView tvAddress = (TextView) convertView.findViewById(R.id.tvInfoAddress);
//			tvAddress.setText(itemInfo.m_strAddress);
//			// 设置地下停车场地标识
//			ImageView ivUnderTag = (ImageView) convertView.findViewById(R.id.ivTagUnderground);
//			if (itemInfo.m_iLocationType == 1)
//				ivUnderTag.setVisibility(0);
//			else
//				ivUnderTag.setVisibility(4);
//			// 设置距离
//			TextView tvDistance = (TextView) convertView.findViewById(R.id.tvInfoDistance);
//			tvDistance.setText(String.format("%d米", itemInfo.m_iDistance));
//
//			return convertView;
//		}
//
//		@Override
//		public int getItemViewType(int position) {
//			return 1;
//		}
//
//		@Override
//		public int getViewTypeCount() {
//			switch (m_iShowType) {
//			case 0:
//				return m_listItemAll.size();
//			case 1:
//				return m_listItemFree.size();
//			case 2:
//				return m_listItemCharge.size();
//			}
//			return 0;
//		}
//
//		@Override
//		public boolean hasStableIds() {
//			return true;
//		}
//
//		// 是否Item监听
//		@Override
//		public boolean isEmpty() {
//			return true;
//		}
//
//		// true所有项目可选择可点击
//		@Override
//		public boolean areAllItemsEnabled() {
//			return true;
//		}
//
//		// 是否显示分割线
//		@Override
//		public boolean isEnabled(int position) {
//			return true;
//		}
//	}
//
//	@Override
//	public void onGetPoiDetailResult(PoiDetailResult arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onGetPoiResult(PoiResult result) {
//		// TODO Auto-generated method stub
//		// TODO GetPoiResult
//		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
//			QParkingApp.ToastTip(NearParkingActivity.this, "未找到停车场！", -100);
//			m_lvInfoList.onFooterRefreshComplete();
//			return;
//		}
//		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//			// 获取应用程序对象
//			QParkingApp appQParking = (QParkingApp) getApplicationContext();
//
//			m_listAdd.clear();
//			int iCount = result.getAllPoi().size();
//			for (int i = 0; i < iCount; i++) {
//				TagParkingItem itemInfo = new TagParkingItem();
//				PoiInfo infoPoi = result.getAllPoi().get(i);
//
//				itemInfo.m_strPid = infoPoi.uid;
//				itemInfo.m_strName = infoPoi.name;
//				itemInfo.m_llParking = infoPoi.location;
//				itemInfo.m_strAddress = infoPoi.address;
//				itemInfo.m_strShareName = "百度";
//				itemInfo.m_iDistance = (int) DistanceUtil.getDistance(appQParking.m_llMe, itemInfo.m_llParking);
//
//				itemInfo.m_iFreeNum = 0;
//				itemInfo.m_iChargeNum = 0;
//				itemInfo.m_iPraiseNum = 0;
//				itemInfo.m_iDespiseNum = 0;
//
//				m_listAdd.add(itemInfo);
//			}
//			// 调用接口线程
//			Thread interfaceThread = new Thread(new Runnable() {
//				public void run() {
//					try {
//						String strParams = "pid=";
//						for (int i = 0; i < m_listAdd.size(); i++) {
//							String strAdded;
//							TagParkingItem itemInfo = m_listAdd.get(i);
//							if (i == 0)
//								strAdded = String.format("%s", URLEncoder.encode(itemInfo.m_strPid));
//							else
//								strAdded = String.format(",%s", URLEncoder.encode(itemInfo.m_strPid));
//
//							strParams += strAdded;
//						}
//						URL urlInterface = new URL("http://app.qutingche.cn/Mobile/DepotFee/getTrapeze");
//						HttpURLConnection hucInterface = (HttpURLConnection) urlInterface.openConnection();
//
//						hucInterface.setDoInput(true);
//						hucInterface.setDoOutput(true);
//						hucInterface.setUseCaches(false);
//
//						hucInterface.setRequestMethod("POST");
//						// 设置DataOutputStream
//						DataOutputStream dosInterface = new DataOutputStream(hucInterface.getOutputStream());
//
//						dosInterface.writeBytes(strParams);
//						dosInterface.flush();
//						dosInterface.close();
//
//						BufferedReader readerBuff = new BufferedReader(new InputStreamReader(
//								hucInterface.getInputStream()));
//						String strData = readerBuff.readLine();
//
//						JSONObject jsonRet = new JSONObject(strData);
//						if (jsonRet.getInt("status") == 1) {
//							JSONArray jsonInfos = jsonRet.getJSONArray("data");
//							// 获取用户信息
//							for (int i = 0; i < jsonInfos.length(); i++) {
//								JSONObject jsonData = jsonInfos.getJSONObject(i);
//								for (int j = 0; j < m_listAdd.size(); j++) {
//									TagParkingItem itemInfo = m_listAdd.get(j);
//									if (itemInfo.m_strPid.equals(jsonData.getString("pid"))) {
//										itemInfo.m_iFreeNum = jsonData.getInt("free_number");
//										itemInfo.m_iChargeNum = jsonData.getInt("charge_number");
//										itemInfo.m_iPraiseNum = jsonData.getInt("praise_number");
//										itemInfo.m_iDespiseNum = jsonData.getInt("despise_number");
//									}
//								}
//							}
//							// 调用接口线程
//							Thread interfaceThread = new Thread(new Runnable() {
//								public void run() {
//									try {
//										// 获取应用程序对象
//										QParkingApp appQParking = (QParkingApp) getApplicationContext();
//										String strParams = String.format(
//												"latitude=%f&longitude=%f&distance=%d&pageCapacity=%d&pageIndex=%d",
//												appQParking.m_llMe.latitude, appQParking.m_llMe.longitude,
//												POI_SEARCH_RADIUS, POI_SEARCH_PAGE_CAPACITY, m_iPageNum);
//										URL urlInterface = new URL("http://app.qutingche.cn/Mobile/Depot/getAround");
//										HttpURLConnection hucInterface = (HttpURLConnection) urlInterface
//												.openConnection();
//
//										hucInterface.setDoInput(true);
//										hucInterface.setDoOutput(true);
//										hucInterface.setUseCaches(false);
//
//										hucInterface.setRequestMethod("POST");
//										// 设置DataOutputStream
//										DataOutputStream dosInterface = new DataOutputStream(hucInterface
//												.getOutputStream());
//
//										dosInterface.writeBytes(strParams);
//										dosInterface.flush();
//										dosInterface.close();
//
//										BufferedReader readerBuff = new BufferedReader(new InputStreamReader(
//												hucInterface.getInputStream()));
//										String strData = readerBuff.readLine();
//
//										JSONObject jsonRet = new JSONObject(strData);
//										if (jsonRet.getInt("status") == 1) {
//											JSONArray jsonInfos = jsonRet.getJSONArray("data");
//											// 获取用户信息
//											for (int i = 0; i < jsonInfos.length(); i++) {
//												JSONObject jsonData = jsonInfos.getJSONObject(i);
//
//												TagParkingItem itemInfo = new TagParkingItem();
//
//												itemInfo.m_strPid = jsonData.getString("id");
//												itemInfo.m_strName = jsonData.getString("name");
//												itemInfo.m_llParking = new LatLng(jsonData.getDouble("latitude"),
//														jsonData.getDouble("longitude"));
//												itemInfo.m_strAddress = jsonData.getString("address");
//												itemInfo.m_strShareName = jsonData.getString("username");
//												itemInfo.m_iDistance = jsonData.getInt("distance");
//
//												itemInfo.m_iLocationType = jsonData.getInt("location_type");
//												itemInfo.m_iFreeNum = jsonData.getInt("free_number");
//												itemInfo.m_iChargeNum = jsonData.getInt("charge_number");
//												itemInfo.m_iPraiseNum = jsonData.getInt("praise_number");
//												itemInfo.m_iDespiseNum = jsonData.getInt("despise_number");
//
//												m_listAdd.add(itemInfo);
//											}
//
//											m_hNotify.sendEmptyMessage(LOAD_MORE_DATA_FOOTER);
//										} else {
//											Message msg = new Message();
//											Bundle bundleData = new Bundle();
//
//											bundleData.putString("info", jsonRet.getString("info"));
//											msg.setData(bundleData);
//
//											msg.what = LOAD_MORE_FAILED;
//											m_hNotify.sendMessage(msg);
//										}
//
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//							});
//							interfaceThread.start();
//						} else {
//							Message msg = new Message();
//							Bundle bundleData = new Bundle();
//
//							bundleData.putString("info", jsonRet.getString("info"));
//							msg.setData(bundleData);
//
//							msg.what = LOAD_MORE_FAILED;
//							m_hNotify.sendMessage(msg);
//						}
//
//					} catch (Exception e) {
//					}
//				}
//			});
//			interfaceThread.start();
//
//			return;
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		m_poiSearch.destroy();
//
//		super.onDestroy();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
//	 * 2015年5月21日上午10:09:06 NearParkingActivity.java TODO
//	 */
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			finish();
//			overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
//		}
//		return super.onKeyUp(keyCode, event);
//	}

}
