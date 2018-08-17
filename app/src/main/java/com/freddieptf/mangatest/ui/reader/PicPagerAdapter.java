package com.freddieptf.mangatest.ui.reader;

import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.ImagePage;

import java.io.File;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by fred on 3/22/15.
 */
public class PicPagerAdapter extends PagerAdapter {

    private ImagePage[] pages;

    public PicPagerAdapter(ImagePage[] pages) {
        super();
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages == null ? 0 : pages.length;
    }

    public String getCurrentPicUri(int pos) {
        return Uri.fromFile(new File(pages[pos].getUrl())).toString();
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
        viewHolder.bind(pages[position]);
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

        void bind(ImagePage imagePage) {
            Log.d("bind", "bind: " + imagePage.getUrl());
            Glide.with(imageView.getContext()).load(imagePage.getUrl()).into(imageView);
            pageNumber.setText(imagePage.getPageId() + "");
        }
    }
}


