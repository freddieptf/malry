package com.freddieptf.mangatest.api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.beans.NetworkChapterAttrs;
import com.freddieptf.mangatest.service.MangaDownloadService;
import com.freddieptf.mangatest.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by fred on 1/22/15.
 */
public class FetchMangaChapter extends AsyncTask<String, Void, String> {

    final String LOG_TAG = getClass().getSimpleName();
    ArrayList<NetworkChapterAttrs> mangaChapterAttrs;
    Context context;

    public FetchMangaChapter(Context c){
        context = c;
    }

    @Override
    protected String doInBackground(String... strings) {

        ArrayList arrayList = getMangaUrls(strings);

        String connected;

        if (arrayList != null && arrayList.size() > 1) {
            Log.d(LOG_TAG, "size: " + arrayList.size());
            Intent downloadIntent = new Intent(context, MangaDownloadService.class);

            downloadIntent.putExtra(MangaDownloadService.ARRAY_LIST, arrayList);
            context.startService(downloadIntent);
            connected = "Yas";

        }else if(arrayList != null && arrayList.size() == 1){
            connected = "No";
            Log.d(LOG_TAG, "size: " + arrayList.size());
        }else{
            connected = "Not available";
        }

        return connected;
    }


    @Override
    protected void onPostExecute(String connected) {
        super.onPostExecute(connected);

        Log.d(LOG_TAG, "onPostExecute" + " - Connected " + connected);

        if(connected.equals("No")) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.no_internet),
                    Toast.LENGTH_SHORT).show();
        }
        else if(connected.equals("Yas")) {
            Toast.makeText(context,
                    "Starting download...",
                    Toast.LENGTH_SHORT).show();
        }else if(connected.equals("Something ELSE")) {
            Toast.makeText(context,
                    "Something else",
                    Toast.LENGTH_SHORT).show();
        }

    }


    private ArrayList getMangaUrls(String[] strings) {

        ArrayList arrayList;
        if(!Utilities.isOnline(context)){
            arrayList = new ArrayList<>();
            arrayList.add(0, "No Internet");
            return arrayList;
        }

        String baseUrl;
        if(strings[2].equals(context.getString(R.string.pref_manga_reader)))
            baseUrl = "http://mapi-freddieptf.rhcloud.com/api/mr/manga/";
        else
            baseUrl = "https://doodle-manga-scraper.p.mashape.com/mangafox.me/manga/";

        arrayList = fetch(strings, baseUrl);
        return arrayList;
    }

    private ArrayList fetch(String[] strings, String baseUrl) {

        baseUrl = baseUrl.concat(strings[0] + "/" + strings[1]);
        mangaChapterAttrs = new ArrayList<>();
        NetworkChapterAttrs a = new NetworkChapterAttrs();
        String name = strings[0];
        name = name.replaceAll("-", " ");
        a.setName(name);

        try {
            String results = ApiUtils.getResultString(new URL(baseUrl));

            if(results.isEmpty()) return null;

            JSONObject mangaStuff = new JSONObject(results);

            String chapterTitle;
             if(!mangaStuff.has("name")){
                 chapterTitle = "Chapter";
             }else{
                 chapterTitle = mangaStuff.getString("name");
             }

            String chapter = "ch" + strings[1];
            Log.i("chapter: ", chapter);

            Log.i(LOG_TAG + " chapter: ", chapter);

            a.setChapter(chapter);
            a.setChapterTitle(chapterTitle);
            mangaChapterAttrs.add(a);


            if(!mangaStuff.has("pages")) return null;

            JSONArray pages = mangaStuff.getJSONArray("pages");

            for(int i = 0; i < pages.length(); i++){
                NetworkChapterAttrs attr = new NetworkChapterAttrs();
                JSONObject singlePageContent = pages.getJSONObject(i);
                attr.setPageId(singlePageContent.getString("pageId"));
                attr.setImageUrl(singlePageContent.getString("url"));
                mangaChapterAttrs.add(attr);
            }

            Log.i(getClass().getSimpleName() + " Size: ", mangaChapterAttrs.size() + "");
            Log.i(getClass().getSimpleName()+ " Chapter", chapter);


        } catch (JSONException | IOException e) {
            Utilities.Log(LOG_TAG, e.getMessage());
        }

        return mangaChapterAttrs;
    }


}
