package com.example.learnwordstrainer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.learnwordstrainer.utils.NotificationHelper;

/**
 * BroadcastReceiver that listens for screen unlock events
 * Shows notifications every 5th unlock
 */
public class ScreenUnlockReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenUnlockReceiver";
    private static final String PREFS_NAME = "screen_unlock_prefs";
    private static final String KEY_UNLOCK_COUNT = "unlock_count";
    private static final int NOTIFICATION_THRESHOLD = 5;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            Log.d(TAG, "Screen unlocked");
            handleScreenUnlock(context);
        }
    }

    /**
     * Handle screen unlock event
     * Increment counter and show notification if threshold is reached
     */
    private void handleScreenUnlock(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int currentCount = prefs.getInt(KEY_UNLOCK_COUNT, 0);
        currentCount++;

        Log.d(TAG, "Unlock count: " + currentCount);

        if (currentCount >= NOTIFICATION_THRESHOLD) {
            // Show notification
            NotificationHelper.showLearningReminder(context);
            // Reset counter
            currentCount = 0;
            Log.d(TAG, "Notification shown, counter reset");
        }

        // Save updated count
        prefs.edit().putInt(KEY_UNLOCK_COUNT, currentCount).apply();
    }

    /**
     * Reset the unlock counter (can be called from settings or manually)
     */
    public static void resetUnlockCounter(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_UNLOCK_COUNT, 0).apply();
        Log.d(TAG, "Unlock counter reset manually");
    }

    /**
     * Get current unlock count
     */
    public static int getCurrentUnlockCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_UNLOCK_COUNT, 0);
    }
}