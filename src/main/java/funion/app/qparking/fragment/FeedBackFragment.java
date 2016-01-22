package funion.app.qparking.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import funion.app.qparking.QParkingApp;
import funion.app.qparking.R;
import funion.app.qparking.tools.OkHttpUtils;

/**
 * Created by Administrator on 2016/1/18.
 */
public class FeedBackFragment extends Fragment {
    private View view;
    private EditText feedback_et;
    private ExpandableListView commonquestion_ex;
    private List<String> parenttitle;
    private List<String> time;
    Map<String,List<String>>child=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      if(view==null){
          view=inflater.inflate(R.layout.feedbackfragment,null);
          initData();
          findViewId();
      }
        return view;
    }

    private void initData() {
        OkHttpUtils.getInstance().post(QParkingApp.URL, new OkHttpUtils.ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String result) {

            }
        },null,"help");
        parenttitle=new ArrayList<String>();
        time=new ArrayList<String>();
        parenttitle.add("为什么导航的时候出错");
        parenttitle.add("停车场找到后可用车信息不符");
        parenttitle.add("收费信息在哪，为什么停车场看到不到");
        parenttitle.add("去停车APP收费吗，怎么用");
        child=new HashMap<String,List<String>>();
        List<String> childlist=new ArrayList<String>();
        childlist.add("清闲确认手机信息是否正常");
        child.put("为什么导航的时候出错", childlist);
        childlist.add("清闲确认手机信息是否正常");
        child.put("停车场找到后可用车信息不符", childlist);
        childlist.add("清闲确认手机信息是否正常");
        child.put("收费信息在哪，为什么停车场看到不到", childlist);
        childlist.add("清闲确认手机信息是否正常");
        child.put("去停车APP收费吗，怎么用", childlist);
        time.add("2016-01-08");
        time.add("2016-01-08");
        time.add("2016-01-08");
        time.add("2016-01-08");

    }

    private void findViewId() {
        feedback_et=(EditText)view.findViewById(R.id.feedback_et);
        commonquestion_ex=(ExpandableListView)view.findViewById(R.id.commonquestion_ex);
        commonquestion_ex.setGroupIndicator(null);
        commonquestion_ex.setAdapter(new MyAdapter());
    }

    public class MyAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return parenttitle.size();
        }

        @Override
        public int getChildrenCount(int i) {
            String key = parenttitle.get(i);
            int size=child.get(key).size();
            return size;
        }

        @Override
        public Object getGroup(int i) {
            return parenttitle.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return i1;
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.feedbackparent_item, null);
               TextView title =(TextView)convertView.findViewById(R.id.parent_title_tv);
                TextView showtime=(TextView)convertView.findViewById(R.id.parent_time_tv);
            title.setText(
                    parenttitle.get(groupPosition));
            showtime.setText(time.get(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView=inflater.inflate(R.layout.feedbackchilditem,null);
                TextView childs=(TextView)convertView.findViewById(R.id.show_child_tv);
            String key=parenttitle.get(childPosition);
            String info=child.get(key).get(childPosition);
           childs.setText(info);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }


    }
}
