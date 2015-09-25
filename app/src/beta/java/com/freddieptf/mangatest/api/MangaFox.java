package com.freddieptf.mangatest.api;

import com.freddieptf.mangatest.API_KEYS;
import com.freddieptf.mangatest.api.helperInterfaces.GetListListener;
import com.freddieptf.mangatest.api.mangafox.Processor;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fred on 8/9/15.
 */
public class MangaFox {

    final String LOG_TAG = getClass().getSimpleName();

    public void getMangaList(final GetListListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection;
                BufferedReader bufferedReader;

                try {
                    URL mangaFox_baseUrl = new URL("https://doodle-manga-scraper.p.mashape.com/mangafox.me/");
                    httpURLConnection = (HttpURLConnection) mangaFox_baseUrl.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.addRequestProperty("X-Mashape-Key", API_KEYS.API_KEY);
                    httpURLConnection.connect();

                    int StatusCode = httpURLConnection.getResponseCode();
                    if (StatusCode != 200) return;
                    Utilities.Log(LOG_TAG, "Status code: " + StatusCode);

                    InputStream inputStream = httpURLConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    String result;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    result = stringBuilder.toString();

                    listener.onGetList(new Processor().processList(result));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
