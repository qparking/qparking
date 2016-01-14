package funion.app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.vo.PlateNumBean;

/**
 * Created by yunze on 2015/12/17.
 */
public class CarLicenceManagerAdapter extends BaseAdapter {
    private Context context;
    private List<PlateNumBean> list_;
    LayoutInflater inflater;
    Handler mhandler;
    public static final int SELECTPOSITION=0;
    public static final int DELETEPOSITION=3;
    public static final int DEFAULTPOSITION=4;

    public CarLicenceManagerAdapter(Context context, List<PlateNumBean> list,Handler handler) {
        this.context = context;
        this.list_ = list;
        this.mhandler=handler;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list_.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.list_item_car_licence, null);
            viewHolder=new ViewHolder();
            viewHolder.default_delete=(TextView)convertView.findViewById(R.id.paltenum_delete_tv);
            viewHolder.default_re=(RelativeLayout)convertView.findViewById(R.id.default_re);
            viewHolder.default_im=(ImageView)convertView.findViewById(R.id.select_default_img);
            viewHolder.show_platenum_detail_tv=(TextView)convertView.findViewById(R.id.show_plate_detail_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
            String isdefault=list_.get(position).getIsDefault();
            viewHolder.show_platenum_detail_tv.setText(list_.get(position).getPlatenum());
            if(isdefault.equals("1")){
                Message message=new Message();
                message.what=DEFAULTPOSITION;
                message.arg1=position;
                mhandler.sendMessage(message);
                viewHolder.default_delete.setVisibility(View.GONE);
                viewHolder.default_im.setImageResource(R.drawable.agree_1);
                viewHolder.show_platenum_detail_tv.setBackgroundResource(R.color.blue);
                viewHolder.show_platenum_detail_tv.setTextColor(context.getResources().getColor(R.color.app_white));
            }else{
                viewHolder.default_delete.setVisibility(View.VISIBLE);
                viewHolder.default_im.setImageResource(R.drawable.me_point);
                viewHolder.show_platenum_detail_tv.setBackgroundResource(R.color.app_white);
                viewHolder.show_platenum_detail_tv.setTextColor(context.getResources().getColor(R.color.app_text_black));
            }
        Log.e("map",list_.toString());

        viewHolder.default_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = SELECTPOSITION;
                msg.arg1 = position;
                mhandler.sendMessage(msg);
            }
        });
        viewHolder.default_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 AlertDialog.Builder builder=new AlertDialog.Builder(context);

                builder.setMessage("确认退出吗");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Message message = new Message();
                        message.what = DELETEPOSITION;
                        message.arg1 = position;
                        mhandler.sendMessage(message);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog=builder.show();

            }
        });
        return convertView;
    }

    public class ViewHolder{
        private RelativeLayout default_re;
        private ImageView default_im;
        private TextView default_delete;
        private TextView show_platenum_detail_tv;
    }




}
