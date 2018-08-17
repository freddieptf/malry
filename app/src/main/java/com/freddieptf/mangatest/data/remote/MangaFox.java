package com.freddieptf.mangatest.data.remote;

import com.freddieptf.mangatest.API_KEYS;
import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.model.MangaItem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fred on 8/9/15.
 */
public class MangaFox {

    private static MangaFox mangaFox;
    private final MangaFoxApiService apiService;

    private MangaFox() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new ApiKeyInterceptor()).build();
        apiService = new Retrofit.Builder()
                .baseUrl("https://doodle-manga-scraper.p.mashape.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MangaFoxApiService.class);
    }

    public static MangaFox getInstance() {
        if (mangaFox == null) mangaFox = new MangaFox();
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

    public ArrayList<MangaItem> getMangaList() throws IOException {
        return apiService.getMangaList().execute().body();
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
