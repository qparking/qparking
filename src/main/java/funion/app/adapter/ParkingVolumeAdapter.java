package funion.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.vo.WashCardBean;

/**
 * Created by yunze on 2015/12/17.
 */
public class ParkingVolumeAdapter extends BaseAdapter {
    private Context context;
    private List<WashCardBean> list_;

    public ParkingVolumeAdapter(Context context,List<WashCardBean> list) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.list_item_park_volume, null);
        return convertView;
    }

    public class ViewHolder{

    }
}
