package funion.app.qparking;
/**
 * 用户协议
 * Created by yunze on 2016/1/12.
 */

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AgreementActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_agreement);
        initTextData();
        initTitle();
    }

    private void initTitle() {
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.come_back);
        findViewById(R.id.include_iv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.include_tv_title)).setText("用户协议");
        ((TextView) findViewById(R.id.detail_tv)).
                setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initTextData() {
        StringBuffer sb = new StringBuffer();
        InputStream is = null;
        try {
            is = getAssets().open("test");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(new String(line.getBytes("UTF-8")) + "\n");
            }
            Log.e("sb", sb.toString());
            ((TextView) findViewById(R.id.detail_tv)).setText(sb.toString());

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}