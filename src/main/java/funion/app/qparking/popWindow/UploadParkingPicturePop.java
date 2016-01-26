package funion.app.qparking.popWindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import funion.app.qparking.R;

/**
 * Created by yunze on 2015/12/16.
 */
public class UploadParkingPicturePop extends PopupWindow {
    private View view;
    private TextView make_photo, select_photoalbum, cancel;

    public UploadParkingPicturePop(Context context, View.OnClickListener onClickListener) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.pop_upload_parking_picture, null);
        this.setContentView(view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        make_photo = (TextView) view.findViewById(R.id.make_photo);
        select_photoalbum = (TextView) view.findViewById(R.id.select_photoalbum);
        cancel = (TextView) view.findViewById(R.id.cancel_tv);
        cancel.setOnClickListener(onClickListener);
        make_photo.setOnClickListener(onClickListener);
        select_photoalbum.setOnClickListener(onClickListener);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.app_transparent));
        this.setBackgroundDrawable(dw);
        setAnimationStyle(R.style.PopupSlowAnimation);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.upload_picture_pop).getTop();
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
