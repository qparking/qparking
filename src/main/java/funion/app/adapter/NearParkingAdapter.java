package funion.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;

import java.util.ArrayList;

import funion.app.common.T;
import funion.app.qparking.BNavigatorActivity;
import funion.app.qparking.MainAct;
import funion.app.qparking.NavigationAct;
import funion.app.qparking.NearParkingActivity;
import funion.app.qparking.OrderParkingActivity;
import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.vo.TagParkingItem1;

/**
 * 附近停车场适配器
 * Created by yunze on 2015/12/29.
 */
public class NearParkingAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TagParkingItem1> tagParkingItem1ArrayList;

    public NearParkingAdapter(Context context, ArrayList<TagParkingItem1> tagParkingItem1ArrayList) {
        this.context = context;
        this.tagParkingItem1ArrayList = tagParkingItem1ArrayList;
    }

    @Override
    public int getCount() {
        return tagParkingItem1ArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return tagParkingItem1ArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_near_parking, null);
            viewHolder = new ViewHolder();
            viewHolder.parkingTitleTv = (TextView) view.findViewById(R.id.near_parking_title_tv);
            //  viewHolder.typeTv=view.findViewById(R.id.)
            viewHolder.addressTv = (TextView) view.findViewById(R.id.address_tv);
            viewHolder.distanceTv = (TextView) view.findViewById(R.id.distance_tv);
            viewHolder.rushRl = (RelativeLayout) view.findViewById(R.id.rush_parking_rl);
            viewHolder.goParkingRl = (RelativeLayout) view.findViewById(R.id.go_parking_rl);
            viewHolder.lotTv = (TextView) view.findViewById(R.id.lot_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
//        viewHolder.lotTv.setText(tagParkingItem1ArrayList.get(i).getM_iFreeNum());
        viewHolder.parkingTitleTv.setText(tagParkingItem1ArrayList.get(i).getM_strName());
        viewHolder.addressTv.setText(tagParkingItem1ArrayList.get(i).getM_strAddress());
        viewHolder.distanceTv.setText(AppTools.distance(tagParkingItem1ArrayList.get(i).getM_iDistance()));
        viewHolder.rushRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderParkingActivity.class);
                intent.putExtra("title", tagParkingItem1ArrayList.get(i).getM_strName());
                intent.putExtra("address", tagParkingItem1ArrayList.get(i).getM_strAddress());
                context.startActivity(intent);
            }
        });
        viewHolder.goParkingRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QParkingApp appQParking = (QParkingApp) context.getApplicationContext();
                BNaviPoint ptStart = new BNaviPoint(appQParking.m_llMe.longitude,
                        appQParking.m_llMe.latitude, "我的位置",
                        BNaviPoint.CoordinateType.BD09_MC);
                BNaviPoint ptEnd = new BNaviPoint(
                        tagParkingItem1ArrayList.get(i).getM_llParking().longitude,
                        tagParkingItem1ArrayList.get(i).getM_llParking().latitude,
                        tagParkingItem1ArrayList.get(i).m_strName,
                        BNaviPoint.CoordinateType.BD09_MC);
                BaiduNaviManager.getInstance().launchNavigator((NearParkingActivity) context,
                        ptStart, ptEnd, RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
                        true, // 真实导航
                        BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
                        new BaiduNaviManager.OnStartNavigationListener() { // 跳转监听
                            @Override
                            public void onJumpToNavigator(Bundle configParams) {
                                Intent intent = new Intent(context,
                                        NavigationAct.class);
//                                intent.putExtras(configParams);
                                intent.putExtra("lat", tagParkingItem1ArrayList.get(i).getM_llParking().latitude);
                                intent.putExtra("lon", tagParkingItem1ArrayList.get(i).getM_llParking().longitude);
                                intent.putExtra("pid", tagParkingItem1ArrayList.get(i).getM_strPid());
                                intent.putExtra("name", tagParkingItem1ArrayList.get(i).getM_strName());
                                intent.putExtra("address", tagParkingItem1ArrayList.get(i).getM_strAddress());
                                intent.putExtra("phone", tagParkingItem1ArrayList.get(i).getPhone());
                                intent.putExtra("img", tagParkingItem1ArrayList.get(i).getParking_img());
                                intent.putExtra("freeNum", tagParkingItem1ArrayList.get(i).getM_iFreeNum());
                                intent.putExtra("chargeNum", tagParkingItem1ArrayList.get(i).getM_iChargeNum());
                                intent.putExtra("praiseNum", tagParkingItem1ArrayList.get(i).getM_iPraiseNum());
                                intent.putExtra("despiseNum", tagParkingItem1ArrayList.get(i).getM_iDespiseNum());//ShareName
                                intent.putExtra("shareName", tagParkingItem1ArrayList.get(i).getM_strShareName());
                                intent.putExtra("locationType", tagParkingItem1ArrayList.get(i).getM_iLocationType());
                                intent.putExtra("distance", tagParkingItem1ArrayList.get(i).getM_iDistance());
                                context.startActivity(intent);
                            }

                            @Override
                            public void onJumpToDownloader() {

                            }
                        });
            }
        });
        return view;
    }

    private class ViewHolder {
        TextView parkingTitleTv;
        TextView addressTv;
        TextView distanceTv;
        RelativeLayout rushRl;
        RelativeLayout goParkingRl;
        TextView lotTv;
    }
}
