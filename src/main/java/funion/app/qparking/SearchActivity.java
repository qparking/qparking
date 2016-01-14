package funion.app.qparking;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.umeng.analytics.MobclickAgent;

import funion.app.qparking.tools.JsonParser;
import funion.app.qparking.view.PullRefreshList;
import funion.app.qparking.view.PullRefreshList.OnFooterRefreshListener;
import funion.app.qparking.view.PullRefreshList.OnHeaderRefreshListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：tagSearchItem
 * @时间：2015上午11:23:55
 */
// 停车场信息节点
class tagSearchItem {
	public String m_strName;
	public LatLng m_llPos;
	public String m_strAddress;
	public int m_iDistance;
};

/**
 * 
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：SearchActivity
 * @时间：2015上午11:24:05
 */
public class SearchActivity extends Activity implements OnClickListener,
		OnGetPoiSearchResultListener {
	private static final int LOAD_MORE_DATA_HEADER = 0;
	private static final int LOAD_MORE_DATA_FOOTER = 1;
	private static final int LOAD_MORE_FAILED = 2;

	final int POI_SEARCH_PAGE_CAPACITY = 10;

	private EditText m_etSearchWord;
	private Spinner m_spinProvince;
	private Spinner m_spinCity;
	private Spinner m_spinCounty;

	private int[] city = { R.array.beijin_province_item,
			R.array.tianjin_province_item, R.array.heibei_province_item,
			R.array.shanxi1_province_item, R.array.neimenggu_province_item,
			R.array.liaoning_province_item, R.array.jilin_province_item,
			R.array.heilongjiang_province_item, R.array.shanghai_province_item,
			R.array.jiangsu_province_item, R.array.zhejiang_province_item,
			R.array.anhui_province_item, R.array.fujian_province_item,
			R.array.jiangxi_province_item, R.array.shandong_province_item,
			R.array.henan_province_item, R.array.hubei_province_item,
			R.array.hunan_province_item, R.array.guangdong_province_item,
			R.array.guangxi_province_item, R.array.hainan_province_item,
			R.array.chongqing_province_item, R.array.sichuan_province_item,
			R.array.guizhou_province_item, R.array.yunnan_province_item,
			R.array.xizang_province_item, R.array.shanxi2_province_item,
			R.array.gansu_province_item, R.array.qinghai_province_item,
			R.array.linxia_province_item, R.array.xinjiang_province_item,
			R.array.hongkong_province_item, R.array.aomen_province_item,
			R.array.taiwan_province_item };
	private int[] countyOfBeiJing = { R.array.beijin_city_item };
	private int[] countyOfTianJing = { R.array.tianjin_city_item };
	private int[] countyOfHeBei = { R.array.shijiazhuang_city_item,
			R.array.tangshan_city_item, R.array.qinghuangdao_city_item,
			R.array.handan_city_item, R.array.xingtai_city_item,
			R.array.baoding_city_item, R.array.zhangjiakou_city_item,
			R.array.chengde_city_item, R.array.cangzhou_city_item,
			R.array.langfang_city_item, R.array.hengshui_city_item };
	private int[] countyOfShanXi1 = { R.array.taiyuan_city_item,
			R.array.datong_city_item, R.array.yangquan_city_item,
			R.array.changzhi_city_item, R.array.jincheng_city_item,
			R.array.shuozhou_city_item, R.array.jinzhong_city_item,
			R.array.yuncheng_city_item, R.array.xinzhou_city_item,
			R.array.linfen_city_item, R.array.lvliang_city_item };
	private int[] countyOfNeiMengGu = { R.array.huhehaote_city_item,
			R.array.baotou_city_item, R.array.wuhai_city_item,
			R.array.chifeng_city_item, R.array.tongliao_city_item,
			R.array.eerduosi_city_item, R.array.hulunbeier_city_item,
			R.array.bayannaoer_city_item, R.array.wulanchabu_city_item,
			R.array.xinganmeng_city_item, R.array.xilinguolemeng_city_item,
			R.array.alashanmeng_city_item };
	private int[] countyOfLiaoNing = { R.array.shenyang_city_item,
			R.array.dalian_city_item, R.array.anshan_city_item,
			R.array.wushun_city_item, R.array.benxi_city_item,
			R.array.dandong_city_item, R.array.liaoning_jinzhou_city_item,
			R.array.yingkou_city_item, R.array.fuxin_city_item,
			R.array.liaoyang_city_item, R.array.panjin_city_item,
			R.array.tieling_city_item, R.array.zhaoyang_city_item,
			R.array.huludao_city_item };
	private int[] countyOfJiLin = { R.array.changchun_city_item,
			R.array.jilin_city_item, R.array.siping_city_item,
			R.array.liaoyuan_city_item, R.array.tonghua_city_item,
			R.array.baishan_city_item, R.array.songyuan_city_item,
			R.array.baicheng_city_item, R.array.yanbian_city_item };
	private int[] countyOfHeiLongJiang = { R.array.haerbing_city_item,
			R.array.qiqihaer_city_item, R.array.jixi_city_item,
			R.array.hegang_city_item, R.array.shuangyashan_city_item,
			R.array.daqing_city_item, R.array.heilongjiang_yichun_city_item,
			R.array.jiamusi_city_item, R.array.qitaihe_city_item,
			R.array.mudanjiang_city_item, R.array.heihe_city_item,
			R.array.suihua_city_item, R.array.daxinganling_city_item };
	private int[] countyOfShangHai = { R.array.shanghai_city_item };

	private int[] countyOfJiangSu = { R.array.nanjing_city_item,
			R.array.wuxi_city_item, R.array.xuzhou_city_item,
			R.array.changzhou_city_item, R.array.nanjing_suzhou_city_item,
			R.array.nantong_city_item, R.array.lianyungang_city_item,
			R.array.huaian_city_item, R.array.yancheng_city_item,
			R.array.yangzhou_city_item, R.array.zhenjiang_city_item,
			R.array.jiangsu_taizhou_city_item, R.array.suqian_city_item };
	private int[] countyOfZheJiang = { R.array.hangzhou_city_item,
			R.array.ningbo_city_item, R.array.wenzhou_city_item,
			R.array.jiaxing_city_item, R.array.huzhou_city_item,
			R.array.shaoxing_city_item, R.array.jinhua_city_item,
			R.array.quzhou_city_item, R.array.zhoushan_city_item,
			R.array.zejiang_huzhou_city_item, R.array.lishui_city_item };
	private int[] countyOfAnHui = { R.array.hefei_city_item,
			R.array.wuhu_city_item, R.array.bengbu_city_item,
			R.array.huainan_city_item, R.array.maanshan_city_item,
			R.array.huaibei_city_item, R.array.tongling_city_item,
			R.array.anqing_city_item, R.array.huangshan_city_item,
			R.array.chuzhou_city_item, R.array.fuyang_city_item,
			R.array.anhui_suzhou_city_item, R.array.chaohu_city_item,
			R.array.luan_city_item, R.array.haozhou_city_item,
			R.array.chizhou_city_item, R.array.xuancheng_city_item };
	private int[] countyOfFuJian = { R.array.huzhou_city_item,
			R.array.xiamen_city_item, R.array.putian_city_item,
			R.array.sanming_city_item, R.array.quanzhou_city_item,
			R.array.zhangzhou_city_item, R.array.nanp_city_item,
			R.array.longyan_city_item, R.array.ningde_city_item };
	private int[] countyOfJiangXi = { R.array.nanchang_city_item,
			R.array.jingdezhen_city_item, R.array.pingxiang_city_item,
			R.array.jiujiang_city_item, R.array.xinyu_city_item,
			R.array.yingtan_city_item, R.array.ganzhou_city_item,
			R.array.jian_city_item, R.array.jiangxi_yichun_city_item,
			R.array.jiangxi_wuzhou_city_item, R.array.shangrao_city_item };
	private int[] countyOfShanDong = { R.array.jinan_city_item,
			R.array.qingdao_city_item, R.array.zaobo_city_item,
			R.array.zaozhuang_city_item, R.array.dongying_city_item,
			R.array.yantai_city_item, R.array.weifang_city_item,
			R.array.jining_city_item, R.array.taian_city_item,
			R.array.weihai_city_item, R.array.rizhao_city_item,
			R.array.laiwu_city_item, R.array.linxi_city_item,
			R.array.dezhou_city_item, R.array.liaocheng_city_item,
			R.array.shandong_bingzhou_city_item, R.array.heze_city_item };
	private int[] countyOfHeNan = { R.array.zhenshou_city_item,
			R.array.kaifang_city_item, R.array.luoyang_city_item,
			R.array.kaipingshan_city_item, R.array.anyang_city_item,
			R.array.hebi_city_item, R.array.xinxiang_city_item,
			R.array.jiaozuo_city_item, R.array.buyang_city_item,
			R.array.xuchang_city_item, R.array.leihe_city_item,
			R.array.sanmenxia_city_item, R.array.nanyang_city_item,
			R.array.shangqiu_city_item, R.array.xinyang_city_item,
			R.array.zhoukou_city_item, R.array.zhumadian_city_item };
	private int[] countyOfHuBei = { R.array.wuhan_city_item,
			R.array.huangshi_city_item, R.array.shiyan_city_item,
			R.array.yichang_city_item, R.array.xiangpan_city_item,
			R.array.erzhou_city_item, R.array.jinmen_city_item,
			R.array.xiaogan_city_item, R.array.hubei_jinzhou_city_item,
			R.array.huanggang_city_item, R.array.xianning_city_item,
			R.array.suizhou_city_item, R.array.enshi_city_item,
			R.array.shenglongjia_city_item };

	private int[] countyOfHuNan = { R.array.changsha_city_item,
			R.array.zhuzhou_city_item, R.array.xiangtan_city_item,
			R.array.hengyang_city_item, R.array.shaoyang_city_item,
			R.array.yueyang_city_item, R.array.changde_city_item,
			R.array.zhangjiajie_city_item, R.array.yiyang_city_item,
			R.array.hunan_bingzhou_city_item, R.array.yongzhou_city_item,
			R.array.huaihua_city_item, R.array.loudi_city_item,
			R.array.xiangxi_city_item };
	private int[] countyOfGuangDong = { R.array.guangzhou_city_item,
			R.array.shaoguan_city_item, R.array.shenzhen_city_item,
			R.array.zhuhai_city_item, R.array.shantou_city_item,
			R.array.foshan_city_item, R.array.jiangmen_city_item,
			R.array.zhangjiang_city_item, R.array.maoming_city_item,
			R.array.zhaoqing_city_item, R.array.huizhou_city_item,
			R.array.meizhou_city_item, R.array.shanwei_city_item,
			R.array.heyuan_city_item, R.array.yangjiang_city_item,
			R.array.qingyuan_city_item, R.array.dongguan_city_item,
			R.array.zhongshan_city_item, R.array.chaozhou_city_item,
			R.array.jiyang_city_item, R.array.yunfu_city_item };
	private int[] countyOfGuangXi = { R.array.nanning_city_item,
			R.array.liuzhou_city_item, R.array.guilin_city_item,
			R.array.guangxi_wuzhou_city_item, R.array.beihai_city_item,
			R.array.fangchenggang_city_item, R.array.qinzhou_city_item,
			R.array.guigang_city_item, R.array.yuelin_city_item,
			R.array.baise_city_item, R.array.hezhou_city_item,
			R.array.hechi_city_item, R.array.laibing_city_item,
			R.array.chuangzuo_city_item };
	private int[] countyOfHaiNan = { R.array.haikou_city_item,
			R.array.sanya_city_item };
	private int[] countyOfChongQing = { R.array.chongqing_city_item };
	private int[] countyOfSiChuan = { R.array.chengdu_city_item,
			R.array.zigong_city_item, R.array.panzhihua_city_item,
			R.array.luzhou_city_item, R.array.deyang_city_item,
			R.array.mianyang_city_item, R.array.guangyuan_city_item,
			R.array.suining_city_item, R.array.neijiang_city_item,
			R.array.leshan_city_item, R.array.nanchong_city_item,
			R.array.meishan_city_item, R.array.yibing_city_item,
			R.array.guangan_city_item, R.array.dazhou_city_item,
			R.array.yaan_city_item, R.array.bazhong_city_item,
			R.array.ziyang_city_item, R.array.abei_city_item,
			R.array.ganmu_city_item, R.array.liangshan_city_item };
	private int[] countyOfGuiZhou = { R.array.guiyang_city_item,
			R.array.lupanshui_city_item, R.array.zhunyi_city_item,
			R.array.anshun_city_item, R.array.tongren_city_item,
			R.array.qingxinan_city_item, R.array.biji_city_item,
			R.array.qingdongnan_city_item, R.array.qingnan_city_item };
	private int[] countyOfYunNan = { R.array.kunming_city_item,
			R.array.qujing_city_item, R.array.yuexi_city_item,
			R.array.baoshan_city_item, R.array.zhaotong_city_item,
			R.array.lijiang_city_item, R.array.simao_city_item,
			R.array.lingcang_city_item, R.array.chuxiong_city_item,
			R.array.honghe_city_item, R.array.wenshan_city_item,
			R.array.xishuangbanna_city_item, R.array.dali_city_item,
			R.array.dehuang_city_item, R.array.nujiang_city_item,
			R.array.diqing_city_item };
	private int[] countyOfXiZang = { R.array.lasa_city_item,
			R.array.changdu_city_item, R.array.shannan_city_item,
			R.array.rgeze_city_item, R.array.naqu_city_item,
			R.array.ali_city_item, R.array.linzhi_city_item };

	private int[] countyOfShanXi2 = { R.array.xian_city_item,
			R.array.tongchuan_city_item, R.array.baoji_city_item,
			R.array.xianyang_city_item, R.array.weinan_city_item,
			R.array.yanan_city_item, R.array.hanzhong_city_item,
			R.array.yulin_city_item, R.array.ankang_city_item,
			R.array.shangluo_city_item };
	private int[] countyOfGanSu = { R.array.lanzhou_city_item,
			R.array.jiayuguan_city_item, R.array.jinchang_city_item,
			R.array.baiyin_city_item, R.array.tianshui_city_item,
			R.array.wuwei_city_item, R.array.zhangyue_city_item,
			R.array.pingliang_city_item, R.array.jiuquan_city_item,
			R.array.qingyang_city_item, R.array.dingxi_city_item,
			R.array.longnan_city_item, R.array.linxia_city_item,
			R.array.gannan_city_item };
	private int[] countyOfQingHai = { R.array.xining_city_item,
			R.array.haidong_city_item, R.array.haibai_city_item,
			R.array.huangnan_city_item, R.array.hainan_city_item,
			R.array.guluo_city_item, R.array.yushu_city_item,
			R.array.haixi_city_item };
	private int[] countyOfNingXia = { R.array.yinchuan_city_item,
			R.array.shizuishan_city_item, R.array.wuzhong_city_item,
			R.array.guyuan_city_item, R.array.zhongwei_city_item };
	private int[] countyOfXinJiang = { R.array.wulumuqi_city_item,
			R.array.kelamayi_city_item, R.array.tulyfan_city_item,
			R.array.hami_city_item, R.array.changji_city_item,
			R.array.boertala_city_item, R.array.bayinguolen_city_item,
			R.array.akesu_city_item, R.array.kemuleisu_city_item,
			R.array.geshen_city_item, R.array.hetian_city_item,
			R.array.yili_city_item, R.array.tacheng_city_item,
			R.array.aleitai_city_item, R.array.shihezi_city_item,
			R.array.alaer_city_item, R.array.tumushihe_city_item,
			R.array.wujiaqu_city_item };
	private int[] countyOfHongKong = { R.array.hongkong_city_item };
	private int[] countyOfAoMen = { R.array.aomen_city_item };
	private int[] countyOfTaiWan = { R.array.taiwan_city_item };

	private Integer m_iProvinceIndex;
	private Integer m_iCityIndex;
	private boolean m_bIsSetDefault = false;
	private String m_strProvince = "", m_strCity = "", m_strCounty = "",
			m_strKeyWord = "";
	private Button m_btCity;

	private List<String> m_listKeyWord = new ArrayList<String>();
	private ListView m_lvRecord;
	private RecordAdapter m_adapterRecord;
	private ProgressDialog m_dlgProgress = null;

	// 搜索信息列表
	private PullRefreshList m_lvSearch;
	private SearchAdapter m_adapterSearch;
	List<tagSearchItem> m_listSearch = new ArrayList<tagSearchItem>();
	int m_iPageNum = 0;
	private Handler m_hNotify;
	private PoiSearch m_poiSearch = null;

	private ArrayAdapter<CharSequence> m_adapterProvince;
	private ArrayAdapter<CharSequence> m_adapterCity;
	private ArrayAdapter<CharSequence> m_adapterCounty;

	// 语音识别
	private SpeechRecognizer speechRecognizer;
	// 语音听写UI
	private RecognizerDialog dlgRecognizer;

	/**
	 * 初始化监听器。
	 */
	private InitListener m_listenerInit = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS)
				QParkingApp.ToastTip(SearchActivity.this, "语音引擎初始化失败", -100);
		}
	};
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener m_listenerRecognizerDialog = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			String strInput = JsonParser.parseIatResult(results
					.getResultString());

			m_etSearchWord.append(strInput);
			if (isLast)
				MakeSearch();
		}

		@Override
		public void onError(SpeechError arg0) {
			QParkingApp.ToastTip(SearchActivity.this, "识别错误", -100);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);

		// 按钮消息
		Button btTemp;
		btTemp = (Button) findViewById(R.id.btSearchBack); // 返回
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btSearchGo); // 搜索
		btTemp.setOnClickListener(this);

		// 语音
		ImageButton ibtVoice = (ImageButton) findViewById(R.id.ibtVoiceInput);
		ibtVoice.setOnClickListener(this);

		m_etSearchWord = (EditText) findViewById(R.id.etSearchWord);
		if (getIntent().getStringExtra("info") != null) {
			m_etSearchWord.setText(getIntent().getStringExtra("info"));
		}

		m_btCity = (Button) findViewById(R.id.btChooseCity);
		m_btCity.setOnClickListener(this);

		QParkingApp appQParking = (QParkingApp) getApplicationContext();
		// 初始化位置
		m_strProvince = appQParking.m_addressAdd.province;
		m_strCity = appQParking.m_addressAdd.city;
		m_strCounty = appQParking.m_addressAdd.district;

		m_btCity.setText(m_strCounty + "▼");
		// 读取搜索记录
		SharedPreferences spSearchRecord = getSharedPreferences("SearchRecord",
				Activity.MODE_PRIVATE);
		int iRecordCount = 0;
		while (true) {
			String strKey = String.format("Record%d", iRecordCount);
			String strRecord = spSearchRecord.getString(strKey, "");
			if (strRecord.length() == 0)
				break;

			m_listKeyWord.add(strRecord);
			iRecordCount++;
		}
		m_lvRecord = (ListView) findViewById(R.id.lvHistory);
		// 实例化自定义适配器
		m_adapterRecord = new RecordAdapter(this, m_listKeyWord);
		m_lvRecord.setAdapter(m_adapterRecord);
		// 设置搜索列表
		m_lvSearch = (PullRefreshList) findViewById(R.id.lvSearchResult);

		// 实例化自定义适配器
		m_adapterSearch = new SearchAdapter(this, m_listSearch);
		m_lvSearch.setAdapter(m_adapterSearch);
		// 连接列表消息
		m_lvSearch.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				tagSearchItem itemSearch = m_listSearch.get(position - 1);

				Intent intentRetData = new Intent();

				intentRetData.putExtra("latitude", itemSearch.m_llPos.latitude);
				intentRetData.putExtra("longitude",
						itemSearch.m_llPos.longitude);
				intentRetData.putExtra("name", itemSearch.m_strName);
				intentRetData.putExtra("address", itemSearch.m_strAddress);

				setResult(1, intentRetData);
				finish();
			}
		});
		m_lvSearch.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
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
		m_lvSearch.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						PoiCitySearchOption optionCitySearch = new PoiCitySearchOption();

						optionCitySearch.keyword(m_strKeyWord);
						optionCitySearch.pageNum(m_iPageNum);
						optionCitySearch.pageCapacity(POI_SEARCH_PAGE_CAPACITY);
						optionCitySearch.city(m_strCounty);

						m_poiSearch.searchInCity(optionCitySearch);
					}
				}).start();

			}
		});
		// POI搜索
		m_poiSearch = PoiSearch.newInstance();
		m_poiSearch.setOnGetPoiSearchResultListener(this);
		m_hNotify = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case LOAD_MORE_DATA_HEADER:
					m_lvSearch.onHeaderRefreshComplete();
					break;
				case LOAD_MORE_DATA_FOOTER: {
					m_lvSearch.onFooterRefreshComplete();

					m_iPageNum++;
				}
					break;
				case LOAD_MORE_FAILED: {
					m_lvSearch.onFooterRefreshComplete();
					QParkingApp.ToastTip(SearchActivity.this, "没有更多的数据", -100);
				}
				default:
					break;
				}
			}

		};

		// 初始化识别对象
		speechRecognizer = SpeechRecognizer.createRecognizer(this, null);
		// 初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		dlgRecognizer = new RecognizerDialog(this, m_listenerInit);

		MakeSearch();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void select(Spinner spin, ArrayAdapter<CharSequence> adapter,
			int arry) {
		adapter = ArrayAdapter.createFromResource(this, arry,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);
		if (m_bIsSetDefault) {
			String[] strItem = getResources().getStringArray(arry);
			for (int i = 0; i < strItem.length; i++) {
				if (spin == m_spinCity && m_strCity.contains(strItem[i])) {
					m_spinCity.setSelection(i);
				}
				if (spin == m_spinCounty && m_strCounty.contains(strItem[i])) {
					m_spinCounty.setSelection(i);
					m_bIsSetDefault = false;
				}
			}
		}
	}

	private void MakeSearch() {
		m_etSearchWord = (EditText) findViewById(R.id.etSearchWord);
		m_strKeyWord = m_etSearchWord.getText().toString();
		if (m_strKeyWord.length() == 0) {
			QParkingApp.ToastTip(SearchActivity.this, "请输入搜索关键字！", -100);
			return;
		}
		// 显示等待对话框
		m_dlgProgress = ProgressDialog.show(SearchActivity.this, null,
				"搜索中... ", true, false);

		PoiCitySearchOption optionCitySearch = new PoiCitySearchOption();

		optionCitySearch.keyword(m_strKeyWord);
		optionCitySearch.pageNum(m_iPageNum);
		optionCitySearch.pageCapacity(POI_SEARCH_PAGE_CAPACITY);
		optionCitySearch.city(m_strCounty);

		m_poiSearch.searchInCity(optionCitySearch);

		m_adapterRecord.InsertItem(m_strKeyWord);
		m_adapterRecord.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btSearchBack:
			finish();
			break;
		case R.id.btChooseCity: {
			final AlertDialog dlgChooseCity = new AlertDialog.Builder(
					SearchActivity.this).create();

			dlgChooseCity.show();
			dlgChooseCity.getWindow().setContentView(R.layout.city_choose);
			dlgChooseCity.getWindow().findViewById(R.id.btMakeSelect)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							m_strProvince = m_spinProvince.getSelectedItem()
									.toString();
							m_strCity = m_spinCity.getSelectedItem().toString();
							m_strCounty = m_spinCounty.getSelectedItem()
									.toString();

							m_btCity.setText(m_strCounty + "▼");
							dlgChooseCity.dismiss();
						}
					});

			m_spinProvince = (Spinner) dlgChooseCity.getWindow().findViewById(
					R.id.spinnerProvince);
			m_spinCity = (Spinner) dlgChooseCity.getWindow().findViewById(
					R.id.spinnerCity);
			m_spinCounty = (Spinner) dlgChooseCity.getWindow().findViewById(
					R.id.spinnerCounty);
			// 设置省市下拉选择框
			m_spinProvince.setPrompt("请选择省份");
			m_adapterProvince = ArrayAdapter
					.createFromResource(this, R.array.province_item,
							android.R.layout.simple_spinner_item);
			m_adapterProvince
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			m_spinProvince.setAdapter(m_adapterProvince);
			m_spinProvince
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							m_iProvinceIndex = m_spinProvince
									.getSelectedItemPosition();
							if (true) {
								m_spinCity.setPrompt("请选择城市");
								select(m_spinCity, m_adapterCity,
										city[m_iProvinceIndex]);
								m_spinCity
										.setOnItemSelectedListener(new OnItemSelectedListener() {
											@Override
											public void onItemSelected(
													AdapterView<?> arg0,
													View arg1, int arg2,
													long arg3) {
												m_iCityIndex = m_spinCity
														.getSelectedItemPosition();
												if (true) {
													m_spinCounty
															.setPrompt("请选择县区");
													switch (m_iProvinceIndex) {
													case 0:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfBeiJing[m_iCityIndex]);
														break;
													case 1:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfTianJing[m_iCityIndex]);
														break;
													case 2:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfHeBei[m_iCityIndex]);
														break;
													case 3:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfShanXi1[m_iCityIndex]);
														break;
													case 4:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfNeiMengGu[m_iCityIndex]);
														break;
													case 5:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfLiaoNing[m_iCityIndex]);
														break;
													case 6:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfJiLin[m_iCityIndex]);
														break;
													case 7:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfHeiLongJiang[m_iCityIndex]);
														break;
													case 8:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfShangHai[m_iCityIndex]);
														break;
													case 9:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfJiangSu[m_iCityIndex]);
														break;
													case 10:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfZheJiang[m_iCityIndex]);
														break;
													case 11:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfAnHui[m_iCityIndex]);
														break;
													case 12:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfFuJian[m_iCityIndex]);
														break;
													case 13:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfJiangXi[m_iCityIndex]);
														break;
													case 14:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfShanDong[m_iCityIndex]);
														break;
													case 15:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfHeNan[m_iCityIndex]);
														break;
													case 16:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfHuBei[m_iCityIndex]);
														break;
													case 17:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfHuNan[m_iCityIndex]);
														break;
													case 18:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfGuangDong[m_iCityIndex]);
														break;
													case 19:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfGuangXi[m_iCityIndex]);
														break;
													case 20:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfHaiNan[m_iCityIndex]);
														break;
													case 21:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfChongQing[m_iCityIndex]);
														break;
													case 22:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfSiChuan[m_iCityIndex]);
														break;
													case 23:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfGuiZhou[m_iCityIndex]);
														break;
													case 24:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfYunNan[m_iCityIndex]);
														break;
													case 25:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfXiZang[m_iCityIndex]);
														break;
													case 26:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfShanXi2[m_iCityIndex]);
														break;
													case 27:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfGanSu[m_iCityIndex]);
														break;
													case 28:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfQingHai[m_iCityIndex]);
														break;
													case 29:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfNingXia[m_iCityIndex]);
														break;
													case 30:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfXinJiang[m_iCityIndex]);
														break;
													case 31:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfHongKong[m_iCityIndex]);
														break;
													case 32:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfAoMen[m_iCityIndex]);
														break;
													case 33:
														select(m_spinCounty,
																m_adapterCounty,
																countyOfTaiWan[m_iCityIndex]);
														break;

													default:
														break;
													}

													m_spinCounty
															.setOnItemSelectedListener(new OnItemSelectedListener() {

																@Override
																public void onItemSelected(
																		AdapterView<?> arg0,
																		View arg1,
																		int arg2,
																		long arg3) {
																}

																@Override
																public void onNothingSelected(
																		AdapterView<?> arg0) {
																}
															});
												}
											}

											@Override
											public void onNothingSelected(
													AdapterView<?> arg0) {
												// TODO Auto-generated method
												// stub

											}
										});
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

			// 设置默认省市区
			String[] strProvince = getResources().getStringArray(
					R.array.province_item);
			for (int i = 0; i < strProvince.length; i++) {
				if (m_strProvince.contains(strProvince[i]))
					m_spinProvince.setSelection(i);
			}
			m_bIsSetDefault = true;
		}
			break;
		case R.id.btSearchGo:
			MakeSearch();
			break;
		case R.id.ibtVoiceInput: {
			m_etSearchWord.setText(null);
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
			// 显示听写对话框
			dlgRecognizer.setListener(m_listenerRecognizerDialog);
			dlgRecognizer.show();
		}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		SharedPreferences spSearchRecord = getSharedPreferences("SearchRecord",
				Activity.MODE_PRIVATE);

		Editor editorSearchRecord = spSearchRecord.edit();
		editorSearchRecord.clear();
		// 用putString的方法保存数据
		for (int i = 0; i < m_listKeyWord.size(); i++) {
			String strKey = String.format("Record%d", i);
			editorSearchRecord.putString(strKey, m_listKeyWord.get(i));
		}
		editorSearchRecord.commit();

		super.onDestroy();
	}

	// 列表适配器
	class SearchAdapter extends BaseAdapter {
		private List<tagSearchItem> m_listItem;
		private LayoutInflater m_layoutInflater;

		public SearchAdapter(Context context, List<tagSearchItem> list) {
			m_listItem = list;
			m_layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return m_listItem.size();
		}

		@Override
		public Object getItem(int position) {
			if (m_listItem.size() == 0)
				return null;

			return m_listItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 控制ITEM 布局内容
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null)
				convertView = m_layoutInflater.inflate(R.layout.search_result,
						null); // 加载布局

			if (m_listItem.size() == 0)
				return convertView;

			tagSearchItem itemSearch = m_listItem.get(position);
			// 设置内容
			TextView tvName = (TextView) convertView
					.findViewById(R.id.tvResultName);
			tvName.setText(String.format("%d.%s", position + 1,
					itemSearch.m_strName));
			TextView tvAddress = (TextView) convertView
					.findViewById(R.id.tvResultAddress);
			tvAddress.setText(itemSearch.m_strAddress);
			TextView tvDistance = (TextView) convertView
					.findViewById(R.id.tvSearchDistance);
			tvDistance.setText(String.format("%d米", itemSearch.m_iDistance));

			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		// 是否Item监听
		@Override
		public boolean isEmpty() {
			return true;
		}

		// true所有项目可选择可点击
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		// 是否显示分割线
		@Override
		public boolean isEnabled(int position) {
			return true;
		}
	}

	// 列表适配器
	class RecordAdapter extends BaseAdapter {
		private List<String> m_listItem;
		private LayoutInflater m_layoutInflater;

		public RecordAdapter(Context context, List<String> list) {
			m_listItem = list;
			m_layoutInflater = LayoutInflater.from(context);
		}

		public void InsertItem(String strInsert) {
			m_listItem.add(0, strInsert);
		}

		@Override
		public int getCount() {
			return m_listItem.size();
		}

		@Override
		public Object getItem(int position) {
			if (m_listItem.size() == 0)
				return null;

			return m_listItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 控制ITEM 布局内容
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = m_layoutInflater.inflate(R.layout.history_record,
						null); // 加载布局
				Button btDelete = (Button) convertView
						.findViewById(R.id.btHistoryDel);
				btDelete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						m_listKeyWord.remove(position);
						m_adapterRecord.notifyDataSetChanged();
					}
				});
				RelativeLayout rlItem = (RelativeLayout) convertView
						.findViewById(R.id.rlHistoryItem);
				rlItem.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// 显示等待对话框
						m_dlgProgress = ProgressDialog.show(
								SearchActivity.this, null, "搜索中... ", true,
								false);

						PoiCitySearchOption optionCitySearch = new PoiCitySearchOption();

						m_strKeyWord = m_listKeyWord.get(position);
						optionCitySearch.keyword(m_strKeyWord);
						optionCitySearch.pageNum(m_iPageNum);
						optionCitySearch.pageCapacity(POI_SEARCH_PAGE_CAPACITY);
						optionCitySearch.city(m_strCounty);

						m_poiSearch.searchInCity(optionCitySearch);
					}
				});
			}

			if (m_listItem.size() == 0)
				return convertView;

			// 设置路线内容
			TextView tvName = (TextView) convertView
					.findViewById(R.id.tvRecordContent);
			tvName.setText(m_listItem.get(position));

			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			return 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		// 是否Item监听
		@Override
		public boolean isEmpty() {
			return true;
		}

		// true所有项目可选择可点击
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		// 是否显示分割线
		@Override
		public boolean isEnabled(int position) {
			return true;
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		m_dlgProgress.dismiss();
		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			QParkingApp.ToastTip(SearchActivity.this, "未找到相关信息！", -100);
			m_lvSearch.onFooterRefreshComplete();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			// 获取应用程序对象
			QParkingApp appQParking = (QParkingApp) getApplicationContext();
			int iCount = result.getAllPoi().size();
			for (int i = 0; i < iCount; i++) {
				tagSearchItem itemSearch = new tagSearchItem();
				PoiInfo infoPoi = result.getAllPoi().get(i);
				itemSearch.m_strName = infoPoi.name;
				itemSearch.m_llPos = infoPoi.location;
				itemSearch.m_strAddress = infoPoi.address;
				itemSearch.m_iDistance = (int) DistanceUtil.getDistance(
						appQParking.m_llMe, itemSearch.m_llPos);

				int j = 0;
				for (j = 0; j < m_listSearch.size(); j++) {
					tagSearchItem itemTmep = m_listSearch.get(j);
					if (itemSearch.m_iDistance < itemTmep.m_iDistance) {
						m_listSearch.add(j, itemSearch);
						break;
					}
				}
				if (j == m_listSearch.size())
					m_listSearch.add(itemSearch);
			}
			m_lvSearch.onFooterRefreshComplete();
			m_adapterRecord.notifyDataSetChanged();

			m_iPageNum++;
			m_lvRecord.setVisibility(View.INVISIBLE);
			m_lvSearch.setVisibility(View.VISIBLE);
		}

	}
}
