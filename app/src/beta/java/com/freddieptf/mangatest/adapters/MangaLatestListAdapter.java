package com.freddieptf.mangatest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.MangaListAdapter.OnMangaClicked;
import com.freddieptf.mangatest.mainUi.fragments.PagerFragment;

/**
 * Created by fred on 8/26/15.
 */
public class MangaLatestListAdapter extends CursorAdapter implements View.OnClickListener {


    public MangaLatestListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    class ViewHolder{
        TextView name;
        TextView chapter;
        TextView date;

        public ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.name);
            chapter = (TextView) view.findViewById(R.id.chapter);
            date = (TextView) view.findViewById(R.id.date);
        }
    }

    OnMangaClicked onMangaClick;

    public void setOnMangaClickListener(OnMangaClicked onMangaClick){
        this.onMangaClick = onMangaClick;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_latest_manga_item, viewGroup, false);
        ViewHolder v = new ViewHolder(view);
        view.setTag(v);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        view.setOnClickListener(this);
        String[] manga = {cursor.getString(PagerFragment.COLUMN_MANGA_NAME),
                cursor.getString(PagerFragment.COLUMN_MANGA_ID)};
        holder.name.setText(manga[0]);
        holder.chapter.setText("Ch:" + cursor.getString(PagerFragment.COLUMN_CHAPTER));
        holder.date.setText(cursor.getString(PagerFragment.COLUMN_DATE));

        holder.name.setTag(manga);
    }

    @Override
    public void onClick(View view) {
        String[] manga = (String[])view.findViewById(R.id.name).getTag();
        onMangaClick.onMangaClicked(manga[0], manga[1]);
    }
}
