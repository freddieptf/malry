package com.freddieptf.mangatest.volleyStuff;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by fred on 2/16/15.
 */
public class VolleySingletonClass {

    private static VolleySingletonClass mInstance;
    private static Context context;
    private static MyImageLoader myImageLoader;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleySingletonClass(Context context) {
        VolleySingletonClass.context = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new LruBitmapCache(LruBitmapCache.getCacheSize(context)));

        myImageLoader = new MyImageLoader(mRequestQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(context)));

    }

    public static synchronized VolleySingletonClass getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingletonClass(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public MyImageLoader getmyLoader() {
        return myImageLoader;
    }

    public static class MyImageLoader extends ImageLoader {

        RequestQueue requestQueue;

        public MyImageLoader(RequestQueue queue, ImageCache imageCache) {
            super(queue, imageCache);
            requestQueue = queue;

        }

        @Override
        public ImageContainer get(String requestUrl, ImageListener listener) {
            return super.get(requestUrl, listener);
        }

        class my implements ImageListener {

            @Override
            public void onResponse(ImageContainer imageContainer, boolean b) {

            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }


        }




    }








}

