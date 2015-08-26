package com.freddieptf.mangatest.api;

import android.content.Context;

/**
 * Created by fred on 8/24/15.
 */
public class Worker {

    private  static Worker worker = null;

    protected Worker(){}

    public static Worker getInstance(){
        if(worker == null) worker = new Worker();
        return worker;
    }

    public WorkerThread getWorkerThread(Context context, int... tasks){
        return new WorkerThread(context, tasks);
    }




}
