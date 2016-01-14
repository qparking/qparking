package funion.app.qparking;
/**
 * 推荐奖励页面
 * Created by yunze on 2015/12/16.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import funion.app.adapter.RecommendRewardAdapter;

public class RecommendRewardActivity extends Activity implements View.OnClickListener {
    private Context context = RecommendRewardActivity.this;
    private ListView listView;
    private RecommendRewardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recommend_reward);
        initTitle();
        initView();
    }

    private void initView() {
        adapter = new RecommendRewardAdapter(context);
        listView = (ListView) findViewById(R.id.recommend_reward_lv);
        listView.setAdapter(adapter);
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("推荐有奖");
    }

    @Override
    public void onClick(View v) {

    }
}
