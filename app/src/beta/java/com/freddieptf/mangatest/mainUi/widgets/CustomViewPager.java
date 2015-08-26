package com.freddieptf.mangatest.mainUi.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by fred on 5/19/15.
 */
public class CustomViewPager extends ViewPager {

    boolean setVertical = false;

        public CustomViewPager(Context context) {
            this(context, null);

        }

        public CustomViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void setVertical(boolean setVertical) {
            if(setVertical) {
                // The majority of the magic happens here
                setPageTransformer(false, new VerticalPageTransformer());
                // The easiest way to get rid of the overscroll drawing that happens on the left and right
                setOverScrollMode(OVER_SCROLL_NEVER);
            }else{
                setPageTransformer(false, new PageTransformer());
            }

            this.setVertical = setVertical;


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

        private class PageTransformer implements ViewPager.PageTransformer{
        @Override
        public void transformPage(View page, float position) {
        }
    }



    /**
         * Swaps the X and Y coordinates of your touch event.
         */
        private MotionEvent swapXY(MotionEvent ev) {
            float width = getWidth();
            float height = getHeight();

            float newX = (ev.getY() / height) * width;
            float newY = (ev.getX() / width) * height;

            ev.setLocation(newX, newY);

            return ev;
        }

//        @Override
//        public boolean onInterceptTouchEvent(MotionEvent ev){
//            boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
//            swapXY(ev); // return touch coordinates to original reference frame for any child views
//            return intercepted;
//        }
//
//        @Override
//        public boolean onTouchEvent(MotionEvent ev) {
//            return super.onTouchEvent(swapXY(ev));
//        }


//    @Override
//    public boolean onInterceptHoverEvent(MotionEvent event) {
//        swapXY(event);
//        return setVertical ? super.onInterceptHoverEvent(swapXY(event)) : super.onInterceptHoverEvent(event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        return setVertical ? super.onTouchEvent(swapXY(ev)) : super.onTouchEvent(ev);
//    }
}
