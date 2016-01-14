package funion.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.vo.AllReleaseTools;

/**
 * Created by 运泽 on 2016/1/5.
 */
public class ShowPlotCarportAdapter extends BaseAdapter {
    private Context context_;
    private List<AllReleaseTools> list_;
    LayoutInflater inflater;

    public ShowPlotCarportAdapter(Context context,List<AllReleaseTools> list){
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
            view=inflater.inflate(R.layout.detailcarport,null);
            viewHolder.number=(TextView)view.findViewById(R.id.num_tv);
            viewHolder.carportname=(TextView)view.findViewById(R.id.carport_name_tv);
            viewHolder.ground=(TextView)view.findViewById(R.id.ground_tv);
            viewHolder.carportnum=(TextView)view.findViewById(R.id.carport_number_tv);
            viewHolder.price=(TextView)view.findViewById(R.id.carport_price_tv);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
            int num=position+1;
            viewHolder.number.setText(num+"");
            viewHolder.carportname.setText(list_.get(position).getName());
            viewHolder.price.setText(list_.get(position).getCharge_number());
            viewHolder.carportnum.setText(list_.get(position).getParking_num());
            String grounds=list_.get(position).getGround();
            if(grounds.equals("0")){
                viewHolder.ground.setText("类型:"+context_.getResources().getString(R.string.download));
            }else if(grounds.equals("1")){
                viewHolder.ground.setText("类型:"+context_.getResources().getString(R.string.upload));
            }
        return view;
    }

    public class ViewHolder{
        TextView number,carportname,price,carportnum,ground;
    }
}
