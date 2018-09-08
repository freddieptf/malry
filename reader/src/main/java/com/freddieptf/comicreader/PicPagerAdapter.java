package com.freddieptf.comicreader;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by fred on 3/22/15.
 */
public class PicPagerAdapter extends PagerAdapter {

    private final String TAG = getClass().getSimpleName();

    private final List<String> pages;

    public PicPagerAdapter(List<String> pages) {
        super();
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages == null ? 0 : pages.size();
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
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.pager_pic_item, container, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.bind(pages.get(position));
        container.addView(view);
        return view;
    }

    class ViewHolder {
        PhotoView imageView;
        TextView pageNumber;
        public ViewHolder(View view) {
            imageView = (PhotoView) view.findViewById(R.id.pager_ImageView_item);
            pageNumber = (TextView) view.findViewById(R.id.tv_mangaPageNumber);
        }

        void bind(String path) {
            Log.d(TAG, "bind: " + path);
            Glide.with(imageView.getContext()).load(path).into(imageView);
        }
    }
}


