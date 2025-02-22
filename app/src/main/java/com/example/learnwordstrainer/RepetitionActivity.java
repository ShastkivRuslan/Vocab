package com.example.learnwordstrainer;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.learnwordstrainer.databinding.ActivityRepetitionBinding;
import com.example.learnwordstrainer.model.Word;
import com.example.learnwordstrainer.repository.WordRepository;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RepetitionActivity extends AppCompatActivity {
    private ActivityRepetitionBinding repetitionBinding;

    private WordRepository wordRepository;
    private TextToSpeech textToSpeech;

    private int currentWordId;
    private String currentWord;
    private String currentTranslation;
    private int currentCorrectAnswerCount;
    private int currentWrongAnswerCount;
    private int correctAnswerIndex;

    private View[] answerLayouts;
    private TextView[] variantValues;
    private TextView[] variantNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repetitionBinding = ActivityRepetitionBinding.inflate(getLayoutInflater());
        setContentView(repetitionBinding.getRoot());

        initializeViews();
        initializeTextToSpeech();
        wordRepository = new WordRepository(getApplication());

        repetitionBinding.btnListen.setOnClickListener(v -> speak(currentWord));

        startNewRound();
    }

    private void initializeViews() {
        answerLayouts = new View[]{
                repetitionBinding.layoutAnswer1,
                repetitionBinding.layoutAnswer2,
                repetitionBinding.layoutAnswer3,
                repetitionBinding.layoutAnswer4
        };

        variantValues = new TextView[]{
                repetitionBinding.tvVariantValue1,
                repetitionBinding.tvVariantValue2,
                repetitionBinding.tvVariantValue3,
                repetitionBinding.tvVariantValue4
        };

        variantNumbers = new TextView[]{
                repetitionBinding.tvVariantNumber1,
                repetitionBinding.tvVariantNumber2,
                repetitionBinding.tvVariantNumber3,
                repetitionBinding.tvVariantNumber4
        };

        for (int i = 0; i < answerLayouts.length; i++) {
            final int index = i;
            answerLayouts[i].setOnClickListener(v -> handleAnswerSelection(index));
        }
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
    }

    private void startNewRound() {
        resetAnswerStyles();
        loadRandomWord();
        loadWrongAnswers();
    }

    private void resetAnswerStyles() {
        for (int i = 0; i < answerLayouts.length; i++) {
            variantNumbers[i].setBackgroundResource(R.drawable.shape_rounded_variants);
        }
    }

    private void handleAnswerSelection(int selectedIndex) {
        for (View layout : answerLayouts) {
            layout.setEnabled(false);
        }

        variantNumbers[correctAnswerIndex].setBackgroundResource(R.drawable.shape_rounded_variants_correct);
        animateCorrectAnswer(answerLayouts[correctAnswerIndex], variantNumbers[correctAnswerIndex]);

        if (selectedIndex != correctAnswerIndex) {
            variantNumbers[selectedIndex].setBackgroundResource(R.drawable.shape_rounded_variants_wrong);
            shakeAnimation(answerLayouts[selectedIndex]);
            currentWrongAnswerCount++;
            repetitionBinding.tvWrongCount.setText(String.valueOf(currentWrongAnswerCount));
        } else {
            currentCorrectAnswerCount++;
            repetitionBinding.tvCorrectCount.setText(String.valueOf(currentCorrectAnswerCount));
        }

        wordRepository.updateScore(
                currentWordId,
                currentCorrectAnswerCount,
                currentWrongAnswerCount
        );

        showResultFooter(selectedIndex != correctAnswerIndex);
    }

    private void loadWrongAnswers() {
        List<String> answers = wordRepository.getRandomTranslations(currentWordId);
        correctAnswerIndex = new Random().nextInt(4);
        answers.add(correctAnswerIndex, currentTranslation);

        for (int i = 0; i < variantValues.length; i++) {
            variantValues[i].setText(answers.get(i));
        }
    }

    private void showResultFooter(boolean isCorrect) {
        if (!isCorrect) {
            repetitionBinding.resultFooter.setCardBackgroundColor(getColor(R.color.success_transparent));
            repetitionBinding.resultFooterIcon.setBackgroundResource(R.drawable.ic_check);
            repetitionBinding.resultFooterText.setText(R.string.correct);
        } else {
            repetitionBinding.resultFooter.setCardBackgroundColor(getColor(R.color.wrong_transparent));
            repetitionBinding.resultFooterIcon.setBackgroundResource(R.drawable.ic_close);
            repetitionBinding.resultFooterText.setText(R.string.wrong);
        }
        repetitionBinding.resultFooterBtn.setOnClickListener(v -> {
            for (View layout : answerLayouts) {
                layout.setEnabled(true);
            }
            startNewRound();
            hideResultFooterWithAnimation();
        });
        showResultFooterWithAnimation();
    }

    private void loadRandomWord() {

        Word randomWord = wordRepository.getRandomWord();
        currentWordId = randomWord.getId();
        currentWord = randomWord.getEnglishWord();
        currentTranslation = randomWord.getTranslation();
        currentCorrectAnswerCount = randomWord.getCorrectAnswerCount();
        currentWrongAnswerCount = randomWord.getWrongAnswerCount();

        repetitionBinding.tvWord.setText(currentWord);
        repetitionBinding.tvCorrectCount.setText(String.valueOf(currentCorrectAnswerCount));
        repetitionBinding.tvWrongCount.setText(String.valueOf(currentWrongAnswerCount));
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
        ObjectAnimator colorFade = ObjectAnimator.ofArgb(layout, "backgroundColor",
                getResources().getColor(R.color.card_background),
                getResources().getColor(R.color.success),
                getResources().getColor(R.color.card_background));

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

    private void showResultFooterWithAnimation() {
        repetitionBinding.resultFooter.setVisibility(View.VISIBLE);
        repetitionBinding.resultFooter.setAlpha(0f);
        repetitionBinding.resultFooter.setTranslationY(200f);

        ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                repetitionBinding.resultFooter,
                "translationY",
                200f,
                60f











        );
        slideUp.setDuration(300);
        slideUp.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                repetitionBinding.resultFooter,
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
                repetitionBinding.resultFooter,
                "translationY",
                60f,
                200f
        );
        slideDown.setDuration(300);
        slideDown.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                repetitionBinding.resultFooter,
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