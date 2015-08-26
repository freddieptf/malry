package com.freddieptf.mangatest.api;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.freddieptf.mangatest.api.helperInterfaces.InsertListener;
import com.freddieptf.mangatest.beans.MangaInfoBean;
import com.freddieptf.mangatest.beans.MangaLatestInfoBean;
import com.freddieptf.mangatest.data.Contract.MangaFoxMangaList;
import com.freddieptf.mangatest.data.Contract.MangaReaderLatestList;
import com.freddieptf.mangatest.data.Contract.MangaReaderMangaList;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 7/30/15.
 */
public class InsertCall extends Thread {

    List<Object> list;
    Uri destination;
    Context context;
    InsertListener insertListener;
    final String LOG_TAG = getClass().getSimpleName();

    public InsertCall(List<Object> list, Uri destination, Context context){
        this.list = list;
        this.destination = destination;
        this.context = context;
    }

    public void setInsertDoneListener(InsertListener insertDone){
        this.insertListener = insertDone;
    }

    @Override
    public void run() {
        super.run();
        Utilities.Log(LOG_TAG, "Starting insert.");

        if(destination.equals(MangaReaderMangaList.CONTENT_URI)
                || destination.equals(MangaFoxMangaList.CONTENT_URI)){
            insert(list, destination);
        }else if(destination.equals(MangaReaderLatestList.CONTENT_URI)){
            insertLatest(list, destination);
        }else {
            Utilities.Log(LOG_TAG, "Insert wut breh?");
        }

        insertListener.onInsertDone();
    }


    public void insert(List<Object> list, Uri destination){

        List<ContentValues> contentValuesList = new ArrayList<>(list.size());

        for(int i = 0; i < list.size(); i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MangaReaderMangaList.COLUMN_MANGA_ID,
                    ((MangaInfoBean)list.get(i)).getManga_ID());
            contentValues.put(MangaReaderMangaList.COLUMN_MANGA_NAME,
                    ((MangaInfoBean)list.get(i)).getManga_NAME());
            contentValuesList.add(contentValues);
        }

        if(contentValuesList.size() > 0){
            ContentValues[] contentValuesArray = new ContentValues[contentValuesList.size()];
            contentValuesList.toArray(contentValuesArray);
            contentValuesList.clear();
            int rowsInserted = context.getContentResolver().bulkInsert(destination, contentValuesArray);
            Utilities.Log(LOG_TAG, "Rows inserted: " + rowsInserted);
        }

    }

    public void insertLatest(List<Object> list, Uri destination){
        List<ContentValues> contentValuesList = new ArrayList<>(list.size());
        for(Object m : list){
            ContentValues cv = new ContentValues();
            cv.put(MangaReaderLatestList.COLUMN_MANGA_NAME, ((MangaLatestInfoBean) m).getMangaTitle());
            cv.put(MangaReaderLatestList.COLUMN_MANGA_ID, ((MangaLatestInfoBean) m).getMangaId());
            cv.put(MangaReaderLatestList.COLUMN_CHAPTER, ((MangaLatestInfoBean) m).getChapter());
            cv.put(MangaReaderLatestList.COLUMN_DATE, ((MangaLatestInfoBean) m).getDate());
            contentValuesList.add(cv);
        }

        if(contentValuesList.size() > 0){
            ContentValues[] contentValues = new ContentValues[contentValuesList.size()];
            contentValuesList.toArray(contentValues);
            contentValuesList.clear();
            int rowsInserted = context.getContentResolver().bulkInsert(destination, contentValues);
            Utilities.Log(LOG_TAG, "Rows inserted: " + rowsInserted);
        }

    }

    //@TODO insertPopular
    public void insertPopular(List<Object> list, Uri destination){

    }
}
