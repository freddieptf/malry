package com.freddieptf.mangatest.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.mainUi.widgets.CustomViewPager;
import com.freddieptf.mangatest.volleyStuff.VolleySingletonClass;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by fred on 3/22/15.
 */
public class PicPagerAdapter extends PagerAdapter {

    String[] picUris;
    Context context;
    ViewHolder viewHolder;
    Boolean offline;
    CustomViewPager customViewPager;
    RequestQueue requestQueue;
    ImageLoader imageLoader;

    public PicPagerAdapter(Context context, String[] objects, Boolean offline, CustomViewPager pager) {
        super();
        this.offline = offline;
        picUris = objects;
        this.context = context;
        customViewPager = pager;
        Log.d(getClass().getSimpleName(), "picUris " + picUris.length);
        requestQueue = VolleySingletonClass.getInstance(context).getRequestQueue();
    }

    class ViewHolder{
        PhotoView imageView;
        TextView pageNumber;
        public ViewHolder(View view){
            imageView = (PhotoView) view.findViewById(R.id.pager_ImageView_item);
            pageNumber = (TextView) view.findViewById(R.id.tv_mangaPageNumber);
        }
    }

    @Override
    public int getCount() {
        return picUris.length;
    }

    public String getCurrentPicUri(){
        return picUris[customViewPager.getCurrentItem()];
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_pic_item, container, false);
        viewHolder = new ViewHolder(view);

        String orientation = viewHolder.imageView.getTag().toString();
        if(orientation.equals("landscape")) {
            customViewPager.setVertical(true);
        }
        viewHolder.pageNumber.setText("p" + (position + 1));

        final String pic = picUris[position];
        if(offline) new LoadImage(viewHolder, pic).execute();

        //@TODO implement online reading. ImageLoader, cache and stuff...(NetworkImageView probably..)

        container.addView(view);
        return view;
    }

    class LoadImage extends AsyncTask<Void, Void, Bitmap>{
        ViewHolder viewHolder;
        String picUri;
        LoadImage(ViewHolder viewHolder, String picUri){
            this.viewHolder = viewHolder;
            this.picUri = picUri;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return BitmapFactory.decodeFile(picUri);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            viewHolder.imageView.setImageBitmap(bitmap);
            viewHolder.imageView.setAlpha(0f);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.imageView.animate().alpha(1f).setDuration(200).start();

        }
    }






}


