package com.solunes.endeapp.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.solunes.endeapp.R;
import com.solunes.endeapp.adapters.PagerAdapter;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.fragments.DataFragment;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.utils.UserPreferences;

import java.util.ArrayList;

public class ReadingActivity extends AppCompatActivity implements DataFragment.OnFragmentListener {

    private static final String TAG = "ReadingActivity";

    public static final String KEY_LAST_PAGER_PSOTION = "last_pager_position";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        DBAdapter dbAdapter = new DBAdapter(this);
        adapter = new PagerAdapter(getSupportFragmentManager(), dbAdapter.getSizeData());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        ArrayList<DataModel> allData = dbAdapter.getAllData();
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tabAt = tabLayout.getTabAt(i);
            View inflate = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabText = (TextView) inflate.findViewById(R.id.textview_custom_tab);
            tabText.setText(String.valueOf(i + 1));
            if (allData.get(i).getTlxNvaLec() > 0) {
                tabText.setTextColor(getResources().getColor(android.R.color.white));
            }
            tabAt.setCustomView(inflate);
        }
        dbAdapter.close();

        int pagerPosition = UserPreferences.getInt(getApplicationContext(), KEY_LAST_PAGER_PSOTION);
        Log.e(TAG, "onCreate: " + pagerPosition);
        viewPager.setCurrentItem(pagerPosition);
    }

    @Override
    public void onTabListener() {
        Log.e(TAG, "onTabListener: " + viewPager.getCurrentItem());
        View customView = tabLayout.getTabAt(viewPager.getCurrentItem()).getCustomView();
        TextView textTab = (TextView) customView.findViewById(R.id.textview_custom_tab);
        textTab.setTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: "+ viewPager.getCurrentItem());
        UserPreferences.putInt(this, KEY_LAST_PAGER_PSOTION, viewPager.getCurrentItem());
    }
}
