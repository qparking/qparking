package funion.app.qparking.popWindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import funion.app.qparking.R;

/**
 * Created by 运泽 on 2015/12/25.
 */
public class SelectPayModePop extends PopupWindow{
    private View view;
    private Button wechat_,alipay_,cancle_;

    public SelectPayModePop(Context context,View.OnClickListener itemsOnClick
    ) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.select_pay_mode, null);
        wechat_=(Button)view.findViewById(R.id.wechat_btn);
        alipay_=(Button)view.findViewById(R.id.alipay_btn);
        cancle_=(Button)view.findViewById(R.id.cancle_btn);
        this.setContentView(view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupSlowAnimation);
        cancle_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        wechat_.setOnClickListener(itemsOnClick);
        alipay_.setOnClickListener(itemsOnClick);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(00000000);
        this.setBackgroundDrawable(dw);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.pop_ll).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

}
