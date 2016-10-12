package com.freddieptf.mangatest.data.remote;

import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.LatestMangaItem;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.model.MangaItem;
import com.freddieptf.mangatest.data.model.PopularMangaItem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fred on 7/25/15..
 */
public class MangaReader {

    private static MangaReader reader;
    private final MangaReaderApiService apiService;

    private MangaReader() {
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

    public static MangaReader getInstance() {
        if (reader == null) reader = new MangaReader();
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

    public ArrayList<MangaItem> getMangaList() throws IOException {
        return apiService.getMangaList().execute().body();
    }

    public ArrayList<LatestMangaItem> getLatestList() throws IOException {
        return apiService.getLatestMangaList().execute().body();
    }

    public ArrayList<PopularMangaItem> getPopularList() throws IOException {
        return apiService.getPopularMangaList().execute().body();
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
