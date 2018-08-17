package com.freddieptf.mangatest.ui.lists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.MangaItem;

import java.util.ArrayList;

/**
 * Created by fred on 2/8/15.
 */
class MangaListAdapter extends RecyclerView.Adapter<MangaListAdapter.MangaItemViewHolder>
        implements View.OnClickListener {

    private ClickCallback clickCallback;
    private ArrayList<MangaItem> items;
    private String source;

    void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    void swapData(ArrayList<MangaItem> items, String source){
        this.items = items;
        this.source = source;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        MangaItem mangaItem = items.get((Integer) view.getTag());
        clickCallback.onMangaItemClick(source, mangaItem);
    }

    @Override
    public MangaItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MangaItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_manga_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MangaItemViewHolder holder, int position) {
        holder.bind(items.get(position));
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class MangaItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mangaTitle;
        MangaItemViewHolder(View view) {
            super(view);
            mangaTitle = (TextView) view.findViewById(R.id.tv_MangaTitle);
        }

        void bind(MangaItem mangaItem){
            mangaTitle.setText(mangaItem.getName());
        }

    }
}
