package com.agamy.android.memoplaces.app;

import android.app.Application;

import com.agamy.android.memoplaces.app.MyConnectivityReceiver;

/**
 * Created by agamy on 1/11/2018.
 */

public class MyApp extends Application {
    public static MyApp mInstance;

    public MyApp() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApp getInstance()
    {
        return mInstance;
    }

    public void setConnectionListener(MyConnectivityReceiver.OnNetworkConnectionChange listener)
    {
        MyConnectivityReceiver.mOnNetworkConnectionChange =listener;
    }



}
