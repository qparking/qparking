package funion.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import funion.app.qparking.R;
import funion.app.qparking.vo.SelectPlotId;

/**
 * Created by 运泽 on 2016/1/4.
 */
public class PlotsReleaseCarPortAdapter extends BaseAdapter{
    private Context context_;
    private List<SelectPlotId> list_;
    LayoutInflater inflater;

    public PlotsReleaseCarPortAdapter(Context context,List<SelectPlotId> list){
        this.context_=context;
        this.list_=list;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list_.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
        if(view==null){
            viewHolder=new ViewHolder();
            view=inflater.inflate(R.layout.plotreleaseitem,null);
            viewHolder.plot_name=(TextView)view.findViewById(R.id.plot_name_tv);
            viewHolder.free_carport=(TextView)view.findViewById(R.id.free_carport_tv);
            viewHolder.address=(TextView)view.findViewById(R.id.plot_add_tv);
            viewHolder.distance=(TextView)view.findViewById(R.id.plot_distance_tv);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
        for(int i=0;i<list_.size();i++){
            viewHolder.plot_name.setText(list_.get(position).getName());
            double dis=(Double.valueOf(list_.get(position).getDistance()));
            if(dis>1000){
                dis=dis/1000;
                DecimalFormat decimalFormat=new DecimalFormat("#.00");
                String dis_kl=decimalFormat.format(dis);
                viewHolder.distance.setText(dis_kl+"公里");
            }else{
                DecimalFormat decimalFormat=new DecimalFormat("#.00");
                String dis_kl=decimalFormat.format(dis);
                viewHolder.distance.setText(dis_kl+"米");
            }
            viewHolder.free_carport.setText("空闲车位:"+list_.get(position).getFreenum());
            viewHolder.address.setText(list_.get(position).getAdd());
        }

        return view;
    }

    public class ViewHolder{
        TextView plot_name,address,distance,free_carport;
    }
}
