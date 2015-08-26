package com.freddieptf.mangatest.recyclerviewdecor.swipestuff;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.freddieptf.mangatest.R;

/**
 * Created by fred on 7/20/15.
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter itemTouchHelperAdapter;

    public ItemTouchHelperCallback(ItemTouchHelperAdapter itemTouchHelperAdapter){
        this.itemTouchHelperAdapter = itemTouchHelperAdapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT);
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;

//        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
//            final float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
//            itemView.setAlpha(alpha);
//        }


        Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.swipe_background);
        drawable.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
        drawable.draw(c);

    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        itemTouchHelperAdapter.onItemDismised(viewHolder.getAdapterPosition());
    }
}
