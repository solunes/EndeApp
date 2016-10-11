package com.solunes.endeapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.solunes.endeapp.R;

public class StatisticFragment extends Fragment {
    private static final String TAG = "StatisticFragment";
    private static final String ARG_PARAM1 = "param1";

    private int param;

    public StatisticFragment() {
        Log.e(TAG, "StatisticFragment: ");
        setMenuVisibility(true);
    }

    public static StatisticFragment newInstance(int param1) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        Log.e(TAG, "newInstance: ");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param = getArguments().getInt(ARG_PARAM1);
        }
        Log.e(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

}
