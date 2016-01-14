package funion.app.qparking;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RouteActivity extends Activity implements OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_activity);

		// 获取应用程序对象
		QParkingApp appQParking = (QParkingApp) getApplicationContext();

		// 返回按钮
		Button btBack = (Button) findViewById(R.id.btRouteBack);
		btBack.setOnClickListener(this);

		// 设置停车场列表
		ListView lvRoute = (ListView) this.findViewById(R.id.lvRouteInfo);

		// 设置路线列表
		List<String> listItem = new ArrayList<String>();
		boolean bIsPickCar = getIntent().getBooleanExtra("PickCar", false);
		for (int i = 0; i < appQParking.m_routeLine.getAllStep().size(); i++) {
			if (bIsPickCar) {
				WalkingRouteLine.WalkingStep stepWalking = (WalkingRouteLine.WalkingStep) appQParking.m_routeLine
						.getAllStep().get(i);
				listItem.add(stepWalking.getInstructions());
			} else {
				DrivingRouteLine.DrivingStep stepDriving = (DrivingRouteLine.DrivingStep) appQParking.m_routeLine
						.getAllStep().get(i);
				listItem.add(stepDriving.getInstructions());
			}
		}
		// 实例化自定义适配器
		InfoListAdapter adapterRouteList = new InfoListAdapter(this, listItem);
		lvRoute.setAdapter(adapterRouteList);
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
		case R.id.btRouteBack:
			finish();
			break;
		}
	}

	// 列表适配器
	class InfoListAdapter extends BaseAdapter {
		private List<String> m_listItem;
		private LayoutInflater m_layoutInflater;

		public InfoListAdapter(Context context, List<String> list) {
			m_listItem = list;
			m_layoutInflater = LayoutInflater.from(context);
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
				convertView = m_layoutInflater.inflate(R.layout.route_info_item, null); // 加载布局

			// 设置索引号
			TextView tvIndex = (TextView) convertView.findViewById(R.id.tvRouteIndex);
			tvIndex.setText(String.format("%d.", position + 1));
			// 设置路线内容
			TextView tvName = (TextView) convertView.findViewById(R.id.tvRouteContent);
			tvName.setText(m_listItem.get(position));

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
