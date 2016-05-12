package com.freddieptf.mangatest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.beans.ChapterAttrs;

import java.util.ArrayList;

/**
 * Created by fred on 2/9/15.
 */
public class MangaChapterAdapter extends ArrayAdapter<ChapterAttrs> implements View.OnClickListener {

    ArrayList<ChapterAttrs> objects;
    OnChapterClicked onChapterClicked;

    public MangaChapterAdapter(Context context, ArrayList<ChapterAttrs> objects) {
        super(context, R.layout.list_detail_chapter_item, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChapterAttrs chapter = getItem(position);

        ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_detail_chapter_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else viewHolder = (ViewHolder) convertView.getTag();

        convertView.setOnClickListener(this);
        viewHolder.mangaID.setText(chapter.chapter_id);
        viewHolder.mangaID.setTag(chapter);
        String ch = chapter.chapter_title;
        if(ch == null || ch.trim().isEmpty()){
            ch = "Chapter " + chapter.chapter_id;
        }
        viewHolder.mangaTitle.setText(ch);

        return convertView;
    }

    @Override
    public void onClick(View view) {
        ChapterAttrs chapter = (ChapterAttrs) view.findViewById(R.id.tv_Chapter_id).getTag();
        onChapterClicked.onChapterClicked(chapter);
    }

    public void setOnChapterClickedListener(OnChapterClicked onChapterClicked){
        this.onChapterClicked = onChapterClicked;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public ChapterAttrs getItem(int position) {
        return objects.get(position);
    }


    public interface OnChapterClicked{
        void onChapterClicked(ChapterAttrs chapterAttrs);
    }

    public static class ViewHolder {
        TextView mangaID, mangaTitle;

        public ViewHolder(View view) {
            mangaID = (TextView) view.findViewById(R.id.tv_Chapter_id);
            mangaTitle = (TextView) view.findViewById(R.id.tv_Chapter_title);
        }
    }


}
