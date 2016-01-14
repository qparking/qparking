package funion.app.qparking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import funion.app.qparking.sqlite.MySqliteManager;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.JudgePlateNum;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;

/**
 * 车位价格页面
 * Created by yunze on 2015/12/16.
 */
public class ParkingpriceActivity extends Activity implements View.OnClickListener {
    private TextView times_settlements, days_settlements, do_next;
    private EditText price, person_name, contact_num;
    private int i = 1;
    Context context;
    private String carport_num,ground,pro,city,dis,addressdetail,latitude,longitude,name,village,villageid;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_parking_price);
        context = this;
        Intent intent=getIntent();
       carport_num=intent.getStringExtra("carport_num");
        ground=intent.getStringExtra("ground");
         pro=intent.getStringExtra("pro");
         city=intent.getStringExtra("city");
         dis=intent.getStringExtra("dis");
        addressdetail=intent.getStringExtra("addressdetail");
         latitude=intent.getStringExtra("latitude");
         longitude=intent.getStringExtra("longitude");
         name=intent.getStringExtra("name");
         village=intent.getStringExtra("village");
         villageid=intent.getStringExtra("villageid");
        initTitle();
        initView();
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        editor=sp.edit();
    }

    private void initView() {
        times_settlements = (TextView) findViewById(R.id.times_settlements);
        days_settlements = (TextView) findViewById(R.id.days_settlements);
        price = (EditText) findViewById(R.id.price);
        person_name = (EditText) findViewById(R.id.person_name);
        contact_num = (EditText) findViewById(R.id.contact_num);
        do_next = (TextView) findViewById(R.id.do_next);
        do_next.setOnClickListener(this);
        times_settlements.setOnClickListener(this);
        days_settlements.setOnClickListener(this);
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("车位价格");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.do_next:
                if(price.getText().toString().equals(null)||price.getText().toString().equals("")){
                    QParkingApp.ToastTip(context,getResources().getString(R.string.enter_price_first),-100);
                    return;
                }
                if(person_name.getText().toString().equals(null)||person_name.getText().toString().equals("")){
                    QParkingApp.ToastTip(context,getResources().getString(R.string.enter_contact_name),-100);
                    return;
                }
                if(contact_num.getText().toString().equals(null)||contact_num.getText().toString().equals("")){
                    QParkingApp.ToastTip(context,getResources().getString(R.string.enter_contact_phonenum),-100);
                    return;
                }
                if(!JudgePlateNum.isPhoNum(contact_num.getText().toString())){
                    QParkingApp.ToastTip(context,getResources().getString(R.string.enter_true_phonenum),-100);
                    return;
                }
                if(pro.equals(null)||pro.equals("")){
                    QParkingApp.ToastTip(context,"暂未定位到地址，请稍后重试",-100);
                    return;
                }
                if(city.equals(null)||city.equals("")){
                    QParkingApp.ToastTip(context,"暂未定位到地址，请稍后重试",-100);
                    return;
                }
                if(dis.equals(null)||dis.equals("")){
                    QParkingApp.ToastTip(context,"暂未定位到地址，请稍后重试",-100);
                    return;
                }
                MySqliteManager mySqliteManager=new MySqliteManager(context);
                mySqliteManager.openDatabase();
                mySqliteManager.select_pro(pro);
                mySqliteManager.select_city(city);
                mySqliteManager.select_dis(dis);
                SelectInfo selectInfo=new SelectInfo();

                Map<String,String> parm=new HashMap<String,String>();
                parm.put("sign",sp.getString("sign",null));
                parm.put("name",name);
                parm.put("longitude",longitude);
                parm.put("latitude",latitude);
                parm.put("village",village);
                parm.put("villageid",villageid);
                parm.put("province",selectInfo.getPro_code()+"");
                parm.put("city",selectInfo.getCity_code()+"");
                parm.put("area",selectInfo.getDis_code()+"");
                parm.put("address",addressdetail);
                parm.put("dtype",i+"");
                parm.put("freenumber","1");
                parm.put("chargenumber",price.getText().toString());
                parm.put("number","1");
                parm.put("ground",ground);
                parm.put("linkman",person_name.getText().toString());
                parm.put("contactWay",contact_num.getText().toString());
                parm.put("parkingNum",carport_num);
                Log.e("show",parm.toString());
                OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String result) {
                        try{
                            JSONObject jsonObject=new JSONObject(result);
                            String code=(String)jsonObject.get("code");
                            String msg=(String)jsonObject.getString("msg");
                            if(code.equals("0")){
                                QParkingApp.ToastTip(context, TransCoding.trans(msg), -100);
                                String parkId=(String)jsonObject.getString("parkId");
                                editor.putString("parkId",parkId);
                                editor.commit();
                                ActivityTools.switchActivity(context,UploadParkingPictureActivity.class,null);
                            }else{
                                QParkingApp.ToastTip(context, TransCoding.trans(msg),-100);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                },parm,"personalparkadd");



                break;
            case R.id.days_settlements:
                days_settlements.setBackgroundResource(R.color.app_green);
                times_settlements.setBackgroundResource(R.color.app_white);
                i = 2;
                break;
            case R.id.times_settlements:
                days_settlements.setBackgroundResource(R.color.app_white);
                times_settlements.setBackgroundResource(R.color.app_green);
                i = 1;
                break;
        }
    }
}
