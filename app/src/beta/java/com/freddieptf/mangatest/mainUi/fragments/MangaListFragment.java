package com.freddieptf.mangatest.mainUi.fragments;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.MangaListAdapter;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.mainUi.baseUi.BaseFragment;
import com.freddieptf.mangatest.service.DownloadMangaDatabase;
import com.freddieptf.mangatest.utils.Utilities;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by fred on 1/30/15.
 */
public class MangaListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MANGA_LOADER = 0;
    MangaListAdapter adapter;
    String source = "";
    private final String LOG_TAG = getClass().getSimpleName();
    public static final String SORT_ORDER = "sort_order";

    Uri PREF_CONTENT_URI;
    boolean search = false;
    String searchQuery = null;
    MaterialDialog materialDialog;

    SmoothProgressBar progressBar;
    ListView listView;
    Cursor searchCursor = null;
    SearchManager searchManager;

    private static final String[] MANGA_COLUMNS = {
            Contract.MangaReaderMangaList._ID,
            Contract.MangaReaderMangaList.COLUMN_MANGA_NAME,
            Contract.MangaReaderMangaList.COLUMN_MANGA_ID

    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_MANGA_NAME = 1;
    public static final int COLUMN_MANGA_ID = 2;



    public MangaListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    protected boolean showTabs() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(MANGA_LOADER, null, this);
        searchManager = (SearchManager)getActivity().getSystemService(MainActivity.SEARCH_SERVICE);

        if(Utilities.isFirstStart(getActivity())){
            showWelcomeDialog();
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.myMangaList));

        if (getArguments() != null) {
            search = true;
            searchQuery = getArguments().getString("Search");
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PREF_CONTENT_URI = Utilities.getPrefContentUri(getActivity());

        progressBar = (SmoothProgressBar) view.findViewById(R.id.progress);
        listView = (ListView) view.findViewById(R.id.lv_MangaList);
        adapter = new MangaListAdapter(getActivity(), null, 0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new listViewOnItemClickListener());

    }


    private class listViewOnItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Cursor cursor = adapter.getCursor();
            if (cursor != null && cursor.moveToPosition(i)) {
                Bundle bundle = new Bundle();
                bundle.putString(MangaDetailsFragment.TITLE_KEY, cursor.getString(COLUMN_MANGA_NAME));
                bundle.putString(MangaDetailsFragment.ID_KEY, cursor.getString(COLUMN_MANGA_ID));
                Fragment fragment = new MangaDetailsFragment();
                fragment.setArguments(bundle);
//                        getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, "details")
                        .addToBackStack(MangaDetailsFragment.DIS_FRAGMENT)
                        .commit();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!Utilities.getCurrentSource(getActivity()).equals(source)) refreshLoaderOnPrefChange();

        getActivity().registerReceiver(broadCastReceiver,
                new IntentFilter(DownloadMangaDatabase.class.getSimpleName()));
    }

    @Override
    public void onPause() {
        super.onPause();
        if(searchCursor != null)searchCursor.close();
        source = Utilities.getCurrentSource(getActivity());
        getActivity().unregisterReceiver(broadCastReceiver);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(Utilities.getCurrentSource(getActivity()).equals(getString(R.string.pref_manga_reader)))
            menu.findItem(R.id.menu_sourceReader).setChecked(true);
        else
            menu.findItem(R.id.menu_sourceFox).setChecked(true);

    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragments, menu);

        menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.menu_sourceReader).setVisible(true);
        menu.findItem(R.id.menu_sourceFox).setVisible(true);

        final SearchView searchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();

        if(searchView == null) Utilities.Log(LOG_TAG, "SearchView: Null");

        if(searchView != null) {
            searchView.setQueryHint("manga name");

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {

                    Uri uri = Contract.VirtualTable.buildVirtualMangaUriWithQuery(s);
                    searchCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    Utilities.Log(LOG_TAG, "onQueryTextChange searchCursor = " + searchCursor.getCount());
                    searchView.setSuggestionsAdapter(new MangaListAdapter(getActivity(), searchCursor, 0));
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Uri uri = Contract.VirtualTable.buildVirtualMangaUriWithQuery(s);
                    searchCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    Utilities.Log(LOG_TAG, "onQueryTextChange searchCursor = " + searchCursor.getCount());
                    searchView.setSuggestionsAdapter(new MangaListAdapter(getActivity(), searchCursor, 0));
                    return true;
                }

            });

            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    Utilities.Log(LOG_TAG, "onSuggestionSelect YES: " + searchCursor.getCount());
                    if (searchCursor != null) {
                        searchCursor.moveToPosition(position);
                    }
                    return true;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    if (searchCursor != null) {
                        searchCursor.moveToPosition(position);
                        Utilities.Log(LOG_TAG, "SuggestionSelect searchCursor = " + searchCursor.getString(1)
                                + " \n" + searchCursor.getString(2));

                        if (getActivity().getCurrentFocus() != null) {
                            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            im.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString(MangaDetailsFragment.TITLE_KEY, searchCursor.getString(1));
                        bundle.putString(MangaDetailsFragment.ID_KEY, searchCursor.getString(2));
                        Fragment fragment = new MangaDetailsFragment();
                        fragment.setArguments(bundle);
//                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, fragment, "details")
                                .addToBackStack(MangaDetailsFragment.DIS_FRAGMENT)
                                .commit();

                    }
                    return true;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    if(searchCursor != null) searchCursor.close();
                    return false;
                }
            });

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_sourceFox:
            case R.id.menu_sourceReader: {
                Utilities.setCurrentSource(getActivity(), item.getTitle().toString());
                refreshLoaderOnPrefChange();
                break;
            }

        }

        return true;
    }

    private void refreshLoaderOnPrefChange() {
        PREF_CONTENT_URI = Utilities.getPrefContentUri(getActivity());
        getLoaderManager().restartLoader(MANGA_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Utilities.Log(LOG_TAG, " onCreateLoader");
        String sortOrder = null;
        try {
            sortOrder = getArguments().getString(SORT_ORDER);
        }catch (NullPointerException e){}

        return new CursorLoader(getActivity(),
                PREF_CONTENT_URI,
                MANGA_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

        if(data.getCount() != 0){
            hideProgressBar();

            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if(actionBar != null) actionBar.setSubtitle(Utilities.getCurrentSource(getActivity())
            + ": " + adapter.getCount() + " manga");

            listView.setAlpha(0f);
            listView.setVisibility(View.VISIBLE);
            listView.animate().alpha(1f).setDuration(300);
        }

        Utilities.Log(LOG_TAG, " onLoadFinshed: " + data.getCount() + " items");



    }

    private void hideProgressBar() {
        progressBar.progressiveStop();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getExtras() != null) {
                Bundle bundle = intent.getExtras();
                String status = bundle.getString(DownloadMangaDatabase.STATUS);
                String operation = bundle.getString(DownloadMangaDatabase.OP);

                Utilities.Log(LOG_TAG, "onReceive op: " + operation + " status: " + status);

                if (materialDialog != null) materialDialog.dismiss();

                if(operation.equals("Done!")){
                    materialDialog = new MaterialDialog.Builder(getActivity())
                            .title(operation)
                            .content(status)
                            .build();
                }
                else{
                        materialDialog = new MaterialDialog.Builder(getActivity())
                                .title(operation)
                                .content(status)
                                .progress(true, 0)
                                .build();
                    }
                }

                materialDialog.show();

            }

    };



    private void showWelcomeDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Welcome Otaku-san!")
                .cancelable(false)
                .positiveText("start")
                .negativeText("later")
                .content("Arigatou for downloading this app. Now go look at some cat pics on the internet while the app"
                         + " sets up some huge manga databases. However, you can browse, download chapters or read whatever's" +
                        " currently downloaded while it's still setting up in the background. If it doesn't set up right, " +
                        "head over to Settings.")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        dialog.dismiss();
                        showProgressBar();
                        Intent intent = new Intent(getActivity(), DownloadMangaDatabase.class);
                        getActivity().startService(intent);
                        Utilities.setFirstStart(getActivity(), false);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        Utilities.setFirstStart(getActivity(), true);
                        getActivity().finish();
                        dialog.dismiss();
                    }
                }).build();

        dialog.show();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.progressiveStart();
    }


}
