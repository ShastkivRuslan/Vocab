package com.example.learnwordstrainer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;

public class BubbleService extends Service {
    private WindowManager windowManager;
    private View bubbleView;
    private View deleteZoneView;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams deleteParams;
    private int screenWidth;
    private int screenHeight;
    private boolean isDeleteZoneVisible = false;
    private boolean isDeleteZoneActive = false;
    private static final int DRAG_TOLERANCE = 20;
    private static final int MARGIN_HORIZONTAL = 20;
    private static final String PREFS_NAME = "BubblePrefs";
    private static final String PREF_BUBBLE_X = "bubbleX";
    private static final String PREF_BUBBLE_Y = "bubbleY";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initViews();
        setupWindowManager();
        setupTouchListener();
        setupClickListener();
        loadBubblePosition();
        windowManager.addView(bubbleView, params);
    }

    private void initViews() {
        Context themedContext = new ContextThemeWrapper(this, R.style.Theme_LearnWordsTrainer);
        LayoutInflater inflater = LayoutInflater.from(themedContext);
        bubbleView = inflater.inflate(R.layout.bubble_layout, null);
        deleteZoneView = inflater.inflate(R.layout.delete_zone_layout, null);
    }

    private void setupWindowManager() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = MARGIN_HORIZONTAL;
        params.y = 100;

        deleteParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        deleteParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        deleteParams.y = 100;
    }

    private void loadBubblePosition() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        params.x = prefs.getInt(PREF_BUBBLE_X, MARGIN_HORIZONTAL);
        params.y = prefs.getInt(PREF_BUBBLE_Y, 100);
    }

    private void saveBubblePosition() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_BUBBLE_X, params.x);
        editor.putInt(PREF_BUBBLE_Y, params.y);
        editor.apply();
    }

    private void setupTouchListener() {
        bubbleView.findViewById(R.id.bubbleCardView).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private long startClickTime;
            private final int MAX_CLICK_DURATION = 200;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        startClickTime = System.currentTimeMillis();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        showDeleteZone();
                        int newX = initialX + (int) (event.getRawX() - initialTouchX);
                        int newY = initialY + (int) (event.getRawY() - initialTouchY);

                        handleDeleteZoneVisibility(newX, newY);

                        params.x = newX;
                        params.y = newY;
                        windowManager.updateViewLayout(bubbleView, params);
                        return true;

                    case MotionEvent.ACTION_UP:
                        long clickDuration = System.currentTimeMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION &&
                                Math.abs(event.getRawX() - initialTouchX) < DRAG_TOLERANCE &&
                                Math.abs(event.getRawY() - initialTouchY) < DRAG_TOLERANCE) {
                            v.performClick();
                        } else {
                            snapToEdge();

                            if (isDeleteZoneVisible && isOverDeleteZone(params.x, params.y)) {
                                performHapticFeedback(v);
                                stopSelf();
                                return true;
                            }
                        }

                        if (isDeleteZoneVisible) {
                            hideDeleteZone();
                        }
                        return true;
                }
                return false;
            }
        });

        bubbleView.findViewById(R.id.bubbleCardView).setOnLongClickListener(v -> {
            performHapticFeedback(v);
            showQuickActions();
            return true;
        });
    }

    private void showQuickActions() {
        //TODO
    }

    private void performHapticFeedback(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    private void handleDeleteZoneVisibility(int x, int y) {
        if (isDeleteZoneActive && isOverDeleteZone(x, y)) {
            animateDeleteZoneHighlight();
        } else if (isDeleteZoneActive && !isOverDeleteZone(x, y)) {
            animateDeleteZoneNormal();
        }
    }

    private void showDeleteZone() {
        if (!isDeleteZoneVisible) {
            windowManager.addView(deleteZoneView, deleteParams);
            isDeleteZoneVisible = true;

            deleteZoneView.setAlpha(0f);
            deleteZoneView.setScaleX(0f);
            deleteZoneView.setScaleY(0f);

            deleteZoneView.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .alpha(0.5f)
                    .setDuration(250)
                    .withEndAction(() -> isDeleteZoneActive = true)
                    .start();
        }
    }

    private void animateDeleteZoneHighlight() {
        deleteZoneView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(150)
                .start();
    }

    private void animateDeleteZoneNormal() {
        deleteZoneView.animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .alpha(0.5f)
                .setDuration(150)
                .start();
    }

    private boolean isOverDeleteZone(int x, int y) {
        int bubbleCenterX = x + bubbleView.getWidth() / 2;
        int bubbleCenterY = y + bubbleView.getHeight() / 2;

        int centerX = screenWidth / 2;
        int deleteZoneY = screenHeight - 100 - deleteZoneView.getHeight() / 2;

        int dx = bubbleCenterX - centerX;
        int dy = bubbleCenterY - deleteZoneY;

        int deletionRadius = 150;

        return dx*dx + dy*dy < deletionRadius*deletionRadius;
    }

    private void snapToEdge() {
        int bubbleWidth = bubbleView.getMeasuredWidth();
        if (bubbleWidth == 0) bubbleWidth = 40;

        final int targetX;
        if (params.x < screenWidth / 2) {
            targetX = MARGIN_HORIZONTAL;
        } else {
            targetX = screenWidth - bubbleWidth - MARGIN_HORIZONTAL;
        }
        final int startX = params.x;

        ValueAnimator xAnimator = ValueAnimator.ofInt(startX, targetX);
        xAnimator.addUpdateListener(animation -> {
            params.x = (int) animation.getAnimatedValue();
            saveBubblePosition();
            windowManager.updateViewLayout(bubbleView, params);
        });

        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f, 0.8f, 1f);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f, 0.8f, 1f);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(bubbleView, scaleXHolder, scaleYHolder);

        ObjectAnimator wobbleAnimator = ObjectAnimator.ofFloat(bubbleView, "rotation", 0f, -10f, 10f, -5f, 5f, 0f);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(bubbleView, "alpha", 1f, 0.8f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet firstSet = new AnimatorSet();
        firstSet.playTogether(wobbleAnimator, scaleAnimator);

        AnimatorSet secondSet = new AnimatorSet();
        secondSet.playTogether(xAnimator, alphaAnimator);

        animatorSet.play(firstSet).before(secondSet);

        animatorSet.setDuration(300);

        xAnimator.setInterpolator(new BounceInterpolator());
        scaleAnimator.setInterpolator(new AnticipateOvershootInterpolator(1.5f));

        animatorSet.start();
    }

    private void hideDeleteZone() {
        if (isDeleteZoneVisible) {
            deleteZoneView.animate()
                    .alpha(0f)
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        if (deleteZoneView != null && isDeleteZoneVisible) {
                            try {
                                windowManager.removeView(deleteZoneView);
                                isDeleteZoneVisible = false;
                                isDeleteZoneActive = false;
                            } catch (IllegalArgumentException e) {
                                // Вид може бути вже видалений
                            }
                        }
                    })
                    .start();
        }
    }

    private void setupClickListener() {
        bubbleView.findViewById(R.id.bubbleCardView).setOnClickListener(v -> {
            performHapticFeedback(v);
            Intent intent = new Intent(this, AddWordFloatingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanupViews();
    }

    private void cleanupViews() {
        if (bubbleView != null) {
            windowManager.removeView(bubbleView);
            bubbleView = null;
        }
        if (deleteZoneView != null && isDeleteZoneVisible) {
            windowManager.removeView(deleteZoneView);
            deleteZoneView = null;
        }
    }
}