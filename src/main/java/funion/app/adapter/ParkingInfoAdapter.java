package funion.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;

import java.util.ArrayList;

import funion.app.common.T;
import funion.app.qparking.BNavigatorActivity;
import funion.app.qparking.MainAct;
import funion.app.qparking.MainActivity;
import funion.app.qparking.NavigationAct;
import funion.app.qparking.NavigationActivity;
import funion.app.qparking.NearParkingActivity;
import funion.app.qparking.OrderParkingActivity;
import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import funion.app.qparking.vo.TagParkingItem1;

/**
 * 首页-停车场信息适配器
 * Created by yunze on 2015/12/25.
 */
public class ParkingInfoAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<TagParkingItem1> list;


    public ParkingInfoAdapter(Context context, ArrayList<TagParkingItem1> list) {
        this.context = context;
        this.list = list;
    }

    public ParkingInfoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);

    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.pager_parking_info, null, false);
        TextView parkingTitle = (TextView) v.findViewById(R.id.parking_title);
        parkingTitle.setText(list.get(position).getM_strName());
        TextView distance = (TextView) v.findViewById(R.id.address_tv);
        TextView freeTimeTv = (TextView) v.findViewById(R.id.free_num_tv);
        freeTimeTv.setText(list.get(position).getM_iFreeNum() == -1 ? "未知" : (list.get(position).getM_iFreeNum() + ""));
        distance.setText(list.get(position).getM_iDistance() + "m " + "|" + list.get(position).getM_strAddress());
        v.findViewById(R.id.rush_parking_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderParkingActivity.class);
                intent.putExtra("title", list.get(position).getM_strName());
                intent.putExtra("address", list.get(position).getM_strAddress());
                context.startActivity(intent);
            }
        });
        v.findViewById(R.id.go_parking_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QParkingApp appQParking = (QParkingApp) context.getApplicationContext();
////                BNaviPoint ptStart = new BNaviPoint(appQParking.m_llMe.longitude,
////                        appQParking.m_llMe.latitude, "我的位置",
////                        BNaviPoint.CoordinateType.BD09_MC);
////                BNaviPoint ptEnd = new BNaviPoint(
////                        list.get(position).getM_llParking().longitude,
////                        list.get(position).getM_llParking().latitude,
////                        list.get(position).m_strName,
////                        BNaviPoint.CoordinateType.BD09_MC);
////                BaiduNaviManager.getInstance().launchNavigator((MainAct) context,
////                        ptStart, ptEnd, RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, // 算路方式
////                        true, // 真实导航
////                        BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, // 在离线策略
////                        new BaiduNaviManager.OnStartNavigationListener() { // 跳转监听
////                            @Override
////                            public void onJumpToNavigator(Bundle configParams) {
////                                Intent intent = new Intent(context,
////                                        BNavigatorActivity.class);
////                                intent.putExtras(configParams);
////                                context.startActivity(intent);
////                            }
////
////                            @Override
////                            public void onJumpToDownloader() {
////
////                            }
////                        });
                if (DistanceUtil.getDistance(appQParking.m_llMe, list.get(position).getM_llParking()) < 50) {
                    T.showShort(context, "起始位置离终点太近，请重新定位");
                    return;
                }
                Intent intent = new Intent(context, NavigationAct.class);
                intent.putExtra("lat", list.get(position).getM_llParking().latitude);
                intent.putExtra("lon", list.get(position).getM_llParking().longitude);
                intent.putExtra("pid", list.get(position).getM_strPid());
                intent.putExtra("name", list.get(position).getM_strName());
                intent.putExtra("address", list.get(position).getM_strAddress());
                intent.putExtra("phone", list.get(position).getPhone());
                intent.putExtra("img", list.get(position).getParking_img());
                intent.putExtra("freeNum", list.get(position).getM_iFreeNum());
                intent.putExtra("chargeNum", list.get(position).getM_iChargeNum());
                intent.putExtra("praiseNum", list.get(position).getM_iPraiseNum());
                intent.putExtra("despiseNum", list.get(position).getM_iDespiseNum());//ShareName
                intent.putExtra("shareName", list.get(position).getM_strShareName());
                intent.putExtra("locationType", list.get(position).getM_iLocationType());
                intent.putExtra("distance", list.get(position).getM_iDistance());
                context.startActivity(intent);
            }
        });
        v.findViewById(R.id.list_parking_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,
                        NearParkingActivity.class);
                context.startActivity(intent);
            }
        });
        container.addView(v);
        return v;
    }

}
