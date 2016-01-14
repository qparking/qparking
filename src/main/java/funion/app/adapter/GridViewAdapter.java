package funion.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import funion.app.qparking.R;


/**
 * Created by Administrator on 2015/12/18 0018.
 */
public class GridViewAdapter extends BaseAdapter {

    String [] city_num_;
    Context context_;
    LayoutInflater inflater;

    public GridViewAdapter(Context context,String[] city_num){
        context_=context;
        city_num_=city_num;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return city_num_.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            convertView=
                    inflater.inflate(R.layout.gridviewnum,null);
            holder=new Holder();
            holder.platenum=(TextView)convertView.findViewById(R.id.plate_num_tv);
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
        }
        holder.platenum.
                setText(city_num_[position]);
        return convertView;
    }

     class Holder{
         public   TextView platenum;
    }
}
