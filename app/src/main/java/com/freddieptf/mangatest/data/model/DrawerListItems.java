package com.freddieptf.mangatest.data.model;

/**
 * Created by fred on 2/14/15.
 */
public class DrawerListItems {

    private String title;
    private int icon;

    public DrawerListItems(){

    }
    public DrawerListItems(String title, int icon){
        this.title = title;
        this.icon = icon;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
