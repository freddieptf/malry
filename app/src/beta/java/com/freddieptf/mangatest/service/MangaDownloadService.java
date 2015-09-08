package com.freddieptf.mangatest.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.beans.NetworkChapterAttrs;
import com.freddieptf.mangatest.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by fred on 2/25/15.
 */
public class MangaDownloadService extends IntentService {

    public static final String ARRAY_LIST = "array_list";
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    final int NOTIFICATION_ID = 1001;
    public static final String FILTER = "com.freddieptf.mangatest";
    private final String LOG_TAG = getClass().getSimpleName();
    File parent;

    public MangaDownloadService() {
        super("MangaDownloadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(LOG_TAG, "Handling intent");
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(getApplicationContext());


        final ArrayList<NetworkChapterAttrs> mangaChapterAttrs = (ArrayList<NetworkChapterAttrs>) intent.getExtras().get(ARRAY_LIST);
        final String parentDirectory = Environment.getExternalStorageDirectory().toString();
        final String manga = mangaChapterAttrs.get(0).getName();

        final String chapter = mangaChapterAttrs.get(0).getChapter();
        final String chapterTitle = mangaChapterAttrs.get(0).getChapterTitle();

        builder.setContentTitle(manga + " " + chapter).setSmallIcon(R.drawable.ic_stat_maps_local_library);
        builder.setContentText("connecting...").setProgress(0, 0, true);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        if (Utilities.externalStorageMounted()) {

            parent = new File(parentDirectory + "/MangaTest/" + manga + "/" + chapter + ": " + chapterTitle);
            ArrayList<ImageData> imageDataArrayList = new ArrayList<>();

            if (!parent.exists() || parent.listFiles().length != mangaChapterAttrs.size() - 1) {
                parent.mkdirs();

                for (int i = 1; i < mangaChapterAttrs.size(); i++) {
                    Log.i("Page " + i, mangaChapterAttrs.get(i).getImageUrl());
                    ImageData imageData = new ImageData();
                    imageData.setImageUrl(mangaChapterAttrs.get(i).getImageUrl());
                    imageData.setPageId(mangaChapterAttrs.get(i).getPageId());
                    imageDataArrayList.add(imageData);
                }

                Log.d(LOG_TAG + "1st size Test, ", "Size: " + imageDataArrayList.size());

                downloadStuff(imageDataArrayList);

            } else{
                Toast.makeText(getApplicationContext(), "Already there", Toast.LENGTH_SHORT).show();
            }

        }


    }


    @SafeVarargs
    protected final Void downloadStuff(ArrayList<ImageData>... arrayLists) {

        ArrayList<ImageData> imageDatas = arrayLists[0];
        Log.i(LOG_TAG + " 2nd Test DoInBacG", "Size: " + imageDatas.size());

        for(int i = 0; i < imageDatas.size(); i++){
            Bitmap bitmap = Utilities.DownloadBitmapFromUrl(imageDatas.get(i).getImageUrl());
            String pageId = imageDatas.get(i).getPageId();

            File file = new File(parent, pageId + ".jpg");

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG + " ImageRequest: ", e.toString());
            }

            builder.setContentText("downloading").setProgress(imageDatas.size(), i, false).setPriority(2);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

        builder.setContentText("Download complete.").setProgress(0, 0, false);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d(LOG_TAG, "Download Complete");

        Intent intent = new Intent(FILTER);
        intent.putExtra("DONE", "Download Complete!");
        sendBroadcast(intent);

        return null;
    }


    private class ImageData{
        String imageUrl, pageId;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getPageId() {
            return pageId;
        }

        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

    }



}
