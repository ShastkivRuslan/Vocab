package com.example.learnwordstrainer.service;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.model.BubblePosition;
import com.example.learnwordstrainer.repository.BubblePositionRepository;
import com.example.learnwordstrainer.ui.activities.AddWordFloatingActivity;
import com.example.learnwordstrainer.viewmodels.BubbleViewModel;

/**
 * Service responsible for displaying and managing a floating bubble UI element
 * that provides quick access to add new words functionality.
 */
public class BubbleService extends Service {

    // Constants
    private static final int DRAG_TOLERANCE = 20;
    private static final int MARGIN_HORIZONTAL = 20;
    private static final long ANIMATION_DURATION = 300;
    private static final long CLICK_DURATION_THRESHOLD = 200;
    private static final int DELETION_ZONE_RADIUS = 150;

    // UI Components
    private WindowManager windowManager;
    private View bubbleView;
    private View deleteZoneView;

    // Layout Parameters
    private WindowManager.LayoutParams bubbleParams;
    private WindowManager.LayoutParams deleteZoneParams;

    // State variables
    private int screenWidth;
    private int screenHeight;
    private boolean isDeleteZoneVisible = false;
    private boolean isDeleteZoneActive = false;

    // MVVM Components
    private BubbleViewModel viewModel;
    private BubblePositionRepository repository;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initRepository();
        initViewModel();
        initViews();
        setupWindowManager();
        setupTouchListener();
        setupClickListener();
        loadBubblePosition();

        windowManager.addView(bubbleView, bubbleParams);
    }

    private void initRepository() {
        repository = new BubblePositionRepository(this);
    }

    private void initViewModel() {
        viewModel = new BubbleViewModel(repository);
    }

    private void initViews() {
        Context themedContext = new ContextThemeWrapper(this, R.style.Theme_LearnWordsTrainer);
        LayoutInflater inflater = LayoutInflater.from(themedContext);
        bubbleView = inflater.inflate(R.layout.bubble_layout, null, false);
        deleteZoneView = inflater.inflate(R.layout.delete_zone_layout, null);
    }

    private void setupWindowManager() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        bubbleParams = createBubbleLayoutParams();
        deleteZoneParams = createDeleteZoneLayoutParams();
    }

    private WindowManager.LayoutParams createBubbleLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = MARGIN_HORIZONTAL;
        params.y = 100;
        return params;
    }

    private WindowManager.LayoutParams createDeleteZoneLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.y = 100;
        return params;
    }

    private void loadBubblePosition() {
        BubblePosition position = viewModel.loadSavedPosition();
        bubbleParams.x = position.x;
        bubbleParams.y = position.y;
    }

    private void setupTouchListener() {
        View bubbleCardView = bubbleView.findViewById(R.id.bubbleCardView);
        bubbleCardView.setOnTouchListener(new BubbleTouchListener());
        bubbleCardView.setOnLongClickListener(view -> {
            provideFeedback(view);
            showQuickActions();
            return true;
        });
    }

    private class BubbleTouchListener implements View.OnTouchListener {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        private long startClickTime;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return handleTouchDown(event);

                case MotionEvent.ACTION_MOVE:
                    return handleTouchMove(event);

                case MotionEvent.ACTION_UP:
                    return handleTouchUp(v, event);
            }
            return false;
        }

        private boolean handleTouchDown(MotionEvent event) {
            initialX = bubbleParams.x;
            initialY = bubbleParams.y;
            initialTouchX = event.getRawX();
            initialTouchY = event.getRawY();
            startClickTime = System.currentTimeMillis();
            return true;
        }

        private boolean handleTouchMove(MotionEvent event) {
            showDeleteZone();
            int newX = calculateNewPosition(initialX, event.getRawX() - initialTouchX);
            int newY = calculateNewPosition(initialY, event.getRawY() - initialTouchY);

            updateDeleteZoneVisibility(newX, newY);
            updateBubblePosition(newX, newY);
            return true;
        }

        private int calculateNewPosition(int initialPosition, float delta) {
            return initialPosition + (int) delta;
        }

        private boolean handleTouchUp(View v, MotionEvent event) {
            long clickDuration = System.currentTimeMillis() - startClickTime;

            boolean wasQuickTap = isQuickTap(clickDuration, event);
            boolean wasOverDeleteZone = false;

            if (!wasQuickTap) {
                snapToEdge();

                if (isDeleteZoneVisible && isBubbleOverDeleteZone()) {
                    provideFeedback(v);
                    wasOverDeleteZone = true;
                    stopSelf();
                }
            }

            if (isDeleteZoneVisible) {
                hideDeleteZone();
            }

            // і не над зоною видалення
            if (wasQuickTap && !wasOverDeleteZone) {
                v.performClick();
            }

            return true;
        }

        private boolean isQuickTap(long clickDuration, MotionEvent event) {
            return clickDuration < CLICK_DURATION_THRESHOLD &&
                    Math.abs(event.getRawX() - initialTouchX) < DRAG_TOLERANCE &&
                    Math.abs(event.getRawY() - initialTouchY) < DRAG_TOLERANCE;
        }
    }

    private void updateBubblePosition(int x, int y) {
        bubbleParams.x = x;
        bubbleParams.y = y;
        windowManager.updateViewLayout(bubbleView, bubbleParams);
    }

    private void showQuickActions() {
        // TODO: Implement quick actions menu
    }

    private void provideFeedback(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    private void updateDeleteZoneVisibility(int x, int y) {
        if (isDeleteZoneActive && isBubbleOverDeleteZone(x, y)) {
            animateDeleteZoneHighlight();
        } else if (isDeleteZoneActive && !isBubbleOverDeleteZone(x, y)) {
            animateDeleteZoneNormal();
        }
    }

    private boolean isBubbleOverDeleteZone() {
        return isBubbleOverDeleteZone(bubbleParams.x, bubbleParams.y);
    }

    private boolean isBubbleOverDeleteZone(int x, int y) {
        int bubbleCenterX = x + bubbleView.getWidth() / 2;
        int bubbleCenterY = y + bubbleView.getHeight() / 2;

        int centerX = screenWidth / 2;
        int deleteZoneY = screenHeight - 100 - deleteZoneView.getHeight() / 2;

        int dx = bubbleCenterX - centerX;
        int dy = bubbleCenterY - deleteZoneY;

        return dx*dx + dy*dy < DELETION_ZONE_RADIUS*DELETION_ZONE_RADIUS;
    }

    private void showDeleteZone() {
        if (!isDeleteZoneVisible) {
            windowManager.addView(deleteZoneView, deleteZoneParams);
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

    private void snapToEdge() {
        int bubbleWidth = bubbleView.getMeasuredWidth();
        if (bubbleWidth == 0) bubbleWidth = 40;

        int targetX = calculateTargetX(bubbleWidth);
        int startX = bubbleParams.x;

        AnimatorSet animatorSet = createSnapAnimations(startX, targetX);
        animatorSet.start();
    }

    private int calculateTargetX(int bubbleWidth) {
        if (bubbleParams.x < screenWidth / 2) {
            return MARGIN_HORIZONTAL;
        } else {
            return screenWidth - bubbleWidth - MARGIN_HORIZONTAL;
        }
    }

    private AnimatorSet createSnapAnimations(int startX, int targetX) {
        ValueAnimator xAnimator = createHorizontalAnimator(startX, targetX);
        ObjectAnimator scaleAnimator = createScaleAnimator();
        ObjectAnimator wobbleAnimator = createWobbleAnimator();
        ObjectAnimator alphaAnimator = createAlphaAnimator();

        AnimatorSet firstSet = new AnimatorSet();
        firstSet.playTogether(wobbleAnimator, scaleAnimator);

        AnimatorSet secondSet = new AnimatorSet();
        secondSet.playTogether(xAnimator, alphaAnimator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(firstSet).before(secondSet);
        animatorSet.setDuration(ANIMATION_DURATION);

        return animatorSet;
    }

    private ValueAnimator createHorizontalAnimator(int startX, int targetX) {
        ValueAnimator xAnimator = ValueAnimator.ofInt(startX, targetX);
        xAnimator.addUpdateListener(animation -> {
            bubbleParams.x = (int) animation.getAnimatedValue();
            viewModel.saveBubblePosition(bubbleParams.x, bubbleParams.y);
            windowManager.updateViewLayout(bubbleView, bubbleParams);
        });
        xAnimator.setInterpolator(new BounceInterpolator());
        return xAnimator;
    }

    private ObjectAnimator createScaleAnimator() {
        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f, 0.8f, 1f);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f, 0.8f, 1f);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(bubbleView, scaleXHolder, scaleYHolder);
        scaleAnimator.setInterpolator(new AnticipateOvershootInterpolator(1.5f));
        return scaleAnimator;
    }

    private ObjectAnimator createWobbleAnimator() {
        return ObjectAnimator.ofFloat(bubbleView, "rotation", 0f, -10f, 10f, -5f, 5f, 0f);
    }

    private ObjectAnimator createAlphaAnimator() {
        return ObjectAnimator.ofFloat(bubbleView, "alpha", 1f, 0.8f, 1f);
    }

    private void hideDeleteZone() {
        if (isDeleteZoneVisible) {
            deleteZoneView.animate()
                    .alpha(0f)
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .setDuration(150)
                    .withEndAction(this::removeDeleteZoneView)
                    .start();
        }
    }

    private void removeDeleteZoneView() {
        if (deleteZoneView != null && isDeleteZoneVisible) {
            try {
                windowManager.removeView(deleteZoneView);
                isDeleteZoneVisible = false;
                isDeleteZoneActive = false;
            } catch (IllegalArgumentException e) {
                // View might already be removed
            }
        }
    }

    private void setupClickListener() {
        bubbleView.findViewById(R.id.bubbleCardView).setOnClickListener(v -> {
            provideFeedback(v);
            launchAddWordActivity();
        });
    }

    private void launchAddWordActivity() {
        Intent intent = new Intent(this, AddWordFloatingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanupViews();
        viewModel = null;
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