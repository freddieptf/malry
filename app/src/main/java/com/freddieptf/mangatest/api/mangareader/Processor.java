package com.freddieptf.mangatest.api.mangareader;

import com.freddieptf.mangatest.beans.MangaInfoBean;
import com.freddieptf.mangatest.beans.MangaLatestInfoBean;
import com.freddieptf.mangatest.beans.MangaPopularInfoBean;
import com.freddieptf.mangatest.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 7/30/15.
 */
public class Processor {

    private static final String MANGA_ID = "id";
    private static final String MANGA_NAME = "manga";
    private static final String MANGA_CHAPTER = "chapter";
    private static final String MANGA_RELEASE_DATE = "release_date";

    public List<MangaInfoBean> processMangaListJSON(String result) {
        List<MangaInfoBean> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(result);
            for(int i = 0; i < array.length(); i++){
                MangaInfoBean mangaInfoBean = new MangaInfoBean();
                JSONObject object = array.getJSONObject(i);
                mangaInfoBean.setManga_ID(object.getString(MANGA_ID));
                mangaInfoBean.setManga_NAME(object.getString(MANGA_NAME));
                list.add(mangaInfoBean);
            }
            int i = 0;
            for(MangaInfoBean m : list){
                i++;
                if(i < 10) Utilities.Log("Processsor", m.getManga_NAME());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MangaLatestInfoBean> processLatestListJSON(String result) {
        List<MangaLatestInfoBean> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(result);
            for(int i= 0; i < array.length(); i++){
                MangaLatestInfoBean latestInfoBean = new MangaLatestInfoBean();
                JSONObject object = array.getJSONObject(i);
                latestInfoBean.setDate(object.getString(MANGA_RELEASE_DATE));
                latestInfoBean.setMangaTitle(object.getString(MANGA_NAME));
                latestInfoBean.setChapter(Integer.parseInt(object.getString(MANGA_CHAPTER)));
                list.add(latestInfoBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MangaPopularInfoBean> processPopularListJSON(String result) {
        List<MangaPopularInfoBean> list = new ArrayList<>();
        return list;
    }
}
