package com.freddieptf.mangatest.ui.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.manga.details.MangaDetailsLoader;
import com.freddieptf.mangatest.data.manga.details.MangaDetailsRepository;

/**
 * Created by freddieptf on 28/09/16.
 */

public class DetailsActivity extends AppCompatActivity {

    public static final String TITLE_KEY = "manga_title";
    public static final String ID_KEY = "manga_id";
    public static final String SOURCE_KEY = "source";

    Toolbar toolbar;
    String mangaTitle, mangaId, source;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mangaTitle = getIntent().getStringExtra(TITLE_KEY);
        mangaId = getIntent().getStringExtra(ID_KEY);
        source = getIntent().getStringExtra(SOURCE_KEY);

        DetailsFragment fragment = (DetailsFragment)
                getSupportFragmentManager().findFragmentById(R.id.details_fragment);

        if (fragment == null) {
            fragment = new DetailsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment, fragment).commit();
        }

        MangaDetailsRepository repository = MangaDetailsRepository.getInstance(this);
        MangaDetailsLoader loader = new MangaDetailsLoader(this, repository);
        loader.setRequestDetails(mangaId, mangaTitle, source);

        DetailsPresenter detailsPresenter =
                new DetailsPresenter(getSupportLoaderManager(), loader, repository, fragment);

    }

    public String getMangaTitle() {
        return mangaTitle;
    }

    public String getMangaId() {
        return mangaId;
    }

    public String getSource() {
        return source;
    }
}
