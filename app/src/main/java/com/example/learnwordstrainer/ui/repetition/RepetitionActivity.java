package com.example.learnwordstrainer.ui.repetition;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.learnwordstrainer.R;
import com.example.learnwordstrainer.databinding.ActivityRepetitionBinding;

import java.util.Locale;

public class RepetitionActivity extends AppCompatActivity {
    private ActivityRepetitionBinding binding;
    private RepetitionViewModel viewModel;
    private TextToSpeech textToSpeech;

    private View[] answerLayouts;
    private TextView[] variantValues;
    private TextView[] variantNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRepetitionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RepetitionViewModel.class);

        initializeViews();
        initializeTextToSpeech();
        observeViewModel();

        binding.btnListen.setOnClickListener(v -> speak(viewModel.getWordForSpeech()));

        viewModel.startNewRound();
    }

    private void initializeViews() {
        answerLayouts = new View[]{
                binding.layoutAnswer1,
                binding.layoutAnswer2,
                binding.layoutAnswer3,
                binding.layoutAnswer4
        };

        variantValues = new TextView[]{
                binding.tvVariantValue1,
                binding.tvVariantValue2,
                binding.tvVariantValue3,
                binding.tvVariantValue4
        };

        variantNumbers = new TextView[]{
                binding.tvVariantNumber1,
                binding.tvVariantNumber2,
                binding.tvVariantNumber3,
                binding.tvVariantNumber4
        };

        for (int i = 0; i < answerLayouts.length; i++) {
            final int index = i;
            answerLayouts[i].setOnClickListener(v -> handleAnswerSelection(index));
        }

        binding.resultFooterBtn.setOnClickListener(v -> {
            for (View layout : answerLayouts) {
                layout.setEnabled(true);
            }
            hideResultFooterWithAnimation();
            viewModel.hideResultFooter();
            viewModel.startNewRound();
            resetAnswerStyles();
        });
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentWord().observe(this, word -> {
            if (word != null) {
                binding.tvWord.setText(word.getEnglishWord());
            }
        });

        viewModel.getAnswerOptions().observe(this, options -> {
            if (options != null) {
                for (int i = 0; i < Math.min(options.size(), variantValues.length); i++) {
                    variantValues[i].setText(options.get(i));
                }
            }
        });

        viewModel.getCorrectAnswerCount().observe(this, count ->
                binding.tvCorrectCount.setText(String.valueOf(count)));

        viewModel.getWrongAnswerCount().observe(this, count ->
                binding.tvWrongCount.setText(String.valueOf(count)));

        viewModel.getIsRoundCompleted().observe(this, isCompleted -> {
            if (isCompleted) {
                for (View layout : answerLayouts) {
                    layout.setEnabled(false);
                }

                Integer correctIndex = viewModel.getCorrectAnswerIndex().getValue();
                if (correctIndex != null) {
                    variantNumbers[correctIndex].setBackgroundResource(R.drawable.shape_rounded_variants_correct);
                    animateCorrectAnswer(answerLayouts[correctIndex], variantNumbers[correctIndex]);
                }
            }
        });

        viewModel.getIsCorrectAnswer().observe(this, isCorrect -> {
            if (viewModel.getIsRoundCompleted().getValue() == Boolean.TRUE) {
                Integer correctIndex = viewModel.getCorrectAnswerIndex().getValue();
                if (correctIndex != null) {
                    showResultFooter(!isCorrect);
                }
            }
        });
        viewModel.getShowResultFooter().observe(this, show -> {
            if (show) {
                Boolean isCorrect = viewModel.getIsCorrectAnswer().getValue();
                if (isCorrect != null) {
                    showResultFooter(!isCorrect);
                }
            } else {
                binding.resultFooter.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void handleAnswerSelection(int selectedIndex) {
        viewModel.checkAnswer(selectedIndex);

        if (viewModel.getIsCorrectAnswer().getValue() != Boolean.TRUE) {
            variantNumbers[selectedIndex].setBackgroundResource(R.drawable.shape_rounded_variants_wrong);
            shakeAnimation(answerLayouts[selectedIndex]);
        }
    }

    private void resetAnswerStyles() {
        for (int i = 0; i < answerLayouts.length; i++) {
            variantNumbers[i].setBackgroundResource(R.drawable.shape_rounded_variants);
        }
    }

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void animateCorrectAnswer(View layout, TextView variantNumber) {
        ObjectAnimator pulse = ObjectAnimator.ofFloat(layout, View.ALPHA, 1f, 0.7f, 1f);

        // Assuming you're in an Activity
        ObjectAnimator colorFade = ObjectAnimator.ofArgb(layout, "backgroundColor",
                ContextCompat.getColor(this, R.color.card_background),
                ContextCompat.getColor(this, R.color.success),
                ContextCompat.getColor(this, R.color.card_background));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(pulse, colorFade);
        set.setDuration(500);
        set.start();

        variantNumber.setBackgroundResource(R.drawable.shape_rounded_variants_correct);
    }

    private void shakeAnimation(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0, 10, -10, 10, -10, 5, -5, 0);
        shake.setDuration(500);
        shake.start();
    }

    private void showResultFooter(boolean isCorrect) {
        LottieAnimationView animationView = findViewById(R.id.onProcessAnimation);
        if (!isCorrect) {
            binding.resultFooter.setCardBackgroundColor(getColor(R.color.success_transparent));
            binding.resultFooterText.setText(R.string.correct);
            animationView.setAnimation(R.raw.correct_anim);
            animationView.playAnimation();
        } else {
            binding.resultFooter.setCardBackgroundColor(getColor(R.color.wrong_transparent));
            binding.resultFooterText.setText(R.string.wrong);
            animationView.setAnimation(R.raw.wrong_anim);
            animationView.playAnimation();
        }

        showResultFooterWithAnimation();
    }

    private void showResultFooterWithAnimation() {
        binding.resultFooter.setVisibility(View.VISIBLE);
        binding.resultFooter.setAlpha(0f);
        binding.resultFooter.setTranslationY(100f);

        ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                binding.resultFooter,
                "translationY",
                100f,
                0f
        );
        slideUp.setDuration(300);
        slideUp.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                binding.resultFooter,
                "alpha",
                0f,
                1f
        );
        fadeIn.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideUp, fadeIn);
        animatorSet.start();
    }

    private void hideResultFooterWithAnimation() {
        ObjectAnimator slideDown = ObjectAnimator.ofFloat(
                binding.resultFooter,
                "translationY",
                60f,
                200f
        );
        slideDown.setDuration(300);
        slideDown.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                binding.resultFooter,
                "alpha",
                1f,
                0f
        );
        fadeOut.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideDown, fadeOut);
        animatorSet.start();
    }
}