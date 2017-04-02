package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class LevelItemAdapter extends RecyclerView.Adapter<LevelItemAdapter.DataObjectHolder>  {
    private ArrayList<LevelObject> mDataSet;
    public static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView textIndex;
        CardView cardView;

        public DataObjectHolder(View itemView) {
            super(itemView);
            textIndex = (TextView) itemView.findViewById(R.id.textIndex);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
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
        holder.textIndex.setText("" + mDataSet.get(position).getIndex());
        setSizeOfTile(holder);
        setRotation(holder, position);
        setColors(holder, position);
        setOnClickListener(holder, position);
    }

    private void setSizeOfTile(DataObjectHolder holder) {
        int tileSize = (StartActivity.size.x - 250) / 5;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();
        layoutParams.height = tileSize;
        layoutParams.width = tileSize;
        holder.cardView.setLayoutParams(layoutParams);
    }

    private void setRotation(final DataObjectHolder holder, final int position) {
        if(LevelsActivity.getFirstUndoneLevel() == mDataSet.get(position).getIndex()) {
            final ObjectAnimator rotation = ObjectAnimator.ofFloat(holder.itemView, "rotation", 0, 20, -20);
            rotation.setRepeatCount(1);
            rotation.setRepeatMode(ValueAnimator.REVERSE);
            rotation.setDuration(LevelsActivity.duration);
            rotation.setInterpolator(new AccelerateInterpolator());
            rotation.setStartDelay(StartActivity.duration * 2);
            rotation.start();
            rotation.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animator) {}
                @Override public void onAnimationEnd(Animator animator) {
                    setRotation(holder, position);
                }
                @Override public void onAnimationCancel(Animator animator) {}
                @Override public void onAnimationRepeat(Animator animator) {}
            });
        }
    }

    private void setColors(DataObjectHolder holder, int position) {
        if(LevelsActivity.finishedLevels.contains(mDataSet.get(position).getIndex())) {
            holder.cardView.setCardBackgroundColor(LevelsActivity.context.getResources().getColor(R.color.colorPrimary));
            holder.textIndex.setTextColor(LevelsActivity.context.getResources().getColor(R.color.colorAccentBright));
        }
        else {
            holder.cardView.setCardBackgroundColor(LevelsActivity.context.getResources().getColor(R.color.colorAccentBright));
            holder.textIndex.setTextColor(LevelsActivity.context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void setOnClickListener(DataObjectHolder holder, final int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClickListener.onItemClick(mDataSet.get(position).getIndex(), view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
