package com.freddieptf.mangatest.api;

import com.freddieptf.mangatest.api.helperInterfaces.GetListListener;
import com.freddieptf.mangatest.api.helperInterfaces.OnDocumentReceived;
import com.freddieptf.mangatest.api.mangareader.GetDocuments;
import com.freddieptf.mangatest.api.mangareader.Processor;
import com.freddieptf.mangatest.utils.Utilities;

import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by fred on 7/25/15.
 * MangaReader Class:
 *
 * Class providing methods that process the Jsoup documents
 * and return Lists with objects containing the relevant manga data.
 */
public class MangaReader {

    final String LOG_TAG = getClass().getSimpleName();

    GetDocuments getDocuments;

    public MangaReader(){
        getDocuments = new GetDocuments();
    }

//    public void getMangaList(final GetListListener listener){
//        getDocuments.getAlphabeticalListDocument(new OnDocumentReceived() {
//            @Override
//            public void onDocumentReceived(Document document) {
//                listener.onGetList(Processor.processAlphabeticalListDocument(document));
//            }
//        });
//    }

    public void getMangaList(final GetListListener listener){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection;
                BufferedReader bufferedReader;

                try {
                    URL mangaFox_baseUrl = new URL("https://doodle-manga-scraper.p.mashape.com/mangareader.net/");
                    httpURLConnection = (HttpURLConnection) mangaFox_baseUrl.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.addRequestProperty("X-Mashape-Key", "8Fp0bd39gLmshw7qSKtW61cjlK6Ip1V1Z5Fjsnhpy813RcQflk");
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
                    listener.onGetList(new com.freddieptf.mangatest.api.mangafox.Processor().processList(result));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void getPopularList(final GetListListener listener){
        getDocuments.getPopularListDocument(new OnDocumentReceived() {
            @Override
            public void onDocumentReceived(List<Document> document) {
                listener.onGetList(Processor.processPopularListDocument(document));
            }
        });
    }

    public void getLatestList(final GetListListener listener){
        getDocuments.getLatestListDocument(new OnDocumentReceived() {
            @Override
            public void onDocumentReceived(List<Document> document) {
                listener.onGetList(Processor.processLatestListDocument(document.get(0)));
            }
        });
    }






}
