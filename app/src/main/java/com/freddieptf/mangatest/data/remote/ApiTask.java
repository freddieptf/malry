package com.freddieptf.mangatest.data.remote;

import android.content.Context;

import com.freddieptf.mangatest.utils.Utilities;

import java.lang.ref.WeakReference;

/**
 * Created by fred on 8/24/15.
 */
public class ApiTask extends Thread {
    public static final int READER_MANGA_LIST = 1;
    public static final int FOX_MANGA_LIST = 2;
    public static final int READER_LATEST_LIST = 3;
    public static final int READER_POPULAR_LIST = 4;
    public static final int ALL_LIST = 5;
    private final String LOG_TAG = getClass().getSimpleName();
    private int[] tasks;
    private MangaReader mangaReader;
    private MangaFox mangaFox;

    public ApiTask(WeakReference<Context> context, int... tasks) {
        this.tasks = tasks;
        mangaReader = MangaReader.getInstance();
        mangaFox = MangaFox.getInstance();
    }

    @Override
    public void run() {
        for (int task : tasks) {
            switch (task) {
                case READER_MANGA_LIST:
//                    mangaReader.getMangaList();
                    break;
                case READER_LATEST_LIST:
//                    mangaReader.getLatestList();
                    break;
                case READER_POPULAR_LIST:
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mangaReader.getPopularList(new GetListListener() {
//                                @Override
//                                public void onGetList(@NonNull List list) {
//                                    Utilities.Log(LOG_TAG, "Popular: " + list.size());
//                                    if (list.size() > 0) {
//                                        context.getContentResolver().delete(Contract.MangaReaderPopularList.CONTENT_URI, null, null);
//                                        DbInsertHelper insertCall = new DbInsertHelper(list, Contract.MangaReaderPopularList.CONTENT_URI, context);
//                                        insertCall.start();
//                                        insertCall.setInsertDoneListener(new InsertListener() {
//                                            @Override
//                                            public void onInsertDone() {
//                                                Utilities.Log(LOG_TAG, "Popular list insert done!");
//                                            }
//                                        });
//                                    }
//                                }
//                            });
//                        }
//                    }).start();
                    break;
                case FOX_MANGA_LIST:
//                    mangaFox.getMangaList();
                    break;
                default:
                    Utilities.Log(LOG_TAG, "Unsupported Task");
                    break;
            }

        }


    }

}
