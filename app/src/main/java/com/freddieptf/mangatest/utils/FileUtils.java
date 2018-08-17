package com.freddieptf.mangatest.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by freddieptf on 25/10/16.
 */

public class FileUtils {

    public static String APP_DIR = "MyManga";

    public static String getMangaChapterDir(String mangaName, String chapter) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + APP_DIR + File.separator + mangaName + File.separator + chapter;
    }

}
