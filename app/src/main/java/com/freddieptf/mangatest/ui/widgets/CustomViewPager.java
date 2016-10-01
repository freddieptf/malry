package com.freddieptf.mangatest.ui.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pixplicity.multiviewpager.MultiViewPager;

/**
 * Created by fred on 5/19/15.
 */
public class CustomViewPager extends MultiViewPager {

    boolean vertical = false;

    public CustomViewPager(Context context) {
        this(context, null);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
        init();
    }

    private void init() {
        if(vertical) setPageTransformer(true, new VerticalPageTransformer());
        else setPageTransformer(false, new PageTransformer() {
                @Override
                public void transformPage(View page, float position) {
                }
            });
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(swapXY(ev));
    }

    private MotionEvent swapXY(MotionEvent ev) {
        if(!vertical) return ev;
        //Get display dimensions
        float displayWidth=this.getWidth();
        float displayHeight=this.getHeight();

        //Get current touch position
        float posX=ev.getX();
        float posY=ev.getY();

        //Transform (X,Y) into (Y,X) taking display dimensions into account
        float newPosX=(posY/displayHeight)*displayWidth;
        float newPosY=(1-posX/displayWidth)*displayHeight;

        //swap the x and y coords of the touch event
        ev.setLocation(newPosX, newPosY);

        return ev;
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);

                // Counteract the default slide transition
                view.setTranslationX(view.getWidth() * -position);

                //set Y position to swipe in from top
                float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
