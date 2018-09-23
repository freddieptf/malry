package com.freddieptf.reader.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by fred on 5/19/15.
 */
public class ReaderViewPager extends ViewPager {

    private float startDragXPos = 0;
    private ReadProgressListener readProgressListener;

    public ReaderViewPager(Context context) {
        this(context, null);
    }

    public ReaderViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setReadProgressListener(ReadProgressListener readProgressListener) {
        this.readProgressListener = readProgressListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float inDragX = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startDragXPos = ev.getX();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if (startDragXPos < inDragX) {
                    // swipe from left to right
                    if(getCurrentItem() == 0) {
                        // if we are at the start
                        readProgressListener.onSwipeToPreviousCh();
                    }
                } else if(startDragXPos > inDragX) {
                    // swipe from right to left
                    if(getCurrentItem() == getAdapter().getCount()-1) {
                        // if we are at the end
                        readProgressListener.onSwipeToNextCh();
                    }
                }
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface ReadProgressListener {
        void onSwipeToNextCh();
        void onSwipeToPreviousCh();
    }

}
