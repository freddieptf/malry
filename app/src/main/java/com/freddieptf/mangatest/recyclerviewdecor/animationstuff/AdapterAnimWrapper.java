package com.freddieptf.mangatest.recyclerviewdecor.animationstuff;

import android.animation.Animator;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fred on 5/5/15.
 */
public abstract class AdapterAnimWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int ANIM_DURATION = 200;
    int pos = -1;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    public AdapterAnimWrapper(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter){
        this.adapter = adapter;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return adapter.onCreateViewHolder(parent, viewType);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        adapter.onBindViewHolder(holder, position);

        if(position > pos){
            Animator animator = getAnimator(holder.itemView);
            animator.setDuration(ANIM_DURATION);
            animator.setInterpolator(new FastOutLinearInInterpolator());
            animator.start();
            pos = position;
        }

    }

    @Override
    public int getItemCount() {
        return adapter.getItemCount();
    }

    protected abstract Animator getAnimator(View view);


}
