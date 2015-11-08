package com.freddieptf.mangatest.mainUi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.GridAdapter;

/**
 * Created by fred on 6/7/15.
 */
public class MangaGridFragment extends Fragment {

    RecyclerView recyclerView;

    public MangaGridFragment(){
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_grid_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getActivity().getIntent().getBundleExtra("bundle");
        String[] picUris = bundle.getStringArray("pic_urls");

        recyclerView = (RecyclerView)view.findViewById(R.id.staggered_recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_columns)));

        GridAdapter adapter = new GridAdapter(picUris, new GridAdapter.OnItemClick() {
            @Override
            public void onGridItemClick(int position) {
                MangaViewerFragment fragment = new MangaViewerFragment();
                Bundle b = new Bundle();
                b.putInt("pos", position);
                fragment.setArguments(b);
                getActivity()
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        recyclerView.setItemViewCacheSize(picUris.length > 24 ? picUris.length/3 : picUris.length);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_grid_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
