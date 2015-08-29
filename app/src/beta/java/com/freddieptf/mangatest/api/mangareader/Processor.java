package com.freddieptf.mangatest.api.mangareader;

import com.freddieptf.mangatest.beans.MangaInfoBean;
import com.freddieptf.mangatest.beans.MangaLatestInfoBean;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 7/30/15.
 */
public class Processor {

    private static final String UL_TAG_CLASS = "series_alpha";
    private static final String TR_TAG_CLASS = "c2";

    public static List<MangaInfoBean> processAlphabeticalListDocument(Document document) {
        Elements li_mangaList = document.select("ul." + UL_TAG_CLASS + " > li");
        List<MangaInfoBean> list = new ArrayList<>();
        Element e;
        for (Element element : li_mangaList) {
            MangaInfoBean mangaInfoBean = new MangaInfoBean();
            e = element.child(0);
            mangaInfoBean.setManga_ID(e.attr("href").replace("/", ""));
            mangaInfoBean.setManga_NAME(e.text());
            list.add(mangaInfoBean);
        }

        return list;
    }

    public static List<MangaLatestInfoBean> processLatestListDocument(Document document) {
        Elements tr_LatestElement = document.select("tr." + TR_TAG_CLASS);

        List<MangaLatestInfoBean> list = new ArrayList<>();

        for(Element element : tr_LatestElement){
            MangaLatestInfoBean latestInfoBean = new MangaLatestInfoBean();
            String date = element.select("td.c1").text();
            String mangaId = element.select("a.chapter").attr("href").replace("/", "");
            String mangaName = element.select("a.chapter").text();
            Elements el = element.getElementsByTag("a");
            String chapter = el.get(1).text();
            int ch = Integer.parseInt(chapter.substring(mangaName.length() + 1));

            latestInfoBean.setDate(date);
            latestInfoBean.setMangaId(mangaId);
            latestInfoBean.setMangaTitle(mangaName);
            latestInfoBean.setChapter(ch);
            list.add(latestInfoBean);
        }

        return list;

    }

    //@TODO processPopularListDocument
    public static List processPopularListDocument(Document document){
        return new ArrayList();
    }
}
