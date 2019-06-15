package com.freddieptf.malry.data.utils;

import com.freddieptf.malry.commons.AlphanumComparator;
import com.freddieptf.malry.data.db.models.Chapter;

import java.util.Comparator;

/**
 * Created by freddieptf on 9/10/18.
 */

public class ChapterTitleComparator implements Comparator<Chapter> {

    private AlphanumComparator alphanumComparator = new AlphanumComparator();

    @Override
    public int compare(Chapter chapter, Chapter t1) {
        return alphanumComparator.compare(chapter.getTitle(), t1.getTitle());
    }
}
