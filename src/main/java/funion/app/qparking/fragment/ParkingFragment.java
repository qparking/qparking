package funion.app.qparking.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import funion.app.qparking.R;

/**
 * Created by Administrator on 2016/1/16.
 */
public class ParkingFragment extends Fragment {
    private View parkingfragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(parkingfragment==null){
            parkingfragment=inflater.inflate(R.layout.parking_view,null);
        }
        return parkingfragment;
    }
}
