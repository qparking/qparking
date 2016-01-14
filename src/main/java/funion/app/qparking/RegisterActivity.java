package funion.app.qparking;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import funion.app.qparking.tools.ActivityTools;
import funion.app.qparking.tools.JudgePlateNum;

public class RegisterActivity extends Activity implements View.OnClickListener {
    private EditText phoneEt;
    private TextView registerTv;
    private Context context = RegisterActivity.this;
    String title,tag;
    Button select_btn;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);
        Intent intent=getIntent();
        title=intent.getStringExtra("title");
        tag=intent.getStringExtra("tag");
        initView();

    }

    private void initView() {
        select_btn=(Button)findViewById(R.id.btn_select);
        findViewById(R.id.include_iv_left).setBackgroundResource(R.drawable.top_back_btn);
        if(title!=null)
        ((TextView) findViewById(R.id.include_tv_title)).setText(title);
        phoneEt = (EditText) findViewById(R.id.input_phone_et);
        registerTv = (TextView) findViewById(R.id.register_tv);
        findViewById(R.id.include_iv_left).setOnClickListener(this);
        registerTv.setOnClickListener(this);
        select_btn.setOnClickListener(this);
        sp=getSharedPreferences("mMessage",MODE_PRIVATE);
        editor=sp.edit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_iv_left:
                finish();
                break;
            case R.id.register_tv:
//                Toast.makeText(context, "下一步", Toast.LENGTH_SHORT);
                if(phoneEt.getText().toString().equals(null)||phoneEt.getText().toString().equals("")){
                    Toast.makeText(context,R.string.enter_phonenum,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!JudgePlateNum.isPhoNum(phoneEt.getText().toString())){
                    Toast.makeText(context,R.string.enter_true_phonenum,Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean last_sel=sp.getBoolean("select",true);
               if(!last_sel){
                   Bundle bundle=new Bundle();
                   bundle.putString("phonenum",phoneEt.getText().toString());
                   if(tag.equals("reg")) {
                       bundle.putString("title", getResources().getString(R.string.set_pwd));
                   }else if(tag.equals("find_pwd")){
                       bundle.putString("title", getResources().getString(R.string.find_pwd));
                   }
                   bundle.putString("tag",tag);
                ActivityTools.switchActivity(context, LoginRegisterActivity.class, bundle);
            }else{
                Toast.makeText(RegisterActivity.this,"请先接受协议",Toast.LENGTH_SHORT).show();
                return;
            }
                break;
            case R.id.agreement_tv:
                Toast.makeText(context, "协议", Toast.LENGTH_SHORT);
                break;
            case R.id.btn_select:
                boolean sel=sp.getBoolean("select",false);
                if(sel){
                    select_btn.setBackgroundResource(R.drawable.pictures_selected);
                    editor.putBoolean("select", false);
                    editor.commit();
                }else{
                    select_btn.setBackgroundResource(R.drawable.me_point);
                    editor.putBoolean("select",true);
                    editor.commit();
                }
                break;
        }
    }
}
