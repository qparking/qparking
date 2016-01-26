package funion.app.qparking.tools;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import funion.app.qparking.R;

/**
 * @author Administrator
 * @包名： funion.app.qparking.tools
 * @类名：Countdown
 * @时间：2015年6月1日下午2:26:56
 * @要做的事情：TODO
 */
public class Countdown {
    private TextView timeTv;
    private long levaeTime;
    private String agr0;
    private boolean isDay;
    private Context context;

    /**
     * 设置时间倒计时
     *
     * @param timeTv
     * @param levaeTime 设置时间
     * @param agr0      秒
     * @param isDay
     */
    public Countdown(TextView timeTv, long levaeTime, String agr0, Boolean isDay, Context context) {
        this.agr0 = agr0;
        this.timeTv = timeTv;
        this.levaeTime = levaeTime;
        this.isDay = isDay;
        this.context = context;
        setLavetime(levaeTime);
        timeTv.setClickable(false);
        if (!isTimerRun) {
            runTime();
        }
    }

    Timer timer = null;
    private boolean isTimerRun = false;

    private void runTime() {
        timer = new Timer();
        isTimerRun = true;
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (levaeTime > 0) {
                    levaeTime--;
                    iniTime.sendEmptyMessage(0);
                } else {

                }

            }
        }, 1000, 1000);
    }

    Handler iniTime = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setLavetime(levaeTime);
        }
    };

    private void setLavetime(long mss) {
        long days = mss / (60 * 60 * 24);
        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / (60);
        long seconds = (mss % (60));

        if (isDay) {
            timeTv.setText(agr0 + days + "天" + hours + "小时" + minutes + "分" + seconds + "秒");
        } else {

            if (mss == 0) {
                timeTv.setText("重新获取");
                timeTv.setTextColor(context.getResources().getColor(R.color.app_white));
                timeTv.setClickable(true);
            } else {
                timeTv.setTextColor(context.getResources().getColor(R.color.app_white));
                timeTv.setText(mss + agr0);
            }
        }
    }

}
