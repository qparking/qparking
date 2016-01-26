package funion.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.vo.AllReleaseTools;

/**
 * Created by 运泽 on 2016/1/5.
 */
public class ShowPlotCarportAdapter extends BaseAdapter {
    private Context context_;
    private List<AllReleaseTools> list_;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    private int selectPosition = -1;
    private List<View> list = new ArrayList<>();

    public ShowPlotCarportAdapter(Context context, List<AllReleaseTools> list) {
        this.context_ = context;
        this.list_ = list;
        inflater = LayoutInflater.from(context);
        options = AppTools.confirgImgInfo(R.drawable.headimage_default, R.drawable.headimage_default);
    }

    @Override
    public int getCount() {
        return list_.size();
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
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.detailcarport, null);
            viewHolder.carportname = (TextView) view.findViewById(R.id.carport_name_tv);
            viewHolder.ground = (TextView) view.findViewById(R.id.type_tv);
            viewHolder.carportnum = (TextView) view.findViewById(R.id.number_tv);
            viewHolder.money = (TextView) view.findViewById(R.id.money_tv);
            viewHolder.parkingImg = (ImageView) view.findViewById(R.id.parking_img_iv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ImageLoader.getInstance().displayImage(list_.get(position).getAvatar(), viewHolder.parkingImg, options);
        viewHolder.carportname.setText(list_.get(position).getName());
        viewHolder.money.setText("￥" + list_.get(position).getCharge_number());
        viewHolder.carportnum.setText(list_.get(position).getParking_num());
        String grounds = list_.get(position).getGround();
        if (grounds.equals("0")) {
            viewHolder.ground.setText("类型:" + context_.getResources().getString(R.string.download));
        } else if (grounds.equals("1")) {
            viewHolder.ground.setText("类型:" + context_.getResources().getString(R.string.upload));
        }
        list.add(view);
        return view;
    }

    public class ViewHolder {
        private TextView carportname, money, carportnum, ground;
        private ImageView parkingImg;
    }

    public void setSelect(int position) {
        this.selectPosition = position;
        if (selectPosition != -1) {
            for (int i = 0; i < list_.size(); i++) {
                if (selectPosition == i) {
                    list.get(i).setBackgroundResource(R.color.gray);
                } else {
                    list.get(i).setBackgroundResource(R.color.app_transparent);
                }
            }
            notifyDataSetChanged();
        }
    }
}
