package com.freddieptf.mangatest.api.mangareader;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.freddieptf.mangatest.api.helperInterfaces.OnDocumentReceived;
import com.freddieptf.mangatest.api.workers.FetchCall;
import com.freddieptf.mangatest.utils.Utilities;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

/**
 * Created by fred on 7/25/15.
 */
public class GetDocuments {

    private final String BASE_URL = "http://www.mangareader.net";
    final String URL_LIST_ALPHABETICAL = "alphabetical";
    final String URL_LIST_POPULAR = "popular";
    final String URL_LIST_LATEST = "latest";
    final static int ALPHABETICAL_ID = 1;
    final static int POPULAR_ID = 2;
    final static int LATEST_ID = 3;

    private final String LOG_TAG = getClass().getSimpleName();
    private ExecutorCompletionService ecs;

    public GetDocuments(){
        ecs = new ExecutorCompletionService(new Executor() {
            @Override
            public void execute(@NonNull Runnable runnable) {
                new Thread(runnable).start();
            }
        });
    }


    public void getAlphabeticalListDocument(final OnDocumentReceived documentReceived) {
        Utilities.Log(LOG_TAG, "getting doc");
        ecs.submit(new GetAlphabeticalListDoc());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FetchCall.DocumentObject documentObject = (FetchCall.DocumentObject) ecs.take().get();
                    List<Document> document = documentObject.getDocumentList();
                    if(document.isEmpty()) throw new NullPointerException();
                    documentReceived.onDocumentReceived(document);
                    document.clear();

                } catch (InterruptedException | ExecutionException | NullPointerException e) {
                    Utilities.Log(LOG_TAG, "Aplhabetical docs: " + e.getMessage());
                }
            }
        }).start();
    }

    public void getPopularListDocument(final OnDocumentReceived documentReceived) {
        Utilities.Log(LOG_TAG, "getting doc");
        final String base = Uri.parse(BASE_URL).buildUpon().appendPath(URL_LIST_POPULAR).build().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetPopularListDoc getPopularListDoc = new GetPopularListDoc();
                List<String> urls = new ArrayList<String>();
                for(int i = 0; i < 1800; i+=30){
                    if(i > 0) urls.add(base + "/" + i);
                    else urls.add(base);
                }
                getPopularListDoc.setUrl(urls);
                ecs.submit(getPopularListDoc);
                try {
                    FetchCall.DocumentObject documentObject = (FetchCall.DocumentObject) ecs.take().get();
                    List<Document> document = documentObject.getDocumentList();
                    if(document.isEmpty()) throw new NullPointerException();
                    documentReceived.onDocumentReceived(document);
                    documentObject.clearList();
                } catch (InterruptedException | ExecutionException | NullPointerException e) {
                    Utilities.Log(LOG_TAG, "Popular doc: " + e.getMessage());
                }
            }
        }).start();
    }

    public void getLatestListDocument(final OnDocumentReceived documentReceived) {
        Utilities.Log(LOG_TAG, "getting doc");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ecs.submit(new GetLatestListDoc());
                try {
                    FetchCall.DocumentObject documentObject = (FetchCall.DocumentObject) ecs.take().get();
                    List<Document> document = documentObject.getDocumentList();
                    if(document.isEmpty()) throw new NullPointerException();
                    documentReceived.onDocumentReceived(document);
                    document.clear();
                } catch (InterruptedException | ExecutionException | NullPointerException e) {
                    Utilities.Log(LOG_TAG, "Latest Doc: " + e.getMessage());
                }
            }
        }).start();
    }

    private class GetAlphabeticalListDoc extends FetchCall {
        public GetAlphabeticalListDoc() {
            super(Uri.parse(BASE_URL).buildUpon().appendPath(URL_LIST_ALPHABETICAL).build().toString(),
                    ALPHABETICAL_ID);
        }
    }

    private class GetPopularListDoc extends FetchCall {
        public GetPopularListDoc() {
            super(Uri.parse(BASE_URL).buildUpon().appendPath(URL_LIST_POPULAR).build().toString(), POPULAR_ID);
        }

        @Override
        public void setUrl(List<String> urls) {
            super.setUrl(urls);
        }
    }

    private class GetLatestListDoc extends FetchCall {
        public GetLatestListDoc() {
            super(Uri.parse(BASE_URL).buildUpon().appendPath(URL_LIST_LATEST).build().toString(), LATEST_ID);
        }
    }


}
