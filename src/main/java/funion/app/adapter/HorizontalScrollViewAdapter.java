package funion.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.vo.TagParkingItem1;

/**
 * Created by yunze on 2015/12/30.
 */
public class HorizontalScrollViewAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<TagParkingItem1> list;

    public HorizontalScrollViewAdapter(Context context, List<TagParkingItem1> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public HorizontalScrollViewAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return 5;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
//        if (convertView == null) {
        convertView = inflater.inflate(R.layout.pager_parking_info, parent,
                false);
//            viewHolder.title = (TextView) convertView.findViewById(R.id.parking_title);
//            viewHolder.address = (TextView) convertView.findViewById(R.id.address_tv);
//            viewHolder.up = (ImageView) convertView.findViewById(R.id.up_iv);
//            viewHolder.listIv = (ImageView) convertView.findViewById(R.id.list_parking_iv);
//            viewHolder.rushParking = (RelativeLayout) convertView.findViewById(R.id.rush_parking_rl);
//            viewHolder.goParking = (RelativeLayout) convertView.findViewById(R.id.go_parking_rl);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
        return convertView;
    }

    //        ViewHolder viewHolder = null;
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.list_hor_gridview, parent,
//                    false);
//            viewHolder = new ViewHolder();
//            viewHolder.mImg = (ImageView) convertView
//                    .findViewById(R.id.ItemImage);
//            viewHolder.mText = (TextView) convertView
//                    .findViewById(R.id.select_tv);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        viewHolder.mImg.setImageResource(mDates.get(position));
//        viewHolder.mText.setText(text[position]);
//        return convertView;
//    }
//
    private class ViewHolder {
        ImageView listIv;
        ImageView up;
        TextView title;
        TextView address;
        RelativeLayout rushParking;
        RelativeLayout goParking;
    }
}
