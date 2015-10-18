package com.freddieptf.mangatest.api.mangareader;

import com.freddieptf.mangatest.api.ApiUtils;
import com.freddieptf.mangatest.api.helperInterfaces.GetListListener;
import com.freddieptf.mangatest.api.helperInterfaces.OnDocumentReceived;

import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by fred on 7/25/15.
 * MangaReader Class:
 *
 * Class providing methods that process the Jsoup documents
 * and return Lists with objects containing manga list data.
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
                try {
                    URL baseUrl = new URL("https://doodle-manga-scraper.p.mashape.com/mangareader.net/");
                    String result = ApiUtils.getResultString(baseUrl);
                    listener.onGetList(new com.freddieptf.mangatest.api.mangafox.Processor().processList(result));
                } catch (MalformedURLException e) {
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
