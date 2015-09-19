package com.freddieptf.mangatest.mainUi.widgets.genreview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.utils.MyColorUtils;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 9/18/15.
 */
public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder>
    implements View.OnClickListener{

    CharSequence[] genres = null;
    SharedPreferences sharedPreferences;
    List<String> pref_list;
    MyColorUtils myColorUtils;
    final String PREF_GENRE = "pref_genre";

    public void setStringSource(CharSequence[] genres){
        this.genres = genres;
    }

    public GenreAdapter(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        pref_list = new ArrayList<>();
        stringTolist(sharedPreferences.getString(PREF_GENRE, ""));
        myColorUtils = new MyColorUtils(context);
    }

    void stringTolist(String s){
        if(!s.isEmpty()) {
            String[] r = s.split(",");
            for (String t : r) {
                if (!t.isEmpty()) pref_list.add(t);
                Utilities.Log("string to list", t);
            }
        }
    }

    void processPos(int pos){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String store = "";
        if(!pref_list.isEmpty()){
            if(pref_list.contains(pos + "")){
                pref_list.remove(pos + "");
            }
            else {
                pref_list.add(pos + "");
            }
        } else{
            pref_list.add(pos + "");
        }
        for(String s : pref_list){
            Utilities.Log("process pos", s);
            store = store.concat(s + ",");
        }
        editor.putString(PREF_GENRE, store);
        editor.apply();
    }

    @Override
    public void onClick(View view) {
        Utilities.Log("onGenreClick", "pos: " + view.getTag());
        processPos((Integer) view.getTag());
        notifyItemChanged((Integer) view.getTag());
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GenreViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_genre_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        holder.textView.setText(genres[position]);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);

        if(pref_list.size() > 0 && pref_list.contains(position + "")){
            holder.textView.setChecked(true);
            holder.textView.setTextColor(Color.WHITE);
            holder.textView.setBackgroundColor(myColorUtils.getAccentColor());
        }

    }

    @Override
    public int getItemCount() {
        return genres.length;
    }

    class GenreViewHolder extends RecyclerView.ViewHolder{
        CheckedTextView textView;
        public GenreViewHolder(View itemView) {
            super(itemView);
            textView = (CheckedTextView) itemView.findViewById(R.id.g_text);
            textView.setChecked(false);
            textView.setTextColor(Color.DKGRAY);
            textView.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
