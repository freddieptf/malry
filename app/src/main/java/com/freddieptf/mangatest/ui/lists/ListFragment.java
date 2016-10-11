package com.freddieptf.mangatest.ui.lists;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.data.manga.lists.MangaListLoader;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.ui.MainActivity;
import com.freddieptf.mangatest.ui.base.BaseFragment;
import com.freddieptf.mangatest.ui.details.DetailsActivity;
import com.freddieptf.mangatest.utils.Utilities;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by fred on 1/30/15.
 */
public class ListFragment extends BaseFragment implements ListView, ListPagerAdapter.PagerHelper,
        ClickCallback {

    private final String LOG_TAG = getClass().getSimpleName();
    ViewPager viewPager;
    SmoothProgressBar progressBar;
    Cursor searchCursor = null;
    SearchManager searchManager;
    SearchView searchView;
    private MangaListAdapter mangaListAdapter;
    private MangaLatestListAdapter latestListAdapter;
    private MangaPopularListAdapter popularListAdapter;
    private ListPresenter presenter;

    public ListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    protected boolean showTabs() {
        return true;
    }

    @Override
    protected boolean hideBottomBar() {
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
        searchManager = (SearchManager)getActivity().getSystemService(MainActivity.SEARCH_SERVICE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (SmoothProgressBar) view.findViewById(R.id.progress);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new ListPagerAdapter(this, getActivity()));
        TabLayout tabLayout = getMainActivityHelper().getTabs();
        tabLayout.setupWithViewPager(viewPager);

        mangaListAdapter = new MangaListAdapter();
        mangaListAdapter.setClickCallback(this);

        latestListAdapter = new MangaLatestListAdapter();
        latestListAdapter.setClickCallback(this);

        popularListAdapter = new MangaPopularListAdapter(getActivity(), null);
//        popularListAdapter.setOnMangaClickedListener(this);
    }

    @Override
    public void getListView(RecyclerView list) {
        int pos = (Integer) list.getTag();
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        switch (pos) {
            case 0:
                list.setAdapter(latestListAdapter);
                break;
            case 1:
//                listView.setAdapter(popularListAdapter);
                break;
            case 2:
                list.setAdapter(mangaListAdapter);
                break;
            default:
                Utilities.Log(LOG_TAG, "NOPE! No list to be shown boiii");
        }
    }

    @Override
    public void setPresenter(ListPresenter listPresenter) {
        presenter = listPresenter;
    }

    @Override
    public void onDataLoad(MangaListLoader.MangaLists mangaLists) {
        Log.d(LOG_TAG, "data" + (mangaLists.getMangaItems() == null ? 0 : mangaLists.getMangaItems().size()));
        mangaListAdapter.swapData(mangaLists.getMangaItems(), Utilities.getCurrentSource(getContext()));
        latestListAdapter.swapData(mangaLists.getLatestMangaItems());
    }

    @Override
    public void showProgress(boolean show) {
        if(show) showProgressBar();
        else hideProgressBar();
    }

    @Override
    public void onMangaItemClick(String source, MangaItem item) {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.TITLE_KEY, item.getName());
        intent.putExtra(DetailsActivity.ID_KEY, item.getMangaId());
        intent.putExtra(DetailsActivity.SOURCE_KEY, source);
        startActivity(intent);
    }

    @Override
    public void onLatestMangaItemClick(LatestMangaItem item) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.TITLE_KEY, item.getMangaTitle());
        intent.putExtra(DetailsActivity.ID_KEY, item.getMangaId());
        intent.putExtra(DetailsActivity.SOURCE_KEY, getString(R.string.pref_manga_reader));
        startActivity(intent);
    }

    @Override
    public void onPopularMangaItemClick() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            if (presenter.getActiveSource().isEmpty()) {
                presenter.setActiveSource(Utilities.getCurrentSource(getContext()));
                presenter.init();
            } else {
                if (!Utilities.getCurrentSource(getActivity()).equals(presenter.getActiveSource()))
                    presenter.switchSource(Utilities.getCurrentSource(getContext()));
                else presenter.init();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchCursor != null) searchCursor.close();
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
        inflater.inflate(R.menu.menu_mangalist, menu);

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
//                    MangaListAdapter adapter = new MangaListAdapter(getActivity(), searchCursor);
//                    adapter.setClickCallback(ListsFragment.this);
//                    searchView.setSuggestionsAdapter(adapter);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Uri uri = Contract.VirtualTable.buildVirtualMangaUriWithQuery(s);
                    searchCursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    Utilities.Log(LOG_TAG, "onQueryTextChange searchCursor = " + searchCursor.getCount());
//                    MangaListAdapter adapter = new MangaListAdapter(getActivity(), searchCursor);
//                    adapter.setClickCallback(ListsFragment.this);
//                    searchView.setSuggestionsAdapter(adapter);
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
                presenter.switchSource(item.getTitle().toString());
                break;
            }
            case R.id.menu_filter: {
                //@TODO launch simple dialog
                break;
            }
        }

        return true;
    }

    private void hideProgressBar() {
        progressBar.progressiveStop();
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.progressiveStart();
    }


}
