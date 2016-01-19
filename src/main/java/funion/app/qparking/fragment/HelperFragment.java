package funion.app.qparking.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import funion.app.qparking.R;

/**
 * Created by Administrator on 2016/1/18.
 */
public class HelperFragment extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       if(view==null){
           view=inflater.inflate(R.layout.helperfragment,null);
           findViewById();
       }
        return view;
    }

    private void findViewById() {
    }
}
