package com.freddieptf.mangatest.mainUi.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.ListsPagerAdapter;
import com.freddieptf.mangatest.adapters.MangaLatestListAdapter;
import com.freddieptf.mangatest.adapters.MangaListAdapter;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.baseUi.BaseFragment;
import com.freddieptf.mangatest.utils.Utilities;

/**
 * Created by fred on 8/25/15.
 */
public class PagerFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ListsPagerAdapter.PagerHelper {

    final String LOG_TAG = getClass().getSimpleName();
    private static final int MANGA_LOADER = 0;

    private final Uri[] contentUris = {
            Contract.MangaFoxMangaList.CONTENT_URI,
            Contract.MangaReaderMangaList.CONTENT_URI,
            Contract.MangaReaderLatestList.CONTENT_URI
    };

    Uri activeUri = contentUris[0];


    private static String[] LIST_COLUMNS = {
            Contract.MangaReaderMangaList._ID,
            Contract.MangaReaderMangaList.COLUMN_MANGA_NAME,
            Contract.MangaReaderMangaList.COLUMN_MANGA_ID
    };

    private static String[] LATEST_COLUMNS = {
            Contract.MangaReaderLatestList._ID,
            Contract.MangaReaderLatestList.COLUMN_MANGA_NAME,
            Contract.MangaReaderLatestList.COLUMN_MANGA_ID,
            Contract.MangaReaderLatestList.COLUMN_CHAPTER,
            Contract.MangaReaderLatestList.COLUMN_DATE
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_MANGA_NAME = 1;
    public static final int COLUMN_MANGA_ID = 2;
    public static final int COLUMN_CHAPTER = 3;
    public static final int COLUMN_DATE = 4;

    MangaListAdapter mangaListAdapter;
    MangaLatestListAdapter latestListAdapter;

    @Override
    protected boolean showTabs() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new ListsPagerAdapter(this));
        TabLayout tabLayout = getMainActivityHelper().getTabs();
        tabLayout.setupWithViewPager(viewPager);

        mangaListAdapter = new MangaListAdapter(getActivity(), null, 0);

        Cursor c = getActivity().getContentResolver().query(Contract.MangaReaderLatestList.CONTENT_URI,
                LATEST_COLUMNS, null, null, null);

        latestListAdapter = new MangaLatestListAdapter(getActivity(), c, 0);



    }

    @Override
    public void getListView(ListView listView) {
        int pos = (Integer) listView.getTag();
        switch (pos){
            case 0:
                changeActiveUri(pos);
                listView.setAdapter(mangaListAdapter);
                break;

            case 1:
                listView.setAdapter(latestListAdapter);
                break;
            default:
                Utilities.Log(LOG_TAG, "NOPE! No listView");
        }
    }

    void changeActiveUri(int pos){
        activeUri = contentUris[pos];
        getActivity().getSupportLoaderManager().restartLoader(MANGA_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.myMangaList));

        getActivity().getSupportLoaderManager().initLoader(MANGA_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                activeUri,
                LIST_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mangaListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
