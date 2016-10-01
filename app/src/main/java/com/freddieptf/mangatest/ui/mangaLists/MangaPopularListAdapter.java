package com.freddieptf.mangatest.ui.mangaLists;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.ui.mangaLists.MangaListAdapter.OnMangaClicked;

/**
 * Created by fred on 9/11/15.
 */
public class MangaPopularListAdapter extends CursorAdapter implements View.OnClickListener {


    OnMangaClicked onMangaClicked;

    public MangaPopularListAdapter(Context context, Cursor c) {
        super(context, c, 0);
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
                if(c != null){
                    c.moveToFirst();
                    onMangaClicked.onMangaClicked(manga[0], manga[1], c.getString(0));
                    c.close();
                }
            }
        }).start();
    }

    public void setOnMangaClickedListener(OnMangaClicked onMangaClicked){
        this.onMangaClicked = onMangaClicked;
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
        view.setOnClickListener(this);
        viewHolder.position.setText((cursor.getPosition() + 1) + ".");
        viewHolder.name.setText(cursor.getString(ListsFragment.COLUMN_MANGA_NAME));
        viewHolder.author.setText(cursor.getString(ListsFragment.COLUMN_MANGA_AUTHOR));
        viewHolder.genre.setText(cursor.getString(ListsFragment.COLUMN_MANGA_GENRE));
        viewHolder.chapters.setText(cursor.getString(ListsFragment.COLUMN_CHAPTER_DETAILS));

        String[] manga = {context.getString(R.string.pref_manga_reader),
                cursor.getString(ListsFragment.COLUMN_MANGA_NAME)
        };

        viewHolder.name.setTag(manga);
    }

    class ViewHolder {
        TextView name, position, genre, chapters, author;
        public ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.name);
            author = (TextView) view.findViewById(R.id.author);
            position = (TextView) view.findViewById(R.id.position);
            genre = (TextView) view.findViewById(R.id.genre);
            chapters = (TextView) view.findViewById(R.id.chapters);
        }
    }
}
