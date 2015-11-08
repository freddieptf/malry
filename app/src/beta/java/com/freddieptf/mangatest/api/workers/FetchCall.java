package com.freddieptf.mangatest.api.workers;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by fred on 7/25/15.
 */
public class FetchCall implements Callable<FetchCall.DocumentObject> {

    int id;
    List<String> urls;

    public FetchCall(){}

    public FetchCall(@NonNull String url, int id){
        urls = new ArrayList<>();
        urls.add(url);
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setUrl(List<String> urls){
        this.urls = urls;
    }

    @Override
    public DocumentObject call() throws Exception {
        DocumentObject documentObject = new DocumentObject();
        for(String url : urls) {
            try {
                Document document = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .timeout(10000)
                        .get();

                //lulz lulz
                for (int i = 0; i < 10; i++) {
                    if(document == null) {
                        Thread.sleep(1000);
                        document = Jsoup.connect(url)
                                .userAgent("Mozilla")
                                .timeout(10000)
                                .get();
                    }
                    else break;
                }

                documentObject.setDocument(document);
                documentObject.setId(id);
            } catch (SocketTimeoutException e) {}
        }
        return documentObject;
    }


    public class DocumentObject{
        public DocumentObject(){
            documents = new ArrayList<>();
        }
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Document> getDocumentList() {
            return documents;
        }

        public void setDocument(Document document) {
            documents.add(document);
        }

        public void clearList(){
            documents = null;
        }

        List<Document> documents;
        int id;
    }
}
