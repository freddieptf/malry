package com.freddieptf.mangatest.beans;

import com.freddieptf.mangatest.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fred on 2/11/15.
 */
public class ChapterAttrs {

    final String LOG_TAG = getClass().getSimpleName();
    public String chapter_id, chapter_title;

    public ChapterAttrs(JSONObject object){
        try {
            chapter_id = object.getString("chapterId");
            chapter_title = object.getString("name");
        } catch (JSONException e) {
            Utilities.Log(LOG_TAG, e.getMessage());
        }


    }

    public static ArrayList<ChapterAttrs> fromJSON(JSONArray array){
        ArrayList<ChapterAttrs> chapters = new ArrayList<>();

           for(int i = array.length(); i >= 0; i--){
                try {
                     ChapterAttrs c = new ChapterAttrs(array.getJSONObject(i));
                     chapters.add(c);

                }catch (JSONException e) {
                    Utilities.Log("chapterAttrs", e.getMessage());
                    }

        }

        return chapters;
    }


}
