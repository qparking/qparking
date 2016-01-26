package funion.app.qparking.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.WrashCarVolumeAdapter;
import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.vo.WashCardBean;

/**
 * Created by Administrator on 2016/1/16.
 */
public class WashingFragment extends Fragment {
    private View washingfragment;
    private ListView listView;
    private WrashCarVolumeAdapter adapter;
    SharedPreferences sp;
    private List<WashCardBean> list;
    private ProgressDialog m_dlgProgress = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(washingfragment==null){
            washingfragment=inflater.inflate(R.layout.washing_view,null);
            m_dlgProgress = ProgressDialog.show(getActivity(), null,
                    "正在获取信息... ", true, true);
            sp=getActivity().getSharedPreferences("mMessage", getActivity().MODE_PRIVATE);
            initData();
            findViewById();
        }
        return washingfragment;
    }

    private void initData() {
        list=new ArrayList<WashCardBean>();
        Map<String,String> params=new HashMap<String,String>();
        params.put("sign",sp.getString("sign",null));
        params.put("stype","0");
        params.put("ps","10");
        params.put("pn","0");

        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    String count=(String)jsonObject.get("count");
                    if(!count.equals(0)){
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){
                            WashCardBean washCardBean=new WashCardBean();
                            JSONObject object= (JSONObject) jsonArray.get(i);
                            washCardBean.setCouponId(object.getString("couponId"));
                            washCardBean.setMerchantId(object.getString("merchantId"));
                            washCardBean.setCouponName(object.getString("couponName"));
                            washCardBean.setCouponAmount(object.getString("couponAmount"));
                            washCardBean.setStatus(object.getString("status"));
                            washCardBean.setCtype(object.getString("ctype"));
                            washCardBean.setUseType(object.getString("utype"));
                            washCardBean.setServType(object.getString("servtype"));
                            washCardBean.setPlatformTicket(object.getString("platformTicket"));
                            washCardBean.setCouponNumber(object.getString("couponNumber"));
                            washCardBean.setMerchantName(object.getString("merchantName"));
                            washCardBean.setQrCode(object.getString("qrCode"));
                            washCardBean.setEnd_date(object.getString("end_date"));
                            list.add(washCardBean);
                        }
                        adapter = new WrashCarVolumeAdapter(getActivity(),list);
                        listView.setAdapter(adapter);
                        m_dlgProgress.dismiss();
                    }else{
                        m_dlgProgress.dismiss();
                        QParkingApp.ToastTip(getActivity(),"暂无数据",-100);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params,"getcoupon");
    }

    private void findViewById() {
        listView=(ListView)washingfragment.findViewById(R.id.washing_volume_lv);
    }
}
