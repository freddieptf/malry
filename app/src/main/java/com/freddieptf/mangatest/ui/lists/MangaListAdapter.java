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

    private OnMangaClicked onMangaClick;
    private ArrayList<MangaItem> items;
    private String source;

    void setOnMangaClickListener(OnMangaClicked onMangaClick){
        this.onMangaClick = onMangaClick;
    }

    void swapData(ArrayList<MangaItem> items, String source){
        this.items = items;
        this.source = source;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        MangaItem mangaItem = items.get((Integer) view.getTag());
        onMangaClick.onMangaClicked(source, mangaItem.getName(), mangaItem.getMangaId());
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

    interface OnMangaClicked{
        void onMangaClicked(String source, String name, String id);
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
