package com.example.learnwordstrainer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.receivers.ScreenUnlockReceiver;
import com.example.learnwordstrainer.ui.mainscreen.MainActivity;

/**
 * Background service that registers screen unlock receiver
 * This service runs in the background and listens for screen unlock events
 */
public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final int NOTIFICATION_ID = 1002;
    private static final String CHANNEL_ID = "notification_service_channel";

    private ScreenUnlockReceiver screenUnlockReceiver;
    private boolean isReceiverRegistered = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NotificationService created");

        // Створюємо канал сповіщень
        createNotificationChannel();

        // Ініціалізуємо receiver
        screenUnlockReceiver = new ScreenUnlockReceiver();
        registerScreenUnlockReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "NotificationService started");

        startForeground(NOTIFICATION_ID, createNotification());

        // Ensure receiver is registered
        if (!isReceiverRegistered) {
            registerScreenUnlockReceiver();
        }

        // Return START_STICKY to restart service if it gets killed
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Screen Unlock Service",
                    NotificationManager.IMPORTANCE_MIN // Мінімальна важливість
            );
            channel.setDescription("Service for tracking screen unlock events");
            channel.setShowBadge(false);
            channel.setSound(null, null);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Vocab Tracker")
                .setContentText("Відстеження розблокування екрану")
                .setSmallIcon(R.drawable.ic_notification) // Замініть на ваш icon
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setSilent(true)
                .setShowWhen(false)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
    }

    /**
     * Register the screen unlock receiver
     */
    private void registerScreenUnlockReceiver() {
        if (screenUnlockReceiver != null && !isReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_USER_PRESENT); // Screen unlocked

            try {
                registerReceiver(screenUnlockReceiver, filter);
                isReceiverRegistered = true;
                Log.d(TAG, "Screen unlock receiver registered");
            } catch (Exception e) {
                Log.e(TAG, "Failed to register screen unlock receiver", e);
            }
        }
    }

    /**
     * Unregister the screen unlock receiver
     */
    private void unregisterScreenUnlockReceiver() {
        if (screenUnlockReceiver != null && isReceiverRegistered) {
            try {
                unregisterReceiver(screenUnlockReceiver);
                isReceiverRegistered = false;
                Log.d(TAG, "Screen unlock receiver unregistered");
            } catch (Exception e) {
                Log.e(TAG, "Failed to unregister screen unlock receiver", e);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "NotificationService destroyed");
        unregisterScreenUnlockReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // This is not a bound service
    }

    /**
     * Handle task removal (when app is swiped away from recent apps)
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "Task removed, restarting service");

        // Restart the service
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
    }
}