package com.freddieptf.mangatest.ui.volleyStuff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.PaletteHelper;
import com.freddieptf.mangatest.utils.Utilities;

/**
 * Created by fred on 2/22/15.
 */
public class FadeInNetworkImageView extends com.android.volley.toolbox.NetworkImageView {

    public final int FADE_IN_TIME = 200;
    PaletteHelper paletteHelper;
    Context context;
    MyColorUtils myColorUtils;
    private boolean local;
    private Bitmap localBitmap;

    public FadeInNetworkImageView(Context context) {
        this(context, null);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        myColorUtils = new MyColorUtils(context);
    }


    public void setLocalBitmap(Bitmap bitmap){
        if(bitmap != null) local = true;
        localBitmap = bitmap;
        requestLayout();
    }

    public void setPaletteHelper(PaletteHelper paletteHelper){
        this.paletteHelper = paletteHelper;
    }


    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        super.setImageUrl(url, imageLoader);
        local = false;
    }

    @Override
    public void setImageBitmap(final Bitmap bm) {
        super.setImageBitmap(bm);

        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(getResources().getColor(android.R.color.transparent)),
                new BitmapDrawable(getContext().getResources(), bm)
        });

        transitionDrawable.setCrossFadeEnabled(true);
        setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(FADE_IN_TIME);

        if(paletteHelper != null && bm != null){
            Utilities.Log("FadeInNetworkImageView", "paletteHelper != null");
            Palette.Builder builder = new Palette.Builder(bm);
            builder.generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int mangaColor = palette.getMutedColor(myColorUtils.getPrimaryColor());
                    int darkMangaColor = myColorUtils.darken(mangaColor, myColorUtils.getPrimaryDarkColor());
                    paletteHelper.OnPaletteGenerated(palette, mangaColor, darkMangaColor);
                }
            });
        }



    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(local) setImageBitmap(localBitmap);
    }
}
