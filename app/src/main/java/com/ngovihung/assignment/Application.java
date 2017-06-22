package com.ngovihung.assignment;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

/**
 * Created by ngo.vi.hung on 6/21/2017.
 */

public class Application extends android.app.Application {

    private static Context mContext;
    private static AppCompatActivity mActiveActivity;


    public static Context getContext() {
        return mContext;
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
        FirebaseApp.initializeApp(this);
    }
}
