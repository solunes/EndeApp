package com.solunes.endeapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.solunes.endeapp.fragments.DataFragment;
import com.solunes.endeapp.models.DataModel;

import java.util.ArrayList;

/**
 * Created by jhonlimaster on 19-11-15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "PagerAdapter";
    private int size;
    private ArrayList<DataModel> dataModels;

    public PagerAdapter(FragmentManager fm, int sizeTable) {
        super(fm);
        this.size = sizeTable;
        this.dataModels = new ArrayList<>();
    }

    public PagerAdapter(FragmentManager fm, int sizeTable, ArrayList<DataModel> dataModels) {
        super(fm);
        this.size = sizeTable;
        this.dataModels = dataModels;
    }

    @Override
    public Fragment getItem(int position) {
        return DataFragment.newInstance(dataModels.get(position).get_id());
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(dataModels.get(position).get_id());
    }

    @Override
    public int getItemPosition(Object object) {
        DataModel dataModel = (DataModel) object;
        for (int i = 0; i < dataModels.size(); i++) {
            if (dataModel.get_id() == dataModels.get(i).get_id()){
                return i;
            }
        }
        return super.getItemPosition(object);
    }
}
