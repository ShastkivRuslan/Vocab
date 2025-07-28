package com.example.learnwordstrainer.ui.addwordfloating;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.databinding.ActivityAddWordFloatingBinding;
import com.example.learnwordstrainer.utils.TypewriterEffect;

import java.util.Locale;

/**
 * Activity that displays a floating dialog for adding new words
 * This activity appears as an overlay on top of other apps
 */
public class AddWordFloatingActivity extends AppCompatActivity {

    private ActivityAddWordFloatingBinding binding;
    private AddWordFloatingViewModel viewModel;
    private LottieAnimationView aiLoadingAnimation;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWordFloatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        aiLoadingAnimation = binding.generatingAiResult;

        setupWindowProperties();  // Configure window to appear as overlay
        setupViewModel();         // Initialize the view model
        setupEventListeners();    // Set up button click listeners
        setupOnBackPressed();     // Handle back button press
        observeViewModel();       // Observe LiveData from view model
        animateDialogAppearance(); // Show dialog with animation
        initTextToSpeech();        //initialize TTS
    }

    /**
     * Configure the window to appear as an overlay on top of other apps
     * with transparent background and dim effect
     */
    private void setupWindowProperties() {
        getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);  // Set as overlay
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));   // Transparent background
        binding.backgroundDim.setBackgroundColor(Color.parseColor("#80000000"));   // Semi-transparent black background
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);     // Allow touches outside dialog
    }

    /**
     * Initialize the ViewModel for this activity
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AddWordFloatingViewModel.class);
    }

    /**
     * Set up click listeners for buttons in the dialog
     */
    private void setupEventListeners() {
        binding.closeButton.setOnClickListener(v -> animateDialogDisappearance());  // Close dialog button
        binding.backgroundDim.setOnClickListener(v -> animateDialogDisappearance()); // Close on background tap
        binding.addBtn.setOnClickListener(v -> viewModel.addWord());                // Add word button
        binding.checkBtn.setOnClickListener(v -> handleCheckButtonClick());         // Check word button
        binding.btnListen.setOnClickListener(v -> {
            if (textToSpeech != null) {
                textToSpeech.speak(getCurrentWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    /**
     * Handle click on the check button to look up word information
     * Validates input and calls API to get word info
     */
    private void handleCheckButtonClick() {
        String word = binding.wordEditText.getText() != null ?
                binding.wordEditText.getText().toString().trim() : "";

        // Validate input word
        if (TextUtils.isEmpty(word)) {
            Toast.makeText(this, "Будь ласка, введіть слово", Toast.LENGTH_SHORT).show();
            return;
        }

        if (word.length() > 50) {
            Toast.makeText(this, "Слово занадто довге", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call API to get word information
        viewModel.getAiWordInfo(word);
    }

    /**
     * Configure back button to close the dialog with animation
     */
    private void setupOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                animateDialogDisappearance();
            }
        });
    }

    private String getCurrentWord() {
        return binding.wordEditText.getText() != null ?
                binding.wordEditText.getText().toString().trim() : "";
    }

    /**
     * Set up observers for LiveData from the ViewModel
     */
    private void observeViewModel() {
        viewModel.getWordAdded().observe(this, this::handleWordAdded);       // Word added status
        viewModel.getErrorMessage().observe(this, this::showError);          // Error messages
        viewModel.getExamples().observe(this, this::displayExamples);        // Word examples
        viewModel.getIsLoading().observe(this, this::handleLoadingState);    // Loading state
    }

    /**
     * Handle when a word is successfully added to the database
     * @param success True if word was added successfully
     */
    private void handleWordAdded(Boolean success) {
        if (Boolean.TRUE.equals(success)) {
            Toast.makeText(this, "Слово додано", Toast.LENGTH_SHORT).show();
            animateDialogDisappearance();  // Close dialog after adding word
        }
    }

    /**
     * Display error messages as toasts
     * @param message Error message to display
     */
    private void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display examples and word information
     * Uses typewriter effect for smooth text appearance
     * @param message The word information and examples text
     */
    private void displayExamples(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        binding.titleTextView.setText(getString(R.string.word_to_know, viewModel.getCurrentWord().getValue()));
        binding.btnListen.setVisibility(VISIBLE);
        binding.tilEnglish.setVisibility(GONE);             // Hide input field
        binding.descriptionText.setVisibility(GONE);        //hide description text
        binding.aiResponseText.setVisibility(VISIBLE);      // Show response text
        TypewriterEffect.typeText(binding.aiResponseText, message, () -> {
            binding.addBtn.setVisibility(VISIBLE);// Show add button after text animation
        });
    }

    /**
     * Handle loading state changes
     * Shows/hides loading animation and adjusts UI accordingly
     * @param isLoading True if loading, false otherwise
     */
    private void handleLoadingState(Boolean isLoading) {
        if (Boolean.TRUE.equals(isLoading)) {
            binding.checkBtn.setVisibility(GONE);               // Hide check button
            aiLoadingAnimation.setVisibility(VISIBLE);             // Show loading animation
            binding.descriptionText.setVisibility(GONE);        // Hide description
            aiLoadingAnimation.playAnimation();                    // Start Lottie animation
            clearFocus();                                       // Hide keyboard
        } else {
            binding.descriptionText.setVisibility(VISIBLE);     // Show description
            aiLoadingAnimation.cancelAnimation();                  // Stop animation
            aiLoadingAnimation.setVisibility(GONE);                // Hide animation view
        }
    }

    /**
     * Animate dialog appearance with scale and fade effects
     * Creates a smooth entrance animation
     */
    private void animateDialogAppearance() {
        // Set initial properties
        binding.dialogCardView.setAlpha(0f);
        binding.dialogCardView.setScaleX(0.85f);
        binding.dialogCardView.setScaleY(0.85f);
        binding.backgroundDim.setAlpha(0f);

        // Background fade-in animation
        ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(binding.backgroundDim, "alpha", 0f, 1f);
        backgroundAnimator.setDuration(350);
        backgroundAnimator.setInterpolator(new DecelerateInterpolator());

        // Card scale and fade animation
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.85f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.85f, 1f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);

        ObjectAnimator cardAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.dialogCardView, scaleX, scaleY, alpha);
        cardAnimator.setDuration(400);
        cardAnimator.setInterpolator(new OvershootInterpolator(1.2f));  // Bounce effect

        // Run animations together
        AnimatorSet set = new AnimatorSet();
        set.playTogether(backgroundAnimator, cardAnimator);
        set.start();
    }

    /**
     * Animate dialog disappearance with scale and fade effects
     * Creates a smooth exit animation before finishing the activity
     */
    private void animateDialogDisappearance() {
        // Background fade-out animation
        ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(binding.backgroundDim, "alpha", 1f, 0f);
        backgroundAnimator.setDuration(300);
        backgroundAnimator.setInterpolator(new AccelerateInterpolator());

        // Card scale and fade animation
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.85f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.85f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);

        ObjectAnimator cardAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.dialogCardView, scaleX, scaleY, alpha);
        cardAnimator.setDuration(300);
        cardAnimator.setInterpolator(new AnticipateInterpolator(1.2f));  // Reverse bounce effect

        // Run animations together
        AnimatorSet set = new AnimatorSet();
        set.playTogether(backgroundAnimator, cardAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();  // Close activity when animation completes
                overridePendingTransition(0, 0);  // No activity transition animation
            }
        });
        set.start();
    }

    /**
     * Hide keyboard and clear focus from any focused view
     */
    private void clearFocus() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                view.clearFocus();
            }
        }
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
    }

    /**
     * Clean up resources when activity is destroyed
     */
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
        aiLoadingAnimation = null;  // Prevent memory leaks
        binding = null;          // Release view binding
    }
}