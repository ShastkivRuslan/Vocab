package com.example.learnwordstrainer.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.learnwordstrainer.receivers.ScreenUnlockReceiver;

/**
 * Background service that registers screen unlock receiver
 * This service runs in the background and listens for screen unlock events
 */
public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private ScreenUnlockReceiver screenUnlockReceiver;
    private boolean isReceiverRegistered = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NotificationService created");
        screenUnlockReceiver = new ScreenUnlockReceiver();
        registerScreenUnlockReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "NotificationService started");

        // Ensure receiver is registered
        if (!isReceiverRegistered) {
            registerScreenUnlockReceiver();
        }

        // Return START_STICKY to restart service if it gets killed
        return START_STICKY;
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