package com.freddieptf.mangatest.mainUi.baseUi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.mainUi.MainActivityHelper;
import com.freddieptf.mangatest.utils.MyColorUtils;

/**
 * Created by fred on 5/20/15.
 */
public class BaseFragment extends Fragment {

    public BaseFragment(){
        setRetainInstance(true);
    }

    protected boolean showToolBarWithDefaultAppColor(){
        return true;
    }

    protected boolean showToolBarSubtitle(){
        return false;
    }

    protected boolean showTabs(){
        return false;
    }

    protected boolean hideBottomBar() {
        return false;
    }

    protected int useNavigationIcon(){
        return R.drawable.ic_stat_maps_local_library;
    }

    protected MainActivityHelper getMainActivityHelper(){
        return ((MainActivityHelper) getActivity()).getMainActivityHelper();
    }

    protected MyColorUtils getMyColorUtils(){
        return new MyColorUtils(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!showToolBarSubtitle()) ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

//        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.primary));

    }

    @Override
    public void onResume() {
        super.onResume();

        if (showToolBarWithDefaultAppColor()) {
            MainActivity.toolbarBig.setBackgroundColor(new MyColorUtils(getActivity()).getPrimaryColor());
            getMyColorUtils().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        getMainActivityHelper().getToolBar().setNavigationIcon(useNavigationIcon());

        if(showTabs()) showTabsNow();
        else hideTabsNow();

        if (hideBottomBar()) getMainActivityHelper().hideBottomBar(true);
        else getMainActivityHelper().hideBottomBar(false);


    }

    private void showTabsNow(){
        getMainActivityHelper().getTabs().setVisibility(View.VISIBLE);
    }

    private void hideTabsNow(){
        getMainActivityHelper().getTabs().setVisibility(View.GONE);
    }

}

