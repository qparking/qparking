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
import funion.app.qparking.vo.MyReleaseTools;

/**
 * Created by 运泽 on 2015/12/31.
 */
public class MyReleaseCarPortAdapter extends BaseAdapter {
    private  Context context_;
    private List<MyReleaseTools> list_;
    LayoutInflater inflater;

    public MyReleaseCarPortAdapter(Context context ,List<MyReleaseTools> list){
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
        ViewHolder viewHolder=null;
        if(view==null){
            viewHolder=new ViewHolder();
            view=inflater.inflate(R.layout.myreleasecarport_item,null);
            viewHolder.plot_name=(TextView)view.findViewById(R.id.plot_name);
            viewHolder.carport_name=(TextView)view.findViewById(R.id.release_name);
            viewHolder.add_time=(TextView)view.findViewById(R.id.release_time);
            viewHolder.carport_num=(TextView)view.findViewById(R.id.release_carportnum);
            viewHolder.status=(ImageView)view.findViewById(R.id.release_status);
            viewHolder.ground=(TextView)view.findViewById(R.id.release_ground);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
        for(int i=0;i<list_.size();i++){
            viewHolder.plot_name.setText(list_.get(position).getPlot_name());
            viewHolder.carport_name.setText(list_.get(position).getName());
            viewHolder.add_time.setText(list_.get(position).getAddTime());
            viewHolder.carport_num.setText(list_.get(position).getCarprot_num());
            String groundnum=list_.get(position).getPlace();
            if(groundnum.equals("0")){
                viewHolder.ground.setText(R.string.download);
            }else if(groundnum.equals("1")){
                viewHolder.ground.setText(R.string.upload);
            }
            String statusnum=list_.get(position).getStatus();
            if(statusnum.equals("0")){
                viewHolder.status.setBackgroundResource(R.drawable.undownline);
            }else if(statusnum.equals("1")){
                viewHolder.status.setBackgroundResource(R.drawable.online_state);
            }else if(statusnum.equals("2")){
                viewHolder.status.setBackgroundResource(R.drawable.prepare);
            }else if(statusnum.equals("3")){
                viewHolder.status.setBackgroundResource(R.drawable.unpass);
            }else if(statusnum.equals("4")){
                viewHolder.status.setBackgroundResource(R.drawable.forbidden);
            }

        }

        return view;
    }

    public class ViewHolder{
        TextView plot_name,carport_name,add_time,carport_num,ground;
        ImageView status;
    }
}
