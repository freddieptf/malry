package com.freddieptf.mangatest.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.freddieptf.mangatest.api.GetManga;
import com.freddieptf.mangatest.beans.MangaDetailsObject;
import com.freddieptf.mangatest.mainUi.fragments.MangaDetailsFragment;

/**
 * Created by fred on 8/31/15.
 */
public class MangaDetailsRequestService extends IntentService {

    public static String LOG_TAG = "MangaRequestService";

    public MangaDetailsRequestService() {
        super("MangaRequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GetManga getManga = new GetManga(getBaseContext());

        MangaDetailsObject mangaDetailsObject = getManga.getManga(intent.getStringExtra("ID"),
                intent.getStringExtra("SOURCE"));

        Intent i = new Intent(LOG_TAG);
        i.putExtra(MangaDetailsFragment.DETAILS_OBJECT, mangaDetailsObject);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}
