package com.freddieptf.mangatest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.mainUi.fragments.ListsFragment;

/**
 * Created by fred on 9/11/15.
 */
public class MangaPopularListAdapter extends CursorAdapter {


    public MangaPopularListAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View itemsView = LayoutInflater.from(context).inflate(R.layout.list_popular_item, parent, false);
        ViewHolder v = new ViewHolder(itemsView);
        itemsView.setTag(v);
        return itemsView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.position.setText("" +cursor.getPosition());
        viewHolder.name.setText(cursor.getString(ListsFragment.COLUMN_MANGA_NAME));
        viewHolder.genre.setText(cursor.getString(ListsFragment.COLUMN_MANGA_GENRE));
        viewHolder.chapters.setText(cursor.getString(ListsFragment.COLUMN_CHAPTER_DETAILS));
    }

    class ViewHolder {
        TextView name, position, genre, chapters;
        public ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.name);
            position = (TextView) view.findViewById(R.id.position);
            genre = (TextView) view.findViewById(R.id.genre);
            chapters = (TextView) view.findViewById(R.id.chapters);
        }
    }
}
