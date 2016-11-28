package com.solunes.endeapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.solunes.endeapp.R;
import com.solunes.endeapp.adapters.StatisticsRecyclerViewAdapter;
import com.solunes.endeapp.dataset.DBAdapter;

/**
 * Este es un fragmento para mostrar las estadisticas de la aplicacion
 */
public class StatisticFragment extends Fragment {
    private static final String TAG = "StatisticFragment";
    private static final String ARG_PARAM1 = "param1";

    private int param;

    public StatisticFragment() {
    }

    public static StatisticFragment newInstance(int param1) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            DBAdapter dbAdapter = new DBAdapter(context);
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            StatisticsRecyclerViewAdapter recyclerViewAdapter =
                    new StatisticsRecyclerViewAdapter(getActivity(), dbAdapter.getSt(param));
            recyclerView.setAdapter(recyclerViewAdapter);
            dbAdapter.close();
        }
        return view;
    }

}
