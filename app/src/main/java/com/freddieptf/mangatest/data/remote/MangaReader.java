package com.freddieptf.mangatest.data.remote;

import android.content.Context;
import android.util.Log;

import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.data.local.DbInsertHelper;
import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fred on 7/25/15..
 */
public class MangaReader {

    private static MangaReader reader;
    private final String LOG_TAG = getClass().getSimpleName();
    private final MangaReaderApiService apiService;
    private final DbInsertHelper dbInsertHelper;
    private WeakReference<Context> context;

    private MangaReader(WeakReference<Context> context) {
        this.context = context;
        dbInsertHelper = new DbInsertHelper();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor())
                .build();

        apiService = new Retrofit.Builder()
                .baseUrl("http://mapi-freddieptf.rhcloud.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(MangaReaderApiService.class);
    }

    public static MangaReader getInstance(WeakReference<Context> context) {
        if (reader == null) reader = new MangaReader(context);
        return reader;
    }

    /**
     * <b>Synchronously</b> get the MangaDetails of the manga identified by @param mangaId
     **/
    public MangaDetails getManga(String mangaId) throws IOException {
        return apiService.getManga(mangaId).execute().body();
    }

    /**
     * <b>Synchronously</b> get the chapter image pages of the manga chapter
     **/
    public ChapterPages getMangaChapterPages(String mangaId, String chId) throws IOException {
        return apiService.getChapterPages(mangaId, chId).execute().body();
    }

    public void updateLatest() {
        // TODO: 22/09/16 update latest
    }

    void getMangaList() {
        apiService.getMangaList().enqueue(new Callback<ArrayList<MangaItem>>() {
            @Override
            public void onResponse(Call<ArrayList<MangaItem>> call, Response<ArrayList<MangaItem>> response) {
                if (!response.body().isEmpty()) {
                    context.get().getContentResolver().delete(Contract.MangaReaderMangaList.CONTENT_URI, null, null);
                    context.get().getContentResolver().delete(Contract.VirtualTable.CONTENT_URI, null, null);
                    Callable<Boolean> insertItems = dbInsertHelper
                            .setDestinationUri(Contract.MangaReaderMangaList.CONTENT_URI)
                            .insertMangaList(context.get(), response.body());
                    try {
                        boolean b = insertItems.call();
                        if (b) Utilities.Log(LOG_TAG, "inserted MangaReader manga list");
                        else Utilities.Log(LOG_TAG, "couldn't insert MangaReader manga list");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MangaItem>> call, Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

    void getLatestList() {
        apiService.getLatestMangaList().enqueue(new Callback<ArrayList<LatestMangaItem>>() {
            @Override
            public void onResponse(Call<ArrayList<LatestMangaItem>> call, Response<ArrayList<LatestMangaItem>> response) {
                if (!response.body().isEmpty()) {
                    context.get().getContentResolver().delete(Contract.MangaReaderLatestList.CONTENT_URI, null, null);
                    Callable<Boolean> insertItems = dbInsertHelper
                            .setDestinationUri(Contract.MangaReaderLatestList.CONTENT_URI)
                            .insertLatestList(context.get(), response.body());
                    try {
                        boolean b = insertItems.call();
                        if (b) Utilities.Log(LOG_TAG, "inserted latest manga");
                        else Utilities.Log(LOG_TAG, "couldn't insert latest manga");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LatestMangaItem>> call, Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

    void getPopularList() {
    }

    private static class RetryInterceptor implements Interceptor {
        private int NUM_RETRY = 3;

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            for (int i = NUM_RETRY; i > 0; i--) {
                try {
                    okhttp3.Response response = chain.proceed(request);
                    if (response.isSuccessful()) return response;
                } catch (IOException e) {
                    if (i == 1) throw e;
                }
            }
            return null;
        }
    }


}
