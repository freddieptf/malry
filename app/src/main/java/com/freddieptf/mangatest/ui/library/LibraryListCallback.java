package com.freddieptf.mangatest.ui.library;

import android.support.v7.util.DiffUtil;

import com.freddieptf.mangatest.data.model.MangaDetails;

import java.util.ArrayList;

/**
 * Created by freddieptf on 29/09/16.
 */

public class LibraryListCallback extends DiffUtil.Callback {

    private final ArrayList<MangaDetails> oldList, newList;

    public LibraryListCallback(ArrayList<MangaDetails> oldList, ArrayList<MangaDetails> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
