package funion.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.SelectInfo;

/**
 * Created by yunze on 2015/12/15.
 */
public class SortVityAdapter extends BaseAdapter {
    private Context context;
    private List<String> list_;

    public SortVityAdapter(Context context,List<String> list) {
        this.context = context;
        this.list_=list;
    }

    @Override
    public int getCount() {
        return list_.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sort_city, null);
            viewHolder.city_show=(TextView)convertView.findViewById(R.id.city_show_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.city_show.setText(list_.get(position).toString());
        return convertView;
    }

    public class ViewHolder{
        TextView city_show;
    }
}
