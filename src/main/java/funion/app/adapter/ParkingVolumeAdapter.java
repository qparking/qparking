package funion.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.vo.WashCardBean;

/**
 * Created by Administrator on 2016/1/25.
 */
public class ParkingVolumeAdapter extends BaseAdapter {
    private Context context;
    private List<WashCardBean> list_;
    private LayoutInflater inflater;

    public ParkingVolumeAdapter(Context context, List<WashCardBean> list) {
        this.context = context;
        this.list_ = list;
        Log.e("map", list_.toString());
        inflater = LayoutInflater.from(context);
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.parkingvolumeitem, null);
            viewHolder.parking_tv = (TextView) view.findViewById(R.id.parking_tv);
            viewHolder.expire_time = (TextView) view.findViewById(R.id.expire_time);
            viewHolder.value_money = (TextView) view.findViewById(R.id.value_money);
            viewHolder.vouchertype_tv = (TextView) view.findViewById(R.id.vouchertype_tv);
            viewHolder.status_im = (ImageView) view.findViewById(R.id.status_im);
            viewHolder.color_im = (ImageView) view.findViewById(R.id.color_im);
            viewHolder.rmb_im=(ImageView)view.findViewById(R.id.rmb_im);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String status=list_.get(i).getStatus();
        if(status.equals("0")){
            long endTime=Long.valueOf(list_.get(i).getEnd_date());
            long now=System.currentTimeMillis();
            String ctype=list_.get(i).getCtype();
            if(endTime>now){
                //已过期
                viewHolder.color_im.setImageResource(R.drawable.parkinggray);
                viewHolder.status_im.setImageResource(R.drawable.overdue);
                if(ctype.equals("0")){
                    viewHolder.vouchertype_tv.setText(R.string.deduction);
                }else if(ctype.equals("1")){
                    viewHolder.rmb_im.setVisibility(View.INVISIBLE);
                    viewHolder.vouchertype_tv.setText(R.string.freetime);
                }
            }else{
                viewHolder.parking_tv.setText(list_.get(i).getCouponName());
                viewHolder.expire_time.setText(TransCoding.strToDate(list_.get(i).getEnd_date()));
                viewHolder.value_money.setText(list_.get(i).getCouponAmount());
                if(ctype.equals("0")){
                    //按价格优惠
                    viewHolder.color_im.setImageResource(R.drawable.parkingformoney);
                    viewHolder.vouchertype_tv.setText(R.string.deduction);
                    viewHolder.status_im.setVisibility(View.INVISIBLE);
                }else if(ctype.equals("1")){
                    //按时间优惠
                    viewHolder.color_im.setImageResource(R.drawable.parkingfortime);
                    viewHolder.vouchertype_tv.setText(R.string.freetime);
                    viewHolder.status_im.setVisibility(View.INVISIBLE);
                    viewHolder.rmb_im.setVisibility(View.INVISIBLE);
                }
            }
        }else if(status.equals("1")){
            //已使用
            viewHolder.parking_tv.setText(list_.get(i).getCouponName());
            viewHolder.expire_time.setText(TransCoding.strToDate(list_.get(i).getEnd_date()));
            viewHolder.value_money.setText(list_.get(i).getCouponAmount());
            String ctype=list_.get(i).getCtype();
            viewHolder.color_im.setImageResource(R.drawable.parkinggray);
            viewHolder.status_im.setImageResource(R.drawable.used);
            if(ctype.equals("0")){
                viewHolder.vouchertype_tv.setText(R.string.deduction);
            }else if(ctype.equals("1")){
                viewHolder.rmb_im.setVisibility(View.INVISIBLE);
                viewHolder.vouchertype_tv.setText(R.string.freetime);
            }
        }
        return view;
    }

    public class ViewHolder {
        private TextView parking_tv, expire_time, value_money, vouchertype_tv;
        private ImageView status_im, color_im,rmb_im;
    }
}
