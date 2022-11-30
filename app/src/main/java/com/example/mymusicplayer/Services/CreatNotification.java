package com.example.mymusicplayer.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.mymusicplayer.AudioModel;
import com.example.mymusicplayer.MainActivity;
import com.example.mymusicplayer.R;
import com.example.mymusicplayer.Services.NotificationActionService;

import java.util.ResourceBundle;

public class CreatNotification {

    public static final String CHANNEL_ID = "channel";
    public static final int NOTIFICATION_ID = 101;


    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PREV = "actionprev";
    public static final String ACTION_IN_APP = "actioninapp";

    public static Notification notification;

    @SuppressLint("UnspecifiedImmutableFlag")
    public static void createNotification(Context context, AudioModel track, int playButton, int pos, int size) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.music);

            PendingIntent pendingIntentPrev;
            int drw_prev;
            if (pos == 0) {
                pendingIntentPrev = null;
                drw_prev = 0;
            } else {
                Intent intentPrev = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_PREV);
                pendingIntentPrev = PendingIntent.getBroadcast(context, 0, intentPrev,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                drw_prev = R.drawable.skip_previous_24;
            }

            Intent intentPlay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);


            // CLICK NOTIFICATION AND OPEN ACTIVITY
            Intent notifyIntent = new Intent(context, MainActivity.class)
                    .setAction(ACTION_IN_APP);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pi = PendingIntent.getActivity(context,11,notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE );
            PendingIntent pendingIntentNext;


            int drw_Next;
            if (pos == size) {
                pendingIntentNext = null;
                drw_Next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                drw_Next = R.drawable.skip_next;
            }

          /*  // Big picture style provide JPG Image
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .bigLargeIcon(icon)
                    .setBigContentTitle("Big picture style")
                    .setSummaryText("Nilu player music is running");
             .setStyle(bigPictureStyle)*/

          /*  // Inbox Style
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                    .addLine("Nilesh")
                    .addLine("nilu")
                    .addLine("patel")
                    .addLine("patidar")
                    .setBigContentTitle("JAY SHREE RAM")
                    .setSummaryText("its full summary text!!");
                      .setStyle(inboxStyle)*/


            // CREATE NOTIFICATION
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getArtist())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true) // show notification first time only
                    .setShowWhen(true)
                   // .setWhen(System.currentTimeMillis())
                    //  .setOngoing(true)    // slide karne par hate nhi
                    .addAction(drw_prev, "previous", pendingIntentPrev)
                    .addAction(playButton, "play", pendingIntentPlay)
                    .addAction(drw_Next, "next", pendingIntentNext)
                    .setContentIntent(pi)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .build();

            notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        }

    }

}
