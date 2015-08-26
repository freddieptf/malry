package com.freddieptf.mangatest.mainUi.fragments;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.ImageLoader;
import com.freddieptf.mangatest.MangaFoxAndReader.FetchManga;
import com.freddieptf.mangatest.MangaFoxAndReader.FetchMangaChapter;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.MangaChapterAdapter;
import com.freddieptf.mangatest.beans.ChapterAttrForAdapter;
import com.freddieptf.mangatest.beans.MangaDetailsObject;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.mainUi.MangaViewerActivity;
import com.freddieptf.mangatest.mainUi.baseUi.BaseFragment;
import com.freddieptf.mangatest.utils.PaletteHelper;
import com.freddieptf.mangatest.utils.Utilities;
import com.freddieptf.mangatest.volleyStuff.FadeInNetworkImageView;
import com.freddieptf.mangatest.volleyStuff.LruBitmapCache;
import com.freddieptf.mangatest.volleyStuff.VolleySingletonClass;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by fred on 1/30/15.
 */
public class MangaDetailsFragment extends BaseFragment implements ListView.OnScrollListener {

    public MangaDetailsFragment(){
        setRetainInstance(false);
    }

    @Override
    protected boolean showToolBarWithDefaultAppColor() {
        return false;
    }

    String mangaTitle, mangaId, imgUrl, mangaAuthor, mangaInfo, mangaStatus, chapterCount;
    public static String DIS_FRAGMENT = "Details";
    public static final String TITLE_KEY = "manga_title";
    public static final String ID_KEY = "manga_id";
    public static final String COVER_URL = "URL";
    public static final String DETAILS_OBJECT = "details_object";


    MangaChapterAdapter adapter;
    boolean loadFinished;
    boolean showFab;
    ListView listView;
    FloatingActionButton fab;
    boolean exists;
    FadeInNetworkImageView coverImageView;
    TextView manga_author, manga_status, manga_info, manga_chapterCount;
    SmoothProgressBar smoothProgressBar;
    String LOG_TAG = getClass().getSimpleName();
    public static PopulateViewsWithData populateViewsWithData;
    MangaDetailsObject mainMangaDetailsObject;
    getMangaFromTitle getManga;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_details, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivityHelper().lockDrawer(true);
        getMainActivityHelper().getToolBar().setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        final View headerRowView = LayoutInflater.from(getActivity()).inflate(R.layout.list_detail_header_item, null);

        coverImageView = (FadeInNetworkImageView)view.findViewById(R.id.iv_MangaDetailsCover);
        manga_author = (TextView)headerRowView.findViewById(R.id.tv_MangaDetails_author);
        manga_info = (TextView)headerRowView.findViewById(R.id.tv_MangaDetails_info);
        manga_status = (TextView)headerRowView.findViewById(R.id.tv_MangaDetails_status);
        manga_chapterCount = (TextView)headerRowView.findViewById(R.id.tv_MangaDetails_chapterCount);

        smoothProgressBar = (SmoothProgressBar)view.findViewById(R.id.progress);

        //search mangareader list for this manga title and use it's manga_id
        //if it does not exist, use MangaEdens manga_id
        mangaTitle = getArguments().getString(TITLE_KEY);
        mangaId = getArguments().getString(ID_KEY);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mangaTitle);
        Toast.makeText(getActivity(), mangaTitle + "\n" + mangaId, Toast.LENGTH_SHORT).show();

        listView = (ListView)view.findViewById(R.id.lv_MangaChapters);
        listView.addHeaderView(headerRowView, null, false);
        listView.setHeaderDividersEnabled(false);

        fab = (FloatingActionButton) headerRowView.findViewById(R.id.mfab);
        fab.setScaleX(0);
        fab.setScaleY(0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainMangaDetailsObject != null) {
                    new getMangaFromTitle(getActivity()).insertMangaToDb(mainMangaDetailsObject);
                    view.animate().scaleX(0).scaleY(0).setInterpolator(new OvershootInterpolator()).setDuration(250);
                }
            }
        });

        populateViewsWithData = new PopulateViewsWithData(listView);

        if(savedInstanceState != null && savedInstanceState.containsKey(DETAILS_OBJECT)){
            Utilities.Log(LOG_TAG, "save instance not null");
            mainMangaDetailsObject = savedInstanceState.getParcelable(DETAILS_OBJECT);
            new PopulateViewsWithData(listView, mainMangaDetailsObject).execute();
        }else{
            MangaExists mangaExists = new MangaExists();
            mangaExists.execute(mangaTitle);
        }

        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ChapterAttrForAdapter ch = adapter.getItem(i - 1);

                Utilities.Log(LOG_TAG, "Id: " + ch.chapter_id);
                Utilities.Log(LOG_TAG, "Chapter: " + ch.chapter_title);

                final String name = searchListForMangaID(mangaTitle);
                String path = viewIfExistsOnDisk(name.replace("-", " "), ch.chapter_id, ch.chapter_title);

                if(path == null){

                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title("Chapter " + ch.chapter_id)
                            .positiveText("download")
                            .negativeText("read")
                            .neutralText("cancel")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    FetchMangaChapter fetchMangaChapter = new FetchMangaChapter(getActivity());
                                    fetchMangaChapter.execute(name, ch.chapter_id);
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    Toast.makeText(getActivity(), "No online reading yet", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNeutral(MaterialDialog dialog) {
                                    super.onNeutral(dialog);
                                    dialog.dismiss();
                                }
                            }).build();
                    dialog.show();

                }else {
                    File file = new File(path);
                    String[] picUris = new String[file.listFiles().length];

                    for(int y = 0; y < file.listFiles().length; y++){
                        picUris[y] = file.listFiles()[y].getAbsolutePath();
                    }

                    Intent intent = new Intent(getActivity(), MangaViewerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray(MangaViewerFragment.PIC_URLS, picUris);
                    bundle.putString("manga_title", name);

                    String chapter;
                    if(ch.chapter_title == null) {
                        chapter = "Chapter " + ch.chapter_id;
                    }else{
                        chapter = "c" + ch.chapter_id + " " + ch.chapter_title;
                    }

                    bundle.putString("chapter_title", chapter);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);

                }

            }
        });
    }



    @Override
    public void onStop() {
        super.onStop();

        if(exists)
            if (mangaId != null) Utilities.writeMangaPageToPrefs(getActivity(), mangaId, 0);

        if(getManga != null && getManga.getStatus() == AsyncTask.Status.RUNNING) getManga.cancel(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mainMangaDetailsObject != null) outState.putParcelable(DETAILS_OBJECT, mainMangaDetailsObject);
    }


    public String viewIfExistsOnDisk(String mangaName, String chapterId, String chapterTitle){

        String path = null;
        if(chapterTitle == null) chapterTitle = "Chapter";

        Utilities.Log(LOG_TAG, "View: " + chapterTitle + " id: " + chapterId);

        if(Utilities.externalStorageMounted()){

            String parentDir = Environment.getExternalStorageDirectory().toString();
            File parent = new File(parentDir + "/MangaTest");

            if(!parent.exists()) return null;

            File dir = new File("");
            File[] mangaDirs = parent.listFiles();

            for (File mangaDir : mangaDirs) {
                if (mangaDir.getName().equals(mangaName)) {
                    dir = new File(parent.getPath() + "/" + mangaName);
                    break;
                }
            }

            if(dir.exists()){
                File[] chapterDirs = dir.listFiles();
                for (File chapterDir : chapterDirs) {
                    if (chapterDir.getName().contains(chapterId)) {
                        path = chapterDir.getPath();
                        break;
                    }
                }
            }

        }

        if(path != null){
           File f = new File(path);
            if(f.exists()){
                return path;
            }
        }

        return null;

    }


    //Search for manga in the Mangalist databases, get its ID and start the manga's info download
    class Search extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return searchListForMangaID(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(Utilities.isOnline(getActivity())) {

                if (!s.isEmpty()) {
                    Utilities.Log(LOG_TAG, "Search Found: " + s);
                    getManga = new getMangaFromTitle(getActivity());
                    getManga.execute(s);
                } else {
                    Log.d(LOG_TAG, "MangaEden: " + mangaId);
                    FetchManga.getMangaFromID getMangaFromID = new FetchManga.getMangaFromID(getActivity());
//                    getMangaFromID.execute(mangaId);
                }

            }else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                smoothProgressBar.progressiveStop();
            }


        }
    }

    //Check if manga Exists in My Manga library
    class MangaExists extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            return mangaExistsInMyLibrary(strings[0]);
        }

        @Override
        protected void onPostExecute(Boolean mangaExists) {
            super.onPostExecute(mangaExists);
            showFab = mangaExists;
            if(!mangaExists){
                Search search = new Search();
                search.execute(mangaTitle);
            }else {
                Log.d("Manga Exists: ", mangaExists.toString());
                new PopulateViewsWithData(listView).execute();
            }
        }
    }

    // returns the manga_id given the manga name
    public String searchListForMangaID(String name){
        Uri mangaUri;
        String mName = "";
        int i;

        if(Utilities.getCurrentSource(getActivity()).equals(getResources().getString(R.string.pref_manga_reader))){
            mangaUri = Contract.MangaReaderMangaList.buildMangaInListWithNameUri(name);
            i=0;
        }else {
            mangaUri = Contract.MangaFoxMangaList.buildMangaInListWithNameUri(name);
            i=1;
        }

        Cursor cursor = getActivity().getContentResolver().query(mangaUri,
                new String[]{Contract.MangaFoxMangaList.COLUMN_MANGA_ID},
                null, null, null);

        if(cursor.moveToFirst()){
            mName = cursor.getString(0);
        }else {
            switch (i){
                case 0:{
                    mangaUri = Contract.MangaFoxMangaList.buildMangaInListWithNameUri(name);
                    cursor = getActivity().getContentResolver().query(mangaUri, null, null, null, null);
                    if(cursor.moveToFirst()) mName = cursor.getString(2);
                    break;
                }
                case 1: {
                    mangaUri = Contract.MangaReaderMangaList.buildMangaInListWithNameUri(name);
                    cursor = getActivity().getContentResolver().query(mangaUri, null, null, null, null);
                    if(cursor.moveToFirst()) mName = cursor.getString(2);
                    break;
                }

                default: mName = "";

            }
        }

        cursor.close();
        return mName;
    }


    public boolean mangaExistsInMyLibrary(String name){
        Uri uri = Contract.MyManga.buildMangaWithNameUri(name);
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            cursor.close();
            exists = true;
            return true;
        }

        exists = false;
        return false;
    }

   //.Get manga data (from Database or a detailsObject),
   //.convert the chapter list JSONArray to an arraylist of custom objects
   // then populate listView with the Array Adapter,
   //.populate other views with their respctive data

    public class PopulateViewsWithData extends AsyncTask<Void, Void, ArrayList<ChapterAttrForAdapter>>{
        ListView listView;
        MangaDetailsObject detailsObject;
        private PopulateViewsWithData(ListView view){
            listView = view;
        }

        private PopulateViewsWithData(ListView view, MangaDetailsObject detailsObject){
            listView = view;
            this.detailsObject = detailsObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listView.setAlpha(0f);
            showProgressBar();
        }

        @Override
        protected ArrayList<ChapterAttrForAdapter> doInBackground(Void... voids) {
            ArrayList<ChapterAttrForAdapter> chapters = new ArrayList<>();
            String[] projection = {Contract.MyManga.COLUMN_MANGA_NAME,
                    Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON,
                    Contract.MyManga.COLUMN_MANGA_AUTHOR,
                    Contract.MyManga.COLUMN_MANGA_COVER,
                    Contract.MyManga.COLUMN_MANGA_INFO,
                    Contract.MyManga.COLUMN_MANGA_STATUS,
                    Contract.MyManga.COLUMN_MANGA_LAST_UPDATE
                    };
            final int COLUMN_manga_NAME = 0;
            final int COLUMN_manga_CHAPTER_JSON = 1;
            final int author = 2;
            final int cover = 3;
            final int info = 4;
            final int status = 5;


            JSONArray array;

            if(detailsObject == null){
                Cursor cursor = getActivity().getContentResolver().query(Contract.MyManga.CONTENT_URI,
                        projection,
                        Contract.MyManga.COLUMN_MANGA_NAME + "=?",
                        new String[] {mangaTitle},
                        null);

                if(cursor != null && cursor.moveToFirst()){
                    String json = cursor.getString(COLUMN_manga_CHAPTER_JSON);
                    mangaAuthor = cursor.getString(author);
                    mangaInfo = cursor.getString(info);
                    mangaStatus = cursor.getString(status);
                    imgUrl = cursor.getString(cover);
                    Log.i(getClass().getSimpleName(), json);
                    try {

                        array = new JSONArray(json);
                        chapters = ChapterAttrForAdapter.fromJSON(array);

                        JSONObject object = array.getJSONObject(array.length() - 1);
                        chapterCount = object.getString("chapterId");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    cursor.close();
                }

            }else{
                String json = detailsObject.getChapters();
                mangaAuthor = detailsObject.getAuthor();
                mangaInfo = detailsObject.getInfo();
                mangaStatus = detailsObject.getStatus();
                imgUrl = detailsObject.getCover();
                Log.i(getClass().getSimpleName(), "json: " + json);
                try {

                    array = new JSONArray(json);
                    chapters = ChapterAttrForAdapter.fromJSON(array);

                    JSONObject object = array.getJSONObject(array.length() - 1);
                    chapterCount = object.getString("chapterId");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return chapters;
        }

        @Override
        protected void onPostExecute(ArrayList<ChapterAttrForAdapter> chapters) {
            super.onPostExecute(chapters);

            if(showFab)
                fab.setImageResource(R.drawable.ic_done_white_24dp);

            Log.d(LOG_TAG, " YOO " + chapters.size());

            adapter = new MangaChapterAdapter(getActivity(), chapters);
            listView.setAdapter(adapter);

            ImageLoader imageLoader = new ImageLoader(VolleySingletonClass.getInstance(getActivity()).getRequestQueue(),
                        new LruBitmapCache(getActivity()));

            coverImageView.setImageUrl(imgUrl, imageLoader);
            coverImageView.setPaletteHelper(new PaletteHelper() {
                @Override
                public void OnPaletteGenerated(final Palette palette, final int mangaColor, final int darkMangaColor) {
                    ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                            getMyColorUtils().getPrimaryColor(), mangaColor);
                    ValueAnimator anim2 = ValueAnimator.ofObject(new ArgbEvaluator(),
                            getMyColorUtils().getPrimaryDarkColor(), darkMangaColor);

                    colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            MainActivity.toolbarBig.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
                        }
                    });

                    anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            getMyColorUtils().setStatusBarColor((Integer)valueAnimator.getAnimatedValue());
                        }
                    });
                    colorAnimator.setDuration(200);
                    anim2.setDuration(150);
                    colorAnimator.start();
                    anim2.start();
                }
            });
            manga_author.setText(mangaAuthor);
            manga_info.setText(Html.fromHtml(mangaInfo));
            manga_status.setText(mangaStatus);
            manga_chapterCount.setText(chapterCount);

            loadFinished = true;
            listView.animate().alpha(1f).setDuration(200).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    hideProgressBar();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });


        }
    }

    public class getMangaFromTitle extends AsyncTask<String, Void, Integer>{

        Context context;
        Handler handler = new Handler();
        public final Boolean DEBUG = true;
        String result;

        public getMangaFromTitle(Context context){
            this.context = context;
        }

        String LOG_TAG = getClass().getSimpleName();

        HttpURLConnection httpURLConnection;
        HttpResponseCache httpResponseCache;
        BufferedReader bufferedReader;

        @Override
        protected void onPostExecute(Integer internets) {
            super.onPostExecute(internets);
            hideProgressBar();
            if(internets == 1){
                new PopulateViewsWithData(listView, mainMangaDetailsObject).execute();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                URL baseUrl;

                if(Utilities.getCurrentSource(context).equals(context.getString(R.string.pref_manga_reader))){
                    baseUrl = new URL
                            ("https://doodle-manga-scraper.p.mashape.com/mangareader.net/manga/" + strings[0] + "/");
                }else {
                    baseUrl = new URL
                            ("https://doodle-manga-scraper.p.mashape.com/mangafox.me/manga/" + strings[0] + "/");
                }

                httpURLConnection = (HttpURLConnection)baseUrl.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.addRequestProperty("X-Mashape-Key", "8Fp0bd39gLmshw7qSKtW61cjlK6Ip1V1Z5Fjsnhpy813RcQflk");
                httpURLConnection.connect();

                int statusCode = httpURLConnection.getResponseCode();

                if(statusCode != 200) return -1;



                Log.i(LOG_TAG, "Status Code: " + statusCode);

                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }

                result = stringBuilder.toString();

                Log.d(LOG_TAG, result);

                mainMangaDetailsObject = processResults(result);


            } catch (MalformedURLException e) {
                if(DEBUG) e.printStackTrace();

                Log.d(LOG_TAG, e.getMessage());

            } catch (IOException e) {
                if(DEBUG) e.printStackTrace();

                Log.d(LOG_TAG, e.getMessage());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "No internet Connection", Toast.LENGTH_LONG).show();
                    }
                });

                return 0;

            }

            finally {
                if(httpURLConnection != null) httpURLConnection.disconnect();

                if(bufferedReader != null) try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return 1;
        }


        public MangaDetailsObject processResults(String string){
            String MANGA_NAME = "name",
                    MANGA_AUTHOR = "author",
                    MANGA_INFO = "info",
                    MANGA_STATUS = "status",
                    MANGA_COVER = "cover",
                    MANGA_LAST_UPDATE = "lastUpdate",
                    MANGA_CHAPTERS = "chapters";

            MangaDetailsObject mangaDetailsObject = new MangaDetailsObject();

            try {
                String name, status, info, cover, author, chapters, lastUpdate;
                JSONObject mainJsonObject = new JSONObject(string);

                name = mainJsonObject.getString(MANGA_NAME);
                mangaDetailsObject.setName(name);

                if(!mainJsonObject.has(MANGA_STATUS))status = "wakaranai";
                else status = mainJsonObject.getString(MANGA_STATUS);
                mangaDetailsObject.setStatus(status);


                if(!mainJsonObject.has(MANGA_INFO))info = "No description";
                else info = mainJsonObject.getString(MANGA_INFO);
                mangaDetailsObject.setInfo(info);


                if(!mainJsonObject.has(MANGA_AUTHOR))author = "Some Mangaka";
                else {
                    try {
                        author = mainJsonObject.getJSONArray(MANGA_AUTHOR).getString(0);
                    }catch(NullPointerException | ArrayIndexOutOfBoundsException | JSONException e){
                        try {
                            author = mainJsonObject.getJSONArray("artist").getString(0);
                        }catch (NullPointerException | ArrayIndexOutOfBoundsException | JSONException ex){
                            author = "Some Mangaka";
                        }
                    }
                }
                mangaDetailsObject.setAuthor(author);


                if(!mainJsonObject.has(MANGA_LAST_UPDATE)) lastUpdate = "";
                else lastUpdate = mainJsonObject.getString(MANGA_LAST_UPDATE);
                mangaDetailsObject.setLastUpdate(lastUpdate);

                cover = mainJsonObject.getString(MANGA_COVER);
                chapters = mainJsonObject.getJSONArray(MANGA_CHAPTERS).toString();
                mangaDetailsObject.setCover(cover);
                mangaDetailsObject.setChapters(chapters);


                if(DEBUG) {
                    Log.d("Manga Chapters: ", chapters);
                    Log.d("Manga author: ", author);
                    Log.d("Manga lastUpdate: ", lastUpdate);
                    Log.d("Manga Name: ", name);
                    Log.d("Manga status: ", status);
                    Log.d("Manga info: ", info);
                    Log.d("Manga cover: ", cover);
                }

                if(info == null || author == null || name == null){
                    name = "...";
                    info = "No info.";
                    author = "Some great mangaka";

                }


            } catch (JSONException e) {
                if(DEBUG) e.printStackTrace();
            }

            return mangaDetailsObject;

        }

         void insertMangaToDb(MangaDetailsObject mangaDetailsObject) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.MyManga.COLUMN_MANGA_NAME, mangaDetailsObject.getName());
             contentValues.put(Contract.MyManga.COLUMN_MANGA_ID, mangaId);
            contentValues.put(Contract.MyManga.COLUMN_MANGA_AUTHOR, mangaDetailsObject.getAuthor());
            contentValues.put(Contract.MyManga.COLUMN_MANGA_INFO, mangaDetailsObject.getInfo());
            contentValues.put(Contract.MyManga.COLUMN_MANGA_COVER, mangaDetailsObject.getCover());
            contentValues.put(Contract.MyManga.COLUMN_MANGA_STATUS, mangaDetailsObject.getStatus());
            contentValues.put(Contract.MyManga.COLUMN_MANGA_CHAPTER_JSON, mangaDetailsObject.getChapters());
             contentValues.put(Contract.MyManga.COLUMN_MANGA_SOURCE, Utilities.getCurrentSource(getActivity()));
            contentValues.put(Contract.MyManga.COLUMN_MANGA_LAST_UPDATE, mangaDetailsObject.getLastUpdate());

             JSONArray array;
             try {
                 array = new JSONArray(mangaDetailsObject.getChapters());
                 JSONObject object = array.getJSONObject(array.length() - 1);
                 contentValues.put(Contract.MyManga.COLUMN_MANGA_LATEST_CHAPTER, object.getString("chapterId"));
             } catch (JSONException e) {
                 e.printStackTrace();
             }


             Uri uri = context.getContentResolver().insert(Contract.MyManga.CONTENT_URI, contentValues);


            if(uri != null) {
                Log.i("URI", uri.getPath());
                Cursor cursor = context.getContentResolver().query(
                        Contract.MyManga.CONTENT_URI.buildUpon().appendPath(uri.getPath()).build(), null, null, null, null);
                if(cursor.moveToFirst()){
                    Log.d("Cursor test: ", "SOMETHING BOII");
                    Log.d("Cursor test: ", cursor.getString(0) + cursor.getString(1));
                }

                cursor.close();
            }else{
                Log.d("Cursor test: ", "failed");
            }
        }

    }

    private void hideProgressBar() {
        smoothProgressBar.progressiveStop();
        smoothProgressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        smoothProgressBar.setVisibility(View.VISIBLE);
        smoothProgressBar.progressiveStart();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int visibleItemCount, int totalItemCount) {

        if(visibleItemCount > 0 && i == 0){
            final View view = absListView.getChildAt(0);
            int viewHeight = view.getHeight();

            if(view.getBottom() < viewHeight/2){
                if(fab.getX() != 0f || fab.getY() != 0f)
                fab.animate().scaleX(0).scaleY(0).setDuration(250).setInterpolator(new FastOutLinearInInterpolator());
            }else{
                if(fab.getX() != 1f || fab.getY() != 1f)
                fab.animate().scaleX(1).scaleY(1).setDuration(250).setInterpolator(new OvershootInterpolator());
            }

        }
    }



}


