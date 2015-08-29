package com.freddieptf.mangatest.api;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

/**
 * Created by fred on 7/25/15.
 */
public class FetchCall implements Callable<FetchCall.DocumentObject> {

    String url;
    int id;

    public FetchCall(){}

    public FetchCall(@NonNull String url, int id){
        this.url = url;
        this.id = id;
    }

    public int getId(){
        return id;
    }

    @Override
    public DocumentObject call() throws Exception {
        DocumentObject documentObject = new DocumentObject();
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(10000)
                    .get();

            //lulz
            for(int i = 0; i < 5; i++){
                if(document == null) Thread.sleep(500);
                document = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .timeout(10000)
                        .get();
            }

            documentObject.setDocument(document);
            documentObject.setId(id);
        }catch (SocketTimeoutException e){}
        return documentObject;
    }


    public class DocumentObject{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }

        Document document;
        int id;
    }
}
