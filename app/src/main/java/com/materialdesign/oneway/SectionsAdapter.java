package com.materialdesign.oneway;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.DataObjectHolder>  {
    private static ArrayList<SectionObject> mDataSet;
    public static MyClickListener myClickListener;
    static LevelItemAdapter levelItemAdapter;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView textSectionName;
        RecyclerView levelItemRecyclerView;

        public DataObjectHolder(View itemView) {
            super(itemView);
            textSectionName = (TextView) itemView.findViewById(R.id.textSectionName);
            levelItemRecyclerView = (RecyclerView) itemView.findViewById(R.id.levelsView);
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
        SectionsAdapter.myClickListener = myClickListener;
    }

    public SectionsAdapter(ArrayList<SectionObject> myDataSet) {
        mDataSet = myDataSet;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View staticView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_view_item, parent, false);
        return new DataObjectHolder(staticView);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {
        holder.textSectionName.setText(mDataSet.get(position).getSectionName());
        setRecyclerView(holder, position);
    }

    private void setRecyclerView(DataObjectHolder holder, int position) {
        holder.levelItemRecyclerView.setLayoutManager(new GridLayoutManager(LevelsActivity.context, 5));
        levelItemAdapter = new LevelItemAdapter(mDataSet.get(position).getLevels());
        holder.levelItemRecyclerView.setAdapter(levelItemAdapter);
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
