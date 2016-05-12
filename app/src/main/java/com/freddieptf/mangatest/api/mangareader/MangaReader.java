package com.freddieptf.mangatest.api.mangareader;

import com.freddieptf.mangatest.api.ApiUtils;
import com.freddieptf.mangatest.api.helperInterfaces.GetListListener;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fred on 7/25/15..
 */
public class MangaReader {

    final String LOG_TAG = getClass().getSimpleName();

    Processor processor;
    public MangaReader(){
        processor = new Processor();
    }

    public void getMangaList(final GetListListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL baseUrl = new URL("http://mapi-freddieptf.rhcloud.com/api/mr/list");
                    String result = ApiUtils.getResultString(baseUrl);
                    listener.onGetList(processor.processMangaListJSON(result));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getLatestList(final GetListListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL baseUrl = new URL("http://mapi-freddieptf.rhcloud.com/api/mr/latest");
                    String result = ApiUtils.getResultString(baseUrl);
                    listener.onGetList(processor.processLatestListJSON(result));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getPopularList(final GetListListener listener){
    }


}
