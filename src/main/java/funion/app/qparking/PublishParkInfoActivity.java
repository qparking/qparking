package funion.app.qparking;/**
 * Created by yunze on 2015/12/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import funion.app.adapter.PlotsAdapter;
import funion.app.qparking.popWindow.SelectPlot;
import funion.app.qparking.tools.ActivityTools;

public class PublishParkInfoActivity extends Activity implements View.OnClickListener,OnGetPoiSearchResultListener{
    private LinearLayout seletcadd_ll;
    private EditText enter_carport_num;
    private TextView select_plot_tv,location_place,upload,download,add_detail;
    private ImageView select_plot_iv;
    private Button nexttype_btn;
    private String PLOT="小区";
    final int POI_SEARCH_PAGE_CAPACITY = 10;
    List<String> poit_ls;
    List<String> plot_id;
    int m_iPageNum = 0;
    Context context;
    final int PICK_ADDRESS_RET = 0;
    private PoiSearch m_poiSearch = null;
    private final int radius=1000;
    SelectPlot selectPlot;
   PlotsAdapter plotsAdapter;
    String pro,city,dis;
    private int n=0;
    private String village,villageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_publish_park_info);
        context=this;
        initTitle();
        initView();
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("车位信息");
    }

    private void initView() {
        seletcadd_ll=(LinearLayout)findViewById(R.id.select_add_ll);
        enter_carport_num=(EditText)findViewById(R.id.enter_carport_num);
        location_place=(TextView)findViewById(R.id.location_place);
        add_detail=(TextView)findViewById(R.id.add_detail);
        select_plot_tv=(TextView)findViewById(R.id.select_plot_tv);
        select_plot_iv=(ImageView)findViewById(R.id.select_plot_iv);
        upload=(TextView)findViewById(R.id.upload);
        download=(TextView)findViewById(R.id.download);
        nexttype_btn=(Button)findViewById(R.id.nexttype_btn);
        seletcadd_ll.setOnClickListener(this);
        select_plot_iv.setOnClickListener(this);
        upload.setOnClickListener(this);
        download.setOnClickListener(this);
        nexttype_btn.setOnClickListener(this);
        m_poiSearch=PoiSearch.newInstance();
        m_poiSearch.setOnGetPoiSearchResultListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.select_add_ll:
                Intent intent=new Intent(context,HoldMapActivity.class);
                startActivityForResult(intent, PICK_ADDRESS_RET);
                break;
            case R.id.select_plot_iv:
                MakeSearch();
                break;
            case R.id.nexttype_btn:
                if(enter_carport_num.getText().toString().equals(null)||enter_carport_num.getText().toString().equals("")){
                    QParkingApp.ToastTip(context,getResources().getString(R.string.enter_carport_num),-100);
                    return;
                    }
            if(select_plot_tv.getText().toString().equals(null)||select_plot_tv.getText().toString().equals("")){
                QParkingApp.ToastTip(context,getResources().getString(R.string.select_plot),-100);
                return;
                }
                QParkingApp appQParking = (QParkingApp) getApplicationContext();
                double latitude=appQParking.m_llMe.latitude;
                double longitude=appQParking.m_llMe.longitude;
                Bundle bundle=new Bundle();
                //车位编号
                bundle.putString("carport_num",enter_carport_num.getText().toString());
                //地上地下
                bundle.putString("ground",n+"");
                //省
                bundle.putString("pro",appQParking.m_addressAdd.province);
                //市
                bundle.putString("city",appQParking.m_addressAdd.city);
                //区
                bundle.putString("dis",appQParking.m_addressAdd.district);
                //经纬度
                bundle.putString("latitude",latitude+"");
                bundle.putString("longitude",longitude+"");
                //停车场标题
                bundle.putString("name","临时停车");
                //小区名字
                bundle.putString("village",select_plot_tv.getText().toString());
                //小区id
                bundle.putString("villageid",villageId);
                //详细地址
                bundle.putString("addressdetail",appQParking.m_addressAdd.street+appQParking.m_addressAdd.streetNumber);
                ActivityTools.switchActivity(context,ParkingpriceActivity.class,bundle);
                break;
            case R.id.upload:
                    upload.setBackgroundResource(R.color.app_green);
                    download.setBackgroundResource(R.color.app_white);
                n=1;
                break;
            case R.id.download:
                    upload.setBackgroundResource(R.color.app_white);
                    download.setBackgroundResource(R.color.app_green);
                    n=0;
                break;
        }
    }

    private void MakeSearch() {
       new Thread(new Runnable() {
           @Override
           public void run() {
//               m_dlgProgress=ProgressDialog.show(PublishParkInfoActivity.this,null,getResources().getString(R.string.searching),true,false);
               QParkingApp appQParking = (QParkingApp) getApplicationContext();
               PoiNearbySearchOption option=new PoiNearbySearchOption();
               option.keyword(PLOT);
               option.pageNum(m_iPageNum);
               option.pageCapacity(POI_SEARCH_PAGE_CAPACITY);
               option.location(appQParking.m_llMe);
               option.radius(radius);
               m_poiSearch.searchNearby(option);
           }
       }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(resultCode==0){
           return;
       }
        if(requestCode==PICK_ADDRESS_RET){
            SetParkingInfo();
            select_plot_tv.setText("");
        }
    }

    private void SetParkingInfo() {
        QParkingApp appQParking = (QParkingApp) getApplicationContext();
        location_place.setText(appQParking.m_addressAdd.province+" "+appQParking.m_addressAdd.city+" "+appQParking.m_addressAdd.district);
        add_detail.setText(appQParking.m_addressAdd.street+" "+appQParking.m_addressAdd.streetNumber);
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
//        m_dlgProgress.dismiss();
        if(poiResult==null||poiResult.error== SearchResult.ERRORNO.RESULT_NOT_FOUND){
            QParkingApp.ToastTip(context,"未找到相关信息",-100);
            return;
        }
        if(poiResult.error==SearchResult.ERRORNO.NO_ERROR){
            int iCount=poiResult.getAllPoi().size();
            poit_ls=new ArrayList<String>();
            plot_id=new ArrayList<String>();
            poit_ls.clear();
            plot_id.clear();
            for(int i=0;i<iCount;i++){
                PoiInfo info=poiResult.getAllPoi().get(i);
                poit_ls.add(info.name);
                plot_id.add(info.uid);
            }
            Toast.makeText(context,poit_ls.toString(),Toast.LENGTH_SHORT);
            plotsAdapter=new PlotsAdapter(context);
            plotsAdapter.notifyDataSetChanged();

            selectPlot=new SelectPlot(context,itemOnclick,poit_ls,plot_id);
            selectPlot.showAtLocation(PublishParkInfoActivity.this.findViewById(R.id.mian_plot), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);

        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    private AdapterView.OnItemClickListener itemOnclick=new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            select_plot_tv.setText(poit_ls.get(i));
            village=poit_ls.get(i);
            villageId=plot_id.get(i);
            selectPlot.dismiss();
        }
    };


}
