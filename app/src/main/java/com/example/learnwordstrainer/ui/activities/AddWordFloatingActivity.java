package com.example.learnwordstrainer.ui.activities;

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
import com.example.learnwordstrainer.databinding.ActivityAddWordFloatingBinding;
import com.example.learnwordstrainer.utils.TypewriterEffect;
import com.example.learnwordstrainer.viewmodels.AddWordFloatingViewModel;

public class AddWordFloatingActivity extends AppCompatActivity {

    private ActivityAddWordFloatingBinding binding;
    private AddWordFloatingViewModel viewModel;
    private LottieAnimationView typingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWordFloatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        typingAnimation = binding.generatingAiResult;

        setupWindowProperties();
        setupViewModel();
        setupEventListeners();
        setupOnBackPressed();
        observeViewModel();
        animateDialogAppearance();
    }

    private void setupWindowProperties() {
        getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.backgroundDim.setBackgroundColor(Color.parseColor("#80000000"));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AddWordFloatingViewModel.class);
    }

    private void setupEventListeners() {
        binding.closeButton.setOnClickListener(v -> animateDialogDisappearance());
        binding.backgroundDim.setOnClickListener(v -> animateDialogDisappearance());
        binding.addBtn.setOnClickListener(v -> viewModel.addWord());
        binding.checkBtn.setOnClickListener(v -> handleCheckButtonClick());
    }

    private void handleCheckButtonClick() {
        String word = binding.wordEditText.getText() != null ?
                binding.wordEditText.getText().toString().trim() : "";

        if (TextUtils.isEmpty(word)) {
            Toast.makeText(this, "Будь ласка, введіть слово", Toast.LENGTH_SHORT).show();
            return;
        }

        if (word.length() > 50) {
            Toast.makeText(this, "Слово занадто довге", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.getAiWordInfo(word);
    }

    private void setupOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                animateDialogDisappearance();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getWordAdded().observe(this, this::handleWordAdded);
        viewModel.getErrorMessage().observe(this, this::showError);
        viewModel.getExamples().observe(this, this::displayExamples);
        viewModel.getIsLoading().observe(this, this::handleLoadingState);
    }

    private void handleWordAdded(Boolean success) {
        if (Boolean.TRUE.equals(success)) {
            Toast.makeText(this, "Слово додано", Toast.LENGTH_SHORT).show();
            animateDialogDisappearance();
        }
    }

    private void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayExamples(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        binding.tilEnglish.setVisibility(GONE);
        binding.aiResponseText.setVisibility(VISIBLE);
        TypewriterEffect.typeText(binding.aiResponseText, message, () -> {
            binding.addBtn.setVisibility(VISIBLE);
        });
    }

    private void handleLoadingState(Boolean isLoading) {
        if (Boolean.TRUE.equals(isLoading)) {
            binding.checkBtn.setVisibility(GONE);
            typingAnimation.setVisibility(VISIBLE);
            typingAnimation.playAnimation();
            clearFocus();
        } else {
            binding.descriptionText.setVisibility(GONE);
            typingAnimation.cancelAnimation();
            typingAnimation.setVisibility(GONE);
        }
    }

    private void animateDialogAppearance() {
        binding.dialogCardView.setAlpha(0f);
        binding.dialogCardView.setScaleX(0.85f);
        binding.dialogCardView.setScaleY(0.85f);
        binding.backgroundDim.setAlpha(0f);

        ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(binding.backgroundDim, "alpha", 0f, 1f);
        backgroundAnimator.setDuration(350);
        backgroundAnimator.setInterpolator(new DecelerateInterpolator());

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.85f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.85f, 1f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);

        ObjectAnimator cardAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.dialogCardView, scaleX, scaleY, alpha);
        cardAnimator.setDuration(400);
        cardAnimator.setInterpolator(new OvershootInterpolator(1.2f));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(backgroundAnimator, cardAnimator);
        set.start();
    }

    private void animateDialogDisappearance() {
        ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(binding.backgroundDim, "alpha", 1f, 0f);
        backgroundAnimator.setDuration(300);
        backgroundAnimator.setInterpolator(new AccelerateInterpolator());

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.85f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.85f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);

        ObjectAnimator cardAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.dialogCardView, scaleX, scaleY, alpha);
        cardAnimator.setDuration(300);
        cardAnimator.setInterpolator(new AnticipateInterpolator(1.2f));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(backgroundAnimator, cardAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        set.start();
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        typingAnimation = null;
        binding = null;
    }
}