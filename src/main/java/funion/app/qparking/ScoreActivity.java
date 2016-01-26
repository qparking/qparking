package funion.app.qparking;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ScoreActivity extends Activity implements OnClickListener
{
	SharedPreferences sp;
	private TextView title;
	private ImageView left_im;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_activity);
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        TextView	tvScore	= (TextView)findViewById(R.id.tvScoreAmont);
        tvScore.setText(sp.getString("integral", null));
		title=(TextView)findViewById(R.id.include_tv_title);
		left_im=(ImageView)findViewById(R.id.include_iv_left);
		title.setText(getResources().getString(R.string.integral_));
		left_im.setBackgroundResource(R.drawable.top_back_btn);
		findViewById(R.id.include_back_left_ll).setOnClickListener(this);

    }

	@Override
	public void onResume()
	{
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
	@Override
	public void onPause()
	{
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.include_back_left_ll:
				finish();
				break;
		}	
		
	}
}
