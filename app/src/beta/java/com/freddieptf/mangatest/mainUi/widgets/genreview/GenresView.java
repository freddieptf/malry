package com.freddieptf.mangatest.mainUi.widgets.genreview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.freddieptf.mangatest.R;

/**
 * Created by fred on 9/18/15.
 */
public class GenresView extends FrameLayout {

    RecyclerView recyclerView;
    CharSequence[] genres;
    GenreAdapter adapter;
    GenreViewUtils genreViewUtils;

    public GenresView(Context context) {
        this(context, null);
    }

    public void setVisibilityListener(GenreViewUtils genreViewUtils){
        this.genreViewUtils = genreViewUtils;
    }

    public GenresView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GenresView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.genre_view, this, true);
        recyclerView = (RecyclerView) findViewById(R.id.genre_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.setHasFixedSize(true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GenresView);
        genres = typedArray.getTextArray(R.styleable.GenresView_genreStringList);
        typedArray.recycle();

        recyclerView.setItemViewCacheSize(genres.length);

        adapter = new GenreAdapter(context);
        adapter.setStringSource(genres);
        recyclerView.setAdapter(adapter);

    }

    public void show(){
        if(getVisibility() == GONE) {
            setVisibility(INVISIBLE);
            setTranslationY(56f);

            Animator[] anims = new Animator[]{
                    ObjectAnimator.ofFloat(this, "alpha", 0f, 1f),
                    ObjectAnimator.ofFloat(this, "translationY", 0f)
            };

            for (Animator a : anims) {
                a.setDuration(250).setInterpolator(new FastOutSlowInInterpolator());
                a.start();
            }

            setVisibility(VISIBLE);
            genreViewUtils.isGenreViewVisible(true);

        } else if(getVisibility() == VISIBLE){
            Animator[] a = new Animator[]{
                    ObjectAnimator.ofFloat(this, "alpha", 1f, 0f),
                    ObjectAnimator.ofFloat(this, "translationY", 0f, 34f)
            };

                a[0].setDuration(175).setInterpolator(new LinearOutSlowInInterpolator());
                a[0].addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        setVisibility(GONE);
                        genreViewUtils.isGenreViewVisible(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            a[1].setDuration(200).setInterpolator(new LinearOutSlowInInterpolator());
            a[0].start();
            a[1].start();

        }

    }

    public void setVisible(boolean visible){
        if(visible) setVisibility(VISIBLE);
        else setVisibility(GONE);
    }


}
