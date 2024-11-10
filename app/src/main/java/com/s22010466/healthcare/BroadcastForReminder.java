package com.s22010466.healthcare;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class BroadcastForReminder extends BroadcastReceiver {
    Uri soundUri;
    String channelID;

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("rMsg");

        // Get the sound URI and channel ID from the preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MODE", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("defaultSound", true)) {
            // Using the device notification
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            channelID = "DEFAULT_SOUND_CHANNEL";
        } else if (sharedPreferences.getBoolean("coolSound", false)) {
            soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.cool);
            channelID = "COOL_SOUND_CHANNEL";
        } else {
            soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.chaos);
            channelID = "CHAOS_SOUND_CHANNEL";
        }

        // Create a notification
        createNotification(context, msg);

        // Reschedule the alarm for the next day
        rescheduleAlarm(context);
    }

    private void createNotification(Context context, String msg) {
        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (channelID.equals("DEFAULT_SOUND_CHANNEL")) {
                String defaultChannelId = "DEFAULT_SOUND_CHANNEL";
                String defaultChannelName = "Default Sound Channel";
                NotificationChannel defaultChannel = new NotificationChannel(defaultChannelId, defaultChannelName, NotificationManager.IMPORTANCE_HIGH);
                defaultChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
                notificationManager.createNotificationChannel(defaultChannel);
            }
            else if (channelID.equals("COOL_SOUND_CHANNEL")) {
                String coolChannelId = "COOL_SOUND_CHANNEL";
                String coolChannelName = "Cool Sound Channel";
                NotificationChannel coolChannel = new NotificationChannel(coolChannelId, coolChannelName, NotificationManager.IMPORTANCE_HIGH);
                coolChannel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.cool), null);
                notificationManager.createNotificationChannel(coolChannel);
            }
            else {
                String chaosChannelId = "CHAOS_SOUND_CHANNEL";
                String chaosChannelName = "Chaos Sound Channel";
                NotificationChannel chaosChannel = new NotificationChannel(chaosChannelId, chaosChannelName, NotificationManager.IMPORTANCE_HIGH);
                chaosChannel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.chaos), null);
                notificationManager.createNotificationChannel(chaosChannel);
            }
        }

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.baseline_medication_24)
                .setContentTitle("It's time!")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(soundUri);

        // Create a pending intent to open login and navigate to reminder fragment when the notification is clicked
        Intent i = new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }

    private void rescheduleAlarm(Context context) {
        // Get the next alarm time (24 hours later)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        // Create a pending intent to trigger the BroadcastReceiver
        Intent intent = new Intent(context, BroadcastForReminder.class);
        intent.putExtra("rMsg", "Take your medicines");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the one-time alarm for the next day
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}

