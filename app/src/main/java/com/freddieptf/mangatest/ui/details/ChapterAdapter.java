package com.freddieptf.mangatest.ui.details;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.Chapter;
import com.freddieptf.mangatest.data.model.MangaDetails;

/**
 * Created by fred on 2/9/15.
 */
public class ChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Chapter[] chapters;
    private MangaDetails mangaDetails;
    private ChapterClickCallback chapterClickCallback;

    public ChapterAdapter(ChapterClickCallback chapterClickCallback) {
        this.chapterClickCallback = chapterClickCallback;
    }

    public void swapData(MangaDetails mangaDetails) {
        this.mangaDetails = mangaDetails;
        chapters = mangaDetails.getChapters();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        chapterClickCallback.onChapterClicked(chapters[(Integer) view.getTag()],
                mangaDetails.getId(), mangaDetails.getSource());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new MangaDetailsViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_detail_header_item, parent, false));

        return new ChapterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_detail_chapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position > 0) {
            ((ChapterViewHolder) holder).bind(chapters[position - 1]);
            holder.itemView.setTag(position - 1);
            holder.itemView.setOnClickListener(this);
        } else {
            ((MangaDetailsViewHolder) holder).bind(mangaDetails);
        }
    }

    @Override
    public int getItemCount() {
        return chapters == null ? 0 : chapters.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 0;
    }

    public interface ChapterClickCallback {
        void onChapterClicked(Chapter chapter, String id, String source);
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterId, chapterTitle;

        public ChapterViewHolder(View view) {
            super(view);
            chapterId = (TextView) view.findViewById(R.id.tv_Chapter_id);
            chapterTitle = (TextView) view.findViewById(R.id.tv_Chapter_title);
        }

        public void bind(Chapter chapter) {
            chapterId.setText(chapter.chapterId);
            chapterTitle.setText(chapter.chapterTitle);
        }
    }

    public static class MangaDetailsViewHolder extends RecyclerView.ViewHolder {
        TextView author, status, info, chapterCount;

        public MangaDetailsViewHolder(View view) {
            super(view);
            author = (TextView) view.findViewById(R.id.tv_MangaDetails_author);
            info = (TextView) view.findViewById(R.id.tv_MangaDetails_info);
            status = (TextView) view.findViewById(R.id.tv_MangaDetails_status);
            chapterCount = (TextView) view.findViewById(R.id.tv_MangaDetails_chapterCount);
        }

        public void bind(MangaDetails mangaDetails) {
            author.setText(mangaDetails.getAuthor());
            info.setText(mangaDetails.getInfo());
            status.setText(mangaDetails.getStatus());
            chapterCount.setText(mangaDetails.getChapters()[0].chapterId);
        }
    }


}
