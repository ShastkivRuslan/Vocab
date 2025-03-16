package com.example.learnwordstrainer.ui.activities;

import android.animation.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.*;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import com.example.learnwordstrainer.databinding.ActivityAddWordFloatingBinding;
import com.example.learnwordstrainer.viewmodels.AddWordFloatingViewModel;

public class AddWordFloatingActivity extends AppCompatActivity {

    private ActivityAddWordFloatingBinding binding;
    private AddWordFloatingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWordFloatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.backgroundDim.setBackgroundColor(Color.parseColor("#80000000"));
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );

        viewModel = new ViewModelProvider(this).get(AddWordFloatingViewModel.class);

        animateDialogAppearance();
        setupEventListeners();
        observeViewModel();
    }

    private void setupEventListeners() {
        binding.closeButton.setOnClickListener(v -> animateDialogDisappearance());
        binding.backgroundDim.setOnClickListener(v -> animateDialogDisappearance());
        binding.saveButton.setOnClickListener(v -> {
            String word = binding.wordEditText.getText().toString().trim();
            String translation = binding.translationEditText.getText().toString().trim();
            viewModel.addWord(word, translation);
        });
    }

    private void observeViewModel() {
        viewModel.wordAdded.observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Слово додано", Toast.LENGTH_SHORT).show();
                animateDialogDisappearance();
            }
        });

        viewModel.errorMessage.observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        );
    }

    private void animateDialogAppearance() {
        CardView dialogCard = binding.dialogCardView;
        dialogCard.setAlpha(0f);
        dialogCard.setScaleX(0.85f);
        dialogCard.setScaleY(0.85f);
        binding.backgroundDim.setAlpha(0f);

        ObjectAnimator backgroundAnimator = ObjectAnimator.ofFloat(binding.backgroundDim, "alpha", 0f, 1f);
        backgroundAnimator.setDuration(350);
        backgroundAnimator.setInterpolator(new DecelerateInterpolator());

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.85f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.85f, 1f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);

        ObjectAnimator cardAnimator = ObjectAnimator.ofPropertyValuesHolder(dialogCard, scaleX, scaleY, alpha);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animateDialogDisappearance();
    }
}

