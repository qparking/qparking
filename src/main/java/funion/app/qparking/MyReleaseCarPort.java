package funion.app.qparking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.adapter.MyReleaseCarPortAdapter;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.view.PullRefreshList;
import funion.app.qparking.vo.MyReleaseTools;

/**
 * Created by 运泽 on 2015/12/31.
 * TODO 我发布的个人停车位列表
 */
public class MyReleaseCarPort extends Activity implements View.OnClickListener {
    private PullRefreshList m_lvInfoList;
    private TextView title_,release_tv,no_release_carport;
    private ImageView back_arrow;
    private RelativeLayout back_ll;
    List<MyReleaseTools> myrelease;
    private static final int LOAD_MORE_DATA_HEADER = 0;
    private static final int LOAD_MORE_DATA_FOOTER = 1;
    private static final int LOAD_MORE_FAILED = 2;
    Context context;
    SharedPreferences sp;
    MyReleaseCarPortAdapter myReleaseCarPortAdapter;
    int m_iPageNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_carport);
        context=this;
        myrelease = new ArrayList<MyReleaseTools>();
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        initView();
    }

    private void initData() {
        Map<String,String> params=new HashMap<String,String>();
        params.put("sign",sp.getString("sign",null));
        params.put("ps",4+"");
        params.put("pn",m_iPageNum+"");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {

                    JSONObject jsonObject = new JSONObject(result);
                    if (result.equals(null) || result.equals("")) {
                        Log.e("show", "暂无数据返回");
                        return;
                    }
                    String count = (String) jsonObject.get("count");
                    if (!count.equals("0")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MyReleaseTools myReleaseTools = new MyReleaseTools();
                            JSONObject js = jsonArray.getJSONObject(i);
                            myReleaseTools.setName((String) js.get("name"));
                            String time = (String) js.get("add_time");
                            String yt = TransCoding.strToDate(time);
                            myReleaseTools.setAddTime(yt);
                            myReleaseTools.setCarprot_num((String) js.get("parking_num"));
                            myReleaseTools.setPlace((String) js.get("ground"));
                            myReleaseTools.setStatus((String) js.get("status"));
                            myReleaseTools.setPlot_name((String) js.get("village"));
                            myrelease.add(myReleaseTools);
                        }
                        m_hNotify.sendEmptyMessage(LOAD_MORE_DATA_FOOTER);

                    } else {
//                        no_release_carport.setVisibility(View.VISIBLE);
                        m_hNotify.sendEmptyMessage(LOAD_MORE_FAILED);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, "myparklist");
    }

    private void initView() {
        title_=(TextView)findViewById(R.id.include_tv_title);
        release_tv=(TextView)findViewById(R.id.include_tv_right);
        back_arrow=(ImageView)findViewById(R.id.include_iv_left);
        back_ll=(RelativeLayout)findViewById(R.id.include_back_left_ll);
        m_lvInfoList=(PullRefreshList)findViewById(R.id.lvReleaseParking);
        no_release_carport=(TextView)findViewById(R.id.no_release_carport);
        title_.setText(getResources().getString(R.string.my_carports));
        release_tv.setText(getResources().getString(R.string.release));
        back_arrow.setBackgroundResource(R.drawable.top_back_btn);
        back_ll.setOnClickListener(this);
        release_tv.setOnClickListener(this);
        initData();
        myReleaseCarPortAdapter=new MyReleaseCarPortAdapter(context,myrelease);
        m_lvInfoList.setAdapter(myReleaseCarPortAdapter);
        m_lvInfoList.setOnHeaderRefreshListener(new PullRefreshList.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh() {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            m_hNotify.sendEmptyMessage(LOAD_MORE_DATA_HEADER);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        m_lvInfoList.setOnFooterRefreshListener(new PullRefreshList.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh() {
                m_iPageNum++;
                initData();
            }
        });

    }


    private Handler m_hNotify = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_MORE_DATA_HEADER:
                    m_lvInfoList.onHeaderRefreshComplete();
                    break;
                case LOAD_MORE_DATA_FOOTER:
//                    myReleaseCarPortAdapter.AddItemList(myrelease);
                    myReleaseCarPortAdapter.notifyDataSetChanged();
                    m_lvInfoList.onFooterRefreshComplete();
//                    m_iPageNum++;
                break;
                case LOAD_MORE_FAILED:
                    m_lvInfoList.onFooterRefreshComplete();
                    if(myrelease.size()>0){
//                        myReleaseCarPortAdapter.AddItemList(myrelease);
                        myReleaseCarPortAdapter.notifyDataSetChanged();
                        m_lvInfoList.onFooterRefreshComplete();
//                        m_iPageNum++;
                    }
                    break;
                default:
                    break;
            }
        }

    };


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.include_back_left_ll:
                finish();
                break;
            case R.id.include_tv_right:
                ActivityTools.switchActivity(context, PublishParkInfoActivity.class, null);
                break;
        }

    }
}
