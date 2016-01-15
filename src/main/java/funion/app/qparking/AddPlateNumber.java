package funion.app.qparking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.GridViewAdapter;
import funion.app.qparking.popWindow.AddPlateNumPop;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.JudgePlateNum;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;

/**
 * Created by Administrator on 2015/12/17 0017.
 * TODO 添加车牌界面
 */

public class AddPlateNumber extends Activity implements View.OnClickListener{

    GridView gv;
    List<Map<String,Object>> data_list;
    private SimpleAdapter simpleAdapter;
    EditText enter_et_;
TextView provice_tv_,city_num_tv_;
    AddPlateNumPop addPlateNumber;
    SharedPreferences sp;
    Context context;


    private String[] provice_logogram={"津","藏","粤","苏","冀","陕","桂","浙","晋","甘","皖","蒙","青","琼","闽","辽","宁","渝","赣"
    ,"吉","川","鲁","黑","新","贵","粤","沪","京","云","湘"};
    private String[] provice={"天津","西藏","广东","江苏","河北","陕西","广西","浙江","陕西","甘肃","安徽","内蒙古","青海",
    "海南","福建","辽宁","宁夏","重庆","江西","吉林","四川","山东","黑龙江","新疆","贵州","广东","上海","北京","云南","湖南"};
   private String[] city_num={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addplate_layout);
        context=this;
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        initView();
    }


    private void initView() {
           provice_tv_= (TextView)findViewById(R.id.provice_tv);
        city_num_tv_=(TextView)findViewById(R.id.city_num_tv);
        provice_tv_.setOnClickListener(this);
		city_num_tv_.setOnClickListener(this);  
        ((ImageView)findViewById(R.id.include_iv_left)).setOnClickListener(this);
        ((Button)findViewById(R.id.add_plate_num_next)).setOnClickListener(this);
        enter_et_=(EditText)findViewById(R.id.enter_et);
        ((ImageView)findViewById(R.id.include_iv_left)).setImageResource(R.drawable.come_back);
        ((TextView)findViewById(R.id.include_tv_title)).setText(getResources().getString(R.string.add_plate_num));
        gv=(GridView)findViewById(R.id.provice_gd);
        data_list=new ArrayList<Map<String,Object>>();
        getData();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.provice_tv:
                String [] logogram={"provice_logogram","fullname"};
                int [] fullname={R.id.logogram,R.id.fullname};
                addPlateNumber=new AddPlateNumPop(context,itemOnclick,logogram,fullname,data_list,null);
                addPlateNumber.showAtLocation(AddPlateNumber.this.findViewById(R.id.addplate_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.city_num_tv:
                addPlateNumber=new AddPlateNumPop(context,itemOnclickCityNum,null,null,null,city_num);
                addPlateNumber.showAtLocation(AddPlateNumber.this.findViewById(R.id.addplate_main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.add_plate_num_next:
                if(enter_et_.getText().toString().equals(null)||enter_et_.getText().toString().equals("")){
                Toast.makeText(AddPlateNumber.this,getResources().getString(R.string.enter_paltenum),Toast.LENGTH_SHORT).show();
                    return;
            }
                String platenum=provice_tv_.getText().toString()+city_num_tv_.getText().toString()+enter_et_.getText().toString();
                if(!JudgePlateNum.isPlateNum(platenum)){
                    Toast.makeText(AddPlateNumber.this,getResources().getString(R.string.enter_true_platenum),Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String,String> params=new HashMap<String,String>();
                params.put("sign",sp.getString("sign",null));
                params.put("platenum",platenum);
                OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String code = (String) jsonObject.get("code");
                            String msg = (String) jsonObject.get("err");
                            if (code.equals("0")) {
                                QParkingApp.ToastTip(context, TransCoding.trans(msg), -100);
                                Intent intent=new Intent();
                                intent.putExtra("result",code);
                                AddPlateNumber.this.setResult(RESULT_OK, intent);
                                AddPlateNumber.this.finish();
                            } else {
                                QParkingApp.ToastTip(context, TransCoding.trans(msg), -100);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, params, "addcard");
//                ActivityTools.switchActivity(AddPlateNumber.this, CarLicenceManagerActivity.class, null);
                break;
        }
    }

    private AdapterView.OnItemClickListener itemOnclick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String info = provice_logogram[position].toString();
            provice_tv_.setText(info);
            addPlateNumber.dismiss();
        }
    };
    private AdapterView.OnItemClickListener itemOnclickCityNum=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String num=city_num[position].toString();
            city_num_tv_.setText(num);
            addPlateNumber.dismiss();
        }
    };

    public List<Map<String,Object>> getData() {
      for(int i=0;i<provice_logogram.length;i++){
          Map<String,Object> map=new HashMap<String,Object>();
          map.put("provice_logogram",provice_logogram[i]);
          map.put("fullname",provice[i]);
          data_list.add(map);
      }
        return data_list;
    }

}
