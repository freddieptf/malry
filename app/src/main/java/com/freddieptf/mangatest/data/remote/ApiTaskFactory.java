package com.freddieptf.mangatest.data.remote;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by fred on 8/24/15.
 */
public class ApiTaskFactory {

    private static ApiTaskFactory worker = null;

    private ApiTaskFactory() {
    }

    public static ApiTaskFactory getInstance() {
        if (worker == null) worker = new ApiTaskFactory();
        return worker;
    }

    public ApiTask createApiTask(WeakReference<Context> context, int... tasks) {
        return new ApiTask(context, tasks);
    }


}
