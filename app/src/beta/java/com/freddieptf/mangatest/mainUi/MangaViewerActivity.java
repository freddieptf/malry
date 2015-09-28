package com.freddieptf.mangatest.mainUi;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.mainUi.fragments.MangaGridFragment;
import com.freddieptf.mangatest.mainUi.fragments.MangaViewerFragment;
import com.freddieptf.mangatest.utils.Utilities;

/**
 * Created by fred on 3/24/15.
 */
public class MangaViewerActivity extends AppCompatActivity {


    int posFromPrefs;
    final String LOG_TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_viewer);


        Bundle bundle = getIntent().getBundleExtra("bundle");
        String mangaTitle = bundle.getString("manga_title");
        try{
            String chapterTitle = bundle.getString("chapter_title");
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

        if(posFromPrefs == 0){
            posFromPrefs = -1;
            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.container, new MangaGridFragment()).commit();
        } else if(posFromPrefs == -1){}
        else{
            MangaViewerFragment mangaViewerFragment = new MangaViewerFragment();
            Bundle b = new Bundle();
            b.putInt("pos", posFromPrefs);
            mangaViewerFragment.setArguments(b);
            getSupportFragmentManager()
                    .beginTransaction().replace(R.id.container, mangaViewerFragment, "viewer").commit();
        }

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
