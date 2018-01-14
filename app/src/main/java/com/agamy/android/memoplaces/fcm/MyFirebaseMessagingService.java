package com.agamy.android.memoplaces.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.agamy.android.memoplaces.R;
import com.agamy.android.memoplaces.ui.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by agamy on 1/13/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title =  remoteMessage.getNotification().getTitle();
            String body =  remoteMessage.getNotification().getBody();
            showNotificationNow(title, body);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void showNotificationNow(String title, String body) {

        Context mContext = getApplicationContext();
        NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(mContext,"myChannelId");
        Intent intent = new Intent(mContext , MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext , 100,intent,PendingIntent.FLAG_ONE_SHOT);

        Notification notification = mBuilder.setContentTitle(title)
                .setContentText(body).setSmallIcon(R.mipmap.ic_launcher_round).setContentIntent(pendingIntent).setAutoCancel(true).build();

        NotificationManager managerCompat = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        if (managerCompat != null) {

            managerCompat.notify(0,notification);
        }
    }


}
