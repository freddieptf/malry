package com.freddieptf.mangatest.mainUi.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.MyMangaListAdapter;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.baseUi.BaseFragment;
import com.freddieptf.mangatest.sync.MangaTestSyncAdapter;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.Arrays;

/**
 * Created by fred on 2/14/15.
 */
public class MyMangaFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final String[] columns = {
            Contract.MyManga._ID,
            Contract.MyManga.COLUMN_MANGA_NAME,
            Contract.MyManga.COLUMN_MANGA_AUTHOR,
            Contract.MyManga.COLUMN_MANGA_STATUS,
            Contract.MyManga.COLUMN_MANGA_INFO,
            Contract.MyManga.COLUMN_MANGA_COVER,
            Contract.MyManga.COLUMN_MANGA_ID,
            Contract.MyManga.COLUMN_MANGA_SOURCE,
            Contract.MyManga.COLUMN_MANGA_LAST_UPDATE
    };
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_MANGA_NAME = 1;
    public static final int COLUMN_MANGA_AUTHOR = 2;
    public static final int COLUMN_MANGA_STATUS = 3;
    public static final int COLUMN_MANGA_INFO = 4;
    public static final int COLUMN_MANGA_COVER = 5;
    public static final int COLUMN_MANGA_ID = 6;
    public static final int COLUMN_MANGA_SOURCE = 7;
    public static final int COLUMN_MANGA_LAST_UPDATE = 8;
    public final int MY_MANGA_LOADER = 0;
    MyMangaListAdapter adapter;
    MaterialDialog dialog;
    ListView listView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] s = intent.getStringArrayExtra(MangaTestSyncAdapter.UPDATE_LIST_EXTRA);
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
            builder.title(R.string.string_check4updates);

            if (s != null && s.length > 0) {
                String static_content = "Found " + s.length + " manga updates.";
                String c = Arrays.toString(s);
                builder.content(static_content + "\n" + c);
                adapter.notifyDataSetChanged();
            } else builder.content("No updates yet mate.");

            if (dialog != null) dialog.dismiss();
            dialog = builder.build();
            dialog.show();
        }
    };

    public MyMangaFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getMainActivityHelper().hideBottomBar(false);
        getMainActivityHelper().getToolBar().setNavigationIcon(R.drawable.ic_menu_white_24dp);
        return inflater.inflate(R.layout.fragment_my_manga, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.list);
        listView.setEmptyView(view.findViewById(R.id.emptyView));

        adapter = new MyMangaListAdapter(getActivity(), 0, new MyMangaListAdapter.ClickListener() {
            @Override
            public void onClick(int index) {
                Cursor cursor = adapter.getCursor();
                if (cursor.moveToFirst()) {

                    cursor.moveToPosition(index);

                    Bundle bundle = new Bundle();
                    bundle.putString(MangaDetailsFragment.TITLE_KEY, cursor.getString(MyMangaFragment.COLUMN_MANGA_NAME));
                    bundle.putString(MangaDetailsFragment.ID_KEY, cursor.getString(MyMangaFragment.COLUMN_MANGA_ID));
                    Fragment fragment = new MangaDetailsFragment();
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment, "details")
                            .addToBackStack(MangaDetailsFragment.DIS_FRAGMENT)
                            .commit();

                }
            }

            @Override
            public void onLongClick(int index) {
                Cursor cursor = adapter.getCursor();
                if (cursor.moveToFirst()) {

                    cursor.moveToPosition(index);

                    String mangaName = cursor.getString(MyMangaFragment.COLUMN_MANGA_NAME);
                    final Uri mangaUri = Contract.MyManga.buildMangaWithNameUri(mangaName);


                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(mangaName)
                            .content("Delete this manga from my library?")
                            .positiveText("Yes")
                            .negativeText("No")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    int rowsDeleted = getActivity().getContentResolver().delete(mangaUri, null, null);
                                    if (rowsDeleted > 0) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    dialog.dismiss();
                                }
                            }).build();

                    dialog.show();
                }
            }

        });

        listView.setAdapter(adapter);
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.isOnline(getActivity())) {
                    MangaTestSyncAdapter.syncImmediately(getActivity());
                    dialog = builder.title(R.string.string_check4updates)
                            .content("Wait a sec while i check for updates")
                            .progress(true, 0)
                            .autoDismiss(false)
                            .build();
                    dialog.show();
                }else {
                    Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_mangalib, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_prefCompactCards);
        if (Utilities.compactCards(getActivity())) item.setChecked(true);
        else item.setChecked(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_prefCompactCards:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                boolean state = sharedPreferences.getBoolean(getResources().getString(R.string.pref_my_manga_cards_key), true);
                sharedPreferences.edit().putBoolean(getResources().getString(R.string.pref_my_manga_cards_key), !state).apply();
                item.setChecked(!state);
                adapter.notifyDataSetChanged();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    public void restart(){
        getLoaderManager().restartLoader(MY_MANGA_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utilities.compactCards(getActivity()) || Utilities.compactCards(getActivity())) {
            adapter.notifyDataSetChanged();
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(MangaTestSyncAdapter.INTENT_FILTER));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MY_MANGA_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                Contract.MyManga.CONTENT_URI,
                columns,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        listView.setAlpha(0f);
        listView.animate().alpha(1f).setDuration(250);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
