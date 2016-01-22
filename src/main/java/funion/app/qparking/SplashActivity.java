package funion.app.qparking;

import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.tools.DialogManager;
import funion.app.qparking.tools.HttpUtil;
import funion.app.qparking.tools.Net;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.tools.TransCoding;
import funion.app.qparking.vo.LeftMenuIconBean;
import funion.app.qparking.vo.ToolBarBean;

public class SplashActivity extends Activity {
    String m_strIsGuidePlayed;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;
    private QParkingApp qParkingApp;
    List<LeftMenuIconBean> leftMenuIconBeans;
    List<ToolBarBean> toolBarBeans;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.splash_activity);
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        editor = sp.edit();
        if(Net.getNetType(context)==0){
            final AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle(null);
            builder.setMessage("您尚未设置网络，是否设置");
            builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.show();
        }
        getImageIcon();
        m_strIsGuidePlayed = getSharedPreferences("qparking_pref", MODE_WORLD_READABLE).getString("guide_activity", "false");
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (m_strIsGuidePlayed.equals("false")) {
                    Bundle bundle=new Bundle();
                    bundle.putParcelableArrayList("leftmenu", (ArrayList<? extends Parcelable>) leftMenuIconBeans);
                    bundle.putParcelableArrayList("toolbar", (ArrayList<? extends Parcelable>) toolBarBeans);
                    Log.e("map","发出:"+toolBarBeans.toString());
                    ActivityTools.switchActivity(context, GuideActivity.class, bundle);
                } else {
                    if (sp.getString("sign", null) != null) {
                        Map<String, String> parm = new HashMap<String, String>();
                        parm.put("sign", sp.getString("sign", null));
                        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(String result) {
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    String code = jsonObject.getString("code");
                                    if (code.equals("0")) {
                                        editor.putString("abatar", jsonObject.getString("photourl"));
                                        editor.putString("username", jsonObject.getString("phone"));
                                        editor.putString("balance", jsonObject.getString("balance"));
                                        editor.putString("integral", jsonObject.getString("integral"));
                                        editor.commit();
                                    }
                                    Bundle bundle=new Bundle();
                                    bundle.putParcelableArrayList("leftmenu", (ArrayList<? extends Parcelable>) leftMenuIconBeans);
                                    bundle.putParcelableArrayList("toolbar", (ArrayList<? extends Parcelable>) toolBarBeans);
                                    Log.e("map", "fachusize:" + toolBarBeans.size() + "");
                                    ActivityTools.switchActivity(context, MainAct.class, bundle);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, parm, "getuserinfo");
                    } else {
                        Bundle bundle=new Bundle();
                        bundle.putParcelableArrayList("leftmenu", (ArrayList<? extends Parcelable>) leftMenuIconBeans);
                        bundle.putParcelableArrayList("toolbar", (ArrayList<? extends Parcelable>) toolBarBeans);
                        Log.e("map", "fachusize:" + toolBarBeans.size() + "");
                        ActivityTools.switchActivity(context, MainAct.class, bundle);
                    }
                }
                finish();
                //两个参数分别表示进入的动画,退出的动画
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 3000);
    }

    private void getImageIcon() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fetch", "0");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if (code.equals("0")) {
                        String time = TransCoding.strToDate(jsonObject.getString("time"));
                        String oldTime = sp.getString("time", null);
                        if (time.equals(oldTime)) {
                            getNewIcon();
                        } else {
                            getNewIcon();
                        }
                        editor.putString("time", time);
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, "appStyleFetch");
    }

    private void getNewIcon() {
        leftMenuIconBeans = new ArrayList<LeftMenuIconBean>();
        toolBarBeans = new ArrayList<ToolBarBean>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("fetch", "1");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                Log.e("map",result.toString());
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("leftmenu");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        LeftMenuIconBean iconBean = new LeftMenuIconBean();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        iconBean.setItemName(jsonObject.getString("itemname"));
                        iconBean.setTitle(jsonObject.getString("title"));
                        iconBean.setFileName("leftmenu");
                        iconBean.setImgurl(jsonObject.getString("img"));
                        iconBean.setColor(jsonObject.getString("color"));
                        leftMenuIconBeans.add(iconBean);
                    }
                    JSONArray array = object.getJSONArray("toolbar");
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object1 = array.getJSONObject(j);
                        ToolBarBean toolBarBean = new ToolBarBean();
                        toolBarBean.setItemName(object1.getString("itemname"));
                        toolBarBean.setTitle(object1.getString("title"));
                        toolBarBean.setFileName("toolbar");
                        toolBarBean.setImgUrl(object1.getString("img"));
                        toolBarBean.setColor(object1.getString("color"));
                        toolBarBeans.add(toolBarBean);
                    }
                    JSONObject homeobject = object.getJSONObject("homemap");
                    JSONObject barobject = homeobject.getJSONObject("barItem");
                    final String lefturl = barobject.getString("leftBarImg");
                    final String righturl = barobject.getString("rightBarImg");
                    JSONObject mapobject = homeobject.getJSONObject("mapItem");
                    final String commonmarking = mapobject.getString("commonmarking");
                    final String selmarking = mapobject.getString("selmarking");
                    final String currentmarking = mapobject.getString("currentmarking");
                    JSONObject actionobject = homeobject.getJSONObject("actionItem");
                    final String subscribe = actionobject.getString("subscribe");
                    final String nav = actionobject.getString("nav");
                    new Thread() {
                        @Override
                        public void run() {
                            if (AppTools.ExistSDCard()) {
                                for (int i = 0; i < leftMenuIconBeans.size(); i++) {
                                    String name = leftMenuIconBeans.get(i).getItemName();
                                    String filename = leftMenuIconBeans.get(i).getFileName();
                                    String url = leftMenuIconBeans.get(i).getImgurl();
                                    HttpUtil.DownLoadImageIcon(url, filename, name);
                                }
                                for (int j = 0; j < toolBarBeans.size(); j++) {
                                    String name = toolBarBeans.get(j).getItemName();
                                    String filename = toolBarBeans.get(j).getFileName();
                                    String url = toolBarBeans.get(j).getImgUrl();
                                    HttpUtil.DownLoadImageIcon(url, filename, name);
                                }
                                HttpUtil.DownLoadImageIcon(lefturl, "barItem", "lefturl");
                                HttpUtil.DownLoadImageIcon(righturl, "barItem", "righturl");
                                HttpUtil.DownLoadImageIcon(commonmarking, "mapItem", "commonmarking");
                                HttpUtil.DownLoadImageIcon(selmarking, "mapItem", "selmarking");
                                HttpUtil.DownLoadImageIcon(currentmarking, "mapItem", "currentmarking");
                                HttpUtil.DownLoadImageIcon(nav, "actionItem", "nav");
                                HttpUtil.DownLoadImageIcon(subscribe, "actionItem", "subscribe");
                            }
                        }
                    }.start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, "appStyleFetch");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            System.exit(0);

        return super.onKeyDown(keyCode, event);
    }
}
