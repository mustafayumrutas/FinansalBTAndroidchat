package com.chatfire.odev.firebaseandroidchat;

import android.app.Application;
import android.content.Context;

/**
 * Created by Mustafaspc on 19-Dec-17.
 */

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}