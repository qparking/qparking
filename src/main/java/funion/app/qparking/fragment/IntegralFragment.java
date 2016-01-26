package funion.app.qparking.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import funion.app.qparking.R;

/**
 * Created by Administrator on 2016/1/16.
 */
public class IntegralFragment extends Fragment {
    private View integralfragment;
    private TextView integral_tv;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(integralfragment==null){
            integralfragment=inflater.inflate(R.layout.integral_view,null);
            sp = getActivity().getSharedPreferences("mMessage", getActivity().MODE_PRIVATE);
            findViewBtId();
        }
        return integralfragment;
    }

    private void findViewBtId() {
        integral_tv=(TextView)integralfragment.findViewById(R.id.integral);
        integral_tv.setText(sp.getString("integral",null));
    }
}
