package com.freddieptf.mangatest.api;

import android.content.Context;

import com.freddieptf.mangatest.api.workers.WorkerThread;

/**
 * Created by fred on 8/24/15.
 */
public class AutoApiWorker {

    private  static AutoApiWorker worker = null;

    protected AutoApiWorker(){}

    public static AutoApiWorker getInstance(){
        if(worker == null) worker = new AutoApiWorker();
        return worker;
    }

    public WorkerThread getWorkerThread(Context context, int... tasks){
        return new WorkerThread(context, tasks);
    }




}
