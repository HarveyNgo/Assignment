package com.ngovihung.assignment;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ngo.vi.hung on 6/21/2017.
 */

public class Application extends MultiDexApplication {

    private static Context mContext;
    private static AppCompatActivity mActiveActivity;


    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        Application.mContext = mContext;
    }

    public static AppCompatActivity getActiveActivity() {
        return mActiveActivity;
    }

    public static void setActiveActivity(AppCompatActivity active) {
        mActiveActivity = active;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
