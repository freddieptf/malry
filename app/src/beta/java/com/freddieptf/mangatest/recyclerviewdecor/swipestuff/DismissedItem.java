package com.freddieptf.mangatest.recyclerviewdecor.swipestuff;

import java.io.File;

/**
 * Created by fred on 7/20/15.
 */
public class DismissedItem {

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    File file;
    int pos;
}
