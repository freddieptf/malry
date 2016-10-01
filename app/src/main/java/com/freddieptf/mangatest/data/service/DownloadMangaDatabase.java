package com.freddieptf.mangatest.data.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.remote.ApiTask;
import com.freddieptf.mangatest.data.remote.ApiTaskFactory;
import com.freddieptf.mangatest.utils.Utilities;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fred on 7/24/15.private
 */
public class DownloadMangaDatabase extends Service {

    public static final String OP = "operation";
    public static final String STATUS = "status";
    public static final String FIX_SELECTION = "fix_selection";
    public static final String FIX_MULTIPLE_SELECTION = "fix_multiple_selection";
    final int NOTIFICATION_ID = 1002;
    private final String LOG_TAG = getClass().getSimpleName();
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new NotificationCompat.Builder(getBaseContext());
        notificationManager = (NotificationManager) getBaseContext().getSystemService(NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(Utilities.isOnline(this)) {

            ExecutorService service = Executors.newFixedThreadPool(2);
            ApiTaskFactory worker = ApiTaskFactory.getInstance();
            WeakReference<Context> context = new WeakReference<>(getBaseContext());

            if (intent.getExtras() != null) {
                if(intent.getExtras().containsKey(FIX_SELECTION)) {
                    switch (intent.getIntExtra(FIX_SELECTION, 100)) {
                        case ApiTask.READER_MANGA_LIST:
                            service.submit(worker.createApiTask(context, ApiTask.READER_MANGA_LIST));
                            service.submit(worker.createApiTask(context, ApiTask.READER_LATEST_LIST));
                            service.submit(worker.createApiTask(context, ApiTask.READER_POPULAR_LIST));
                            break;
                        case ApiTask.FOX_MANGA_LIST:
                            service.submit(worker.createApiTask(context, ApiTask.FOX_MANGA_LIST));
                            break;
                        case ApiTask.ALL_LIST:
                        default:
                            service.submit(worker.createApiTask(context, ApiTask.READER_MANGA_LIST));
                            service.submit(worker.createApiTask(context, ApiTask.READER_LATEST_LIST));
                            service.submit(worker.createApiTask(context, ApiTask.READER_POPULAR_LIST));
                            service.submit(worker.createApiTask(context, ApiTask.FOX_MANGA_LIST));
                            Utilities.Log(LOG_TAG, "default");
                            break;
                    }
                    // TODO: 18/09/16 no need for this else statement..clean up thos gaddamn options
                }else if(intent.getExtras().containsKey(FIX_MULTIPLE_SELECTION)){
                    service.submit(worker.createApiTask(context, intent.getIntArrayExtra(FIX_MULTIPLE_SELECTION)));
                }
            }
            service.shutdown();
        }
        else{
            Utilities.Log(LOG_TAG, "No Internets breh");
        }

        return START_NOT_STICKY;
    }

    private void init(){
        builder.setContentTitle(getString(R.string.app_name))
                .setProgress(0, 0, true)
                .setContentText("Preparing to download databases")
                .setSmallIcon(R.drawable.ic_stat_maps_local_library);
    }
}
