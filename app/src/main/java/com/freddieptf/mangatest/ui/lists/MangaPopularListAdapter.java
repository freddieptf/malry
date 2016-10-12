package com.freddieptf.mangatest.ui.lists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.PopularMangaItem;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by fred on 9/11/15.
 */
class MangaPopularListAdapter extends RecyclerView.Adapter<MangaPopularListAdapter.PopularMangaItemViewHolder> implements View.OnClickListener {

    private ClickCallback clickCallback;
    private ArrayList<PopularMangaItem> items;

    public void swapData(ArrayList<PopularMangaItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(final View view) {
        clickCallback.onPopularMangaItemClick(items.get((Integer) view.getTag()));
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    @Override
    public PopularMangaItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PopularMangaItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_popular_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PopularMangaItemViewHolder holder, int position) {
        holder.bind(items.get(position));
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class PopularMangaItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, position, genre, chapters, author;

        PopularMangaItemViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            author = (TextView) view.findViewById(R.id.author);
            position = (TextView) view.findViewById(R.id.position);
            genre = (TextView) view.findViewById(R.id.genre);
            chapters = (TextView) view.findViewById(R.id.chapters);
        }

        public void bind(PopularMangaItem item) {
            name.setText(item.getName());
            author.setText(item.getAuthor());
            position.setText(item.getRank() + "");
            chapters.setText(item.getDetails());
            genre.setText(Arrays.toString(item.getGenre()));
        }
    }
}
