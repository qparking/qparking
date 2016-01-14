package funion.app.qparking.popWindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import funion.app.adapter.SortVityAdapter;
import funion.app.qparking.R;

/**
 * Created by yunze on 2015/12/15.
 */
public class SortPartPop extends PopupWindow {
    private View v;
    private ListView sortCityListView;
    private SortVityAdapter adapter;

    public SortPartPop(Context context,List<String> list,AdapterView.OnItemClickListener onItemClickListener) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.pop_sort_city, null);
        sortCityListView = (ListView) v.findViewById(R.id.sort_city_lv);
        adapter = new SortVityAdapter(context,list);
        sortCityListView.setAdapter(adapter);
        sortCityListView.setOnItemClickListener(onItemClickListener);
        this.setContentView(v);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);

    }
}
