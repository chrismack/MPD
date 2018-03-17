package com.sceneit.chris.sceneit.main.data;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sceneit.chris.sceneit.R;
import com.sceneit.chris.sceneit.main.MainActivity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Chris on 16/03/2018.
 */

public class Messaging extends FirebaseMessagingService{

    private final static AtomicInteger notifId = new AtomicInteger(0);

    public Messaging() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notif = remoteMessage.getNotification();
        String title = notif.getTitle() != null ? notif.getTitle() : "";
        String body = notif.getBody() != null ? notif.getBody() : "";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.getContext(), "General")
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(MainActivity.getContext());
        notifManager.notify(notifId.getAndIncrement(), mBuilder.build());
    }
}
