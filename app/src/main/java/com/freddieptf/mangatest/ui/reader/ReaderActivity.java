package com.freddieptf.mangatest.ui.reader;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.utils.Utilities;

/**
 * Created by fred on 3/24/15.
 */
public class ReaderActivity extends AppCompatActivity {

    public static final String CHAPTER_BOII = "chapter_pages";
    final String LOG_TAG = getClass().getSimpleName();
    int posFromPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_viewer);

        ChapterPages chapterPages = getIntent().getParcelableExtra(CHAPTER_BOII);
        String mangaTitle = chapterPages.getMangaName();
        String chapterTitle = chapterPages.getChapterTitle();
        try{
            posFromPrefs = Utilities.readMangaPageFromPrefs(this, chapterTitle);
            Utilities.Log(LOG_TAG, "" + posFromPrefs);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mangaTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        if(savedInstanceState != null && savedInstanceState.containsKey("pos"))
            posFromPrefs = savedInstanceState.getInt("pos");

        ReaderFragment readerFragment = new ReaderFragment();
        Bundle b = new Bundle();
        b.putInt("pos", posFromPrefs);
        readerFragment.setArguments(b);
        if(savedInstanceState == null)
            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.container, readerFragment, "viewer").commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manga_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pos", posFromPrefs);
    }
}
