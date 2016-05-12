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
import com.freddieptf.mangatest.utils.Utilities;

/**
 * Created by fred on 2/8/15.
 */
public class MangaListAdapter extends CursorAdapter implements View.OnClickListener {

    OnMangaClicked onMangaClick;

    public MangaListAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public void setOnMangaClickListener(OnMangaClicked onMangaClick){
        this.onMangaClick = onMangaClick;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_manga_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        view.setOnClickListener(this);
        String[] manga = {Utilities.getCurrentSource(context),
                cursor.getString(ListsFragment.COLUMN_MANGA_NAME),
                cursor.getString(ListsFragment.COLUMN_MANGA_ID)};
        viewHolder.mangaTitle.setText(manga[1]);
        viewHolder.mangaTitle.setTag(manga);
    }

    @Override
    public void onClick(View view) {
        String[] manga = (String[])view.findViewById(R.id.tv_MangaTitle).getTag();
        onMangaClick.onMangaClicked(manga[0], manga[1], manga[2]);
    }

    public interface OnMangaClicked{
        void onMangaClicked(String source, String name, String id);
    }

    public static class ViewHolder {
        TextView mangaTitle;

        public ViewHolder(View view) {
            mangaTitle = (TextView) view.findViewById(R.id.tv_MangaTitle);
        }
    }
}
