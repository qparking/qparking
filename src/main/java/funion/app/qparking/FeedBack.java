package funion.app.qparking;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import funion.app.adapter.MyInfoAdapter;
import funion.app.qparking.fragment.FeedBackFragment;
import funion.app.qparking.fragment.HelperFragment;

/**
 * Created by Administrator on 2016/1/18.
 */
public class FeedBack extends FragmentActivity implements View.OnClickListener{
    private TextView feedback_tv,help_tv;
    private ViewPager feedback_vp;
    List<Fragment> list;
    private MyInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        initView();
    }

    private void initView() {
        feedback_tv=(TextView)findViewById(R.id.feed_back_tv);
        help_tv=(TextView)findViewById(R.id.help_tv);
        feedback_vp=(ViewPager)findViewById(R.id.feedback_vp);
        findViewById(R.id.back_im).setOnClickListener(this);
        help_tv.setOnClickListener(this);
        feedback_tv.setOnClickListener(this);
        list=new ArrayList<Fragment>();
        list.add(new FeedBackFragment());
        list.add(new HelperFragment());
        adapter=new MyInfoAdapter(getSupportFragmentManager(),list);
        feedback_vp.setAdapter(adapter);
        feedback_vp.setCurrentItem(0);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.help_tv:
                feedback_vp.setCurrentItem(0);
                help_tv.setTextColor(getResources().getColor(R.color.app_green));
                help_tv.setBackgroundResource(R.color.app_white);
                feedback_tv.setTextColor(getResources().getColor(R.color.app_white));
                feedback_tv.setBackgroundResource(R.color.app_green);
                break;
            case R.id.feed_back_tv:
                feedback_vp.setCurrentItem(1);
                feedback_tv.setTextColor(getResources().getColor(R.color.app_green));
                feedback_tv.setBackgroundResource(R.color.app_white);
                help_tv.setBackgroundResource(R.color.app_green);
                help_tv.setTextColor(getResources().getColor(R.color.app_white));
                break;
            case R.id.back_im:
                finish();
                break;
        }
    }
}
