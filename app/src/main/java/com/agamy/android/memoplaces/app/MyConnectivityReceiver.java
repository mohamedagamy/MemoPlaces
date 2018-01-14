package com.agamy.android.memoplaces.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by agamy on 1/11/2018.
 */

public class MyConnectivityReceiver extends BroadcastReceiver {

    public static OnNetworkConnectionChange mOnNetworkConnectionChange;
   public interface OnNetworkConnectionChange{
       void onNetworkChange(boolean isConnected);
   }
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (mOnNetworkConnectionChange != null) {
            mOnNetworkConnectionChange.onNetworkChange(isConnected);
        }

    }

    public boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) MyApp.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }
}
