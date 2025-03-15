package com.example.learnwordstrainer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.learnwordstrainer.databinding.ActivityAddWordFloatingBinding;
import com.example.learnwordstrainer.repository.WordRepository;

public class AddWordFloatingActivity extends AppCompatActivity {

    private ActivityAddWordFloatingBinding binding;
    private WordRepository wordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWordFloatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wordRepository = new WordRepository(this.getApplication());

        getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.backgroundDim.setBackgroundColor(Color.parseColor("#80000000")); // 50% прозорість

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );

        animateDialogAppearance();
        setupEventListeners();
    }

    private void setupEventListeners() {
        binding.closeButton.setOnClickListener(v -> {
            animateDialogDisappearance();
        });

        binding.backgroundDim.setOnClickListener(v -> {
            animateDialogDisappearance();
        });

        binding.saveButton.setOnClickListener(v -> {
            saveWord();
        });
    }

    private void saveWord() {
        String wordText = binding.wordEditText.getText().toString().trim();
        String translation = binding.translationEditText.getText().toString().trim();
        if (wordText.isEmpty() || translation.isEmpty()) {
            Toast.makeText(this,"Заповніть всі поля" , Toast.LENGTH_SHORT).show();
            return;
        }
        wordRepository.addWord(wordText, translation);

        Toast.makeText(this, "Слово додано", Toast.LENGTH_SHORT).show();

        animateDialogDisappearance();
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
        cardAnimator.setInterpolator(new OvershootInterpolator(1.2f)); // приємний "пружинний" ефект

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
