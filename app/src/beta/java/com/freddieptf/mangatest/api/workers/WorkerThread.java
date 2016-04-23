package com.freddieptf.mangatest.api.workers;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.freddieptf.mangatest.api.helperInterfaces.GetListListener;
import com.freddieptf.mangatest.api.helperInterfaces.InsertListener;
import com.freddieptf.mangatest.api.mangafox.MangaFox;
import com.freddieptf.mangatest.api.mangareader.MangaReader;
import com.freddieptf.mangatest.data.Contract;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by fred on 8/24/15.
 */
public class WorkerThread extends Thread {
    int[] tasks;
    MangaReader mangaReader;
    MangaFox mangaFox;
    ExecutorService service;
    Context context;
    Handler handler;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    final String LOG_TAG = getClass().getSimpleName();
    public static final int ALL_LIST = 5;
    public static final int READER_ALPHA_LIST = 1;
    public static final int FOX_ALPHA_LIST = 2;
    public static final int READER_LATEST_LIST = 3;
    public static final int READER_POPULAR_LIST = 4;

    public WorkerThread(Context context, int... tasks) {
        this.tasks = tasks;
        mangaReader = new MangaReader();
        mangaFox = new MangaFox();
        this.context = context;
    }

    @Override
    public void run() {
        for(int task : tasks) {
            switch (task) {
                case READER_ALPHA_LIST:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mangaReader.getMangaList(new GetListListener() {
                                @Override
                                public void onGetList(@NonNull List list) {
                                    Utilities.Log(LOG_TAG, "Reader Alphabet list: " + list.size());
                                    if (list.size() > 0) {
                                        context.getContentResolver().delete(Contract.MangaReaderMangaList.CONTENT_URI, null, null);
                                        context.getContentResolver().delete(Contract.VirtualTable.CONTENT_URI, null, null);
                                        final InsertCall insertCall = new InsertCall(list, Contract.MangaReaderMangaList.CONTENT_URI, context);
                                        insertCall.start();
                                        insertCall.setInsertDoneListener(new InsertListener() {
                                            @Override
                                            public void onInsertDone() {
                                                Utilities.Log(LOG_TAG, "Reader insert done");
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    }).start();
                    break;
                case FOX_ALPHA_LIST:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mangaFox.getMangaList(new GetListListener() {
                                @Override
                                public void onGetList(@NonNull List list) {
                                    Utilities.Log(LOG_TAG, "Fox Alphabet: " + list.size());
                                    if (list.size() > 0) {
                                        context.getContentResolver().delete(Contract.MangaFoxMangaList.CONTENT_URI, null, null);
//                                context.getContentResolver().delete(Contract.VirtualTable.CONTENT_URI, null, null);

                                        InsertCall insertCall = new InsertCall(list, Contract.MangaFoxMangaList.CONTENT_URI, context);
                                        insertCall.start();
                                        insertCall.setInsertDoneListener(new InsertListener() {
                                            @Override
                                            public void onInsertDone() {
                                                Utilities.Log(LOG_TAG, "Fox insert done");
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).start();
                    break;
                case READER_LATEST_LIST:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mangaReader.getLatestList(new GetListListener() {
                                @Override
                                public void onGetList(@NonNull List list) {
                                    Utilities.Log(LOG_TAG, "Latest: " + list.size());
                                    if (list.size() > 0) {
                                        context.getContentResolver().delete(Contract.MangaReaderLatestList.CONTENT_URI, null, null);
                                        InsertCall insertCall = new InsertCall(list, Contract.MangaReaderLatestList.CONTENT_URI, context);
                                        insertCall.start();
                                        insertCall.setInsertDoneListener(new InsertListener() {
                                            @Override
                                            public void onInsertDone() {
                                                Utilities.Log(LOG_TAG, "Latest list insert done!");
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).start();
                    break;
                case READER_POPULAR_LIST:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mangaReader.getPopularList(new GetListListener() {
                                @Override
                                public void onGetList(@NonNull List list) {
                                    Utilities.Log(LOG_TAG, "Popular: " + list.size());
                                    if (list.size() > 0) {
                                        context.getContentResolver().delete(Contract.MangaReaderPopularList.CONTENT_URI, null, null);
                                        InsertCall insertCall = new InsertCall(list, Contract.MangaReaderPopularList.CONTENT_URI, context);
                                        insertCall.start();
                                        insertCall.setInsertDoneListener(new InsertListener() {
                                            @Override
                                            public void onInsertDone() {
                                                Utilities.Log(LOG_TAG, "Popular list insert done!");
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).start();
                    break;

                default:
                    Utilities.Log(LOG_TAG, "Unsupported Task");
                    break;
            }

        }




    }

}
