package funion.app.qparking;

import java.util.ArrayList;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

//停车场信息节点
class TagMapItem {
	public String m_strName;
	public String m_strSize;
	public String m_strStatus;
};

@SuppressLint("DefaultLocale")
public class OfflineMapActivity extends Activity implements OnClickListener, MKOfflineMapListener {
	private MKOfflineMap m_mapOffline = null;
	private ListView m_lvMapOffline = null;
	private TextView m_tvTitle = null;
	private int m_iIndexSel = -1;

	// 离线地图列表
	private List<TagMapItem> m_listMap = new ArrayList<TagMapItem>();
	private ArrayList<MKOLSearchRecord> m_arrayMap = null;
	private MapListAdapter m_adapterMapList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.offline_map_activity);

		initView();

	}

	private void initView() {
		m_tvTitle = (TextView) findViewById(R.id.tvOfflineMapTitle);
		m_lvMapOffline = (ListView) findViewById(R.id.lvOfflineMap);
		// 链接按钮消息
		Button btTemp;
		btTemp = (Button) findViewById(R.id.btOfflineMapBack);
		btTemp.setOnClickListener(this);

		m_mapOffline = new MKOfflineMap();
		m_mapOffline.init(this);

		// 获取已下过的离线地图信息
		m_arrayMap = m_mapOffline.getOfflineCityList();
		if (m_arrayMap != null)
			FillMapList();
		// 实例化自定义适配器
		m_adapterMapList = new MapListAdapter(this, m_listMap);
		m_lvMapOffline.setAdapter(m_adapterMapList);
		// 连接列表消息
		m_lvMapOffline.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				MKOLSearchRecord recordSearch = m_arrayMap.get(position);
				if (m_iIndexSel > -1)
					recordSearch = m_arrayMap.get(m_iIndexSel).childCities.get(position);
				if (recordSearch.childCities == null) {
					if (m_listMap.get(position).m_strStatus.equals("已下载"))
						return;
					if (m_listMap.get(position).m_strStatus.equals("下载完成"))
						return;
					m_mapOffline.remove(recordSearch.cityID);
					m_mapOffline.start(recordSearch.cityID);
				} else {
					m_listMap.clear();
					for (MKOLSearchRecord record : recordSearch.childCities) {
						TagMapItem itemMap = new TagMapItem();

						itemMap.m_strName = record.cityName;
						itemMap.m_strSize = record.childCities == null ? formatDataSize(record.size) : "";
						itemMap.m_strStatus = record.childCities == null ? "下载" : "";
						MKOLUpdateElement elementUpdate = m_mapOffline.getUpdateInfo(record.cityID);
						if (elementUpdate != null) {
							if (elementUpdate.ratio < 100)
								itemMap.m_strStatus = String.format("%d%%", elementUpdate.ratio);
							else
								itemMap.m_strStatus = "已下载";
							if (elementUpdate.update)
								itemMap.m_strStatus = "可更新";
						}

						m_listMap.add(itemMap);
					}
					m_iIndexSel = position;
					m_adapterMapList.SetItemSrc(m_listMap);
					m_tvTitle.setText(recordSearch.cityName);
				}
				m_adapterMapList.notifyDataSetChanged();
			}
		});

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy() 2015年5月16日 OfflineMapActivity.java
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		m_mapOffline.destroy();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btOfflineMapBack: {
			if (m_tvTitle.getText().equals("离线地图"))
				finish();
			else {
				m_iIndexSel = -1;

				m_tvTitle.setText("离线地图");
				m_listMap.clear();
				FillMapList();
				m_adapterMapList.notifyDataSetChanged();
			}
		}
			break;
		}

	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		// TODO Auto-generated method stub
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement elementUpdate = m_mapOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (elementUpdate != null) {
				for (int i = 0; i < m_listMap.size(); i++) {
					TagMapItem itemMap = m_listMap.get(i);
					if (itemMap.m_strName.equals(elementUpdate.cityName)) {
						itemMap.m_strStatus = String.format("%d%%", elementUpdate.ratio);
						if (elementUpdate.ratio == 100)
							itemMap.m_strStatus = "下载完成";
						m_adapterMapList.notifyDataSetChanged();
					}
				}
			}
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			break;
		}
	}

	@SuppressLint("DefaultLocale")
	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}

		return ret;
	}

	public void FillMapList() {
		for (MKOLSearchRecord record : m_arrayMap) {
			TagMapItem itemMap = new TagMapItem();

			itemMap.m_strName = record.cityName;
			itemMap.m_strSize = record.childCities == null ? formatDataSize(record.size) : "";
			itemMap.m_strStatus = record.childCities == null ? "下载" : "";
			MKOLUpdateElement elementUpdate = m_mapOffline.getUpdateInfo(record.cityID);
			if (elementUpdate != null) {
				if (elementUpdate.ratio < 100)
					itemMap.m_strStatus = String.format("%d%%", elementUpdate.ratio);
				else
					itemMap.m_strStatus = "已下载";
				if (elementUpdate.update)
					itemMap.m_strStatus = "可更新";
			}
			m_listMap.add(itemMap);
		}
	}

	// 列表适配器
	class MapListAdapter extends BaseAdapter {
		private List<TagMapItem> m_listItem;
		private LayoutInflater m_layoutInflater;

		public MapListAdapter(Context context, List<TagMapItem> list) {
			m_listItem = list;
			m_layoutInflater = LayoutInflater.from(context);
		}

		public void SetItemSrc(List<TagMapItem> list) {
			m_listItem = list;
		}

		@Override
		public int getCount() {
			return m_listItem.size();
		}

		@Override
		public Object getItem(int position) {
			return m_listItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 控制ITEM 布局内容
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = m_layoutInflater.inflate(R.layout.map_item, null); // 加载布局

			TagMapItem itemMap = m_listItem.get(position);
			// 设置地图包名字
			TextView tvName = (TextView) convertView.findViewById(R.id.tvMapName);
			tvName.setText(itemMap.m_strName);
			// 设置地图包大小
			TextView tvSize = (TextView) convertView.findViewById(R.id.tvMapSize);
			tvSize.setText(itemMap.m_strSize);
			// 设置地图包状态
			TextView tvStatus = (TextView) convertView.findViewById(R.id.tvMapStatus);
			tvStatus.setText(itemMap.m_strStatus);

			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return m_listItem.size();
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
}
