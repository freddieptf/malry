package com.freddieptf.mangatest.ui.lists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.LatestMangaItem;

import java.util.ArrayList;

/**
 * Created by fred on 8/26/15.
 */
class MangaLatestListAdapter extends RecyclerView.Adapter<MangaLatestListAdapter.LatestMangaItemViewHolder> implements View.OnClickListener {

    private ClickCallback clickCallback;
    private ArrayList<LatestMangaItem> items;

    void swapData(ArrayList<LatestMangaItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @Override
    public void onClick(final View view) {
        clickCallback.onLatestMangaItemClick(items.get((Integer) view.getTag()));
    }

    @Override
    public LatestMangaItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LatestMangaItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_latest_manga_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(LatestMangaItemViewHolder holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class LatestMangaItemViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;

        LatestMangaItemViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
        }

        public void bind(LatestMangaItem item) {
            name.setText(item.getMangaTitle());
            date.setText(item.getReleaseDate());
        }
    }
}
