package com.freddieptf.mangatest.api.workers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.freddieptf.mangatest.api.helperInterfaces.InsertListener;
import com.freddieptf.mangatest.beans.MangaInfoBean;
import com.freddieptf.mangatest.beans.MangaLatestInfoBean;
import com.freddieptf.mangatest.beans.MangaPopularInfoBean;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.data.Contract.MangaFoxMangaList;
import com.freddieptf.mangatest.data.Contract.MangaReaderLatestList;
import com.freddieptf.mangatest.data.Contract.MangaReaderMangaList;
import com.freddieptf.mangatest.data.Contract.MangaReaderPopularList;
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
        }else if(destination.equals(MangaReaderPopularList.CONTENT_URI)){
            insertPopular(list, destination);
        } else {
            Utilities.Log(LOG_TAG, "Bitch where?");
        }

        if(insertListener != null) insertListener.onInsertDone();
    }


    public void insert(List<Object> list, Uri destination){

        List<ContentValues> contentValuesList = new ArrayList<>(list.size());

        for(Object m : list){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MangaReaderMangaList.COLUMN_MANGA_NAME, ((MangaInfoBean) m).getManga_NAME());
            contentValues.put(MangaReaderMangaList.COLUMN_MANGA_ID, ((MangaInfoBean) m).getManga_ID());
            contentValuesList.add(contentValues);
        }

        if(contentValuesList.size() > 0){
            ContentValues[] contentValuesArray = new ContentValues[contentValuesList.size()];
            contentValuesList.toArray(contentValuesArray);
            contentValuesList.clear();
            int rowsInserted = context.getContentResolver().bulkInsert(destination, contentValuesArray);
            int virtualRowsInserted = context.getContentResolver().bulkInsert(Contract.VirtualTable.CONTENT_URI, contentValuesArray);
            Utilities.Log(LOG_TAG, "Rows inserted: " + rowsInserted);
            Utilities.Log(LOG_TAG, "Virtual rows inserted: " + virtualRowsInserted);
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

    public void insertPopular(List<Object> list, Uri destination){
        List<ContentValues> contentValuesList = new ArrayList<>(list.size());
        for(Object m : list){
            ContentValues cv = new ContentValues();
            cv.put(MangaReaderPopularList.COLUMN_MANGA_NAME, ((MangaPopularInfoBean)m).getName());
            cv.put(MangaReaderPopularList.COLUMN_CHAPTER_DETAILS, ((MangaPopularInfoBean)m).getChapterCount());
            cv.put(MangaReaderPopularList.COLUMN_MANGA_AUTHOR, ((MangaPopularInfoBean)m).getAuthor());
            cv.put(MangaReaderPopularList.COLUMN_MANGA_GENRE, ((MangaPopularInfoBean)m).getGenre());
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
}
