package com.freddieptf.mangatest.api.mangafox;

import com.freddieptf.mangatest.beans.MangaInfoBean;
import com.freddieptf.mangatest.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 8/9/15.
 */
public class Processor {

    final String MANGA_ID = "mangaId";
    final String MANGA_NAME = "name";

    public List<MangaInfoBean> processList(String result){
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
}
