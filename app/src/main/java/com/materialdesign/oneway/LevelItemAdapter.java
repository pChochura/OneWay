package com.materialdesign.oneway;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LevelItemAdapter extends RecyclerView.Adapter<LevelItemAdapter.DataObjectHolder>  {
    private ArrayList<LevelObject> mDataSet;
    public static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView textIndex;

        public DataObjectHolder(View itemView) {
            super(itemView);
            textIndex = (TextView) itemView.findViewById(R.id.textIndex);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            myClickListener.onLongItemClick(getAdapterPosition(), v);
            return false;
        }
    }

    public static void setOnItemClickListener(MyClickListener myClickListener) {
        LevelItemAdapter.myClickListener = myClickListener;
    }

    public LevelItemAdapter(ArrayList<LevelObject> myDataSet) {
        mDataSet = myDataSet;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View staticView = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_view_item, parent, false);
        return new DataObjectHolder(staticView);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {
        holder.textIndex.setText("" + (position + 1));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
        void onLongItemClick(int position, View v);
    }
}
