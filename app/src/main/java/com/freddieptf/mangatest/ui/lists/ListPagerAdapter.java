package com.freddieptf.mangatest.ui.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.utils.Utilities;

/**
 * Created by fred on 8/25/15.
 */
class ListPagerAdapter extends android.support.v4.view.PagerAdapter {


    Context context; //// FIXME: 11/10/16 noooope
    private String[] titles = {"Latest", "Popular", "List"};
    private PagerHelper helper;

    ListPagerAdapter(PagerHelper helper, Context context) {
        this.helper = helper;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.pager_list_item, container, false);

        PagerViewHolder viewHolder = new PagerViewHolder(view);
        viewHolder.list.setTag(position);
        helper.getListView(viewHolder.list);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 2) return Utilities.getCurrentSource(context);
        else return titles[position];

    }

    interface PagerHelper {
        void getListView(RecyclerView listView);
    }

    static class PagerViewHolder {
        private RecyclerView list;
        public PagerViewHolder(View view) {
            list = (RecyclerView) view.findViewById(R.id.list);
        }
    }

}
