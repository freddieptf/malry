package com.freddieptf.mangatest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.mainUi.fragments.MyMangaFragment;
import com.freddieptf.mangatest.utils.Utilities;
import com.freddieptf.mangatest.volleyStuff.FadeInNetworkImageView;
import com.freddieptf.mangatest.volleyStuff.LruBitmapCache;
import com.freddieptf.mangatest.volleyStuff.VolleySingletonClass;

/**
 * Created by fred on 2/15/15.
 */
public class MyMangaListAdapter extends CursorAdapter implements View.OnClickListener, View.OnLongClickListener {

    final String LOG_TAG = getClass().getSimpleName();
    Context context;
    ImageLoader imageLoader;
    RequestQueue requestQueue;
    private ClickListener myClickListener;


    public MyMangaListAdapter(Context context, int flags, ClickListener clickListener) {
        super(context, null, flags);
        this.context = context;
        myClickListener = clickListener;
        requestQueue = VolleySingletonClass.getInstance(context).getRequestQueue();
        imageLoader = new ImageLoader(requestQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(context)));

    }

    @Override
    public void onClick(View view) {
        myClickListener.onClick((Integer)view.getTag());
    }

    @Override
    public boolean onLongClick(View view) {
        myClickListener.onLongClick((Integer)view.getTag());
        return true;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view  = LayoutInflater.from(context).inflate(R.layout.list_my_manga_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder)view.getTag();

        viewHolder.card.setTag(cursor.getPosition());

        viewHolder.card.setOnClickListener(this);
        viewHolder.card.setOnLongClickListener(this);
        viewHolder.updateNum.setVisibility(View.GONE);

        String mangaName = cursor.getString(MyMangaFragment.COLUMN_MANGA_NAME);
        String mangaAuthor = cursor.getString(MyMangaFragment.COLUMN_MANGA_AUTHOR);
        String mangaStatus = cursor.getString(MyMangaFragment.COLUMN_MANGA_STATUS);
        String mangaInfo = cursor.getString(MyMangaFragment.COLUMN_MANGA_INFO);
//        String lastUpdated = cursor.getString(MyMangaFragment.COLUMN_MANGA_LAST_UPDATE);

//        String date = Utilities.formatDate(lastUpdated);
//        Utilities.Log(LOG_TAG, date.isEmpty() ? "No date" : date);


        int updateMargin = Utilities.readMangaPageFromPrefs(context, cursor.getString(MyMangaFragment.COLUMN_MANGA_ID));
        if (updateMargin != 0) {
            viewHolder.updateNum.setVisibility(View.VISIBLE);
            viewHolder.updateNum.setText("" + updateMargin);
        }

        viewHolder.mangaName.setText(mangaName);
        viewHolder.mangaAuthor.setText(mangaAuthor);
        viewHolder.mangaStatus.setText(mangaStatus);
        viewHolder.mangaInfo.setText(Html.fromHtml(mangaInfo));
//        viewHolder.lastUpdated.setText(lastUpdated);


        if(!Utilities.compactCards(context)){
            viewHolder.imageViewLayout.setVisibility(View.VISIBLE);
            viewHolder.infoRow.setMinimumHeight(context.getResources().getDimensionPixelSize(R.dimen.myNavBarMargin));
            imageLoader.setBatchedResponseDelay(200);
            viewHolder.imageView.setImageUrl(cursor.getString(MyMangaFragment.COLUMN_MANGA_COVER), imageLoader);

        }else{
            viewHolder.imageViewLayout.setVisibility(View.GONE);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setMinimumHeight(context.getResources().getDimensionPixelSize(R.dimen.noDp));
            viewHolder.infoRow.setMinimumHeight(context.getResources().getDimensionPixelSize(R.dimen.noDp));
        }


    }

    public interface ClickListener {
        void onClick(int index);
        void onLongClick(int index);
    }

    public static class ViewHolder {
        CardView card;
        TextView mangaName;
        TextView mangaAuthor;
        TextView mangaStatus;
        TextView mangaInfo;
        TextView updateNum;
        TextView lastUpdated;
        FadeInNetworkImageView imageView;
        RelativeLayout imageViewLayout;
        TableRow infoRow;

        public ViewHolder(View view) {
            card = (CardView) view.findViewById(R.id.card);
            mangaName = (TextView) view.findViewById(R.id.tv_MyManga_name);
            mangaAuthor = (TextView) view.findViewById(R.id.tv_MyManga_author);
            mangaStatus = (TextView) view.findViewById(R.id.tv_MyManga_status);
            mangaInfo = (TextView) view.findViewById(R.id.tv_MyManga_info);
            updateNum = (TextView) view.findViewById(R.id.tv_mangaUpdates);
            imageView = (FadeInNetworkImageView) view.findViewById(R.id.iv_myMangaImageView);
//            lastUpdated = (TextView)view.findViewById(R.id.tv_MyManga_lastUpdate);
            imageViewLayout = (RelativeLayout) view.findViewById(R.id.iv_myMangaImageView_Layout);
            infoRow = (TableRow) view.findViewById(R.id.tr_MyManga_InfoRow);
        }
    }




}
