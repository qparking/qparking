package funion.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.vo.PlateNumBean;

/**
 * Created by 运泽 on 2015/12/19.
 */
public class ABstractSpinnerAdapter extends BaseAdapter{

    public static interface IOnItemSelectListener{
        public void onItemClick(int pos);
    }

    private Context mContext;
    private List<PlateNumBean> mObjects;
    private int mSelectItem = 0;

    private LayoutInflater mInflater;

    public  ABstractSpinnerAdapter(Context context){
        init(context);
    };

    public void refreshData(List<PlateNumBean> objects, int selIndex){
        mObjects = objects;
        if (selIndex < 0){
            selIndex = 0;
        }
        if (selIndex >= mObjects.size()){
            selIndex = mObjects.size() - 1;
        }

        mSelectItem = selIndex;
    }

    private void init(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return mObjects.size();
    }

    @Override
    public Object getItem(int pos) {
        return mObjects.get(pos).toString();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinneritem, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextView.setText(mObjects.get(pos).getPlatenum());

        return convertView;
    }

    public static class ViewHolder
    {
        public TextView mTextView;
    }
}
