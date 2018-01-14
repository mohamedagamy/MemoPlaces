package com.agamy.android.memoplaces.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by agamy on 1/13/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "RegId";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        //d5-H2_5f5Ho:APA91bHDJw9mu58-OBFUjhkcuzcXZWgpENit3mW0DM6wMj2ftdX1796Dy9uKWZf0JH4hd01BIADj0pBio5KecRXU67TpV4FDKSE3Ww_i5Jut0H4QFYEfltE-GNZepzcFft_o4nxOxKFo

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }



}
