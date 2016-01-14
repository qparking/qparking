package funion.app.qparking;
/**
 * 车位详情页面
 * Created by yunze on 2015/12/16.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ParkIngDetailsActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_parking_details);
        initTitle();
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.include_tv_title)).setText("车位详情");
    }

    @Override
    public void onClick(View v) {

    }
}
