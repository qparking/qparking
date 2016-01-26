package funion.app.qparking.view;

import android.app.Activity;;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import funion.app.adapter.LeftMenuAdapter;
import funion.app.common.T;
import funion.app.qparking.IntegralExChange;
import funion.app.qparking.LesseeActivity;
import funion.app.qparking.LoginActivity;
import funion.app.qparking.MipcaActivityCapture;
import funion.app.qparking.MyOrderActivity;
import funion.app.qparking.MyWalletActivity;
import funion.app.qparking.OfflineMapActivity;
import funion.app.qparking.PersonalCenterActivity;
import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import funion.app.qparking.RecommendActivity;
import funion.app.qparking.SetActivity;
import funion.app.qparking.SuggestionActivity;
import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.AppTools;
import funion.app.qparking.vo.LeftMenuIconBean;
import funion.app.qparking.vo.ToolBarBean;

/**
 * Created by Administrator on 2016/1/22.
 */
public class SildMenuView implements View.OnClickListener {
    private Activity activity;
    private SlidingMenu menu;
    private RelativeLayout login_re_;//未登录布局
    private LinearLayout m_llMenu;//已登陆布局
    private LinearLayout bottomset_ll;
    private TextView phonenum_tv, balance, offline_tv, tvSuggestion, tvMenuSet;
    private LinearLayout m_llMenuBar;//侧滑布局
    private RelativeLayout m_rlMainUI;
    private int m_iMainMenuLen;
    private ListView leftmenu_lst;
    final int MENU_ANIM_LEN = 50;
    final int MENU_ANIM_INTERVAL = 20; // 菜单动画延迟
    LeftMenuAdapter leftMenuAdapter;
    private ImageView offline_iv, ivSuggestion, ivMenuSet;
    private List<LeftMenuIconBean> leftMenuIconBeanList;
    private List<ToolBarBean> toolBarBeanList;
    List<Bitmap> leftmenulist;
    List<Bitmap> toolbarlist;
    SharedPreferences sp;

    public void initSildingMenu(Activity act, List<LeftMenuIconBean> leftbean, List<ToolBarBean> toolbarbean, List<Bitmap> leftmenu, List<Bitmap> toolbar) {
        activity = act;
        leftMenuIconBeanList = leftbean;
        toolBarBeanList = toolbarbean;
        leftmenulist = leftmenu;
        toolbarlist = toolbar;
        sp = activity.getSharedPreferences("mMessage", activity.MODE_PRIVATE);
        menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setShadowWidth(R.dimen.shadow_width);
        menu.setShadowDrawable(null);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.leftmenu);
        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                updateView();
            }
        });
        bottomset_ll = (LinearLayout) activity.findViewById(R.id.bottomset_ll);
        login_re_ = (RelativeLayout) activity.findViewById(R.id.login_re);
        m_llMenu = (LinearLayout) activity.findViewById(R.id.llMenuMe);
        m_llMenuBar = (LinearLayout) activity.findViewById(R.id.llMenuBar);
        m_rlMainUI = (RelativeLayout) activity.findViewById(R.id.rlMainUI);
        phonenum_tv = (TextView) activity.findViewById(R.id.tvMenuMe);
        balance = (TextView) activity.findViewById(R.id.tvBalance);
        offline_iv = (ImageView) activity.findViewById(R.id.offline_iv);
        ivSuggestion = (ImageView) activity.findViewById(R.id.ivSuggestion);
        ivMenuSet = (ImageView) activity.findViewById(R.id.ivMenuSet);
        leftmenu_lst = (ListView) activity.findViewById(R.id.left_menu_lst);
        offline_tv = (TextView) activity.findViewById(R.id.offline_tv);
        tvSuggestion = (TextView) activity.findViewById(R.id.tvSuggestion);
        tvMenuSet = (TextView) activity.findViewById(R.id.tvMenuSet);
        activity.findViewById(R.id.rlMenuMe).setOnClickListener(this);
        activity.findViewById(R.id.offline_rl).setOnClickListener(this);
        activity.findViewById(R.id.rlSuggestion).setOnClickListener(this);
        activity.findViewById(R.id.rlMenuSet).setOnClickListener(this);
        offline_tv.setText(toolBarBeanList.get(0).getTitle());
        offline_iv.setImageBitmap(toolbarlist.get(0));
        tvSuggestion.setText(toolBarBeanList.get(1).getTitle());
        ivSuggestion.setImageBitmap(toolbarlist.get(1));
        tvMenuSet.setText(toolBarBeanList.get(2).getTitle());
        ivMenuSet.setImageBitmap(toolbarlist.get(2));
        String toolbarcolor = "#" + toolbarbean.get(0).getColor();
        bottomset_ll.setBackgroundColor(Color.parseColor(toolbarcolor));
        leftMenuAdapter = new LeftMenuAdapter(activity, leftMenuIconBeanList, leftmenulist);
        String lstcolor = "#" + leftMenuIconBeanList.get(0).getColor();
        leftmenu_lst.setBackgroundColor(Color.parseColor(lstcolor));
        leftmenu_lst.setAdapter(leftMenuAdapter);
        leftmenu_lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                    String sign = sp.getString("sign", null);
                                                    switch (i) {
                                                        case 0:
                                                            if (sign != null) {
                                                                ActivityTools.switchActivity(activity, MyWalletActivity.class, null);
                                                            } else {
                                                                menu.toggle();
                                                                T.show(activity, R.string.login_first, Toast.LENGTH_SHORT);
                                                                ActivityTools.switchActivity(activity, LoginActivity.class, null);
                                                            }
                                                            break;
                                                        case 1:
                                                            if (sign != null) {
                                                                ActivityTools.switchActivity(activity, MyOrderActivity.class, null);
                                                            } else {
                                                                menu.toggle();
                                                                T.show(activity, R.string.login_first, Toast.LENGTH_SHORT);
                                                                ActivityTools.switchActivity(activity, LoginActivity.class, null);
                                                            }
                                                            break;
                                                        case 2:
                                                            if (sign != null) {
                                                                ActivityTools.switchActivity(activity, LesseeActivity.class, null);
                                                            } else {
                                                                menu.toggle();
                                                                QParkingApp.ToastTip(activity, activity.getResources().getString(R.string.login_first), -100);
                                                                ActivityTools.switchActivity(activity, LoginActivity.class, null);
                                                            }
                                                            break;
                                                        case 3:
                                                            ActivityTools.switchActivity(activity, MipcaActivityCapture.class, null);
                                                            break;
                                                        case 4:
                                                            if (sign != null) {
                                                                ActivityTools.switchActivity(activity, IntegralExChange.class, null);
                                                            } else {
                                                                menu.toggle();
                                                                QParkingApp.ToastTip(activity, activity.getResources().getString(R.string.login_first), -100);
                                                                ActivityTools.switchActivity(activity, LoginActivity.class, null);
                                                            }
                                                            break;
                                                        case 5:
                                                            ActivityTools.switchActivity(activity, RecommendActivity.class, null);
                                                            break;
                                                    }
                                                }
                                            }

        );
    }

    private void updateView() {
        initData();
    }

    private void initData() {
        String sign = sp.getString("sign", null);
        if (sign != null) {
            login_re_.setVisibility(View.GONE);
            m_llMenu.setVisibility(View.VISIBLE);
            phonenum_tv.setText(sp.getString("username", null));
            balance.setText(sp.getString("balance", null));
            DisplayImageOptions options = AppTools.confirgImgInfo(R.drawable.headimage_default, R.drawable.headimage_default);
            ImageLoader.getInstance().displayImage(sp.getString("avatar", ""),
                    ((ImageView) activity.findViewById(R.id.ivMenuMe)), options);
        } else {
            login_re_.setVisibility(View.VISIBLE);
            m_llMenu.setVisibility(View.GONE);
        }
        m_llMenuBar.setVisibility(View.VISIBLE);
    }

    public void openMenu() {
        menu.toggle();
    }

    public void setTouchModeAbove(int mode) {
        menu.setTouchModeAbove(mode);
    }

    @Override
    public void onClick(View view) {
        String userId = sp.getString("userId", null);
        switch (view.getId()) {

        }

    }
};

