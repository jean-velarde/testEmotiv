package com.emotiv.cortexv2example.application;

import android.app.Application;
import android.content.Context;

import com.emotiv.cortexv2example.cortexlib.CortexLibManager;

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        CortexLibManager.load(this);
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CortexLibManager.stop();
    }

    public static Context getCurrentContext() {
        return mContext;
    }
}