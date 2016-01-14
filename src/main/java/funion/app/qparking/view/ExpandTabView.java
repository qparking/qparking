package funion.app.qparking.view;

import java.util.ArrayList;

import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import funion.app.qparking.tools.DisplayUtil;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * 菜单控件头部，封装了下拉动画，动态生成头部按钮个数
 * 
 * @author yueyueniao
 */

public class ExpandTabView extends LinearLayout implements OnDismissListener {

	private ToggleButton selectedButton;
	private ArrayList<String> mTextArray = new ArrayList<String>();
	private ArrayList<RelativeLayout> mViewArray = new ArrayList<RelativeLayout>();
	private ArrayList<ToggleButton> mToggleButton = new ArrayList<ToggleButton>();
	private Context mContext;
	private final int SMALL = 0;
	private int displayWidth;
	private int displayHeight;
	private PopupWindow popupWindow;
	private int selectPosition;

	public ExpandTabView(Context context) {
		super(context);
		init(context);
	}

	public ExpandTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressWarnings("deprecation")
	private void init(Context context) {
		mContext = context;
		displayWidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
		displayHeight = ((Activity) mContext).getWindowManager().getDefaultDisplay().getHeight();
		setOrientation(LinearLayout.HORIZONTAL);
	}

	/**
	 * 根据选择的位置设置tabitem显示的值
	 */
	public void setTitle(String valueText, int position) {
		if (position < mToggleButton.size()) {
			mToggleButton.get(position).setText(valueText);
		}
	}

	public void setTitle(String title) {

	}

	/**
	 * 根据选择的位置获取tabitem显示的值
	 */
	public String getTitle(int position) {
		if (position < mToggleButton.size() && mToggleButton.get(position).getText() != null) {
			return mToggleButton.get(position).getText().toString();
		}
		return "";
	}

	/**
	 * 设置tabitem的个数和初始值
	 */
	@SuppressWarnings("deprecation")
	public void setValue(ArrayList<String> textArray, ArrayList<View> viewArray) {
		if (mContext == null) {
			return;
		}

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mTextArray = textArray;
		for (int i = 0; i < viewArray.size(); i++) {
			final RelativeLayout r = new RelativeLayout(mContext);
			
			// 根据位置判断内容展示的布局大小
			if (i == 2) {
				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				r.addView(viewArray.get(i), rl);
			} else {
				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.FILL_PARENT, (int) (displayHeight * 0.5));
				r.addView(viewArray.get(i), rl);
			}

			// 添加列表底部装饰布局
			final LinearLayout linearLayout = new LinearLayout(mContext);
			RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			linearLayoutParams.addRule(RelativeLayout.BELOW, viewArray.get(i).getId());
			r.addView(linearLayout, linearLayoutParams);
			linearLayout.setBackgroundColor(getResources().getColor(R.color.app_white));
			linearLayout.setOrientation(VERTICAL);

			final TextView line1 = new TextView(mContext);
			LayoutParams lineParam = new LayoutParams(LayoutParams.FILL_PARENT, 2);
			line1.setBackgroundColor(getResources().getColor(R.color.halving_line));
			linearLayout.addView(line1, lineParam);
			ImageView image = new ImageView(mContext);
			LayoutParams imageParam = new LayoutParams(DisplayUtil.dip2px(mContext, 20), DisplayUtil.dip2px(mContext,
					20));
			imageParam.topMargin = 2;
			imageParam.bottomMargin = 2;
			imageParam.gravity = Gravity.CENTER;

			image.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_double));
			linearLayout.addView(image, imageParam);
			final TextView line2 = new TextView(mContext);
			line2.setBackgroundColor(getResources().getColor(R.color.halving_line));
			linearLayout.addView(line2, lineParam);

			mViewArray.add(r);
			r.setTag(SMALL);

			// 添加tab button项
			ToggleButton tButton = (ToggleButton) inflater.inflate(R.layout.choose_toggle_button, this, false);
			addView(tButton);
			ImageView line = new ImageView(mContext);
			line.setImageDrawable(getResources().getDrawable(R.drawable.choosebar_line));
			line.setBackgroundColor(getResources().getColor(R.color.app_white));
			if (i < viewArray.size() - 1) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.FILL_PARENT);
				addView(line, lp);
			}

			mToggleButton.add(tButton);
			tButton.setTag(i);
			tButton.setText(mTextArray.get(i));

			r.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onPressBack();
				}
			});

			r.setBackgroundColor(mContext.getResources().getColor(R.color.translucent_gray));
			tButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					// initPopupWindow();
					ToggleButton tButton = (ToggleButton) view;

					if (selectedButton != null && selectedButton != tButton) {
						selectedButton.setChecked(false);
					}
					selectedButton = tButton;
					selectPosition = (Integer) selectedButton.getTag();
					startAnimation();
					if (mOnButtonClickListener != null && tButton.isChecked()) {
						mOnButtonClickListener.onClick(selectPosition);
					}
				}
			});
		}
	}

	private void startAnimation() {
		if (popupWindow == null) {
			// 根据位置设置popwindow大小
			if (selectPosition == 0) {
				popupWindow = new PopupWindow(mViewArray.get(selectPosition), displayWidth, displayHeight);
			} else if (selectPosition == 1) {
				popupWindow = new PopupWindow(mViewArray.get(selectPosition), displayWidth, displayHeight);
			} else if (selectPosition == 2) {
				popupWindow = new PopupWindow(mViewArray.get(selectPosition), displayWidth, displayHeight);
			}
			// 取消popwin显示隐藏动画
			popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
			popupWindow.setFocusable(false);
			popupWindow.setOutsideTouchable(true);
		}

		if (selectedButton.isChecked()) {
			if (!popupWindow.isShowing()) {
				showPopup(selectPosition);
			} else {
				popupWindow.setOnDismissListener(this);
				popupWindow.dismiss();
				hideView();
			}
		} else {
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
				hideView();
			}
		}
	}

	private void showPopup(int position) {
		View tView = mViewArray.get(selectPosition).getChildAt(0);
		if (tView instanceof ViewBaseAction) {
			ViewBaseAction f = (ViewBaseAction) tView;
			f.show();
		}
		if (popupWindow.getContentView() != mViewArray.get(position)) {
			popupWindow.setContentView(mViewArray.get(position));
		}
		popupWindow.showAsDropDown(this, 0, 0);
	}

	/**
	 * 如果菜单成展开状态，则让菜单收回去
	 */
	public boolean onPressBack() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			hideView();
			if (selectedButton != null) {
				selectedButton.setChecked(false);
			}
			return true;
		} else {
			return false;
		}

	}

	private void hideView() {
		View tView = mViewArray.get(selectPosition).getChildAt(0);
		if (tView instanceof ViewBaseAction) {
			ViewBaseAction f = (ViewBaseAction) tView;
			f.hide();
		}
	}

	@Override
	public void onDismiss() {
		showPopup(selectPosition);
		popupWindow.setOnDismissListener(null);
	}

	private OnButtonClickListener mOnButtonClickListener;

	/**
	 * 设置tabitem的点击监听事件
	 */
	public void setOnButtonClickListener(OnButtonClickListener l) {
		mOnButtonClickListener = l;
	}

	/**
	 * 自定义tabitem点击回调接口
	 */
	public interface OnButtonClickListener {
		public void onClick(int selectPosition);
	}

	public interface ViewBaseAction {
		/**
		 * 菜单隐藏操作
		 */
		public void hide();

		/**
		 * 菜单显示操作
		 */
		public void show();
	}

}
