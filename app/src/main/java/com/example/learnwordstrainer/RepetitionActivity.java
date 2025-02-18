package com.example.learnwordstrainer;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.learnwordstrainer.databinding.ActivityRepetitionBinding;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RepetitionActivity extends AppCompatActivity {
    private ActivityRepetitionBinding repetitionBinding;
    private DatabaseHelper dbHelper;
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
        initializeDatabase();

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

    private void initializeDatabase() {
        dbHelper = new DatabaseHelper(this);
    }

    private void startNewRound() {
        resetAnswerStyles();
        loadRandomWord();
        loadWrongAnswers();
    }

    private void resetAnswerStyles() {
        for (int i = 0; i < answerLayouts.length; i++) {
            variantNumbers[i].setBackgroundResource(R.drawable.shape_rounded_variants);
            answerLayouts[i].setBackgroundResource(R.drawable.shape_rounded_container);
        }
    }

    private void handleAnswerSelection(int selectedIndex) {
        for (View layout : answerLayouts) {
            layout.setEnabled(false);
        }

        variantNumbers[correctAnswerIndex].setBackgroundResource(R.drawable.shape_rounded_variants_correct);
        answerLayouts[correctAnswerIndex].setBackgroundResource(R.drawable.shape_rounded_container_correct);

        if (selectedIndex != correctAnswerIndex) {
            variantNumbers[selectedIndex].setBackgroundResource(R.drawable.shape_rounded_variants_wrong);
            answerLayouts[selectedIndex].setBackgroundResource(R.drawable.shape_rounded_container_wrong);
            currentWrongAnswerCount++;
            repetitionBinding.tvWrongCount.setText(String.valueOf(currentWrongAnswerCount));
        } else {
            currentCorrectAnswerCount++;
            repetitionBinding.tvCorrectCount.setText(String.valueOf(currentCorrectAnswerCount));
        }

        dbHelper.updateScore(
                currentWordId,
                currentCorrectAnswerCount,
                currentWrongAnswerCount
        );


        new Handler().postDelayed(() -> {
            WordResultDialog dialog = new WordResultDialog(
                    this,
                    selectedIndex == correctAnswerIndex,
                    () -> {

                        for (View layout : answerLayouts) {
                            layout.setEnabled(true);
                        }
                        startNewRound();
                    });
            dialog.show();
        }, 500);
    }

    private void loadWrongAnswers() {
        List<String> answers = dbHelper.getRandomTranslations(currentWordId);
        correctAnswerIndex = new Random().nextInt(4);
        answers.add(correctAnswerIndex, currentTranslation);

        for (int i = 0; i < variantValues.length; i++) {
            variantValues[i].setText(answers.get(i));
        }
    }

    private void loadRandomWord() {
        try (Cursor cursor = dbHelper.getRandomWord()) {
            if (cursor != null && cursor.moveToFirst()) {
                currentWordId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                currentWord = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ENGLISH));
                currentTranslation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRANSLATION));
                currentCorrectAnswerCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CORRECT_ANSWER_COUNT));
                currentWrongAnswerCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WRONG_ANSWER_COUNT));

                repetitionBinding.tvWord.setText(currentWord);
                repetitionBinding.tvCorrectCount.setText(String.valueOf(currentCorrectAnswerCount));
                repetitionBinding.tvWrongCount.setText(String.valueOf(currentWrongAnswerCount));
            }
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
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}