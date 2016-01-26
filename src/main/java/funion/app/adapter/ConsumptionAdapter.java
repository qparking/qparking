package funion.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.vo.MyOrderConsumption;

/**
 * 我的订单-消费适配器
 * Created by yunze on 2015/12/15.
 */
public class ConsumptionAdapter extends BaseAdapter {
    private Context context;
    private List<MyOrderConsumption> list;
    DisplayImageOptions options;


    public ConsumptionAdapter(Context context, List<MyOrderConsumption> list) {
        this.context = context;
        this.list = list;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_empty) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_error)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_error)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)    //设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_consumption, null);
            viewHolder.parkingImg = (ImageView) convertView.findViewById(R.id.park_img_iv);
            viewHolder.parkingName = (TextView) convertView.findViewById(R.id.park_name_tv);
            viewHolder.consumptionTime = (TextView) convertView.findViewById(R.id.consumption_time_tv);
            viewHolder.consumptionMoney = (TextView) convertView.findViewById(R.id.consumption_money_tv);
            viewHolder.payMethod = (TextView) convertView.findViewById(R.id.status_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(list.get(position).getAvatar(), viewHolder.parkingImg, options);
        viewHolder.parkingName.setText(list.get(position).getPlateNum());
        viewHolder.consumptionTime.setText(TransCoding.strToDate(list.get(position).getTime()));
        viewHolder.consumptionMoney.setText("¥:" + list.get(position).getPay() + "元");
        viewHolder.payMethod.setText(payMethod(list.get(position).getChannel()));
        return convertView;
    }

    public class ViewHolder {
        private ImageView parkingImg;
        private TextView parkingName;
        private TextView consumptionTime;
        private TextView consumptionMoney;
        private TextView payMethod;
    }

    /**
     * 判断支付类型方法
     *
     * @param type 支付类型
     * @return
     */
    private String payMethod(String type) {
        if (type.equals("wx")) {
            return "微信支付";
        }
        if (type.equals("alipay")) {
            return "阿里支付";
        }
        return "余额";
    }
}
