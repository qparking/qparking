package funion.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import funion.app.qparking.R;

/**
 * 历史搜索适配器
 * Created by yunze on 2016/1/4.
 */
public class SearchHistoryAdapter extends BaseAdapter {
    private List<String> list = new ArrayList<>();
    private Context context;

    public SearchHistoryAdapter(Context context, Set<String> set) {
        this.context = context;
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            list.add(o.toString());
            Log.e("list", o.toString());
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_search_history, null);
            holder = new ViewHolder();
            holder.tv = (TextView) view.findViewById(R.id.history_tv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv.setText(list.get(i));
        return view;
    }

    private class ViewHolder {
        private TextView tv;
    }
}
