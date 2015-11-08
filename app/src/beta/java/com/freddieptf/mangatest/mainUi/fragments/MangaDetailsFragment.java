package com.freddieptf.mangatest.mainUi.fragments;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.toolbox.ImageLoader;
import com.freddieptf.mangatest.api.FetchMangaChapter;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.adapters.MangaChapterAdapter;
import com.freddieptf.mangatest.api.GetManga;
import com.freddieptf.mangatest.beans.ChapterAttrs;
import com.freddieptf.mangatest.beans.MangaDetailsObject;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.mainUi.MainActivity;
import com.freddieptf.mangatest.mainUi.MangaViewerActivity;
import com.freddieptf.mangatest.mainUi.baseUi.BaseFragment;
import com.freddieptf.mangatest.service.MangaDetailsRequestService;
import com.freddieptf.mangatest.utils.PaletteHelper;
import com.freddieptf.mangatest.utils.Utilities;
import com.freddieptf.mangatest.volleyStuff.FadeInNetworkImageView;
import com.freddieptf.mangatest.volleyStuff.LruBitmapCache;
import com.freddieptf.mangatest.volleyStuff.VolleySingletonClass;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by fred on 1/30/15.
 */
public class MangaDetailsFragment extends BaseFragment implements ListView.OnScrollListener,
       MangaChapterAdapter.OnChapterClicked {

    String mangaTitle, mangaId, source, imgUrl, mangaAuthor, mangaInfo, mangaStatus, chapterCount;
    public static String DIS_FRAGMENT = "Details";
    public static final String TITLE_KEY = "manga_title";
    public static final String ID_KEY = "manga_id";
    public static final String SOURCE_KEY = "source";
    public static final String COVER_URL = "URL";
    public static final String DETAILS_OBJECT = "details_object";
    public final String MANGA_COLOR = "mc";
    public final String DARK_MANGA_COLOR = "dmc";

    MangaChapterAdapter adapter;
    boolean showFab;
    ListView listView;
    FloatingActionButton fab;
    boolean exists;
    FadeInNetworkImageView coverImageView;
    TextView manga_author, manga_status, manga_info, manga_chapterCount;
    SmoothProgressBar smoothProgressBar;
    String LOG_TAG = getClass().getSimpleName();
    public static PopulateViewsWithData populateViewsWithData;
    MangaDetailsObject cacheMangaDetailsObject;
    public int myMangaColor = -1, myDarkMangaColor = -1;
    boolean animate;


    public MangaDetailsFragment(){
        setRetainInstance(false);
    }

    @Override
    protected boolean showToolBarWithDefaultAppColor() {
        return false;
    }

    @Override
    protected int useNavigationIcon() {
        return R.drawable.abc_ic_ab_back_mtrl_am_alpha;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_details, container, false);
    }

    @Override
    protected boolean lockDrawer() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View headerRowView = LayoutInflater.from(getActivity()).inflate(R.layout.list_detail_header_item, null);

        coverImageView = (FadeInNetworkImageView)view.findViewById(R.id.iv_MangaDetailsCover);
        manga_author = (TextView)headerRowView.findViewById(R.id.tv_MangaDetails_author);
        manga_info = (TextView)headerRowView.findViewById(R.id.tv_MangaDetails_info);
        manga_status = (TextView)headerRowView.findViewById(R.id.tv_MangaDetails_status);
        manga_chapterCount = (TextView)headerRowView.findViewById(R.id.tv_MangaDetails_chapterCount);
        smoothProgressBar = (SmoothProgressBar)view.findViewById(R.id.progress);

        mangaTitle = getArguments().getString(TITLE_KEY);
        mangaId = getArguments().getString(ID_KEY);
        source = getArguments().getString(SOURCE_KEY);

        if(source == null){
            Uri uri = Contract.MyManga.buildMangaWithNameUri(mangaTitle);
            Cursor c = getActivity().getContentResolver().query(uri,
                    new String[]{Contract.MyManga._ID, Contract.MyManga.COLUMN_MANGA_SOURCE},
                    null, null, null);
            if(c != null && c.moveToFirst()) {
                source = c.getString(1);
                c.close();
            }
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mangaTitle);
        Toast.makeText(getActivity(), mangaTitle + "\n" + mangaId + "\n" + source, Toast.LENGTH_SHORT).show();

        listView = (ListView)view.findViewById(R.id.lv_MangaChapters);
        listView.addHeaderView(headerRowView, null, false);
        listView.setHeaderDividersEnabled(false);

        fab = (FloatingActionButton) headerRowView.findViewById(R.id.mfab);
        fab.setScaleX(0);
        fab.setScaleY(0);

        populateViewsWithData = new PopulateViewsWithData(listView);
        MangaExists mangaExists = new MangaExists();

        if(savedInstanceState != null && savedInstanceState.containsKey(DETAILS_OBJECT)){
            cacheMangaDetailsObject = savedInstanceState.getParcelable(DETAILS_OBJECT);
            new PopulateViewsWithData(listView, cacheMangaDetailsObject).execute();
        }else {
            mangaExists.execute(mangaTitle);
        }

        if(savedInstanceState != null
                && (savedInstanceState.containsKey(MANGA_COLOR) && savedInstanceState.containsKey(DARK_MANGA_COLOR))){
            getMyColorUtils().setStatusBarColor(savedInstanceState.getInt(DARK_MANGA_COLOR));
            MainActivity.toolbarBig.setBackgroundColor(savedInstanceState.getInt(MANGA_COLOR));
            animate = false;
        }else {
            animate = true;
        }

        listView.setOnScrollListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cacheMangaDetailsObject != null) {
                    GetManga.insertToLibrary(getActivity(), cacheMangaDetailsObject, mangaId, source);
                    view.animate().scaleX(0).scaleY(0).setInterpolator(new OvershootInterpolator()).setDuration(250);
                }
            }
        });

    }

    @Override
    public void onChapterClicked(final ChapterAttrs ch) {
        final String name = mangaId;
        String path = viewIfOnDisk(name.replace("-", " "), ch.chapter_id, ch.chapter_title);

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
                            fetchMangaChapter.execute(name, ch.chapter_id, source);
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

            for(int y = 0; y < file.listFiles().length; y++)
                picUris[y] = file.listFiles()[y].getAbsolutePath();

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

    @Override
    public void onStop() {
        super.onStop();
        if(exists)
            if (mangaId != null) Utilities.writeMangaPageToPrefs(getActivity(), mangaId, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(MangaDetailsRequestService.LOG_TAG));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(cacheMangaDetailsObject != null) outState.putParcelable(DETAILS_OBJECT, cacheMangaDetailsObject);
        if(cacheMangaDetailsObject == null) outState.putString("Faker", "Running");

        if(myMangaColor != -1 && myDarkMangaColor != -1){
            outState.putInt(MANGA_COLOR, myMangaColor);
            outState.putInt(DARK_MANGA_COLOR, myDarkMangaColor);
        }
    }


    public String viewIfOnDisk(String mangaName, String chapterId, String chapterTitle){

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
                if(Utilities.isOnline(getActivity())) {
                    showProgressBar();
                    Intent intent = new Intent(getActivity(), MangaDetailsRequestService.class);
                    intent.putExtra("ID", mangaId);
                    intent.putExtra("SOURCE", source);
                    getActivity().startService(intent);
                }else Toast.makeText(getActivity(), "You connected to the internets bro?", Toast.LENGTH_LONG).show();
            }else {
                Log.d("Manga Exists: ", mangaExists.toString());
                new PopulateViewsWithData(listView).execute();
            }
        }
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

   //.Get manga data (from Database or a MangadetailsObject),
   //.convert the chapter list JSONArray to an arraylist of custom objects
   // then populate listView with the Array Adapter,
   //.populate other views with their respctive data

    public class PopulateViewsWithData extends AsyncTask<Void, Void, ArrayList<ChapterAttrs>>{
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
        protected ArrayList<ChapterAttrs> doInBackground(Void... voids) {
            ArrayList<ChapterAttrs> chapters = new ArrayList<>();
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
                        chapters = ChapterAttrs.fromJSON(array);

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
                Log.i(LOG_TAG, "json: " + json);
                try {

                    array = new JSONArray(json);
                    chapters = ChapterAttrs.fromJSON(array);

                    JSONObject object = array.getJSONObject(array.length() - 1);
                    chapterCount = object.getString("chapterId");

                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            return chapters;
        }

        @Override
        protected void onPostExecute(ArrayList<ChapterAttrs> chapters) {
            super.onPostExecute(chapters);

            if(chapters != null) {
                if (showFab) fab.setImageResource(R.drawable.ic_done_white_24dp);

                Log.d(LOG_TAG, "chapters: " + chapters.size());
                adapter = new MangaChapterAdapter(getActivity(), chapters);
                adapter.setOnChapterClickedListener(MangaDetailsFragment.this);
                listView.setAdapter(adapter);

                ImageLoader imageLoader = new ImageLoader(VolleySingletonClass.getInstance(getActivity()).getRequestQueue(),
                        new LruBitmapCache(getActivity()));

                coverImageView.setImageUrl(imgUrl, imageLoader);
                coverImageView.setPaletteHelper(new PaletteHelper() {
                    @Override
                    public void OnPaletteGenerated(final Palette palette, final int mangaColor, final int darkMangaColor) {
                        myMangaColor = mangaColor;
                        myDarkMangaColor = darkMangaColor;
                        if (animate) {
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
                                    getMyColorUtils().setStatusBarColor((Integer) valueAnimator.getAnimatedValue());
                                }
                            });
                            colorAnimator.setDuration(200);
                            anim2.setDuration(150);
                            colorAnimator.start();
                            anim2.start();
                        }
                    }
                });

                manga_author.setText(mangaAuthor);
                manga_info.setText(Html.fromHtml(mangaInfo));
                manga_status.setText(mangaStatus);
                manga_chapterCount.setText(chapterCount);

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
    }

    void hideProgressBar() {
        smoothProgressBar.progressiveStop();
        smoothProgressBar.setVisibility(View.GONE);
    }

    void showProgressBar() {
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


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cacheMangaDetailsObject = intent.getParcelableExtra(DETAILS_OBJECT);
            if(cacheMangaDetailsObject != null)
                new PopulateViewsWithData(listView, cacheMangaDetailsObject).execute();
        }
    };



}


