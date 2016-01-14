package funion.app.qparking.view;

import funion.app.qparking.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 个人中心圆角头像控件
 * 
 * @author Administrator
 */
public class RoundImageView extends ImageView {

	private Paint paint;
	private Paint paint2;

	public Paint inPaint;
	public int inPad = 5;

	public Paint outPaint;
	public int outPad = 5;
	private int pad = 0;

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RoundImageView(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		paint2 = new Paint();
		paint2.setXfermode(null);
		inPaint = new Paint();
		inPaint.setColor(getResources().getColor(R.color.app_white));
		inPaint.setAntiAlias(true);
		outPaint = new Paint();
		outPaint.setColor(getResources().getColor(R.color.round_outpad));
		outPaint.setAntiAlias(true);
	}

	@Override
	public void draw(Canvas canvas) {
		pad = inPad + outPad;
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap);
		super.draw(canvas2);
		drawLeftUp(canvas2);
		drawLeftDown(canvas2);
		drawRightUp(canvas2);
		drawRightDown(canvas2);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, outPaint);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - outPad, inPaint);// 圆
		canvas.drawBitmap(bitmap, 0, 0, paint2);
	}

	private void drawLeftUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0, getHeight() / 2);
		path.lineTo(0, 0);
		path.lineTo(getWidth() / 2, 0);
		path.arcTo(new RectF(pad, pad, getWidth() - pad, getHeight() - pad), -90, -90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawLeftDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0, getHeight() / 2);
		path.lineTo(0, getHeight());
		path.lineTo(getWidth() / 2, getHeight());
		path.arcTo(new RectF(pad, pad, getWidth() - pad, getHeight() - pad), 90, 90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawRightUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth(), getHeight() / 2);
		path.lineTo(getWidth(), 0);
		path.lineTo(getWidth() / 2, 0);
		path.arcTo(new RectF(pad, pad, getWidth() - pad, getHeight() - pad), -90, 90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawRightDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth(), getHeight() / 2);
		path.lineTo(getWidth(), getHeight());
		path.lineTo(getWidth() / 2, getHeight());
		path.arcTo(new RectF(pad, pad, getWidth() - pad, getHeight() - pad), 90, -90);
		path.close();
		canvas.drawPath(path, paint);
	}
}