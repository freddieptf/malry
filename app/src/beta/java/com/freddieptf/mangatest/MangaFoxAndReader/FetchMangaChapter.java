package com.freddieptf.mangatest.MangaFoxAndReader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.beans.NetworkChapterAttrs;
import com.freddieptf.mangatest.service.MangaDownloadService;
import com.freddieptf.mangatest.service.MangaDownloadServiceTest;
import com.freddieptf.mangatest.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by fred on 1/22/15.
 */
public class FetchMangaChapter extends AsyncTask<String, Void, String> {

    ArrayList<NetworkChapterAttrs> mangaChapterAttrs;
    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;
    Context context;

    public FetchMangaChapter(Context c){
        context = c;

    }


    @Override
    protected String doInBackground(String... strings) {

        Log.d(getClass().getSimpleName(), "doInBackground");

        ArrayList arrayList = getMangaUrls(strings);

        String connected;

        if (arrayList != null && arrayList.size() > 1) {
            Log.d(getClass().getSimpleName(), "size: " + arrayList.size());
//            Intent downloadIntent = new Intent(context, MangaDownloadService.class);

            Intent downloadIntent = new Intent(context, MangaDownloadServiceTest.class);

            downloadIntent.putExtra(MangaDownloadService.ARRAY_LIST, arrayList);
            context.startService(downloadIntent);
            connected = "Yas";

        }else if(arrayList != null && arrayList.size() == 1){
            connected = "No";
            Log.d(getClass().getSimpleName(), "size: " + arrayList.size());
        }else{
            connected = "Not available";
        }

        return connected;
    }


    @Override
    protected void onPostExecute(String connected) {
        super.onPostExecute(connected);

        Log.d(getClass().getSimpleName(), "onPostExecute" + " - Connected " + connected);

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

        String baseUrlReader, baseUrlFox;
        baseUrlReader = "https://doodle-manga-scraper.p.mashape.com/mangareader.net/manga/";
        baseUrlFox = "https://doodle-manga-scraper.p.mashape.com/mangafox.me/manga/";

        arrayList = fetch(strings, baseUrlReader);

        if(arrayList == null || arrayList.size() < 1){
            arrayList = fetch(strings, baseUrlFox);
            Log.d(getClass().getSimpleName(), "FOX UP TI IT");
        }

        return arrayList;
    }

    private ArrayList fetch(String[] strings, String baseUrl) {

        baseUrl = baseUrl.concat(strings[0] + "/" + strings[1]);

        Log.d(getClass().getSimpleName(), baseUrl);

        mangaChapterAttrs = new ArrayList<>();
        NetworkChapterAttrs a = new NetworkChapterAttrs();
        String name = strings[0];
        name = name.replaceAll("-", " ");
        a.setName(name);

        try {
            URL url = new URL(baseUrl);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("X-Mashape-Key", "8Fp0bd39gLmshw7qSKtW61cjlK6Ip1V1Z5Fjsnhpy813RcQflk");
            httpURLConnection.connect();

            int status = httpURLConnection.getResponseCode();

            Log.i(getClass().getSimpleName() + " Status", "" + status);

            if(status != 200) return null;

            InputStream inputStream = httpURLConnection.getInputStream();

            if(inputStream == null) return null;

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String l;
            while((l = bufferedReader.readLine()) != null){
               stringBuilder.append(l);
                Log.i("Line", l);
            }

            String results = stringBuilder.toString();
            Log.i(getClass().getSimpleName()+ " TESTS: ", results);


            JSONObject mangaStuff = new JSONObject(results);

            String chapterTitle;
             if(!mangaStuff.has("name")){
                 chapterTitle = "Chapter";
             }else{
                 chapterTitle = mangaStuff.getString("name");
             }

            String chapter = "ch" + strings[1];
            Log.i("chapter: ", chapter);

            Log.i(getClass().getSimpleName()+ " chapter: ", chapter);

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
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null) httpURLConnection.disconnect();
        }

        if(bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mangaChapterAttrs;
    }


}
