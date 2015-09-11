package com.freddieptf.mangatest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.utils.Utilities;

/**
 * Created by fred on 8/25/15.
 */
public class ListsPagerAdapter extends android.support.v4.view.PagerAdapter{


    String[] titles = {"Latest", "Popular", "List"};

    PagerHelper helper;
    Context context;

    public ListsPagerAdapter(PagerHelper helper, Context context){
        this.helper = helper;
        this.context = context;
    }

    //this viewholder looks pretty much useless to me, AFAIK
    class PagerViewHolder {
        ListView list;
        public PagerViewHolder(View view){
            list = (ListView) view.findViewById(R.id.list);
        }
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


    public interface PagerHelper {
        void getListView(ListView listView);
    }

}
