package funion.app.qparking;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.umeng.analytics.MobclickAgent;

public class HoldMapActivity extends Activity implements OnClickListener,
		OnGetGeoCoderResultListener {
	private View m_viewLocation = null;
	private GeoCoder m_coderGeo = null;
	private LocationClient m_clientLocation;
	private MapView m_viewBaiduMap;
	private int m_iLocationCount;

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

			// 地图动画
			QParkingApp appQParking = (QParkingApp) getApplicationContext();
			appQParking.m_llMe = new LatLng(location.getLatitude(),
					location.getLongitude());
			// 容错判断
			if (appQParking.m_llMe.latitude == 0
					&& appQParking.m_llMe.longitude == 0) {
				m_iLocationCount++;
				if (m_iLocationCount > 3)
					m_clientLocation.stop();

				return;
			}

			// 反Geo搜索
			MapStatusUpdate updateStatus = MapStatusUpdateFactory
					.newLatLngZoom(appQParking.m_llMe, 17);
			m_viewBaiduMap.getMap().animateMapStatus(updateStatus);

			// 停止定位
			m_clientLocation.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hold_to_pick_address_activity);

		m_iLocationCount = 0;
		// 链接按钮消息
		Button btTemp;
		btTemp = (Button) findViewById(R.id.btHoldBack);
		btTemp.setOnClickListener(this);

		m_viewBaiduMap = (MapView) findViewById(R.id.holdBaiduMap);
		m_viewLocation = LayoutInflater.from(HoldMapActivity.this).inflate(
				R.layout.location_point, null);

		m_viewBaiduMap.showZoomControls(false);
		m_viewBaiduMap.showScaleControl(false);

		m_viewBaiduMap.getMap().setMyLocationEnabled(true);
		m_viewBaiduMap.getMap().setMyLocationConfigeration(
				new MyLocationConfiguration(LocationMode.NORMAL, true,
						BitmapDescriptorFactory.fromView(m_viewLocation)));

		m_viewBaiduMap.getMap().setOnMapLongClickListener(
				new OnMapLongClickListener() {
					public void onMapLongClick(LatLng point) {
						QParkingApp appQParking = (QParkingApp) getApplicationContext();
						appQParking.m_llSubmit = point;
						m_coderGeo.reverseGeoCode(new ReverseGeoCodeOption()
								.location(point));
					}
				});
		// 初始化搜索模块，注册事件监听
		m_coderGeo = GeoCoder.newInstance();
		m_coderGeo.setOnGetGeoCodeResultListener(this);
		// 定位初始化
		m_clientLocation = new LocationClient(this);
		m_clientLocation.registerLocationListener(new LocationListener());

		LocationClientOption optionLocation = new LocationClientOption();
		optionLocation.setOpenGps(true);// 打开gps
		optionLocation.setCoorType("bd09ll"); // 设置坐标类型为百度坐标
		optionLocation.setScanSpan(1000);
		m_clientLocation.setLocOption(optionLocation);
		m_clientLocation.start();


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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btHoldBack:
			finish();
			break;
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			QParkingApp.ToastTip(HoldMapActivity.this, "定位失败，请稍后重试！", -100);
			return;
		}

		QParkingApp appQParking = (QParkingApp) getApplicationContext();
		appQParking.m_addressAdd = result.getAddressDetail();
		appQParking.m_llMe=result.getLocation();
		setResult(1, null);
		finish();
	}
}
