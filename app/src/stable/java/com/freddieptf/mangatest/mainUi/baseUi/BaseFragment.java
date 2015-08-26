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

    protected boolean showToolBarWithDefaultAppColor(){
        return true;
    }

    protected boolean showToolBarExtra(){
        return false;
    }

    protected void showToolBarExtra(boolean show){
        if(showToolBarExtra() || show) MainActivity.toolBarExtra.setVisibility(View.VISIBLE);
        else MainActivity.toolBarExtra.setVisibility(View.GONE);
    }

    protected boolean showToolBarSubtitle(){
        return false;
    }

    protected MainActivityHelper getMainActivityHelper(){
        return ((MainActivityHelper) getActivity()).getMainActivityHelper();
    }

    protected MyColorUtils getMyColorUtils(){
        return new MyColorUtils(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMainActivityHelper().lockDrawer(false);
        setRetainInstance(true);

        if(showToolBarExtra()) MainActivity.toolBarExtra.setVisibility(View.VISIBLE);
        else MainActivity.toolBarExtra.setVisibility(View.GONE);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getMainActivityHelper().hideToolBarShadow(false);

        if(!showToolBarSubtitle()) ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

//        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.primary));


    }

    @Override
    public void onResume() {
        super.onResume();

        if(showToolBarWithDefaultAppColor()){
            MainActivity.toolbarBig.setBackgroundColor(new MyColorUtils(getActivity()).getPrimaryColor());

            if(((BaseActivity) getActivity()).hasNavDrawer())
                getMyColorUtils().setStatusBarColor(getResources().getColor(android.R.color.transparent));
            else
                getMyColorUtils().setStatusBarColor(getResources().getColor(R.color.primary_dark));

        }
    }
}

