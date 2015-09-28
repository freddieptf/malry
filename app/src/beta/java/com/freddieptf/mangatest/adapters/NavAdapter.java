package com.freddieptf.mangatest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.beans.DrawerListItems;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.ThemeUtilities;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.ArrayList;

/**
 * Created by fred on 4/22/15.
 */
public class NavAdapter extends RecyclerView.Adapter<NavAdapter.DrawerViewHolder> implements View.OnClickListener {

    ArrayList<DrawerListItems> arrayList;
    DrawerItemClick drawerItemClick;
    Context context;
    int activePos = -1;
    MyColorUtils myColorUtils;

    public NavAdapter(ArrayList<DrawerListItems> arrayList, Context context, DrawerItemClick drawerItemClick){
        this.arrayList = arrayList;
        this.context = context;
        this.drawerItemClick = drawerItemClick;
        myColorUtils = new MyColorUtils(context);
    }

    public void setActivePostion(int position){
        int beforeChange = activePos;
        activePos = position;

        if(beforeChange > -1)notifyItemChanged(beforeChange);
        notifyItemChanged(activePos);
        Utilities.Log("SetActivePosition", "a: " + activePos + " b: " + beforeChange);
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_drawer_item, parent, false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        holder.drawerItem.setTag(position);
        holder.drawerItem.setOnClickListener(this);


        int activeColor = activePos == position ? myColorUtils.getAccentColor()
                : myColorUtils.getPrimaryTextColor();

        DrawerListItems drawerListItems = arrayList.get(position);
        holder.title.setText(drawerListItems.getTitle());
        holder.imageView.setImageResource(drawerListItems.getIcon());

        if(!new ThemeUtilities(context).isLight())
            holder.imageView.setColorFilter(myColorUtils.getPrimaryTextColor());

        if(activePos < 3) {
            holder.drawerItem.setSelected(activePos == position);
            holder.title.setTextColor(activeColor);
            holder.imageView.setColorFilter(activeColor);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(View view) {
        if((Integer)view.getTag() < 3) {
            drawerItemClick.onClick((Integer) view.getTag());
            setActivePostion((Integer) view.getTag());
        }else{
            drawerItemClick.onClickSettings();
        }
    }

    public static class DrawerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;
        LinearLayout drawerItem;
        View divider;

        public DrawerViewHolder(View view){
            super(view);
            divider = view.findViewById(R.id.divider);
            title = (TextView)view.findViewById(R.id.drawer_list_item_text);
            imageView = (ImageView)view.findViewById(R.id.drawer_list_item_image);
            drawerItem = (LinearLayout) view.findViewById(R.id.drawer_item);
        }
    }

    public interface DrawerItemClick{
        void onClick(int position);
        void onClickSettings();
    }

}
