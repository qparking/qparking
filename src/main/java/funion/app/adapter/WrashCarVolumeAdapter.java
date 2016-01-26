package funion.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.vo.WashCardBean;

/**
 * Created by Administrator on 2016/1/25.
 */
public class WrashCarVolumeAdapter extends BaseAdapter {
    private Context context;
    private List<WashCardBean> list_;
    private LayoutInflater inflater;

    public WrashCarVolumeAdapter(Context context,List<WashCardBean> list) {
        this.context = context;
        this.list_=list;
        Log.e("map", list_.toString());
        inflater=LayoutInflater.from(context);
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
            convertView=inflater.inflate(R.layout.list_item_park_volume,null);
            viewHolder.title_tv=(TextView)convertView.findViewById(R.id.title_tv);
            viewHolder.content_tv=(TextView)convertView.findViewById(R.id.content_tv);
            viewHolder.usescope_tv=(TextView)convertView.findViewById(R.id.usescope_tv);
            viewHolder.expire_time_tv=(TextView)convertView.findViewById(R.id.expire_time);
            viewHolder.status_im=(ImageView)convertView.findViewById(R.id.status_im);
            viewHolder.color_im=(ImageView)convertView.findViewById(R.id.color_im);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        String status=list_.get(position).getStatus();
        if (status.equals("0")){
            long endTime=Long.valueOf(list_.get(position).getEnd_date());
            long now=System.currentTimeMillis();
            if(endTime>now){
                viewHolder.status_im.setImageResource(R.drawable.overdue);
                viewHolder.color_im.setImageResource(R.drawable.wrashoverdue);
            }else {
                viewHolder.color_im.setImageResource(R.drawable.wrashvolume);
                viewHolder.status_im.setVisibility(View.INVISIBLE);
            }
        }else if(status.equals("1")){
            viewHolder.status_im.setImageResource(R.drawable.used);
            viewHolder.color_im.setImageResource(R.drawable.wrashoverdue);
        }
        viewHolder.title_tv.setText(list_.get(position).getCouponName());
        viewHolder.expire_time_tv.setText(TransCoding.strToDate(list_.get(position).getEnd_date()));
        return convertView;
    }

    public class ViewHolder{
        private TextView title_tv,content_tv,usescope_tv,expire_time_tv;
        private ImageView status_im,color_im;
    }
}
