package funion.app.qparking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author Administrator
 * @包名： funion.app.qparking
 * @类名：AddParkingActivity
 * @时间：2015下午2:15:39
 * @要做的事情：TODO
 */
public class AddParkingActivity extends Activity implements OnClickListener {
	final int PICK_ADDRESS_RET = 0;

	private int m_iParkingTypeNameID;
	private int m_iParkingTypeViewID;
	private int m_iPersonalTypeID;
	private int m_iPersonalTypeViewID;
	private Runnable m_runnableInterface;

	private ProgressDialog m_dlgProgress = null;
	private Handler m_hNotify = null;

	private Spinner m_spinProvince;
	private Spinner m_spinCity;
	private Spinner m_spinCounty;

	private int[] city = { R.array.beijin_province_item, R.array.tianjin_province_item, R.array.heibei_province_item,
			R.array.shanxi1_province_item, R.array.neimenggu_province_item, R.array.liaoning_province_item,
			R.array.jilin_province_item, R.array.heilongjiang_province_item, R.array.shanghai_province_item,
			R.array.jiangsu_province_item, R.array.zhejiang_province_item, R.array.anhui_province_item,
			R.array.fujian_province_item, R.array.jiangxi_province_item, R.array.shandong_province_item,
			R.array.henan_province_item, R.array.hubei_province_item, R.array.hunan_province_item,
			R.array.guangdong_province_item, R.array.guangxi_province_item, R.array.hainan_province_item,
			R.array.chongqing_province_item, R.array.sichuan_province_item, R.array.guizhou_province_item,
			R.array.yunnan_province_item, R.array.xizang_province_item, R.array.shanxi2_province_item,
			R.array.gansu_province_item, R.array.qinghai_province_item, R.array.linxia_province_item,
			R.array.xinjiang_province_item, R.array.hongkong_province_item, R.array.aomen_province_item,
			R.array.taiwan_province_item };
	private int[] countyOfBeiJing = { R.array.beijin_city_item };
	private int[] countyOfTianJing = { R.array.tianjin_city_item };
	private int[] countyOfHeBei = { R.array.shijiazhuang_city_item, R.array.tangshan_city_item,
			R.array.qinghuangdao_city_item, R.array.handan_city_item, R.array.xingtai_city_item,
			R.array.baoding_city_item, R.array.zhangjiakou_city_item, R.array.chengde_city_item,
			R.array.cangzhou_city_item, R.array.langfang_city_item, R.array.hengshui_city_item };
	private int[] countyOfShanXi1 = { R.array.taiyuan_city_item, R.array.datong_city_item, R.array.yangquan_city_item,
			R.array.changzhi_city_item, R.array.jincheng_city_item, R.array.shuozhou_city_item,
			R.array.jinzhong_city_item, R.array.yuncheng_city_item, R.array.xinzhou_city_item,
			R.array.linfen_city_item, R.array.lvliang_city_item };
	private int[] countyOfNeiMengGu = { R.array.huhehaote_city_item, R.array.baotou_city_item, R.array.wuhai_city_item,
			R.array.chifeng_city_item, R.array.tongliao_city_item, R.array.eerduosi_city_item,
			R.array.hulunbeier_city_item, R.array.bayannaoer_city_item, R.array.wulanchabu_city_item,
			R.array.xinganmeng_city_item, R.array.xilinguolemeng_city_item, R.array.alashanmeng_city_item };
	private int[] countyOfLiaoNing = { R.array.shenyang_city_item, R.array.dalian_city_item, R.array.anshan_city_item,
			R.array.wushun_city_item, R.array.benxi_city_item, R.array.dandong_city_item,
			R.array.liaoning_jinzhou_city_item, R.array.yingkou_city_item, R.array.fuxin_city_item,
			R.array.liaoyang_city_item, R.array.panjin_city_item, R.array.tieling_city_item,
			R.array.zhaoyang_city_item, R.array.huludao_city_item };
	private int[] countyOfJiLin = { R.array.changchun_city_item, R.array.jilin_city_item, R.array.siping_city_item,
			R.array.liaoyuan_city_item, R.array.tonghua_city_item, R.array.baishan_city_item,
			R.array.songyuan_city_item, R.array.baicheng_city_item, R.array.yanbian_city_item };
	private int[] countyOfHeiLongJiang = { R.array.haerbing_city_item, R.array.qiqihaer_city_item,
			R.array.jixi_city_item, R.array.hegang_city_item, R.array.shuangyashan_city_item, R.array.daqing_city_item,
			R.array.heilongjiang_yichun_city_item, R.array.jiamusi_city_item, R.array.qitaihe_city_item,
			R.array.mudanjiang_city_item, R.array.heihe_city_item, R.array.suihua_city_item,
			R.array.daxinganling_city_item };
	private int[] countyOfShangHai = { R.array.shanghai_city_item };

	private int[] countyOfJiangSu = { R.array.nanjing_city_item, R.array.wuxi_city_item, R.array.xuzhou_city_item,
			R.array.changzhou_city_item, R.array.nanjing_suzhou_city_item, R.array.nantong_city_item,
			R.array.lianyungang_city_item, R.array.huaian_city_item, R.array.yancheng_city_item,
			R.array.yangzhou_city_item, R.array.zhenjiang_city_item, R.array.jiangsu_taizhou_city_item,
			R.array.suqian_city_item };
	private int[] countyOfZheJiang = { R.array.hangzhou_city_item, R.array.ningbo_city_item, R.array.wenzhou_city_item,
			R.array.jiaxing_city_item, R.array.huzhou_city_item, R.array.shaoxing_city_item, R.array.jinhua_city_item,
			R.array.quzhou_city_item, R.array.zhoushan_city_item, R.array.zejiang_huzhou_city_item,
			R.array.lishui_city_item };
	private int[] countyOfAnHui = { R.array.hefei_city_item, R.array.wuhu_city_item, R.array.bengbu_city_item,
			R.array.huainan_city_item, R.array.maanshan_city_item, R.array.huaibei_city_item,
			R.array.tongling_city_item, R.array.anqing_city_item, R.array.huangshan_city_item,
			R.array.chuzhou_city_item, R.array.fuyang_city_item, R.array.anhui_suzhou_city_item,
			R.array.chaohu_city_item, R.array.luan_city_item, R.array.haozhou_city_item, R.array.chizhou_city_item,
			R.array.xuancheng_city_item };
	private int[] countyOfFuJian = { R.array.huzhou_city_item, R.array.xiamen_city_item, R.array.putian_city_item,
			R.array.sanming_city_item, R.array.quanzhou_city_item, R.array.zhangzhou_city_item, R.array.nanp_city_item,
			R.array.longyan_city_item, R.array.ningde_city_item };
	private int[] countyOfJiangXi = { R.array.nanchang_city_item, R.array.jingdezhen_city_item,
			R.array.pingxiang_city_item, R.array.jiujiang_city_item, R.array.xinyu_city_item,
			R.array.yingtan_city_item, R.array.ganzhou_city_item, R.array.jian_city_item,
			R.array.jiangxi_yichun_city_item, R.array.jiangxi_wuzhou_city_item, R.array.shangrao_city_item };
	private int[] countyOfShanDong = { R.array.jinan_city_item, R.array.qingdao_city_item, R.array.zaobo_city_item,
			R.array.zaozhuang_city_item, R.array.dongying_city_item, R.array.yantai_city_item,
			R.array.weifang_city_item, R.array.jining_city_item, R.array.taian_city_item, R.array.weihai_city_item,
			R.array.rizhao_city_item, R.array.laiwu_city_item, R.array.linxi_city_item, R.array.dezhou_city_item,
			R.array.liaocheng_city_item, R.array.shandong_bingzhou_city_item, R.array.heze_city_item };
	private int[] countyOfHeNan = { R.array.zhenshou_city_item, R.array.kaifang_city_item, R.array.luoyang_city_item,
			R.array.kaipingshan_city_item, R.array.anyang_city_item, R.array.hebi_city_item,
			R.array.xinxiang_city_item, R.array.jiaozuo_city_item, R.array.buyang_city_item, R.array.xuchang_city_item,
			R.array.leihe_city_item, R.array.sanmenxia_city_item, R.array.nanyang_city_item,
			R.array.shangqiu_city_item, R.array.xinyang_city_item, R.array.zhoukou_city_item,
			R.array.zhumadian_city_item };
	private int[] countyOfHuBei = { R.array.wuhan_city_item, R.array.huangshi_city_item, R.array.shiyan_city_item,
			R.array.yichang_city_item, R.array.xiangpan_city_item, R.array.erzhou_city_item, R.array.jinmen_city_item,
			R.array.xiaogan_city_item, R.array.hubei_jinzhou_city_item, R.array.huanggang_city_item,
			R.array.xianning_city_item, R.array.suizhou_city_item, R.array.enshi_city_item,
			R.array.shenglongjia_city_item };

	private int[] countyOfHuNan = { R.array.changsha_city_item, R.array.zhuzhou_city_item, R.array.xiangtan_city_item,
			R.array.hengyang_city_item, R.array.shaoyang_city_item, R.array.yueyang_city_item,
			R.array.changde_city_item, R.array.zhangjiajie_city_item, R.array.yiyang_city_item,
			R.array.hunan_bingzhou_city_item, R.array.yongzhou_city_item, R.array.huaihua_city_item,
			R.array.loudi_city_item, R.array.xiangxi_city_item };
	private int[] countyOfGuangDong = { R.array.guangzhou_city_item, R.array.shaoguan_city_item,
			R.array.shenzhen_city_item, R.array.zhuhai_city_item, R.array.shantou_city_item, R.array.foshan_city_item,
			R.array.jiangmen_city_item, R.array.zhangjiang_city_item, R.array.maoming_city_item,
			R.array.zhaoqing_city_item, R.array.huizhou_city_item, R.array.meizhou_city_item,
			R.array.shanwei_city_item, R.array.heyuan_city_item, R.array.yangjiang_city_item,
			R.array.qingyuan_city_item, R.array.dongguan_city_item, R.array.zhongshan_city_item,
			R.array.chaozhou_city_item, R.array.jiyang_city_item, R.array.yunfu_city_item };
	private int[] countyOfGuangXi = { R.array.nanning_city_item, R.array.liuzhou_city_item, R.array.guilin_city_item,
			R.array.guangxi_wuzhou_city_item, R.array.beihai_city_item, R.array.fangchenggang_city_item,
			R.array.qinzhou_city_item, R.array.guigang_city_item, R.array.yuelin_city_item, R.array.baise_city_item,
			R.array.hezhou_city_item, R.array.hechi_city_item, R.array.laibing_city_item, R.array.chuangzuo_city_item };
	private int[] countyOfHaiNan = { R.array.haikou_city_item, R.array.sanya_city_item };
	private int[] countyOfChongQing = { R.array.chongqing_city_item };
	private int[] countyOfSiChuan = { R.array.chengdu_city_item, R.array.zigong_city_item, R.array.panzhihua_city_item,
			R.array.luzhou_city_item, R.array.deyang_city_item, R.array.mianyang_city_item,
			R.array.guangyuan_city_item, R.array.suining_city_item, R.array.neijiang_city_item,
			R.array.leshan_city_item, R.array.nanchong_city_item, R.array.meishan_city_item, R.array.yibing_city_item,
			R.array.guangan_city_item, R.array.dazhou_city_item, R.array.yaan_city_item, R.array.bazhong_city_item,
			R.array.ziyang_city_item, R.array.abei_city_item, R.array.ganmu_city_item, R.array.liangshan_city_item };
	private int[] countyOfGuiZhou = { R.array.guiyang_city_item, R.array.lupanshui_city_item, R.array.zhunyi_city_item,
			R.array.anshun_city_item, R.array.tongren_city_item, R.array.qingxinan_city_item, R.array.biji_city_item,
			R.array.qingdongnan_city_item, R.array.qingnan_city_item };
	private int[] countyOfYunNan = { R.array.kunming_city_item, R.array.qujing_city_item, R.array.yuexi_city_item,
			R.array.baoshan_city_item, R.array.zhaotong_city_item, R.array.lijiang_city_item, R.array.simao_city_item,
			R.array.lingcang_city_item, R.array.chuxiong_city_item, R.array.honghe_city_item,
			R.array.wenshan_city_item, R.array.xishuangbanna_city_item, R.array.dali_city_item,
			R.array.dehuang_city_item, R.array.nujiang_city_item, R.array.diqing_city_item };
	private int[] countyOfXiZang = { R.array.lasa_city_item, R.array.changdu_city_item, R.array.shannan_city_item,
			R.array.rgeze_city_item, R.array.naqu_city_item, R.array.ali_city_item, R.array.linzhi_city_item };

	private int[] countyOfShanXi2 = { R.array.xian_city_item, R.array.tongchuan_city_item, R.array.baoji_city_item,
			R.array.xianyang_city_item, R.array.weinan_city_item, R.array.yanan_city_item, R.array.hanzhong_city_item,
			R.array.yulin_city_item, R.array.ankang_city_item, R.array.shangluo_city_item };
	private int[] countyOfGanSu = { R.array.lanzhou_city_item, R.array.jiayuguan_city_item, R.array.jinchang_city_item,
			R.array.baiyin_city_item, R.array.tianshui_city_item, R.array.wuwei_city_item, R.array.zhangyue_city_item,
			R.array.pingliang_city_item, R.array.jiuquan_city_item, R.array.qingyang_city_item,
			R.array.dingxi_city_item, R.array.longnan_city_item, R.array.linxia_city_item, R.array.gannan_city_item };
	private int[] countyOfQingHai = { R.array.xining_city_item, R.array.haidong_city_item, R.array.haibai_city_item,
			R.array.huangnan_city_item, R.array.hainan_city_item, R.array.guluo_city_item, R.array.yushu_city_item,
			R.array.haixi_city_item };
	private int[] countyOfNingXia = { R.array.yinchuan_city_item, R.array.shizuishan_city_item,
			R.array.wuzhong_city_item, R.array.guyuan_city_item, R.array.zhongwei_city_item };
	private int[] countyOfXinJiang = { R.array.wulumuqi_city_item, R.array.kelamayi_city_item,
			R.array.tulyfan_city_item, R.array.hami_city_item, R.array.changji_city_item, R.array.boertala_city_item,
			R.array.bayinguolen_city_item, R.array.akesu_city_item, R.array.kemuleisu_city_item,
			R.array.geshen_city_item, R.array.hetian_city_item, R.array.yili_city_item, R.array.tacheng_city_item,
			R.array.aleitai_city_item, R.array.shihezi_city_item, R.array.alaer_city_item, R.array.tumushihe_city_item,
			R.array.wujiaqu_city_item };
	private int[] countyOfHongKong = {};
	private int[] countyOfAoMen = {};
	private int[] countyOfTaiWan = {};

	private Integer m_iProvinceIndex;
	private Integer m_iCityIndex;
	private boolean m_bIsSetDefault = false;

	private ArrayAdapter<CharSequence> m_adapterProvince;
	private ArrayAdapter<CharSequence> m_adapterCity;
	private ArrayAdapter<CharSequence> m_adapterCounty;

	private TextView m_tvCity;
	private TextView m_tvCounty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_parking_activity);

		// 初始化状态
		m_iParkingTypeNameID = R.id.tvUpground;
		m_iParkingTypeViewID = R.id.viewUpground;
		m_iPersonalTypeID = R.id.tvPersonalPark;
		m_iPersonalTypeViewID = R.id.viewPersonalPark;

		// 设置默认shape
		((GradientDrawable) findViewById(R.id.viewUpground).getBackground()).setColor(0xFF00c1a0);
		((GradientDrawable) findViewById(R.id.viewUnderground).getBackground()).setColor(0xFFFFFFFF);
		((GradientDrawable) findViewById(R.id.viewIndoor).getBackground()).setColor(0xFFFFFFFF);

		((GradientDrawable) findViewById(R.id.viewPersonalPark).getBackground()).setColor(0xFF00c1a0);
		((GradientDrawable) findViewById(R.id.viewPublicPark).getBackground()).setColor(0xFFFFFFFF);
		// 返回按钮
		Button btTemp;
		btTemp = (Button) findViewById(R.id.btTopBack);
		btTemp.setOnClickListener(this);
		btTemp = (Button) findViewById(R.id.btParkingSubmit);
		btTemp.setOnClickListener(this);

		// 连接消息
		View viewTemp;

		viewTemp = findViewById(R.id.viewUpground);
		viewTemp.setOnClickListener(this);
		viewTemp = findViewById(R.id.viewUnderground);
		viewTemp.setOnClickListener(this);
		viewTemp = findViewById(R.id.viewIndoor);
		viewTemp.setOnClickListener(this);
		viewTemp = findViewById(R.id.viewPersonalPark);
		viewTemp.setOnClickListener(this);
		viewTemp = findViewById(R.id.viewPublicPark);
		viewTemp.setOnClickListener(this);

		m_tvCity = (TextView) findViewById(R.id.tvAddressCity);
		m_tvCity.setOnClickListener(this);
		m_tvCounty = (TextView) findViewById(R.id.tvAddressCounty);
		m_tvCounty.setOnClickListener(this);

		LinearLayout rlTemp = (LinearLayout) findViewById(R.id.llHoldToPickAddress);
		rlTemp.setOnClickListener(this);
		// 调用接口线程
		m_runnableInterface = new Runnable() {
			public void run() {
				try {
					QParkingApp appQParking = (QParkingApp) getApplicationContext();

					String strShell = String.format("shell=%s", appQParking.m_strUserID);
					String strName = String.format("name=%s",
							URLEncoder.encode(((EditText) findViewById(R.id.etParkingName)).getText().toString()));
					String strLongitude = String.format("longitude=%f", appQParking.m_llSubmit.longitude);
					String strLatitude = String.format("latitude=%f", appQParking.m_llSubmit.latitude);
					// TODO:将名称改为 个人/停车场 类型
					String strChargeType = String.format("chargeType=%d",
							m_iParkingTypeViewID == R.id.viewPersonalPark ? 0 : 1);
					String strProvince = String.format("province=%s",
							URLEncoder.encode(appQParking.m_addressAdd.province));
					String strCity = String.format("city=%s", URLEncoder.encode(appQParking.m_addressAdd.city));
					String strDistrict = String.format("district=%s",
							URLEncoder.encode(appQParking.m_addressAdd.district));
					String strAddress = String.format("address=%s",
							URLEncoder.encode(((EditText) findViewById(R.id.etAddressDetails)).getText().toString()));
					String strLocationType = "";
					switch (m_iParkingTypeViewID) {
					case R.id.viewUpground:
						strLocationType = "locationType=0";
						break;
					case R.id.viewUnderground:
						strLocationType = "locationType=1";
						break;
					case R.id.viewIndoor:
						strLocationType = "locationType=2";
						break;
					}

					String strParams = strShell + "&" + strName + "&" + strLongitude + "&" + strLatitude + "&"
							+ strChargeType + "&" + strProvince + "&" + strCity + "&" + strDistrict + "&" + strAddress
							+ "&" + strLocationType;
					URL urlInterface = new URL("http://app.qutingche.cn/Mobile/Depot/addDepot");
					HttpURLConnection hucInterface = (HttpURLConnection) urlInterface.openConnection();

					hucInterface.setDoInput(true);
					hucInterface.setDoOutput(true);
					hucInterface.setUseCaches(false);

					hucInterface.setRequestMethod("POST");
					// 设置DataOutputStream
					DataOutputStream dosInterface = new DataOutputStream(hucInterface.getOutputStream());

					dosInterface.writeBytes(strParams);
					dosInterface.flush();
					dosInterface.close();

					BufferedReader readerBuff = new BufferedReader(new InputStreamReader(hucInterface.getInputStream()));
					String strData = readerBuff.readLine();

					JSONObject jsonRet = new JSONObject(strData);

					Message msg = new Message();
					Bundle bundleData = new Bundle();

					bundleData.putString("info", jsonRet.getString("info"));
					msg.setData(bundleData);

					msg.what = 0;
					m_hNotify.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		};
		// 设置通信句柄
		m_hNotify = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0: {
					m_dlgProgress.dismiss();
					QParkingApp.ToastTip(AddParkingActivity.this, msg.peekData().getString("info"), -100);
				}
					break;
				}
			}
		};
		SetParkingInfo();
	}

	void SetParkingInfo() {
		QParkingApp appQParking = (QParkingApp) getApplicationContext();
		// 添加默认停车场名称
		EditText etParkingName = (EditText) findViewById(R.id.etParkingName);
		etParkingName.setText(appQParking.m_addressAdd.street + appQParking.m_addressAdd.streetNumber + "停车场");
		// 设置详细地址
		((TextView) findViewById(R.id.tvAddressCity)).setText(appQParking.m_addressAdd.city);
		((TextView) findViewById(R.id.tvAddressCounty)).setText(appQParking.m_addressAdd.district);
		((TextView) findViewById(R.id.etAddressDetails)).setText(appQParking.m_addressAdd.street
				+ appQParking.m_addressAdd.streetNumber);
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

	@Override
	public void onClick(View v) {
		if (m_iParkingTypeViewID == v.getId() || m_iPersonalTypeViewID == v.getId())
			return;

		switch (v.getId()) {
		case R.id.btTopBack:
			finish();
			break;
		case R.id.viewUpground:
		case R.id.viewUnderground:
		case R.id.viewIndoor: {
			TextView tvTypeName;
			View viewType;
			// 复位原始设置
			tvTypeName = (TextView) findViewById(m_iParkingTypeNameID);
			viewType = findViewById(m_iParkingTypeViewID);

			tvTypeName.setTextColor(0xFF4d4d4d);
			((GradientDrawable) viewType.getBackground()).setColor(0xFFFFFFFF);
			// 设置当前项
			m_iParkingTypeViewID = v.getId();
			switch (m_iParkingTypeViewID) {
			case R.id.viewUpground:
				m_iParkingTypeNameID = R.id.tvUpground;
				break;
			case R.id.viewUnderground:
				m_iParkingTypeNameID = R.id.tvUnderground;
				break;
			case R.id.viewIndoor:
				m_iParkingTypeNameID = R.id.tvIndoor;
				break;
			}
			tvTypeName = (TextView) findViewById(m_iParkingTypeNameID);
			viewType = findViewById(m_iParkingTypeViewID);

			tvTypeName.setTextColor(0xFFFFFFFF);
			((GradientDrawable) viewType.getBackground()).setColor(0xFF00c1a0);
		}
			break;
		case R.id.viewPersonalPark:
		case R.id.viewPublicPark: {
			TextView tvTypeName;
			View viewType;
			// 复位原始设置
			tvTypeName = (TextView) findViewById(m_iPersonalTypeID);
			viewType = findViewById(m_iPersonalTypeViewID);

			tvTypeName.setTextColor(0xFF4d4d4d);
			((GradientDrawable) viewType.getBackground()).setColor(0xFFFFFFFF);
			// 设置当前项
			m_iPersonalTypeViewID = v.getId();
			switch (m_iPersonalTypeViewID) {
			case R.id.viewPersonalPark:
				m_iPersonalTypeID = R.id.tvPersonalPark;
				break;
			case R.id.viewPublicPark:
				m_iPersonalTypeID = R.id.tvPublicPark;
				break;
			}
			tvTypeName = (TextView) findViewById(m_iPersonalTypeID);
			viewType = findViewById(m_iPersonalTypeViewID);

			tvTypeName.setTextColor(0xFFFFFFFF);
			((GradientDrawable) viewType.getBackground()).setColor(0xFF00c1a0);
		}
			break;
		case R.id.btParkingSubmit: {
			// QParkingApp appQParking = (QParkingApp) getApplicationContext();
			// if (appQParking.m_strUserID.length() == 0) {
			// AlertDialog.Builder adLogonTip = new
			// Builder(AddParkingActivity.this);
			// adLogonTip.setMessage("登录之后才能添加停车场，是否立即登录？");
			// adLogonTip.setTitle("登录提示");
			// adLogonTip.setPositiveButton("确定", new
			// android.content.DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog, int which) {
			// QParkingApp appQParking = (QParkingApp) getApplicationContext();
			// // 界面跳转
			// Intent intentNewActivity = new Intent();
			// appQParking.m_bIsGoCenter = false;
			// intentNewActivity.setClass(AddParkingActivity.this,
			// LoginActivity.class);
			// startActivity(intentNewActivity);
			// }
			// });
			// adLogonTip.setNegativeButton("取消", new
			// android.content.DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog, int which) {
			// dialog.dismiss();
			// }
			// });
			// adLogonTip.show();
			//
			// return;
			// }
			// String strName = ((EditText)
			// findViewById(R.id.etParkingName)).getText().toString();
			// if (strName.length() == 0) {
			// QParkingApp.ToastTip(this, "请填停车场名！", -100);
			// return;
			// }
			//
			// // 显示等待对话框
			// m_dlgProgress = ProgressDialog.show(AddParkingActivity.this,
			// null, "提交信息，请稍后... ", true, false);
			//
			// new Thread(m_runnableInterface).start();
			
			switch (m_iPersonalTypeViewID) {
			case R.id.viewPersonalPark: {
				QParkingApp.ToastTip(AddParkingActivity.this, "个人", -100);
			}
				break;
			case R.id.viewPublicPark: {
				QParkingApp.ToastTip(AddParkingActivity.this, "停车场", -100);
			}
				break;
			}
		}
			break;
		// 长按至地址选择
		case R.id.llHoldToPickAddress: {
			Intent intentNewActivity = new Intent();
			intentNewActivity.setClass(AddParkingActivity.this, HoldMapActivity.class);
			startActivityForResult(intentNewActivity, PICK_ADDRESS_RET);
		}
			break;
		case R.id.tvAddressCity:
		case R.id.tvAddressCounty: {
			final AlertDialog dlgChooseCity = new AlertDialog.Builder(AddParkingActivity.this).create();

			dlgChooseCity.show();
			dlgChooseCity.getWindow().setContentView(R.layout.city_choose);
			dlgChooseCity.getWindow().findViewById(R.id.btMakeSelect).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					QParkingApp appQParking = (QParkingApp) getApplicationContext();

					appQParking.m_addressAdd.province = m_spinProvince.getSelectedItem().toString();
					appQParking.m_addressAdd.city = m_spinCity.getSelectedItem().toString();
					appQParking.m_addressAdd.district = m_spinCounty.getSelectedItem().toString();

					m_tvCity.setText(appQParking.m_addressAdd.city);
					m_tvCounty.setText(appQParking.m_addressAdd.district);
					dlgChooseCity.dismiss();
				}
			});

			m_spinProvince = (Spinner) dlgChooseCity.getWindow().findViewById(R.id.spinnerProvince);
			m_spinCity = (Spinner) dlgChooseCity.getWindow().findViewById(R.id.spinnerCity);
			m_spinCounty = (Spinner) dlgChooseCity.getWindow().findViewById(R.id.spinnerCounty);
			// 设置省市下拉选择框
			m_spinProvince.setPrompt("请选择省份");
			m_adapterProvince = ArrayAdapter.createFromResource(this, R.array.province_item,
					android.R.layout.simple_spinner_item);
			m_adapterProvince.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			m_spinProvince.setAdapter(m_adapterProvince);
			m_spinProvince.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					m_iProvinceIndex = m_spinProvince.getSelectedItemPosition();
					if (true) {
						m_spinCity.setPrompt("请选择城市");
						select(m_spinCity, m_adapterCity, city[m_iProvinceIndex]);
						m_spinCity.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
								m_iCityIndex = m_spinCity.getSelectedItemPosition();
								if (true) {
									m_spinCounty.setPrompt("请选择县区");
									switch (m_iProvinceIndex) {
									case 0:
										select(m_spinCounty, m_adapterCounty, countyOfBeiJing[m_iCityIndex]);
										break;
									case 1:
										select(m_spinCounty, m_adapterCounty, countyOfTianJing[m_iCityIndex]);
										break;
									case 2:
										select(m_spinCounty, m_adapterCounty, countyOfHeBei[m_iCityIndex]);
										break;
									case 3:
										select(m_spinCounty, m_adapterCounty, countyOfShanXi1[m_iCityIndex]);
										break;
									case 4:
										select(m_spinCounty, m_adapterCounty, countyOfNeiMengGu[m_iCityIndex]);
										break;
									case 5:
										select(m_spinCounty, m_adapterCounty, countyOfLiaoNing[m_iCityIndex]);
										break;
									case 6:
										select(m_spinCounty, m_adapterCounty, countyOfJiLin[m_iCityIndex]);
										break;
									case 7:
										select(m_spinCounty, m_adapterCounty, countyOfHeiLongJiang[m_iCityIndex]);
										break;
									case 8:
										select(m_spinCounty, m_adapterCounty, countyOfShangHai[m_iCityIndex]);
										break;
									case 9:
										select(m_spinCounty, m_adapterCounty, countyOfJiangSu[m_iCityIndex]);
										break;
									case 10:
										select(m_spinCounty, m_adapterCounty, countyOfZheJiang[m_iCityIndex]);
										break;
									case 11:
										select(m_spinCounty, m_adapterCounty, countyOfAnHui[m_iCityIndex]);
										break;
									case 12:
										select(m_spinCounty, m_adapterCounty, countyOfFuJian[m_iCityIndex]);
										break;
									case 13:
										select(m_spinCounty, m_adapterCounty, countyOfJiangXi[m_iCityIndex]);
										break;
									case 14:
										select(m_spinCounty, m_adapterCounty, countyOfShanDong[m_iCityIndex]);
										break;
									case 15:
										select(m_spinCounty, m_adapterCounty, countyOfHeNan[m_iCityIndex]);
										break;
									case 16:
										select(m_spinCounty, m_adapterCounty, countyOfHuBei[m_iCityIndex]);
										break;
									case 17:
										select(m_spinCounty, m_adapterCounty, countyOfHuNan[m_iCityIndex]);
										break;
									case 18:
										select(m_spinCounty, m_adapterCounty, countyOfGuangDong[m_iCityIndex]);
										break;
									case 19:
										select(m_spinCounty, m_adapterCounty, countyOfGuangXi[m_iCityIndex]);
										break;
									case 20:
										select(m_spinCounty, m_adapterCounty, countyOfHaiNan[m_iCityIndex]);
										break;
									case 21:
										select(m_spinCounty, m_adapterCounty, countyOfChongQing[m_iCityIndex]);
										break;
									case 22:
										select(m_spinCounty, m_adapterCounty, countyOfSiChuan[m_iCityIndex]);
										break;
									case 23:
										select(m_spinCounty, m_adapterCounty, countyOfGuiZhou[m_iCityIndex]);
										break;
									case 24:
										select(m_spinCounty, m_adapterCounty, countyOfYunNan[m_iCityIndex]);
										break;
									case 25:
										select(m_spinCounty, m_adapterCounty, countyOfXiZang[m_iCityIndex]);
										break;
									case 26:
										select(m_spinCounty, m_adapterCounty, countyOfShanXi2[m_iCityIndex]);
										break;
									case 27:
										select(m_spinCounty, m_adapterCounty, countyOfGanSu[m_iCityIndex]);
										break;
									case 28:
										select(m_spinCounty, m_adapterCounty, countyOfQingHai[m_iCityIndex]);
										break;
									case 29:
										select(m_spinCounty, m_adapterCounty, countyOfNingXia[m_iCityIndex]);
										break;
									case 30:
										select(m_spinCounty, m_adapterCounty, countyOfXinJiang[m_iCityIndex]);
										break;
									case 31:
										select(m_spinCounty, m_adapterCounty, countyOfHongKong[m_iCityIndex]);
										break;
									case 32:
										select(m_spinCounty, m_adapterCounty, countyOfAoMen[m_iCityIndex]);
										break;
									case 33:
										select(m_spinCounty, m_adapterCounty, countyOfTaiWan[m_iCityIndex]);
										break;

									default:
										break;
									}

									m_spinCounty.setOnItemSelectedListener(new OnItemSelectedListener() {

										@Override
										public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
										}

										@Override
										public void onNothingSelected(AdapterView<?> arg0) {
										}
									});
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}
						});
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

			// 设置默认省市区
			String[] strProvince = getResources().getStringArray(R.array.province_item);
			for (int i = 0; i < strProvince.length; i++) {
				QParkingApp appQParking = (QParkingApp) getApplicationContext();
				if (appQParking.m_addressAdd.province.contains(strProvince[i]))
					m_spinProvince.setSelection(i);
			}
			m_bIsSetDefault = true;
		}
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0)
			return;

		switch (requestCode) {
		case PICK_ADDRESS_RET:
			SetParkingInfo();
			break;
		}
	}

	private void select(Spinner spin, ArrayAdapter<CharSequence> adapter, int arry) {
		adapter = ArrayAdapter.createFromResource(this, arry, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);
		if (m_bIsSetDefault) {
			String[] strItem = getResources().getStringArray(arry);
			for (int i = 0; i < strItem.length; i++) {
				QParkingApp appQParking = (QParkingApp) getApplicationContext();
				if (spin == m_spinCity && appQParking.m_addressAdd.city.contains(strItem[i])) {
					m_spinCity.setSelection(i);
				}
				if (spin == m_spinCounty && appQParking.m_addressAdd.district.contains(strItem[i])) {
					m_spinCounty.setSelection(i);
					m_bIsSetDefault = false;
				}
			}
		}
	}
}
