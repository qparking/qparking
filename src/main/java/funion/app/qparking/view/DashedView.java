package funion.app.qparking.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class DashedView extends View
{
	public DashedView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DashedView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DashedView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	protected void onDraw(Canvas canvas)
	{
        // TODO Auto-generated method stub
        super.onDraw(canvas);  

        Paint	paintDashed	= new Paint();
        
        paintDashed.setStyle(Paint.Style.STROKE);
        paintDashed.setColor(Color.LTGRAY);
        paintDashed.setStrokeWidth(1.5f);
        Path path = new Path();     
        path.moveTo(0, getHeight()/2);
        path.lineTo(getWidth(), getHeight()/2);
        
        PathEffect	effects = new DashPathEffect(new float[]{6,3}, 1);
        paintDashed.setPathEffect(effects);
        canvas.drawPath(path, paintDashed);
    }
}
