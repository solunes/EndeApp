package com.solunes.endeapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.utils.StatisticsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase es un recyclerview adapter de las estadisticas
 */
public class StatisticsRecyclerViewAdapter extends RecyclerView.Adapter<StatisticsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "StatisticsRecyclerViewAdapter";
    private List<StatisticsItem> mValues;
    private Context context;

    public StatisticsRecyclerViewAdapter(Context context, List<StatisticsItem> items) {
        this.context = context;
        mValues = items;
    }

    public StatisticsRecyclerViewAdapter(Context context) {
        this.context = context;
        mValues = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_statistics, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.valueView.setText(holder.mItem.getValue());
        holder.countView.setText(String.valueOf(holder.mItem.getCount()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView valueView;
        final TextView countView;
        StatisticsItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            valueView = (TextView) view.findViewById(R.id.st_item_value);
            countView = (TextView) view.findViewById(R.id.st_item_count);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + countView.getText() + "'";
        }
    }
}
