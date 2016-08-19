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

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return DataFragment.newInstance();
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
