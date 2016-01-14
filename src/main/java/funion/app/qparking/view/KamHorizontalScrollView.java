package funion.app.qparking.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import funion.app.adapter.HorizontalScrollViewAdapter;
import funion.app.qparking.vo.TagParkingItem1;

/**
 * Created by yunze on 2015/12/30.
 */
public class KamHorizontalScrollView extends HorizontalScrollView {
    private LinearLayout wrapper;
    private HorizontalScrollViewAdapter mAdapter;
    private Context context;
    private List<View> list = new ArrayList<>();
    private float filingStartX;
    private float filingEndX;
    private int i;

    public KamHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public KamHorizontalScrollView(Context context) {
        super(context);
        this.context = context;
    }

    public void setAdapter(HorizontalScrollViewAdapter mAdapter) {
        this.mAdapter = mAdapter;
        wrapper = (LinearLayout) getChildAt(0);
        android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.width = getWinWidth();
        params.height = LayoutParams.WRAP_CONTENT;
        for (int i = 0; i < mAdapter.getCount() - 1; i++) {
            View v = mAdapter.getView(i, null, wrapper);
            v.setLayoutParams(params);
            list.add(v);
            addRight(list.get(i));
        }
        i = 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                filingStartX = ev.getX();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean addRight(View view) {
        if (view == null || wrapper == null)
            return false;
        android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.width = getWinWidth();
        params.height = LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(params);
        wrapper.addView(view);
        return true;
    }

    private int getWinWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
}
