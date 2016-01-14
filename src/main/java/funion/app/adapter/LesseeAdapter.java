package funion.app.adapter;

import java.util.ArrayList;

import funion.app.qparking.MainActivity;
import funion.app.qparking.ParkingDetailActivity;
import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LesseeAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> list;
	private LayoutInflater m_layoutInflater;

	private static class ViewHolder {
		TextView name;
	}

	public LesseeAdapter(Context context, ArrayList<String> leesseeList) {
		this.context = context;
		this.list = leesseeList;
		m_layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = m_layoutInflater.inflate(R.layout.lessee_item, null);

			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.lessee_area);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				QParkingApp.ToastTip(context, position + "", -100);
				Intent intent = new Intent();
				intent.setClass(context, ParkingDetailActivity.class);
				context.startActivity(intent);
			}
		});

		return convertView;
	}
}