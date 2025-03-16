package com.example.learnwordstrainer.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.learnwordstrainer.model.BubblePosition;

/**
 * Repository responsible for managing bubble position data
 */
public class BubblePositionRepository {
    private static final String PREFS_NAME = "BubblePrefs";
    private static final String PREF_BUBBLE_X = "bubbleX";
    private static final String PREF_BUBBLE_Y = "bubbleY";
    private static final int DEFAULT_X = 20;
    private static final int DEFAULT_Y = 100;

    private final Context context;

    public BubblePositionRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Load saved bubble position from SharedPreferences
     */
    public BubblePosition loadPosition() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int x = prefs.getInt(PREF_BUBBLE_X, DEFAULT_X);
        int y = prefs.getInt(PREF_BUBBLE_Y, DEFAULT_Y);
        return new BubblePosition(x, y);
    }

    /**
     * Save bubble position to SharedPreferences
     */
    public void savePosition(int x, int y) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_BUBBLE_X, x);
        editor.putInt(PREF_BUBBLE_Y, y);
        editor.apply();
    }
}