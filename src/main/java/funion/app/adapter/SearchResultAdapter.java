package funion.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.vo.TagParkingItem1;

/**
 * 搜索结果适配器
 * Created by yunze on 2016/1/4.
 */
public class SearchResultAdapter extends BaseAdapter {
    private ArrayList<TagParkingItem1> list;
    private Context context;

    public SearchResultAdapter(Context context, ArrayList<TagParkingItem1> list) {
        this.context = context;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_search_result, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name_tv);
            viewHolder.address = (TextView) convertView.findViewById(R.id.address_tv);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance_name_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(list.get(position).getM_strName());
        viewHolder.address.setText(list.get(position).getM_strAddress());
        viewHolder.distance.setText(AppTools.distance(list.get(position).getM_iDistance()));
        return convertView;
    }

    private class ViewHolder {
        private TextView name;
        private TextView address;
        private TextView distance;
    }
}
