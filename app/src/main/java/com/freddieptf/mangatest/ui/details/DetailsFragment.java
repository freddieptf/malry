package com.freddieptf.mangatest.ui.details;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.manga.chapters.ChapterLocalSource;
import com.freddieptf.mangatest.data.model.Chapter;
import com.freddieptf.mangatest.data.model.ChapterPages;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.data.service.ChapterDownloadService;
import com.freddieptf.mangatest.ui.reader.ReaderActivity;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by fred on 1/30/15.
 */
public class DetailsFragment extends Fragment implements DetailsView, ChapterAdapter.ChapterClickCallback {

    RecyclerView recyclerView;
    ImageView coverImageView;
    SmoothProgressBar smoothProgressBar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatingActionButton;

    DetailsPresenter detailsPresenter;
    ChapterAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manga_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coverImageView = (ImageView) view.findViewById(R.id.iv_MangaDetailsCover);
        coverImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.image_tint),
                PorterDuff.Mode.SRC_ATOP);
        smoothProgressBar = (SmoothProgressBar) view.findViewById(R.id.progress);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitle(getActivity().getIntent().getStringExtra(DetailsActivity.TITLE_KEY));
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);

        adapter = new ChapterAdapter(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.lv_MangaChapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsPresenter.save();
            }
        });
    }

    @Override
    public void setPresenter(DetailsPresenter detailsPresenter) {
        this.detailsPresenter = detailsPresenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        detailsPresenter.start();
        floatingActionButton.hide();
    }

    @Override
    public void onDataLoad(MangaDetails mangaDetails) {
        if (mangaDetails != null) {
            if (mangaDetails.getId() == null) floatingActionButton.show();
            Glide.with(this).load(mangaDetails.getCover()).crossFade(250).into(coverImageView);
            adapter.swapData(mangaDetails);
        } else {
            Snackbar.make(recyclerView, "Something went wrong while trying to fetch the data", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public void onDataSaved() {
        floatingActionButton.hide();
        Snackbar.make(recyclerView, "This Manga has been saved to your Library", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            smoothProgressBar.setVisibility(View.VISIBLE);
            smoothProgressBar.progressiveStart();
        } else {
            smoothProgressBar.setVisibility(View.GONE);
            smoothProgressBar.progressiveStop();
        }
    }

    @Override
    public void onChapterClicked(final Chapter ch, final String mangaId, final String source) {
        ChapterPages chapterPages = new ChapterLocalSource().getChapter(source, mangaId, ch, getContext());
        if (chapterPages == null) {
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("MangaChapter " + ch.chapterId)
                    .positiveText("download")
                    .negativeText("read")
                    .neutralText("cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            Intent intent = new Intent(getActivity(), ChapterDownloadService.class);
                            intent.putExtra(ChapterDownloadService.MANGA_NAME, mangaId);
                            intent.putExtra(ChapterDownloadService.MANGA_CHAPTER, ch.chapterId);
                            intent.putExtra(ChapterDownloadService.MANGA_SOURCE, source);
                            getActivity().startService(intent);
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

        } else {
            Intent intent = new Intent(getActivity(), ReaderActivity.class);
            intent.putExtra(ReaderActivity.CHAPTER_BOII, chapterPages);
            startActivity(intent);
        }

    }

    @Override
    public void onError() {

    }

}


