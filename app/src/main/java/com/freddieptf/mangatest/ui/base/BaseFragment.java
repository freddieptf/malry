package com.freddieptf.mangatest.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.freddieptf.mangatest.R;

/**
 * Created by fred on 5/20/15.
 */
public class BaseFragment extends Fragment {

    public BaseFragment(){
        setRetainInstance(true);
    }

    protected boolean showToolBarSubtitle(){
        return false;
    }

    protected boolean showTabs(){
        return false;
    }

    protected int useNavigationIcon(){
        return R.drawable.ic_stat_maps_local_library;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!showToolBarSubtitle()) ((BaseActivity) getActivity()).setToolbarSubTitle("");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).setNavigationIcon(useNavigationIcon());
        if(showTabs()) showTabsNow();
        else hideTabsNow();

    }

    private void showTabsNow(){
        ((BaseActivity) getActivity()).showTabs();
    }

    private void hideTabsNow(){
        ((BaseActivity) getActivity()).hideTabs();
    }

}

