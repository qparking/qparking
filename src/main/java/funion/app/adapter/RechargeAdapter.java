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
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.vo.MyOrderRecharge;

/**
 * Created by yunze on 2015/12/15.
 */
public class RechargeAdapter extends BaseAdapter {
    private Context context;
    private List<MyOrderRecharge> list;

    public RechargeAdapter(Context context, List<MyOrderRecharge> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_recharge, null);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time_tv);
            viewHolder.payMoney = (TextView) convertView.findViewById(R.id.pay_money_tv);
            viewHolder.payMethod = (TextView) convertView.findViewById(R.id.channel_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.time.setText(TransCoding.strToDate(list.get(position).getAddtime()));
        viewHolder.payMoney.setText("¥:" + list.get(position).getAmount() + "元");
        viewHolder.payMethod.setText(AppTools.payMethod(list.get(position).getChannel()));
        return convertView;
    }

    private class ViewHolder {
        private TextView time;
        private TextView payMoney;
        private TextView payMethod;
    }
}
