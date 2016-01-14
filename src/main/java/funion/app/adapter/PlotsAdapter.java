package funion.app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import funion.app.qparking.vo.SelectPlotId;

/**
 * Created by 运泽 on 2015/12/29.
 */
public  class PlotsAdapter extends BaseAdapter {
    Context context_;
    List<String> plots_;
    List<String> plots_id_;
    private LayoutInflater inflater;

    public PlotsAdapter(Context context,List<String> plots,List<String> plots_id){
        context_=context;
        plots_=plots;
        plots_id_=plots_id;
        inflater=LayoutInflater.from(context);
    }
    public PlotsAdapter(Context context){
        context_=context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return plots_.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null){
            viewHolder=new ViewHolder();
            view=inflater.inflate(R.layout.showplots,null);
            viewHolder.tv=(TextView)view.findViewById(R.id.plots_tv);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
        SelectPlotId selectPlotId=new SelectPlotId();
        viewHolder.tv.setText(plots_.get(i));
        selectPlotId.setName(plots_.get(i));
        selectPlotId.setUid(plots_id_.get(i));
        return view;
    }

    public class ViewHolder{
        TextView tv;
    }
}
