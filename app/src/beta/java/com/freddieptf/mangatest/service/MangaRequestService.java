package com.freddieptf.mangatest.service;

import android.app.IntentService;
import android.content.Intent;

import com.freddieptf.mangatest.api.GetManga;
import com.freddieptf.mangatest.beans.MangaDetailsObject;
import com.freddieptf.mangatest.mainUi.fragments.MangaDetailsFragment;

/**
 * Created by fred on 8/31/15.
 */
public class MangaRequestService extends IntentService {

    public static String LOG_TAG = "MangaRequestService";

    public MangaRequestService() {
        super("MangaRequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GetManga getManga = new GetManga(getBaseContext());

        MangaDetailsObject mangaDetailsObject = getManga.getManga(intent.getStringExtra("ID"),
                intent.getStringExtra("SOURCE"));

        Intent i = new Intent(LOG_TAG);
        i.putExtra(MangaDetailsFragment.DETAILS_OBJECT, mangaDetailsObject);
        sendBroadcast(i);

    }
}
