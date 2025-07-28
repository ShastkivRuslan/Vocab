package com.example.learnwordstrainer.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.learnwordstrainer.domain.model.BubblePosition;

/**
 * Repository responsible for managing bubble position data
 */
public class BubbleRepository {
    private static final String PREFS_NAME = "BubblePrefs";
    private static final String PREF_BUBBLE_X = "bubbleX";
    private static final String PREF_BUBBLE_Y = "bubbleY";
    private static final String PREF_BUBBLE_ENABLED = "bubble_enabled";
    private static final String PREF_BUBBLE_SIZE = "bubble_size";
    private static final int DEFAULT_X = 20;
    private static final int DEFAULT_Y = 100;
    private static final int DEFAULT_SIZE = 40;
    private static final boolean DEFAULT_ENABLED = true;

    private final Context context;

    public BubbleRepository(Context context) {
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

    public boolean isBubbleEnabled() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_BUBBLE_ENABLED, DEFAULT_ENABLED);
    }

    public int getBubbleSize() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_BUBBLE_SIZE, DEFAULT_SIZE);
    }

    public void setBubbleSize(int size) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_BUBBLE_SIZE, size);
        editor.apply();
    }

    public void setBubbleEnabled(boolean isEnabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_BUBBLE_ENABLED, isEnabled);
        editor.apply();
    }

    public boolean toggleBubble() {
        boolean currentState = isBubbleEnabled();
        boolean newState = !currentState;
        setBubbleEnabled(newState);
        return newState;
    }

    /**
     * Перевіряє чи розмір в допустимих межах
     * @param size розмір для перевірки
     * @return true якщо розмір допустимий
     */
    public boolean isValidBubbleSize(int size) {
        return size >= 30 && size <= 80;
    }
}