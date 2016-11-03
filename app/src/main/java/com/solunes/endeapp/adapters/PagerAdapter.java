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

    public PagerAdapter(FragmentManager fm, int sizeTable, ArrayList<DataModel> dataModels) {
        super(fm);
        this.size = sizeTable;
        this.dataModels = dataModels;
    }

    @Override
    public Fragment getItem(int position) {
        return DataFragment.newInstance(dataModels.get(position).getId());
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
