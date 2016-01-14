package funion.app.qparking.view;

import java.util.Date;

import funion.app.qparking.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 上下拉刷新自定义 listview
 * 
 * @author Administrator
 */
public class PullRefreshList extends ListView implements OnScrollListener {
	private final static int STATE_HEAD_NONE = 0;
	private final static int STATE_HEAD_PULL = 1;
	private final static int STATE_HEAD_RELEASE = 2;
	private final static int STATE_HEAD_LOADING = 3;

	private final static int STATE_FOOT_NONE = 0;
	private final static int STATE_FOOT_PULL = 1;
	private final static int STATE_FOOT_RELEASE = 2;
	private final static int STATE_FOOT_LOADING = 3;

	private final static int PULL_FACTOR = 3;

	private LinearLayout m_llHeader;
	private LinearLayout m_llFooter;

	private TextView m_tvHeaderTip;
	private TextView m_tvFooterTip;
	private TextView m_tvHeaderRefreshTime;
	private TextView m_tvFooterRefreshTime;
	private ImageView m_ivHeaderArrow;
	private ImageView m_ivFooterArrow;
	private ProgressBar m_pbHeaderWaiting;
	private ProgressBar m_pbFooterWaiting;

	private RotateAnimation m_raTurnArrow;
	private RotateAnimation m_raTurnArrowBack;

	private boolean m_bIsTouching;

	private int m_iHeaderHeight;
	private int m_iFooterHeight;

	private int m_iPrePosY;
	private int m_iFirstVisiableItem;
	private int m_iLastVisiableItem;
	private int m_iItemCount;
	private int m_iHeaderState;
	private int m_iFooterState;
	private boolean m_bIsPullBack;

	public interface OnHeaderRefreshListener {
		public void onHeaderRefresh();
	}

	public interface OnFooterRefreshListener {
		public void onFooterRefresh();
	}

	private OnHeaderRefreshListener onHeaderRefreshListener;
	private OnFooterRefreshListener onFooterRefreshListener;

	public void setOnHeaderRefreshListener(
			OnHeaderRefreshListener listenerRefreshHeader) {
		onHeaderRefreshListener = listenerRefreshHeader;
	}

	public void setOnFooterRefreshListener(
			OnFooterRefreshListener listenerRefreshFooter) {
		onFooterRefreshListener = listenerRefreshFooter;
	}

	public PullRefreshList(Context context) {
		super(context);
		InitData(context);
	}

	public PullRefreshList(Context context, AttributeSet attrs) {
		super(context, attrs);
		InitData(context);
	}

	private void InitData(Context context) {
		// 表头
		m_llHeader = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.pull_list_header, null);
		m_ivHeaderArrow = (ImageView) m_llHeader
				.findViewById(R.id.ivHeaderArrow);

		m_pbHeaderWaiting = (ProgressBar) m_llHeader
				.findViewById(R.id.pbHeaderWaiting);
		m_tvHeaderTip = (TextView) m_llHeader
				.findViewById(R.id.tvHeaderRefreshTip);
		m_tvHeaderRefreshTime = (TextView) m_llHeader
				.findViewById(R.id.tvHeaderRefreshTime);

		MeasureView(m_llHeader);
		m_iHeaderHeight = m_llHeader.getMeasuredHeight();

		m_llHeader.setPadding(0, -1 * m_iHeaderHeight, 0, 0);
		m_llHeader.invalidate();

		addHeaderView(m_llHeader, null, false);
		// 表脚
		m_llFooter = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.pull_list_footer, null);
		m_ivFooterArrow = (ImageView) m_llFooter
				.findViewById(R.id.ivFooterArrow);

		m_pbFooterWaiting = (ProgressBar) m_llFooter
				.findViewById(R.id.pbFooterWaiting);
		m_tvFooterTip = (TextView) m_llFooter
				.findViewById(R.id.tvFooterRefreshTip);
		m_tvFooterRefreshTime = (TextView) m_llFooter
				.findViewById(R.id.tvFooterRefreshTime);

		MeasureView(m_llFooter);
		m_iFooterHeight = m_llFooter.getMeasuredHeight();

		m_llFooter.setPadding(0, 0, 0, -1 * m_iFooterHeight);
		m_llFooter.invalidate();
		addFooterView(m_llFooter, null, false);

		setOnScrollListener(this);

		m_raTurnArrow = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		m_raTurnArrow.setInterpolator(new LinearInterpolator());
		m_raTurnArrow.setDuration(250);
		m_raTurnArrow.setFillAfter(true);

		m_raTurnArrowBack = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		m_raTurnArrowBack.setInterpolator(new LinearInterpolator());
		m_raTurnArrowBack.setDuration(200);
		m_raTurnArrowBack.setFillAfter(true);

		m_iHeaderState = STATE_HEAD_NONE;
		m_bIsTouching = false;
	}

	public void onScroll(AbsListView view, int firstVisiableItem,
			int visibleItemCount, int totalItemCount) {
		m_iFirstVisiableItem = firstVisiableItem;
		m_iLastVisiableItem = firstVisiableItem + visibleItemCount;
		m_iItemCount = totalItemCount;
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getHeaderViewsCount() > 0) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				if (m_iFirstVisiableItem == 0 && !m_bIsTouching) {
					m_bIsTouching = true;
					m_iPrePosY = (int) event.getY();
				}
			}
				break;
			case MotionEvent.ACTION_UP: {
				switch (m_iHeaderState) {
				case STATE_HEAD_PULL: {
					m_iHeaderState = STATE_HEAD_NONE;
					onHeaderViewChange();
				}
					break;
				case STATE_HEAD_RELEASE: {
					m_iHeaderState = STATE_HEAD_LOADING;
					onHeaderViewChange();
					onHeaderRefresh();
				}
					break;
				default:
					break;
				}

				m_bIsTouching = false;
				m_bIsPullBack = false;
			}
				break;
			case MotionEvent.ACTION_MOVE: {
				int iCurPosY = (int) event.getY();

				if (!m_bIsTouching && m_iFirstVisiableItem == 0) {
					m_bIsTouching = true;
					m_iPrePosY = iCurPosY;
				}
				switch (m_iHeaderState) {
				case STATE_HEAD_NONE: {
					if (iCurPosY - m_iPrePosY > 0) {
						m_iHeaderState = STATE_HEAD_PULL;
						onHeaderViewChange();
					}
				}
					break;
				case STATE_HEAD_PULL: {
					if ((iCurPosY - m_iPrePosY) / PULL_FACTOR >= m_iHeaderHeight) {
						m_iHeaderState = STATE_HEAD_RELEASE;
						m_bIsPullBack = true;
						onHeaderViewChange();
					} else if (iCurPosY - m_iPrePosY <= 0) {
						m_iHeaderState = STATE_HEAD_NONE;
						onHeaderViewChange();
					}
					m_llHeader.setPadding(0, -1 * m_iHeaderHeight
							+ (iCurPosY - m_iPrePosY) / PULL_FACTOR, 0, 0);

				}
					break;
				case STATE_HEAD_RELEASE: {
					if (((iCurPosY - m_iPrePosY) / PULL_FACTOR < m_iHeaderHeight)
							&& (iCurPosY - m_iPrePosY) > 0) {
						m_iHeaderState = STATE_HEAD_PULL;
						onHeaderViewChange();
					} else if (iCurPosY - m_iPrePosY <= 0) {
						m_iHeaderState = STATE_HEAD_NONE;
						onHeaderViewChange();
					}
					m_llHeader.setPadding(0, (iCurPosY - m_iPrePosY)
							/ PULL_FACTOR - m_iHeaderHeight, 0, 0);
				}
					break;
				default:
					break;
				}
			}
				break;
			}
		}
		if (getFooterViewsCount() > 0) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				if (m_iLastVisiableItem == m_iItemCount - 1 && !m_bIsTouching) {
					m_bIsTouching = true;
					m_iPrePosY = (int) event.getY();
				}
			}
				break;
			case MotionEvent.ACTION_UP: {
				switch (m_iFooterState) {
				case STATE_FOOT_PULL: {
					m_iFooterState = STATE_FOOT_NONE;
					onFooterViewChange();
				}
					break;
				case STATE_FOOT_RELEASE: {
					m_iFooterState = STATE_FOOT_LOADING;
					onFooterViewChange();
					onFooterRefresh();
				}
					break;
				default:
					break;
				}

				m_bIsTouching = false;
				m_bIsPullBack = false;
			}
				break;
			case MotionEvent.ACTION_MOVE: {
				int iCurPosY = (int) event.getY();

				if (!m_bIsTouching && m_iLastVisiableItem == m_iItemCount - 1) {
					m_bIsTouching = true;
					m_iPrePosY = iCurPosY;
				}
				switch (m_iFooterState) {
				case STATE_FOOT_NONE: {
					if (iCurPosY - m_iPrePosY < 0) {
						m_iFooterState = STATE_FOOT_PULL;
						onFooterViewChange();
					}
				}
					break;
				case STATE_FOOT_PULL: {
					if (Math.abs(iCurPosY - m_iPrePosY) / PULL_FACTOR >= m_iFooterHeight) {
						m_iFooterState = STATE_FOOT_RELEASE;
						m_bIsPullBack = true;
						onFooterViewChange();
					} else if (iCurPosY - m_iPrePosY >= 0) {
						m_iFooterState = STATE_FOOT_NONE;
						onFooterViewChange();
					}
					m_llFooter.setPadding(
							0,
							0,
							0,
							-1 * m_iFooterHeight
									+ Math.abs(iCurPosY - m_iPrePosY)
									/ PULL_FACTOR);

				}
					break;
				case STATE_FOOT_RELEASE: {
					if ((Math.abs(iCurPosY - m_iPrePosY) / PULL_FACTOR < m_iFooterHeight)
							&& (iCurPosY - m_iPrePosY) < 0) {
						m_iFooterState = STATE_FOOT_PULL;
						onFooterViewChange();
					} else if (iCurPosY - m_iPrePosY >= 0) {
						m_iFooterState = STATE_FOOT_NONE;
						onFooterViewChange();
					}
					m_llFooter.setPadding(0, 0, 0,
							Math.abs(iCurPosY - m_iPrePosY) / PULL_FACTOR
									- m_iFooterHeight);
				}
					break;
				default:
					break;
				}
			}
				break;
			}
		}

		return super.onTouchEvent(event);
	}

	private void onHeaderViewChange() {
		switch (m_iHeaderState) {
		case STATE_HEAD_RELEASE: {
			m_ivHeaderArrow.setVisibility(View.VISIBLE);
			m_pbHeaderWaiting.setVisibility(View.GONE);
			m_tvHeaderTip.setVisibility(View.VISIBLE);
			m_tvHeaderRefreshTime.setVisibility(View.VISIBLE);

			m_ivHeaderArrow.clearAnimation();
			m_ivHeaderArrow.startAnimation(m_raTurnArrow);

			m_tvHeaderTip.setText("松开刷新");
		}
			break;
		case STATE_HEAD_PULL: {
			m_pbHeaderWaiting.setVisibility(View.GONE);
			m_tvHeaderTip.setVisibility(View.VISIBLE);
			m_tvHeaderRefreshTime.setVisibility(View.VISIBLE);
			m_ivHeaderArrow.clearAnimation();
			m_ivHeaderArrow.setVisibility(View.VISIBLE);
			if (m_bIsPullBack) {
				m_bIsPullBack = false;
				m_ivHeaderArrow.clearAnimation();
				m_ivHeaderArrow.startAnimation(m_raTurnArrowBack);

				m_tvHeaderTip.setText("下拉刷新");
			} else {
				m_tvHeaderTip.setText("下拉刷新");
			}
		}
			break;
		case STATE_HEAD_LOADING: {
			m_llHeader.setPadding(0, 0, 0, 0);

			m_pbHeaderWaiting.setVisibility(View.VISIBLE);
			m_ivHeaderArrow.clearAnimation();
			m_ivHeaderArrow.setVisibility(View.GONE);
			m_tvHeaderTip.setText("正在刷新...");
			m_tvHeaderRefreshTime.setVisibility(View.VISIBLE);
		}
			break;
		case STATE_HEAD_NONE: {
			m_llHeader.setPadding(0, -1 * m_iHeaderHeight, 0, 0);

			m_pbHeaderWaiting.setVisibility(View.GONE);
			m_ivHeaderArrow.clearAnimation();
			m_ivHeaderArrow.setImageResource(R.drawable.arrow_down);
			m_tvHeaderTip.setText("下拉刷新");
			m_tvHeaderRefreshTime.setVisibility(View.VISIBLE);
		}
			break;
		}
	}

	private void onFooterViewChange() {
		switch (m_iFooterState) {
		case STATE_FOOT_RELEASE: {
			m_ivFooterArrow.setVisibility(View.VISIBLE);
			m_pbFooterWaiting.setVisibility(View.GONE);
			m_tvFooterTip.setVisibility(View.VISIBLE);
			m_tvFooterRefreshTime.setVisibility(View.VISIBLE);

			m_ivFooterArrow.clearAnimation();
			m_ivFooterArrow.startAnimation(m_raTurnArrow);

			m_tvFooterTip.setText("松开刷新");
		}
			break;
		case STATE_FOOT_PULL: {
			m_pbFooterWaiting.setVisibility(View.GONE);
			m_tvFooterTip.setVisibility(View.VISIBLE);
			m_tvFooterRefreshTime.setVisibility(View.VISIBLE);
			m_ivFooterArrow.clearAnimation();
			m_ivFooterArrow.setVisibility(View.VISIBLE);
			if (m_bIsPullBack) {
				m_bIsPullBack = false;
				m_ivFooterArrow.clearAnimation();
				m_ivFooterArrow.startAnimation(m_raTurnArrowBack);

				m_tvFooterTip.setText("上拉刷新");
			} else {
				m_tvFooterTip.setText("上拉刷新");
			}
		}
			break;
		case STATE_FOOT_LOADING: {
			m_llFooter.setPadding(0, 0, 0, 0);

			m_pbFooterWaiting.setVisibility(View.VISIBLE);
			m_ivFooterArrow.clearAnimation();
			m_ivFooterArrow.setVisibility(View.GONE);
			m_tvFooterTip.setText("正在刷新...");
			m_tvFooterRefreshTime.setVisibility(View.VISIBLE);
		}
			break;
		case STATE_FOOT_NONE: {
			m_llFooter.setPadding(0, 0, 0, -1 * m_iFooterHeight);

			m_pbFooterWaiting.setVisibility(View.GONE);
			m_ivFooterArrow.clearAnimation();
			m_ivFooterArrow.setImageResource(R.drawable.arrow_up);
			m_tvFooterTip.setText("上拉刷新");
			m_tvFooterRefreshTime.setVisibility(View.VISIBLE);
		}
			break;
		}
	}

	public void onHeaderRefreshComplete() {
		m_iHeaderState = STATE_HEAD_NONE;
		m_tvHeaderRefreshTime.setText("�?��更新�?" + new Date().toLocaleString());
		onHeaderViewChange();

		invalidateViews();
	}

	public void onFooterRefreshComplete() {
		m_iFooterState = STATE_HEAD_NONE;
		m_tvFooterRefreshTime.setText("�?��更新�?" + new Date().toLocaleString());
		onFooterViewChange();

		invalidateViews();
	}

	private void onHeaderRefresh() {
		if (onHeaderRefreshListener == null)
			return;

		onHeaderRefreshListener.onHeaderRefresh();
	}

	private void onFooterRefresh() {
		if (onFooterRefreshListener == null)
			return;

		onFooterRefreshListener.onFooterRefresh();
	}

	private void MeasureView(View view) {
		ViewGroup.LayoutParams paramsLayout = view.getLayoutParams();
		if (paramsLayout == null)
			paramsLayout = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

		int iViewHeightSpec;
		int iViewWidthSpec = ViewGroup.getChildMeasureSpec(0, 0,
				paramsLayout.width);
		int iLayoutParamsHeight = paramsLayout.height;
		if (iLayoutParamsHeight > 0)
			iViewHeightSpec = MeasureSpec.makeMeasureSpec(iLayoutParamsHeight,
					MeasureSpec.EXACTLY);
		else
			iViewHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);

		view.measure(iViewWidthSpec, iViewHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		m_tvHeaderRefreshTime.setText("�?��更新�?" + new Date().toLocaleString());
		m_tvFooterRefreshTime.setText("�?��更新�?" + new Date().toLocaleString());

		super.setAdapter(adapter);
	}
}
