package com.freddieptf.mangatest.mainUi.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.NavAdapter;
import com.freddieptf.mangatest.beans.DrawerListItems;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.mainUi.Settings;

import java.util.ArrayList;

/**
 * Created by fred on 4/22/15.
 */
public class NavDrawerFrag extends Fragment {

    int activePosition = -1;
    final String ACTIVE_POSITION = "active_position";
    final String LOG_TAG = getClass().getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.drawer_recyclerview, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setFitsSystemWindows(true);

        activePosition = 1;
        if(savedInstanceState != null){
            activePosition = savedInstanceState.getInt(ACTIVE_POSITION);
        }

        String[] fragmentTitles = getResources().getStringArray(R.array.drawer_list);
        int[] icons = {R.drawable.ic_stat_maps_local_library,
                R.drawable.ic_list,
                R.drawable.ic_action_download,
                R.drawable.ic_settings};

        ArrayList<DrawerListItems> arrayList = new ArrayList<>();
        DrawerListItems drawerListItems;

        for(int i = 0; i<fragmentTitles.length; i++){
            drawerListItems = new DrawerListItems();
            drawerListItems.setTitle(fragmentTitles[i]);
            drawerListItems.setIcon(icons[i]);
            arrayList.add(drawerListItems);
        }

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.drawer_recyclerlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final NavAdapter adapter = new NavAdapter(arrayList, getActivity(), new NavAdapter.DrawerItemClick() {
            @Override
            public void onClick(int position) {
                ((MainActivity) getActivity()).selectItem(position);
                activePosition = position;
            }

            @Override
            public void onClickSettings() {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.setActivePostion(activePosition);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ACTIVE_POSITION, activePosition);
    }

}
