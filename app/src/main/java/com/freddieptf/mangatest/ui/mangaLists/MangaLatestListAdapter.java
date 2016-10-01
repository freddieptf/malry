package com.freddieptf.mangatest.ui.mangaLists;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.ui.mangaLists.MangaListAdapter.OnMangaClicked;

/**
 * Created by fred on 8/26/15.
 */
public class MangaLatestListAdapter extends CursorAdapter implements View.OnClickListener {

    OnMangaClicked onMangaClick;

    public MangaLatestListAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

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
        String[] manga = {context.getString(R.string.pref_manga_reader),
                cursor.getString(ListsFragment.COLUMN_MANGA_NAME)
                };
        holder.name.setText(manga[1] + " Ch" + cursor.getString(ListsFragment.COLUMN_CHAPTER));
        holder.date.setText(cursor.getString(ListsFragment.COLUMN_DATE));

        holder.name.setTag(manga);
    }

    @Override
    public void onClick(final View view) {
        final String[] manga = (String[])view.findViewById(R.id.name).getTag();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri m = Contract.MangaReaderMangaList.buildMangaInListWithNameUri(manga[1]);
                Cursor c = view.getContext().getContentResolver().query(m,
                        new String[]{
                                Contract.MangaReaderMangaList.COLUMN_MANGA_ID}, null, null, null);
                if(c != null && c.moveToFirst()){
                    onMangaClick.onMangaClicked(manga[0], manga[1], c.getString(0));
                    c.close();
                }
            }
        }).start();
    }

    class ViewHolder {
        TextView name;
        TextView date;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.date);
        }
    }
}
