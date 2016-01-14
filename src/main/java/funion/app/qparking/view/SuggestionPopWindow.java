package funion.app.qparking.view;

import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SuggestionPopWindow extends PopupWindow implements OnItemClickListener {

	private Context mContext;
	private ListView mListView;
	private NormalSpinerAdapter mAdapter;
	private IOnItemSelectListener mItemSelectListener;

	public static interface IOnItemSelectListener {
		public void onItemClick(int pos);
	};

	public SuggestionPopWindow(Context context) {
		super(context);

		mContext = context;
		init();
	}

	public void setItemListener(IOnItemSelectListener listener) {
		mItemSelectListener = listener;
	}

	private void init() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.suggestion_popwin, null);
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		setFocusable(true);
		// 设置背景（去掉popwindow边框）
		// ColorDrawable dw = new ColorDrawable(0x00);
		// setBackgroundDrawable(dw);

		mListView = (ListView) view.findViewById(R.id.listview);

		mAdapter = new NormalSpinerAdapter(mContext);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	public void refreshData(List<String> list, int selIndex) {
		if (list != null && selIndex != -1) {
			mAdapter.refreshData(list, selIndex);
		}
	}

	public void notifyChanged() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		dismiss();
		if (mItemSelectListener != null) {
			mItemSelectListener.onItemClick(pos);
		}
	}

	public class NormalSpinerAdapter extends AbstractSpinerAdapter<String> {

		public NormalSpinerAdapter(Context context) {
			super(context);
		}
	}

	public abstract class AbstractSpinerAdapter<T> extends BaseAdapter {

		private List<T> mObjects = new ArrayList<T>();
		private int mSelectItem = 0;

		private LayoutInflater mInflater;

		public AbstractSpinerAdapter(Context context) {
			init(context);
		}

		public void refreshData(List<T> objects, int selIndex) {
			mObjects = objects;
			if (selIndex < 0) {
				selIndex = 0;
			}
			if (selIndex >= mObjects.size()) {
				selIndex = mObjects.size() - 1;
			}

			mSelectItem = selIndex;
		}

		private void init(Context context) {
			mContext = context;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {

			return mObjects.size();
		}

		@Override
		public Object getItem(int pos) {
			return mObjects.get(pos).toString();
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(int pos, View convertView, ViewGroup arg2) {
			ViewHolder viewHolder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.suggestion_popwin_item, null);
				viewHolder = new ViewHolder();
				viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String item = (String) getItem(pos);
			viewHolder.mTextView.setText(item);

			return convertView;
		}

		public class ViewHolder {
			public TextView mTextView;
		}

	}

}
