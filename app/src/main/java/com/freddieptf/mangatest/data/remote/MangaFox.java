package com.freddieptf.mangatest.data.remote;

import android.content.Context;
import android.util.Log;

import com.freddieptf.mangatest.API_KEYS;
import com.freddieptf.mangatest.data.local.Contract;
import com.freddieptf.mangatest.data.local.DbInsertHelper;
import com.freddieptf.mangatest.data.model.ChapterPages;
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
 * Created by fred on 8/9/15.
 */
public class MangaFox {

    private static MangaFox mangaFox;
    private final MangaFoxApiService apiService;
    private final DbInsertHelper dbInsertHelper;
    private final String LOG_TAG = getClass().getSimpleName();
    private WeakReference<Context> context;

    private MangaFox(WeakReference<Context> context) {
        this.context = context;
        dbInsertHelper = new DbInsertHelper();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new ApiKeyInterceptor()).build();
        apiService = new Retrofit.Builder()
                .baseUrl("https://doodle-manga-scraper.p.mashape.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MangaFoxApiService.class);
    }

    public static MangaFox getInstance(WeakReference<Context> context) {
        if (mangaFox == null) mangaFox = new MangaFox(context);
        return mangaFox;
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

    void getMangaList() {
        apiService.getMangaList().enqueue(new Callback<ArrayList<MangaItem>>() {
            @Override
            public void onResponse(Call<ArrayList<MangaItem>> call, Response<ArrayList<MangaItem>> response) {
                // FIXME: 12/09/16 probably handle the isEmpty/null possibilty first then return.
                if (!response.body().isEmpty()) {
                    context.get().getContentResolver().delete(Contract.MangaFoxMangaList.CONTENT_URI, null, null);
                    Callable<Boolean> insertItems = dbInsertHelper
                            .setDestinationUri(Contract.MangaFoxMangaList.CONTENT_URI)
                            .insertMangaList(context.get(), response.body());
                    try {
                        boolean b = insertItems.call();
                        if (b) Utilities.Log(LOG_TAG, "inserted Mangafox manga list");
                        else Utilities.Log(LOG_TAG, "couldn't insert Mangafox manga list");
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

    private static class ApiKeyInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            request = request.newBuilder()
                    .addHeader("X-Mashape-Key", API_KEYS.API_KEY)
                    .build();
            return chain.proceed(request);
        }
    }
}
