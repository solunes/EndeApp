package com.solunes.endeapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.solunes.endeapp.fragments.DataFragment;

/**
 * Created by jhonlimaster on 19-11-15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "PagerAdapter";
    private DataFragment.OnFragmentListener listener;

    public PagerAdapter(FragmentManager fm, DataFragment.OnFragmentListener listener) {
        super(fm);
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return DataFragment.newInstance(listener);
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "50";
    }
}
