package funion.app.qparking;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.OkHttpUtils;
import funion.app.qparking.vo.HomeMapImg;
import funion.app.qparking.vo.LeftMenuImg;
import funion.app.qparking.vo.ToolbarImg;

public class SplashActivity extends Activity {
    String m_strIsGuidePlayed;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;
    private QParkingApp qParkingApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.splash_activity);
        getResponse();
        sp = getSharedPreferences("mMessage", MODE_PRIVATE);
        editor = sp.edit();
        m_strIsGuidePlayed = getSharedPreferences("qparking_pref", MODE_WORLD_READABLE).getString("guide_activity", "false");
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (m_strIsGuidePlayed.equals("false")) {
                    ActivityTools.switchActivity(context, GuideActivity.class, null);
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
                                    ActivityTools.switchActivity(context, MainAct.class, null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, parm, "getuserinfo");
                    } else {
                        ActivityTools.switchActivity(context, MainAct.class, null);
                    }
                }
                finish();
                //两个参数分别表示进入的动画,退出的动画
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 3000);
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

    public void getResponse() {
        Map<String, String> param = new HashMap<>();
        param.put("fetch", "1");
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {
                Type listType;
                try {
                    qParkingApp = (QParkingApp) getApplicationContext();
                    JSONObject obj = new JSONObject(result);
                    String leftMenu = obj.getString("leftmenu");
                    String toolbar = obj.getString("toolbar");
                    Gson gson = new Gson();
                    listType = new TypeToken<ArrayList<LeftMenuImg>>() {
                    }.getType();
                    ArrayList<LeftMenuImg> tempList = gson.fromJson(leftMenu, listType);
                    qParkingApp.leftMenuImgArrayList.addAll(tempList);
                    listType = new TypeToken<ArrayList<ToolbarImg>>() {
                    }.getType();
                    ArrayList<ToolbarImg> tempList1 = gson.fromJson(toolbar, listType);
                    qParkingApp.toolbarImgArrayList.addAll(tempList1);
//                    Log.e("leftmenu", leftMenu);
                    String homeMap = obj.getString("homemap");
                    Log.e("homeMap", homeMap);
                    obj = new JSONObject(homeMap);
                    String barItem = obj.getString("barItem");
                    String mapItem = obj.getString("mapItem");
                    String actionItem = obj.getString("actionItem");
                    obj = new JSONObject(barItem);
                    String leftBarImg = obj.getString("leftBarImg");
                    String rightBarImg = obj.getString("rightBarImg");
                    qParkingApp.leftBarImg = leftBarImg;
                    qParkingApp.rightBarImg = rightBarImg;
                    obj = new JSONObject(mapItem);
                    String commonmarking = obj.getString("commonmarking");
                    String selmarking = obj.getString("selmarking");
                    String currentmarking = obj.getString("currentmarking");
                    qParkingApp.commonmarking = commonmarking;
                    qParkingApp.selmarking = selmarking;
                    qParkingApp.currentmarking = currentmarking;
                    obj = new JSONObject(actionItem);
                    String subscribe = obj.getString("subscribe");
                    String nav = obj.getString("nav");
                    qParkingApp.subscribe = subscribe;
                    qParkingApp.nav = nav;
//                    Log.e("result", "leftBarImg:" + leftBarImg + "rightBarImg:"
//                            + rightBarImg + " commonmarking:" + commonmarking +
//                            " selmarking:"
//                            + selmarking + " currentmarking:" + currentmarking
//                            + " subscribe:" + subscribe + " nav:" + nav);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param, "appStyleFetch");
    }
}
