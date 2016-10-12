package com.freddieptf.mangatest.data.manga.lists;

import android.content.Context;

import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.data.local.DbInsertHelper;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static com.freddieptf.mangatest.data.local.DbInsertHelper.LOG_TAG;

/**
 * Created by freddieptf on 11/10/16.
 */
public class MangaListRepository implements MangaListSource {

    private static MangaListRepository INSTANCE;
    private final DbInsertHelper dbInsertHelper;
    private MangaListLocalSource localSource;
    private MangaListRemoteSource remoteSource;
    private ArrayList<MangaItem> readerListCache;
    private ArrayList<MangaItem> foxListCache;
    private ArrayList<LatestMangaItem> latestListCache;
    private ArrayList<PopularMangaItem> popularListCache;

    private MangaListRepository() {
        localSource = MangaListLocalSource.getInstance();
        remoteSource = MangaListRemoteSource.getInstance();
        dbInsertHelper = new DbInsertHelper();
    }

    public static MangaListRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new MangaListRepository();
        return INSTANCE;
    }

    @Override
    public ArrayList<MangaItem> getMangaReaderMangaList(Context context) {
        ArrayList<MangaItem> items = localSource.getMangaReaderMangaList(context);
        if (items == null) {
            items = remoteSource.getMangaReaderMangaList(context);
            if (items != null) {
                context.getContentResolver().delete(Contract.MangaReaderMangaList.CONTENT_URI, null, null);
                Callable<Boolean> insertItems = dbInsertHelper
                        .setDestinationUri(Contract.MangaReaderMangaList.CONTENT_URI)
                        .insertMangaList(context, items);
                try {
                    boolean b = insertItems.call();
                    if (b) Utilities.Log(LOG_TAG, "inserted MangaReader manga list");
                    else Utilities.Log(LOG_TAG, "couldn't insert MangaReader manga list");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        readerListCache = items;
        return items;
    }

    @Override
    public ArrayList<MangaItem> getMangaFoxMangaList(Context context) {
        ArrayList<MangaItem> items = localSource.getMangaFoxMangaList(context);
        if (items == null) {
            items = remoteSource.getMangaFoxMangaList(context);
            if (items != null) {
                context.getContentResolver().delete(Contract.MangaFoxMangaList.CONTENT_URI, null, null);
                Callable<Boolean> insertItems = dbInsertHelper
                        .setDestinationUri(Contract.MangaFoxMangaList.CONTENT_URI)
                        .insertMangaList(context, items);
                try {
                    boolean b = insertItems.call();
                    if (b) Utilities.Log(LOG_TAG, "inserted Mangafox manga list");
                    else Utilities.Log(LOG_TAG, "couldn't insert Mangafox manga list");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        foxListCache = items;
        return items;
    }

    @Override
    public ArrayList<LatestMangaItem> getMangaReaderLatestList(Context context) {
        ArrayList<LatestMangaItem> items = localSource.getMangaReaderLatestList(context);
        if (items == null) {
            items = remoteSource.getMangaReaderLatestList(context);
            if (items != null) {
                context.getContentResolver().delete(Contract.MangaReaderLatestList.CONTENT_URI, null, null);
                Callable<Boolean> insertItems = dbInsertHelper
                        .setDestinationUri(Contract.MangaReaderLatestList.CONTENT_URI)
                        .insertLatestList(context, items);
                try {
                    boolean b = insertItems.call();
                    if (b) Utilities.Log(LOG_TAG, "inserted latest manga");
                    else Utilities.Log(LOG_TAG, "couldn't insert latest manga");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        latestListCache = items;
        return items;
    }

    @Override
    public ArrayList<PopularMangaItem> getMangaReaderPopularList(Context context) {
        ArrayList<PopularMangaItem> items = localSource.getMangaReaderPopularList(context);
        if (items == null) {
            items = remoteSource.getMangaReaderPopularList(context);
            if (items != null) {
                context.getContentResolver().delete(Contract.MangaReaderPopularList.CONTENT_URI, null, null);
                Callable<Boolean> insertItems = dbInsertHelper
                        .setDestinationUri(Contract.MangaReaderPopularList.CONTENT_URI)
                        .insertPopularList(context, items);

                try {
                    boolean b = insertItems.call();
                    if (b) Utilities.Log(LOG_TAG, "inserted popular manga");
                    else Utilities.Log(LOG_TAG, "couldn't insert popular manga");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        popularListCache = items;
        return items;
    }

    boolean readerListCacheAvailable() {
        return readerListCache != null && !readerListCache.isEmpty();
    }

    boolean foxListCacheAvailable() {
        return foxListCache != null && !foxListCache.isEmpty();
    }

    ArrayList<MangaItem> getReaderListCache() {
        return readerListCache;
    }

    ArrayList<MangaItem> getFoxListCache() {
        return foxListCache;
    }

    ArrayList<LatestMangaItem> getLatestListCache() {
        return latestListCache;
    }

    ArrayList<PopularMangaItem> getPopularListCache() {
        return popularListCache;
    }
}
