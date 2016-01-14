package funion.app.qparking.popWindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;

import funion.app.adapter.ABstractSpinnerAdapter;
import funion.app.adapter.ABstractSpinnerAdapter.IOnItemSelectListener;
import funion.app.qparking.R;

/**
 * Created by 运泽 on 2015/12/19.
 */
public class SpinnerPopWindows extends PopupWindow implements OnItemClickListener {
    private Context mContext;
    private ListView mListView;
    private ABstractSpinnerAdapter mAdapter;
    private IOnItemSelectListener mItemSelectListener;

    public SpinnerPopWindows(Context context){
        super(context);
        mContext=context;
        init();
    }

    private void init(){
        View view= LayoutInflater.from(mContext).inflate(R.layout.spinner_window_layout,null);
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw=new ColorDrawable(0x00);
        setBackgroundDrawable(dw);

        mListView=(ListView)view.findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);

    }




    public void setItemListener(IOnItemSelectListener listener){
        mItemSelectListener = listener;
    }

    public void setAdatper(ABstractSpinnerAdapter adapter){
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        dismiss();
        if (mItemSelectListener != null){
            mItemSelectListener.onItemClick(i);
        }

    }
}
