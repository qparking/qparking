package funion.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import funion.app.qparking.R;

/**
 * Created by yunze on 2015/12/17.
 */
public class ParkingVolumeAdapter extends BaseAdapter {
    private Context context;

    public ParkingVolumeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 5;
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
}
