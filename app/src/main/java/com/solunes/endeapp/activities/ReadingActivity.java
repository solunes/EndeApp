package com.solunes.endeapp.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.solunes.endeapp.R;
import com.solunes.endeapp.adapters.PagerAdapter;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.fragments.DataFragment;

public class ReadingActivity extends AppCompatActivity implements DataFragment.OnFragmentListener {

    private static final String TAG = "ReadingActivity";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        DBAdapter dbAdapter = new DBAdapter(this);
        adapter = new PagerAdapter(getSupportFragmentManager(), dbAdapter.getSizeData());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tabAt = tabLayout.getTabAt(i);
            View inflate = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabText = (TextView) inflate.findViewById(R.id.textview_custom_tab);
            tabText.setText(String.valueOf(i+1));
            tabAt.setCustomView(inflate);
        }
        dbAdapter.close();
    }

    @Override
    public void onTabListener() {
        Log.e(TAG, "onTabListener: " + viewPager.getCurrentItem());
        View customView = tabLayout.getTabAt(viewPager.getCurrentItem()).getCustomView();
        TextView textTab = (TextView) customView.findViewById(R.id.textview_custom_tab);
        textTab.setTextColor(getResources().getColor(android.R.color.white));
    }
}
