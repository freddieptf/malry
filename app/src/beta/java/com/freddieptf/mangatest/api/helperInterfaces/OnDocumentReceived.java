package com.freddieptf.mangatest.api.helperInterfaces;

import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Created by fred on 7/30/15.
 */
public interface OnDocumentReceived {
    void onDocumentReceived(List<Document> document);
}
