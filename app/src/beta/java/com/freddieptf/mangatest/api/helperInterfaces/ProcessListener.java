package com.freddieptf.mangatest.api.helperInterfaces;

import android.support.annotation.NonNull;

import com.freddieptf.mangatest.beans.MangaInfoBean;
import com.freddieptf.mangatest.beans.MangaLatestInfoBean;

import java.util.List;

/**
 * Created by fred on 7/30/15.
 */
public interface ProcessListener {
    void onAlphabeticalListProcessed(@NonNull List<MangaInfoBean> list);
    void onLatestListProcessed(@NonNull List<MangaLatestInfoBean> list);
    void onPopularListProcessed(@NonNull List list);
}
