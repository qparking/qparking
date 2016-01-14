package funion.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.vo.MyOrderBooking;

/**
 * 我的订单-预约适配器
 * Created by yunze on 2015/12/15.
 */
public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<MyOrderBooking> list;

    public OrderAdapter(Context context, List<MyOrderBooking> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_order, null);
            viewHolder.parkName = (TextView) convertView.findViewById(R.id.park_name_tv);
            viewHolder.cost = (TextView) convertView.findViewById(R.id.cost_tv);
            viewHolder.orderTime = (TextView) convertView.findViewById(R.id.order_time_tv);
            viewHolder.licencePlate = (TextView) convertView.findViewById(R.id.licence_plate_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.parkName.setText(list.get(position).getParkName());
        viewHolder.cost.setText(list.get(position).getCost());
        viewHolder.orderTime.setText(AppTools.getTimeformat(list.get(position).getReserve_time()));
        viewHolder.licencePlate.setText(list.get(position).getPlateNum());
        return convertView;
    }

    public class ViewHolder {
        private TextView parkName;
        private TextView cost;
        private TextView orderTime;
        private TextView licencePlate;
    }
}
