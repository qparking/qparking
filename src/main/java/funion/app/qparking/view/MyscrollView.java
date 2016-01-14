package funion.app.qparking.view;

import funion.app.qparking.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class MyscrollView extends HorizontalScrollView {
    // 滑动速度
    private VelocityTracker vTracker = null;
    private MyScrollListener myScrollListener = null;
    // 手滑动放开后是否滚动
    public boolean isScrolling = false;

    // 子控件
    private ViewGroup view1;
    private ViewGroup view2;
    private ViewGroup view3;
    private ViewGroup view4;
    private ViewGroup view5;

    // 屏幕宽
    private int parentWidth;
    // 子控件宽高
    private int childWidth;
    private int childHeight;
    // 滚动一页的距离
    private int onceScrollLength;

    // 手指滑动开始x坐标
    private float filingStartX;
    // 手指滑动结束x坐标
    private float filingEndX;
    // 子控件之间得到距离为margin *2
    private int margin;

    public MyscrollView(Context context) {
        super(context);
    }

    public MyscrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 从xml内取得自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyScrollView);
        for (int i = 0; i < a.getIndexCount(); i++) {
            switch (a.getIndex(i)) {
                case R.styleable.MyScrollView_margin:
                    margin = a.getDimensionPixelSize(a.getIndex(i), 20);//获取dp所对应的px
                    break;
                case R.styleable.MyScrollView_height:
                    childHeight = a.getDimensionPixelSize(a.getIndex(i), 100);
                    break;
                default:
                    break;
            }
        }
        a.recycle();
    }

    public MyscrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        childWidth = parentWidth - margin * 6;
        onceScrollLength = parentWidth - margin * 4;

        LinearLayout wrapper = (LinearLayout) getChildAt(0);
        view1 = (ViewGroup) wrapper.getChildAt(0);
        view2 = (ViewGroup) wrapper.getChildAt(1);
        view3 = (ViewGroup) wrapper.getChildAt(2);
        view4 = (ViewGroup) wrapper.getChildAt(3);
        view5 = (ViewGroup) wrapper.getChildAt(4);

        view1.getLayoutParams().width = childWidth;
        view2.getLayoutParams().width = childWidth;
        view3.getLayoutParams().width = childWidth;
        view4.getLayoutParams().width = childWidth;
        view5.getLayoutParams().width = childWidth;
        view1.getLayoutParams().height = childHeight;
        view2.getLayoutParams().height = childHeight;
        view3.getLayoutParams().height = childHeight;
        view4.getLayoutParams().height = childHeight;
        view5.getLayoutParams().height = childHeight;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 初始化位置
        if (changed) {
            scrollToMiddleImmediately();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScrolling) {
            isScrolling = false;
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                isScrolling = true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                filingStartX = ev.getX();
                if (vTracker == null) {
                    vTracker = VelocityTracker.obtain();
                } else {
                    vTracker.clear();
                }
                vTracker.addMovement(ev);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                filingEndX = ev.getX();
                // 判断移动速度
                if (vTracker.getXVelocity() > 500) {
                    smoothScrollToLeft();
                    return super.dispatchTouchEvent(ev);
                } else if (vTracker.getXVelocity() < -500) {
                    smoothScrollToRight();
                    return super.dispatchTouchEvent(ev);
                }
                // 手向左滑动
                if (filingStartX > filingEndX) {
                    if ((filingStartX - filingEndX) > parentWidth * 0.3) {
                        smoothScrollToRight();
                    } else {
                        smoothScrollToMiddle();
                    }
                    // 手向右滑动
                } else if (filingStartX < filingEndX) {
                    if ((filingEndX - filingStartX) > parentWidth * 0.3) {
                        smoothScrollToLeft();
                    } else {
                        smoothScrollToMiddle();
                    }
                }
                releaseVelocityTracker();
                break;

            case MotionEvent.ACTION_MOVE:
                vTracker.addMovement(ev);
                vTracker.computeCurrentVelocity(1000);
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // 释放VelocityTracker
    private void releaseVelocityTracker() {
        if (null != vTracker) {
            vTracker.clear();
            vTracker.recycle();
            vTracker = null;
        }
    }

    // 立即滚动到中间位置，初始化时用
    public void scrollToMiddleImmediately() {
        scrollTo(childWidth * 2 + margin * 4, 0);
    }

    // 延迟一点时间滚动到中间，延迟时间是给view生成时间
    private void scrollToMiddle(final int flag) {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollTo(childWidth * 2 + margin * 4, 0);
                // 该线程将处理控件移动后的回调
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myScrollListener.switchFinish(flag);
                    }
                }, 100);
            }
        }, 250);
    }

    // 滚动重写，判断出滚动到指定位置后，执行自定义的滚动完成回调函数
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (isScrolling) {
            if (l == onceScrollLength) {
                isScrolling = false;
                myScrollListener.scrollFinish(10);
                scrollToMiddle(10);
            } else if (l == onceScrollLength * 3) {
                isScrolling = false;
                myScrollListener.scrollFinish(20);
                scrollToMiddle(20);
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    // 滚动到中间（带动画）
    private void smoothScrollToMiddle() {
        this.post(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(childWidth * 2 + margin * 4, 0);
            }
        });
    }

    // 滚动到左边（带动画）
    private void smoothScrollToLeft() {
        this.post(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(childWidth + margin * 2, 0);
            }
        });
    }

    // 滚动到右边（带动画）
    private void smoothScrollToRight() {
        this.post(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(childWidth * 3 + margin * 6, 0);
            }
        });
    }

    public void setMyScrollListener(MyScrollListener myScrollListener) {
        this.myScrollListener = myScrollListener;
    }

    public interface MyScrollListener {
        /**
         * 滚动完成回调
         *
         * @param flag 10-手向右拖动 20-手向左拖动
         */
        public void scrollFinish(int flag);

        /**
         * 切换回中间的view后回调
         *
         * @param flag 10-手向右拖动后 20-手向左拖动后
         */
        public void switchFinish(int flag);
    }

}
