package com.freddieptf.mangatest.mainUi.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.ListsPagerAdapter;
import com.freddieptf.mangatest.adapters.MangaLatestListAdapter;
import com.freddieptf.mangatest.adapters.MangaListAdapter;
import com.freddieptf.mangatest.adapters.MangaPopularListAdapter;
import com.freddieptf.mangatest.api.workers.WorkerThread;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.mainUi.baseUi.BaseFragment;
import com.freddieptf.mangatest.mainUi.widgets.Cab;
import com.freddieptf.mangatest.mainUi.widgets.genreview.GenresView;
import com.freddieptf.mangatest.mainUi.widgets.genreview.OnGenreChangedListener;
import com.freddieptf.mangatest.service.DownloadMangaDatabase;
import com.freddieptf.mangatest.utils.Utilities;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by fred on 1/30/15.
 */
public class ListsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ListsPagerAdapter.PagerHelper, MangaListAdapter.OnMangaClicked, OnGenreChangedListener {

    private static final int MANGA_LOADER = 100331;
    private static final int LATEST_MANGA_LOADER = 100332;
    private static final int POPULAR_MANGA_LOADER = 100333;

    MangaListAdapter mangaListAdapter;
    MangaLatestListAdapter latestListAdapter;
    MangaPopularListAdapter popularListAdapter;
    String source = "";
    private final String LOG_TAG = getClass().getSimpleName();
    public static final String SORT_ORDER = "sort_order";
    String genres = "";
    Cab cab;

    Uri PREF_CONTENT_URI;

    SmoothProgressBar progressBar;
    GenresView genresView;
    Cursor searchCursor = null;
    SearchManager searchManager;
    SearchView searchView;

    private static final String[] MANGA_COLUMNS = {
            Contract.MangaReaderMangaList._ID,
            Contract.MangaReaderMangaList.COLUMN_MANGA_NAME,
            Contract.MangaReaderMangaList.COLUMN_MANGA_ID

    };

    private static final String[] POPULAR_COLUMNS = {
            Contract.MangaReaderPopularList._ID,
            Contract.MangaReaderPopularList.COLUMN_MANGA_NAME,
            Contract.MangaReaderPopularList.COLUMN_MANGA_AUTHOR,
            Contract.MangaReaderPopularList.COLUMN_CHAPTER_DETAILS,
            Contract.MangaReaderPopularList.COLUMN_MANGA_GENRE
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
    public static final int COLUMN_MANGA_AUTHOR = 2;
    public static final int COLUMN_CHAPTER_DETAILS = 3;
    public static final int COLUMN_MANGA_GENRE = 4;



    public ListsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    protected boolean showTabs() {
        return true;
    }

    @Override
    protected boolean lockDrawer() {
        return cab.isVisible();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LATEST_MANGA_LOADER, null, latestListLoader);
        getLoaderManager().initLoader(POPULAR_MANGA_LOADER, null, popularListLoader);
        getLoaderManager().initLoader(MANGA_LOADER, null, this);

        searchManager = (SearchManager)getActivity().getSystemService(MainActivity.SEARCH_SERVICE);

        if(Utilities.isFirstStart(getActivity())) showWelcomeDialog();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.myMangaList));

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PREF_CONTENT_URI = Utilities.getPrefContentUri(getActivity());

        progressBar = (SmoothProgressBar) view.findViewById(R.id.progress);
        genresView = (GenresView) view.findViewById(R.id.genre_view);
        cab = getMainActivityHelper().getCab();
        cab.setOnCloseListener(onActionClose);

        genresView.setOnGenreChangedListener(this);
        genres = genresView.getSelectedGenres();

        if(!genres.isEmpty()) restartPopularLoader(genres);

        if(savedInstanceState != null){
            genresView.restoreState(savedInstanceState);
            cab.restoreState(savedInstanceState);
        }

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new ListsPagerAdapter(this, getActivity()));
        TabLayout tabLayout = getMainActivityHelper().getTabs();
        tabLayout.setupWithViewPager(viewPager);


        mangaListAdapter = new MangaListAdapter(getActivity(), null);
        mangaListAdapter.setOnMangaClickListener(this);

        latestListAdapter = new MangaLatestListAdapter(getActivity(), null);
        latestListAdapter.setOnMangaClickListener(this);

        popularListAdapter = new MangaPopularListAdapter(getActivity(), null);
        popularListAdapter.setOnMangaClickedListener(this);
    }

    private void restartPopularLoader(String genres) {
        Bundle bundle = new Bundle();
        bundle.putString("uri", Contract.MangaReaderPopularList.buildListWithGenreUri(genres).toString());
        getLoaderManager().restartLoader(POPULAR_MANGA_LOADER, bundle, popularListLoader);
    }


    @Override
    public void getListView(ListView listView) {
        int pos = (Integer) listView.getTag();
        switch (pos){
            case 0:
                listView.setAdapter(latestListAdapter);
                break;
            case 1:
                listView.setAdapter(popularListAdapter);
                break;
            case 2:
                listView.setAdapter(mangaListAdapter);
                break;
            default:
                Utilities.Log(LOG_TAG, "NOPE! No listView");
        }
    }

    @Override
    public void onMangaClicked(String source, String name, String id) {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }

        Bundle bundle = new Bundle();
        bundle.putString(MangaDetailsFragment.TITLE_KEY, name);
        bundle.putString(MangaDetailsFragment.ID_KEY, id);
        bundle.putString(MangaDetailsFragment.SOURCE_KEY, source);
        Fragment fragment = new MangaDetailsFragment();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "details")
                .addToBackStack(MangaDetailsFragment.DIS_FRAGMENT)
                .commit();
    }

    @Override
    public void onGenreChange(String genres) {
        this.genres = genres;
        restartPopularLoader(genres);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!Utilities.getCurrentSource(getActivity()).equals(source)) refreshLoaderOnPrefChange();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(searchCursor != null)searchCursor.close();
        source = Utilities.getCurrentSource(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        genresView.saveState(outState);
        if(cab != null) cab.saveState(outState);
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
        menu.findItem(R.id.menu_filter).setVisible(true);

        searchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();

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
                    MangaListAdapter adapter = new MangaListAdapter(getActivity(), searchCursor);
                    adapter.setOnMangaClickListener(ListsFragment.this);
                    searchView.setSuggestionsAdapter(adapter);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Uri uri = Contract.VirtualTable.buildVirtualMangaUriWithQuery(s);
                    searchCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    Utilities.Log(LOG_TAG, "onQueryTextChange searchCursor = " + searchCursor.getCount());
                    MangaListAdapter adapter = new MangaListAdapter(getActivity(), searchCursor);
                    adapter.setOnMangaClickListener(ListsFragment.this);
                    searchView.setSuggestionsAdapter(adapter);
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
            case R.id.menu_filter: {
                startMyActionMode();
                break;
            }
        }

        return true;
    }

    private void refreshLoaderOnPrefChange() {
        PREF_CONTENT_URI = Utilities.getPrefContentUri(getActivity());
        getLoaderManager().restartLoader(MANGA_LOADER, null, this);
    }

    private void startMyActionMode() {
        cab.startCabMode(true);
        getMainActivityHelper().getToolBar().setVisibility(View.GONE);
        cab.setTitle(getString(R.string.pick_genre));
        genresView.show();
        getMainActivityHelper().lockDrawer(true);
    }

    private void stopMyActionMode(){
        getMainActivityHelper().getToolBar().setVisibility(View.VISIBLE);
        cab.startCabMode(false);
        genresView.hide();
        getMainActivityHelper().lockDrawer(false);
    }

    private Cab.OnActionClose onActionClose = new Cab.OnActionClose() {
        @Override
        public void onClose() {
            stopMyActionMode();
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
        mangaListAdapter.swapCursor(data);
        Utilities.Log(LOG_TAG, "onLoadFinshed: " + data.getCount() + " items");

        if(data.getCount() > 0){
            hideProgressBar();
        }


    }

    private void hideProgressBar() {
        progressBar.progressiveStop();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private LoaderManager.LoaderCallbacks<Cursor> latestListLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    Contract.MangaReaderLatestList.CONTENT_URI,
                    LATEST_COLUMNS,
                    null, null, null);

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            latestListAdapter.swapCursor(data);
            Utilities.Log(LOG_TAG, "onLoadFinshed: " + data.getCount() + " items");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> popularListLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(args != null && args.containsKey("uri"))
                return new CursorLoader(getActivity(),
                    Uri.parse(args.getString("uri")),
                    POPULAR_COLUMNS,
                    null, null, null);

            return new CursorLoader(getActivity(),
                    Contract.MangaReaderPopularList.CONTENT_URI,
                    POPULAR_COLUMNS,
                    null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            popularListAdapter.swapCursor(data);
            if(cab != null) cab.setExtraText("" + data.getCount());
            Utilities.Log(LOG_TAG, "onLoadFinshed: " + data.getCount() + " items");
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void showWelcomeDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Welcome Otaku-san!")
                .cancelable(false)
                .positiveText("start")
                .negativeText("later")
                .content("Download and set up manga databases? If it doesn't set up right, " +
                        "head over to Settings.")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        dialog.dismiss();
                        showProgressBar();
                        Intent intent = new Intent(getActivity(), DownloadMangaDatabase.class);
                        intent.putExtra(DownloadMangaDatabase.FIX_SELECTION, WorkerThread.ALL_LIST);
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
