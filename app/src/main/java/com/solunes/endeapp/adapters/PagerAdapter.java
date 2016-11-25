package com.solunes.endeapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.solunes.endeapp.fragments.DataFragment;
import com.solunes.endeapp.models.DataModel;

import java.util.ArrayList;

/**
 * Adaptador del pagerview
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "PagerAdapter";
    private int size;
    private ArrayList<DataModel> dataModels;
    private int lecId;

    public PagerAdapter(FragmentManager fm, int sizeTable, ArrayList<DataModel> dataModels, int lecId) {
        super(fm);
        this.size = sizeTable;
        this.dataModels = dataModels;
        this.lecId = lecId;
    }

    @Override
    public Fragment getItem(int position) {
        return DataFragment.newInstance(dataModels.get(position).getId(), lecId);
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public int getItemPosition(Object object) {
        DataModel dataModel = (DataModel) object;
        for (int i = 0; i < dataModels.size(); i++) {
            if (dataModel.getId() == dataModels.get(i).getId()) {
                return i;
            }
        }
        return super.getItemPosition(object);
    }
}
