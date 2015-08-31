package com.freddieptf.mangatest.beans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fred on 2/11/15.
 */
public class ChapterAttrs {

    public String chapter_id, chapter_title;

    public ChapterAttrs(JSONObject object){
        try {
            chapter_id = object.getString("chapterId");
            chapter_title = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static ArrayList<ChapterAttrs> fromJSON(JSONArray array){
        ArrayList<ChapterAttrs> chapters = new ArrayList<>();

           for(int i = array.length(); i >= 0; i--){
                try {
                     ChapterAttrs c = new ChapterAttrs(array.getJSONObject(i));
                     chapters.add(c);

                }catch (JSONException e) {
                        e.printStackTrace();
                    }

        }

        return chapters;
    }


}
