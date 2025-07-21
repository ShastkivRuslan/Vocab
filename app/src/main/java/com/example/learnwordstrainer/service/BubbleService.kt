package com.example.learnwordstrainer.service;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;

import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.ui.activities.AddWordFloatingActivity;
import com.example.learnwordstrainer.ui.activities.MainActivity;
import com.example.learnwordstrainer.model.BubblePosition;
import com.example.learnwordstrainer.repository.BubbleRepository;
import com.example.learnwordstrainer.viewmodels.BubbleViewModel;
import com.google.android.material.card.MaterialCardView;

/**
 * Service responsible for displaying and managing a floating bubble UI element
 * that provides quick access to add new words functionality.
 * Configured as a foreground service to ensure continuous operation.
 */
public class BubbleService extends Service {

    // Constants
    private static final int DRAG_TOLERANCE = 20;
    private static final int MARGIN_HORIZONTAL = 20;
    private static final long ANIMATION_DURATION = 300;
    private static final long CLICK_DURATION_THRESHOLD = 200;
    private static final int DELETION_ZONE_RADIUS = 150;
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "bubble_service_channel";
    private static final String ACTION_STOP_SERVICE = "com.example.learnwordstrainer.STOP_BUBBLE_SERVICE";

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
    private boolean isScreenOn = true;
    private boolean isBubbleAdded = false;
    private BroadcastReceiver screenReceiver;

    private PowerManager.WakeLock wakeLock;
    private static final String ACTION_UPDATE_SETTINGS = "com.example.learnwordstrainer.UPDATE_BUBBLE_SETTINGS";
    private BroadcastReceiver settingsReceiver;

    // MVVM Components
    private BubbleViewModel viewModel;
    private BubbleRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        initRepository();
        initViewModel();
        acquireWakeLock();
        registerScreenReceiver();

        if (isScreenOn) {
            initViews();
            setupWindowManager();
            setupTouchListener();
            setupClickListener();
            loadBubblePosition();

            try {
                windowManager.addView(bubbleView, bubbleParams);
                isBubbleAdded = true;
            } catch (IllegalStateException e) {
                android.util.Log.w("BubbleService", "View already added during onCreate", e);
                isBubbleAdded = true;
            }
        }

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        // Перевіряємо стан екрану та відновлюємо бульбашку якщо потрібно
        if (isScreenOn && bubbleView == null) {
            restoreBubble();
        }

        // Завжди перезапускати сервіс при завершенні
        return START_STICKY;
    }

    private void acquireWakeLock() {
        if (wakeLock == null || !wakeLock.isHeld()) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "BubbleService::WakeLock");
            wakeLock.acquire(10 * 60 * 1000L); // 10 хвилин максимум
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Bubble Service Channel",
                NotificationManager.IMPORTANCE_MIN); // ВАЖЛИВО: IMPORTANCE_MIN замість IMPORTANCE_LOW
        channel.setDescription("Channel for Word Trainer Bubble Service");
        channel.setShowBadge(false);
        channel.setSound(null, null); // Без звуку
        channel.enableVibration(false); // Без вібрації
        channel.enableLights(false); // Без світла
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET); // Не показувати на екрані блокування

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, BubbleService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Vocab.")
                .setContentText(isScreenOn ? "Бульбашка активна" : "Бульбашка призупинена")
                .setSmallIcon(R.drawable.ic_bubble)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_close, "Зупинити", stopPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN) // PRIORITY_MIN - найнижчий пріоритет
                .setVisibility(NotificationCompat.VISIBILITY_SECRET) // Приховати на екрані блокування
                .setSilent(true) // Тихе сповіщення
                .setShowWhen(false) // Не показувати час
                .setOngoing(true) // Постійне сповіщення
                .setCategory(NotificationCompat.CATEGORY_SERVICE) // Категорія сервісу
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initRepository() {
        repository = new BubbleRepository(this);
    }

    private void initViewModel() {
        viewModel = new BubbleViewModel(repository);
    }

    private void initViews() {
        if (bubbleView != null) return; // Уже ініціалізовано

        Context themedContext = new ContextThemeWrapper(this, R.style.Theme_LearnWordsTrainer);
        LayoutInflater inflater = LayoutInflater.from(themedContext);
        bubbleView = inflater.inflate(R.layout.bubble_layout, null, false);
        deleteZoneView = inflater.inflate(R.layout.delete_zone_layout, null);

        int savedSize = viewModel.getSavedBubbleSize(); // потрібно додати цей метод до ViewModel
        setBubbleSize(savedSize > 0 ? savedSize : 40); // 40dp за замовчуванням
    }

    private void setupWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

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
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
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
        if (bubbleParams != null && viewModel != null) {
            BubblePosition position = viewModel.loadSavedPosition();
            bubbleParams.x = position.x;
            bubbleParams.y = position.y;
        }
    }

    private void setupTouchListener() {
        if (bubbleView != null) {
            View bubbleCardView = bubbleView.findViewById(R.id.bubbleCardView);
            bubbleCardView.setOnTouchListener(new BubbleTouchListener());
            bubbleCardView.setOnLongClickListener(view -> {
                provideFeedback(view);
                showQuickActions();
                return true;
            });
        }
    }

    private void setupClickListener() {
        if (bubbleView != null) {
            bubbleView.findViewById(R.id.bubbleCardView).setOnClickListener(v -> {
                provideFeedback(v);
                launchAddWordActivity();
            });
        }
    }

    public void restoreBubble() {
        try {
            if (bubbleView == null) {
                initViews();
                setupWindowManager();
                setupTouchListener();
                setupClickListener();
                loadBubblePosition();
            }

            if (bubbleView != null && windowManager != null && !isBubbleAdded) {
                if (!bubbleView.isAttachedToWindow()) {
                    windowManager.addView(bubbleView, bubbleParams);
                    isBubbleAdded = true;
                }
            }
        } catch (IllegalStateException e) {
            android.util.Log.w("BubbleService", "View already added to window manager", e);
            isBubbleAdded = true;
        } catch (Exception e) {
            android.util.Log.e("BubbleService", "Error restoring bubble", e);
        }
    }

    public void hideBubble() {
        try {
            if (bubbleView != null && windowManager != null && isBubbleAdded) {
                if (bubbleView.isAttachedToWindow()) {
                    windowManager.removeView(bubbleView);
                }
                isBubbleAdded = false;
            }
        } catch (IllegalArgumentException e) {
            android.util.Log.d("BubbleService", "View wasn't in window manager", e);
            isBubbleAdded = false;
        } catch (Exception e) {
            android.util.Log.e("BubbleService", "Error hiding bubble", e);
            isBubbleAdded = false;
        }
        bubbleView = null;
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
        if (bubbleParams != null && windowManager != null && bubbleView != null) {
            bubbleParams.x = x;
            bubbleParams.y = y;
            windowManager.updateViewLayout(bubbleView, bubbleParams);
        }
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
        if (bubbleView == null || deleteZoneView == null) return false;

        int bubbleCenterX = x + bubbleView.getWidth() / 2;
        int bubbleCenterY = y + bubbleView.getHeight() / 2;

        int centerX = screenWidth / 2;
        int deleteZoneY = screenHeight - 100 - deleteZoneView.getHeight() / 2;

        int dx = bubbleCenterX - centerX;
        int dy = bubbleCenterY - deleteZoneY;

        return dx*dx + dy*dy < DELETION_ZONE_RADIUS*DELETION_ZONE_RADIUS;
    }

    private void showDeleteZone() {
        if (!isDeleteZoneVisible && deleteZoneView != null && windowManager != null) {
            try {
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
            } catch (Exception e) {
                // Логування помилки
            }
        }
    }

    private void animateDeleteZoneHighlight() {
        if (deleteZoneView != null) {
            deleteZoneView.animate().cancel();
            deleteZoneView.clearAnimation();

            deleteZoneView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(150)
                    .start();
        }
    }

    private void animateDeleteZoneNormal() {
        if (deleteZoneView != null) {
            deleteZoneView.animate().cancel();
            deleteZoneView.clearAnimation();

            deleteZoneView.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .alpha(0.5f)
                    .setDuration(150)
                    .start();
        }
    }

    private void snapToEdge() {
        if (bubbleView == null) return;

        int bubbleWidth = bubbleView.getMeasuredWidth();
        if (bubbleWidth == 0) bubbleWidth = 40;

        int targetX = calculateTargetX(bubbleWidth);
        int startX = bubbleParams.x;

        AnimatorSet animatorSet = createSnapAnimations(startX, targetX);
        animatorSet.start();
    }

    private int calculateTargetX(int bubbleWidth) {
        // Тепер отримуємо реальну ширину динамічно
        if (bubbleWidth == 0) {
            bubbleWidth = getBubbleSizeDp();
            if (bubbleWidth <= 0) bubbleWidth = 40; // резервне значення
            bubbleWidth = dpToPx(bubbleWidth);
        }

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
            if (bubbleParams != null && viewModel != null && windowManager != null && bubbleView != null) {
                bubbleParams.x = (int) animation.getAnimatedValue();
                viewModel.saveBubblePosition(bubbleParams.x, bubbleParams.y);
                windowManager.updateViewLayout(bubbleView, bubbleParams);
            }
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
        if (deleteZoneView != null) {
            deleteZoneView.animate().cancel();
            deleteZoneView.clearAnimation();

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

    private void launchAddWordActivity() {
        Intent intent = new Intent(this, AddWordFloatingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanupViews();
        releaseWakeLock();
        viewModel = null;

        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
            screenReceiver = null;
        }
    }

    private void cleanupViews() {
        if (bubbleView != null && isBubbleAdded) {
            try {
                if (bubbleView.isAttachedToWindow()) {
                    windowManager.removeView(bubbleView);
                }
            } catch (IllegalArgumentException e) {
                android.util.Log.d("BubbleService", "View wasn't in window manager during cleanup", e);
            }
            isBubbleAdded = false;
            bubbleView = null;
        }

        if (deleteZoneView != null && isDeleteZoneVisible) {
            try {
                windowManager.removeView(deleteZoneView);
            } catch (IllegalArgumentException e) {
                android.util.Log.d("BubbleService", "Delete zone view wasn't in window manager", e);
            }
            deleteZoneView = null;
            isDeleteZoneVisible = false;
            isDeleteZoneActive = false;
        }
    }

    private void registerScreenReceiver() {
        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    isScreenOn = false;
                    hideBubble();
                    releaseWakeLock();
                    updateNotification();
                } else if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_USER_PRESENT.equals(action)) {
                    isScreenOn = true;
                    acquireWakeLock();
                    if (!isBubbleAdded || bubbleView == null || !bubbleView.isAttachedToWindow()) {
                        restoreBubble();
                    }
                    updateNotification();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenReceiver, filter);
    }

    private void updateNotification() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, createNotification());
        }
    }

    private void setBubbleSize(int sizeDp) {
        if (bubbleView == null) return;

        MaterialCardView bubbleCardView = bubbleView.findViewById(R.id.bubbleCardView);
        if (bubbleCardView == null) return;

        int sizePixels = dpToPx(sizeDp);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) bubbleCardView.getLayoutParams();
        layoutParams.width = sizePixels;
        layoutParams.height = sizePixels;

        bubbleCardView.setLayoutParams(layoutParams);

        ImageView imageView = bubbleView.findViewById(R.id.bubbleIcon);
        FrameLayout.LayoutParams imageParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();

        imageParams.width = sizePixels / 2;
        imageParams.height = sizePixels / 2;

        imageView.setLayoutParams(imageParams);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private int getBubbleSizeDp() {
        if (bubbleView == null) return -1;

        MaterialCardView bubbleCardView = bubbleView.findViewById(R.id.bubbleCardView);
        if (bubbleCardView == null) return -1;

        int widthPixels = bubbleCardView.getLayoutParams().width;
        return pxToDp(widthPixels);
    }

    private int pxToDp(int px) {
        return (int) (px / getResources().getDisplayMetrics().density);
    }
}
