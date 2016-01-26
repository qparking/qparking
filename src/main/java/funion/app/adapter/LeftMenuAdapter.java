package funion.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.vo.LeftMenuIconBean;

/**
 * Created by Administrator on 2016/1/21.
 */
public class LeftMenuAdapter extends BaseAdapter {
    private Context context;
    private List<LeftMenuIconBean> leftMenuIconBeans;
    private List<Bitmap> leftmenulist_;
    private LayoutInflater m_layoutInflater;

    public LeftMenuAdapter(Context context,List<LeftMenuIconBean> list,List<Bitmap> leftmenulist){
        this.context=context;
        this.leftMenuIconBeans=list;
        this.leftmenulist_=leftmenulist;
        m_layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return leftMenuIconBeans.size();
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
            view=m_layoutInflater.inflate(R.layout.leftmenuitem,null);
            viewHolder.left_icon_im=(ImageView)view.findViewById(R.id.left_icon_im);
            viewHolder.left_menu_tv=(TextView)view.findViewById(R.id.left_menu_tv);
            viewHolder.left_menu_re=(RelativeLayout)view.findViewById(R.id.left_menu_re);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
            String name=leftMenuIconBeans.get(position).getTitle();
            viewHolder.left_icon_im.setImageBitmap(leftmenulist_.get(position));
            viewHolder.left_menu_tv.setText(name);
        String backColor="#"+leftMenuIconBeans.get(position).getColor();
        viewHolder.left_menu_re.setBackgroundColor(Color.parseColor(backColor));

        return view;
    }

    public class ViewHolder{
        private ImageView left_icon_im;
        private TextView left_menu_tv;
        private RelativeLayout left_menu_re;
    }
}
