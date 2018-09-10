package com.freddieptf.mangatest;

import android.app.Application;

import com.freddieptf.reader.data.ReaderDataManager;
import com.squareup.leakcanary.LeakCanary;

import androidx.room.Room;

/**
 * Created by freddieptf on 22/09/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        AppDb appDb = Room.databaseBuilder(this, AppDb.class, "app.db")
                .allowMainThreadQueries()
                .build();
        ReaderDataManager.INSTANCE.use(appDb);

    }

}
