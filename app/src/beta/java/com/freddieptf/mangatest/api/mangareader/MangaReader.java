package com.freddieptf.mangatest.api.mangareader;

import com.freddieptf.mangatest.api.helperInterfaces.GetListListener;
import com.freddieptf.mangatest.api.helperInterfaces.OnDocumentReceived;

import org.jsoup.nodes.Document;

/**
 * Created by fred on 7/25/15.
 * MangaReader Class:
 *
 * Class providing methods that process the Jsoup documents
 * and return Lists with objects containing the relevant manga data.
 */
public class MangaReader {

    GetDocuments getDocuments;
    public MangaReader(){
        getDocuments = new GetDocuments();
    }

    public void getMangaList(final GetListListener listener){
        getDocuments.getAlphabeticalListDocument(new OnDocumentReceived() {
            @Override
            public void onDocumentReceived(Document document) {
                listener.onGetList(Processor.processAlphabeticalListDocument(document));
            }
        });
    }

    public void getPopularList(final GetListListener listener){
        getDocuments.getPopularListDocument(new OnDocumentReceived() {
            @Override
            public void onDocumentReceived(Document document) {
                listener.onGetList(Processor.processPopularListDocument(document));
            }
        });
    }

    public void getLatestList(final GetListListener listener){
        getDocuments.getLatestListDocument(new OnDocumentReceived() {
            @Override
            public void onDocumentReceived(Document document) {
                listener.onGetList(Processor.processLatestListDocument(document));
            }
        });
    }






}
