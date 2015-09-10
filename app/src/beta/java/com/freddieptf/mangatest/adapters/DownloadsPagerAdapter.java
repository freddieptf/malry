package com.freddieptf.mangatest.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.recyclerviewdecor.DividerItemDecoration;

/**
 * Created by fred on 9/8/15.
 */
public class DownloadsPagerAdapter extends PagerAdapter {

    String[] titles = {"My Files", "Running"};

    @Override
    public int getCount() {
        return 1;
    }

    DonwloadsPagerHelper pagerHelper;
    public DownloadsPagerAdapter(DonwloadsPagerHelper pagerHelper){
        this.pagerHelper = pagerHelper;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.pager_download_item, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_pagerItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), DividerItemDecoration.VERTICAL_LIST));
        pagerHelper.getRecyclerView(position, recyclerView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public interface DonwloadsPagerHelper{
        void getRecyclerView(int position, RecyclerView recyclerView);
    }
}
