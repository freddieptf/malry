package com.freddieptf.mangatest.ui.lists;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.manga.lists.MangaListLoader;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;
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

        popularListAdapter = new MangaPopularListAdapter();
        popularListAdapter.setClickCallback(this);
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
                list.setAdapter(popularListAdapter);
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
        popularListAdapter.swapData(mangaLists.getPopularMangaItems());
    }

    @Override
    public void showProgress(boolean show) {
        if(show) showProgressBar();
        else hideProgressBar();
    }

    @Override
    public void onMangaItemClick(String source, MangaItem item) {
        startActivity(createDetailActivtyIntent(item.getName(), item.getMangaId(), source));
    }

    @Override
    public void onLatestMangaItemClick(LatestMangaItem item) {
        startActivity(createDetailActivtyIntent(item.getMangaTitle(), item.getMangaId(),
                getString(R.string.pref_manga_reader)));
    }

    @Override
    public void onPopularMangaItemClick(PopularMangaItem item) {
        startActivity(createDetailActivtyIntent(item.getName(), item.getMangaId(),
                getString(R.string.pref_manga_reader)));
    }

    private Intent createDetailActivtyIntent(String name, String id, String source) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.TITLE_KEY, name);
        intent.putExtra(DetailsActivity.ID_KEY, id);
        intent.putExtra(DetailsActivity.SOURCE_KEY, source);
        return intent;
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
