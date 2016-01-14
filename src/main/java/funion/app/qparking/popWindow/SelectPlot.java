package funion.app.qparking.popWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import java.util.List;

import funion.app.adapter.PlotsAdapter;
import funion.app.qparking.R;
import funion.app.qparking.tools.DisplayUtil;

/**
 * Created by 运泽 on 2015/12/29.
 */
public class SelectPlot extends PopupWindow{
    private View view;
    private ListView plot_lv;

    public SelectPlot(Context context,AdapterView.OnItemClickListener itemClickListener,List<String> plots,List<String> plots_id){
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.select_plot,null);
        plot_lv=(ListView)view.findViewById(R.id.show_plot_lv);
        this.setContentView(view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(DisplayUtil.dip2px(context, 300));
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupSlowAnimation);
        plot_lv.setOnItemClickListener(itemClickListener);

        PlotsAdapter plotsAdapter=new PlotsAdapter(context,plots,plots_id);

        plot_lv.setAdapter(plotsAdapter);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height=view.findViewById(R.id.plot_hight).getTop();
                int y=(int)motionEvent.getY();
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });
    }


}
