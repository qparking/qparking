package funion.app.qparking;
/**
 * 车牌管理
 * Created by yunze on 2015/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.CarLicenceManagerAdapter;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.vo.PlateNumBean;

public class CarLicenceManagerActivity extends Activity implements View.OnClickListener {
    private ImageView addImg;
    private CarLicenceManagerAdapter adapter;
    private ListView listView;
    private Context context = CarLicenceManagerActivity.this;
    SharedPreferences sp;
    List<PlateNumBean> list;
    private static final int GET_PLATENUM_SUCCESS=1;
    private static final int GET_PLATENUM_FAILS=2;
    int originaldefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_parking_lot);
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        initTitle();
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.my_parking_lot_lv);
        initData();
    }

    private void initData() {
        list = new ArrayList<PlateNumBean>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sign", sp.getString("sign", null));
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = (int) jsonObject.get("code");
                    if (code == 0) {
                        String count = (String) jsonObject.get("count");
                        int count_ = Integer.valueOf(count);
                        if (count_ != 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                PlateNumBean bean = new PlateNumBean();
                                JSONObject js = jsonArray.getJSONObject(i);
                                bean.setPlatenum(TransCoding.trans(js.getString("plateNum")));
                                bean.setIsDefault(js.getString("isDefault"));
                                bean.setPlateType(js.getString("plateType"));
                                String isDefault=js.getString("isDefault");
                                if(isDefault.equals("1")){
                                    list.add(0,bean);
                                }else {
                                    list.add(bean);
                                }
                            }

                            handler.sendEmptyMessage(GET_PLATENUM_SUCCESS);
                        } else {
                            handler.sendEmptyMessage(GET_PLATENUM_FAILS);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, "getuserplate");
    }

    private void initTitle() {
        addImg = (ImageView) findViewById(R.id.include_iv_right);
        addImg.setVisibility(View.VISIBLE);
        addImg.setBackgroundResource(R.drawable.add);
        addImg.setOnClickListener(this);
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.top_back_btn);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("车牌管理");

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_PLATENUM_SUCCESS:
                    adapter = new CarLicenceManagerAdapter(context,list,handler);
                    listView.setAdapter(adapter);
                    break;
                case GET_PLATENUM_FAILS:
                    break;
                case CarLicenceManagerAdapter.SELECTPOSITION:
                    int position=msg.arg1;
                    setDefaultCarPlateNum(position);
                    break;
                case CarLicenceManagerAdapter.DELETEPOSITION:
                    int delposition=msg.arg1;
                    deletePlateNum(delposition);
                    break;
                case CarLicenceManagerAdapter.DEFAULTPOSITION:
                    originaldefault=msg.arg1;
                    break;

            }
        }
    };

    private void deletePlateNum(final int delposition) {
        String delcarplatenum=list.get(delposition).getPlatenum();
        Map<String,String> params=new HashMap<String,String>();
        params.put("sign",sp.getString("sign", null));
        params.put("platenum",delcarplatenum);
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    int code=(int)jsonObject.get("code");
                    String msg=(String)jsonObject.get("msg");
                    if(code==0){
                        QParkingApp.ToastTip(context,TransCoding.trans(msg),-100);
                        list.remove(delposition);
                        adapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },params,"delplatenums");
    }

    private void setDefaultCarPlateNum(final int position) {
        String carplatenum=list.get(position).getPlatenum();
        Map<String,String> params=new HashMap<String,String>();
        params.put("sign",sp.getString("sign", null));
        params.put("platenum",carplatenum);
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    int code= (int) jsonObject.get("code");
                    String backmsg= (String) jsonObject.get("msg");
                    if(code==0){
                        QParkingApp.ToastTip(context, TransCoding.trans(backmsg), -100);
                        list.get(position).setIsDefault(1 + "");
                        list.get(originaldefault).setIsDefault(0 + "");
                        list.add(0, list.get(position));
                        list.remove(position+1);
                        adapter.notifyDataSetChanged();
                    }else{
                        QParkingApp.ToastTip(context,TransCoding.trans(backmsg),-100);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params,"defaultplatenum");
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.include_iv_right:
                if(list.size()>=3){
                    QParkingApp.ToastTip(context,getResources().getString(R.string.notice_delete_platenum),-100);
                    return;
                }
                Intent intent=new Intent(context,AddPlateNumber.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.include_iv_left:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            String code = data.getExtras().getString("result");
            if (code.equals(0+"")) {
                initData();
            }
        }
    }
}
