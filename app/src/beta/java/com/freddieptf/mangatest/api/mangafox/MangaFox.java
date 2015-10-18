package com.freddieptf.mangatest.api.mangafox;

import com.freddieptf.mangatest.api.ApiUtils;
import com.freddieptf.mangatest.api.helperInterfaces.GetListListener;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fred on 8/9/15.
 * MangaFox: getMangaList() gets the MangaFox manga list
 */
public class MangaFox {

    final String LOG_TAG = getClass().getSimpleName();

    public void getMangaList(final GetListListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL baseUrl = new URL("https://doodle-manga-scraper.p.mashape.com/mangafox.me/");
                    String result = ApiUtils.getResultString(baseUrl);
                    listener.onGetList(new Processor().processList(result));
                } catch (MalformedURLException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
