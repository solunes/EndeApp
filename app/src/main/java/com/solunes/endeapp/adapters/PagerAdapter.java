package com.solunes.endeapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.solunes.endeapp.fragments.DataFragment;
import com.solunes.endeapp.models.DataModel;

import java.util.ArrayList;

/**
 * Created by jhonlimaster on 19-11-15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "PagerAdapter";
    private int size;

    public PagerAdapter(FragmentManager fm, int sizeTable) {
        super(fm);
        this.size = sizeTable;
    }

    @Override
    public Fragment getItem(int position) {
        return DataFragment.newInstance(position+1);
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position + 1) + "";
    }
}
