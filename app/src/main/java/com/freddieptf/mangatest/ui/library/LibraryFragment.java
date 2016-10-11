package com.freddieptf.mangatest.ui.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.sync.MangaLibrarySync;
import com.freddieptf.mangatest.ui.base.BaseFragment;
import com.freddieptf.mangatest.ui.details.DetailsActivity;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.ArrayList;

/**
 * Created by fred on 2/14/15.
 */
public class LibraryFragment extends BaseFragment implements LibraryView, LibraryAdapter.ClickCallback {

    private static final String TAG = "LibraryFragment";
    LibraryPresenter libraryPresenter;
    LibraryAdapter libraryAdapter;
    RecyclerView recyclerView;
    TextView emptyView;
    MaterialDialog.Builder builder;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //// FIXME: 28/09/16 what to do when we receive updates
            Object[] s = intent.getStringArrayExtra(MangaLibrarySync.UPDATE_LIST_EXTRA);
            if (s != null && s.length > 0) {
                libraryAdapter.notifyDataSetChanged();
            } else {
                Snackbar.make(recyclerView, "no updates found", Snackbar.LENGTH_SHORT).show();
            }

        }
    };

    public LibraryFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    protected boolean hideBottomBar() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_manga, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        libraryAdapter = new LibraryAdapter(this);
        emptyView = (TextView) view.findViewById(R.id.emptyView);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(libraryAdapter);

        builder = new MaterialDialog.Builder(getContext());
    }

    @Override
    public void setPresenter(LibraryPresenter libraryPresenter) {
        this.libraryPresenter = libraryPresenter;
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDataLoad(ArrayList<MangaDetails> mangaDetailsArrayList) {
        Log.d(TAG, "items: " + mangaDetailsArrayList.size());
        libraryAdapter.swapData(mangaDetailsArrayList);
    }

    @Override
    public void onItemClick(String mangaName) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.TITLE_KEY, mangaName);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(final String mangaName, final int position) {
        builder.content("Do you want to delete " + mangaName + "?")
                .positiveText("yes")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        libraryPresenter.deleteItem(mangaName, position);
                    }
                })
                .negativeText("no")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    @Override
    public void onItemDeleted(String name, int position) {
        Snackbar.make(recyclerView, name + " has been deleted!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        libraryPresenter.start();
        if (!Utilities.compactCards(getActivity()) || Utilities.compactCards(getActivity())) {
            libraryAdapter.notifyDataSetChanged();
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(MangaLibrarySync.INTENT_FILTER));
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
                libraryAdapter.notifyDataSetChanged();
                break;

        }
        return super.onOptionsItemSelected(item);

    }
}
