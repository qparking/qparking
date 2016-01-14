package funion.app.qparking;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import java.util.List;
import funion.app.adapter.ShowPlotCarportAdapter;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.vo.AllReleaseTools;

/**
 * Created by 运泽 on 2016/1/5.
 * TODO小区停车位详细信息
 */
public class ShowPoltCarport extends Activity implements View.OnClickListener{
    Context context;
    private ImageView imageView;
    private ListView m_lvInfoList;
    ShowPlotCarportAdapter showPlotCarportAdapter;
    List<AllReleaseTools> list;
    QParkingApp appQParking;
    double dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_plot_carport);
        context=this;
        Intent intent=getIntent();
        list=intent.getParcelableArrayListExtra("list");
        appQParking =(QParkingApp) getApplicationContext();
        initView();
    }

    private void initView() {
        findViewById(R.id.include_back_left_ll).setOnClickListener(this);
        imageView=(ImageView)findViewById(R.id.include_iv_left);
        m_lvInfoList=(ListView)findViewById(R.id.showPlotsCarport);
        imageView.setBackgroundResource(R.drawable.top_back_btn);
        showPlotCarportAdapter=new ShowPlotCarportAdapter(context,list);
        m_lvInfoList.setAdapter(showPlotCarportAdapter);
        m_lvInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int headercount = m_lvInfoList.getHeaderViewsCount();
                LatLng lat = new LatLng(Double.valueOf(list.get(position - headercount).getLatitude()), Double.valueOf(list.get(position - 1).getLongitude()));
                appQParking.m_itemParking.m_llParking = lat;
                double distances = trust(lat);
                appQParking.m_itemParking.m_strPid = list.get(position - headercount).getUser_id();
                appQParking.m_itemParking.m_strName = list.get(position - headercount).getName();
                appQParking.m_itemParking.m_strAddress = list.get(position - headercount).getAddress();
                appQParking.m_itemParking.m_strShareName = list.get(position - headercount).getLinkman();
                appQParking.m_itemParking.m_iLocationType = Integer.valueOf(list.get(position - headercount).getGround());
                appQParking.m_itemParking.m_iDistance = (int) distances;
                ActivityTools.switchActivity(context, NavigationActivity.class, null);
            }
        });
    }

    private double trust(LatLng lat) {
        double latf=appQParking.m_llMe.latitude;
        double lonf=appQParking.m_llMe.longitude;
        LatLng from=new LatLng(latf,lonf);
        dis= DistanceUtil.getDistance(from,lat);
        return dis;
    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())){
            case R.id.include_back_left_ll:
                finish();
                break;
        }
    }
}
