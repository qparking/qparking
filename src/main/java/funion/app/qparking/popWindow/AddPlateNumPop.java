package funion.app.qparking.popWindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

import funion.app.adapter.GridViewAdapter;
import funion.app.qparking.R;

/**
 * Created by 运泽 on 2016/1/8.
 */
public class AddPlateNumPop extends PopupWindow {
    private View view;
    private GridView gridView;
    private SimpleAdapter simpleAdapter;

    public AddPlateNumPop(Context context,AdapterView.OnItemClickListener onItemClickListener,String[] logogram,int[] fullname,
                          List<Map<String,Object>> data_list,String[] city_num){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.addplate_item,null);
        gridView=(GridView)view.findViewById(R.id.provice_gd);
        this.setContentView(view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupSlowAnimation);
        if(city_num!=null) {
            gridView.setAdapter(new GridViewAdapter(context,city_num));
        }else{
            simpleAdapter = new SimpleAdapter(context, data_list, R.layout.gridviewtext, logogram, fullname);
            gridView.setAdapter(simpleAdapter);
        }
        gridView.setOnItemClickListener(onItemClickListener);
        ColorDrawable dw=new ColorDrawable(00000000);
        this.setBackgroundDrawable(dw);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.pop_ll).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}
