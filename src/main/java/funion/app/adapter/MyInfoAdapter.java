package funion.app.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;

/**
 * Created by Administrator on 2016/1/16.
 */
public class MyInfoAdapter extends FragmentStatePagerAdapter {
    public List<Fragment> mListViews;

    public MyInfoAdapter(FragmentManager supportFragmentManager, List<Fragment> listViews) {
        super(supportFragmentManager);
        this.mListViews = listViews;
    }


    @Override
    public Fragment getItem(int position) {
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }
}
