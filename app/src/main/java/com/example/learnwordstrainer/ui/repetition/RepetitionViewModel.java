package com.example.learnwordstrainer.ui.repetition;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.example.learnwordstrainer.domain.model.Word;
import com.example.learnwordstrainer.data.repository.WordRepository;

import java.util.List;
import java.util.Random;

public class RepetitionViewModel extends AndroidViewModel {
    //private final WordRepository wordRepository;

    private final MutableLiveData<Word> currentWord = new MutableLiveData<>();
    private final MutableLiveData<List<String>> answerOptions = new MutableLiveData<>();
    private final MutableLiveData<Integer> correctAnswerIndex = new MutableLiveData<>();
    private final MutableLiveData<Integer> correctAnswerCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> wrongAnswerCount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCorrectAnswer = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRoundCompleted = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showResultFooter = new MutableLiveData<>(false);

    public RepetitionViewModel(@NonNull Application application) {
        super(application);
        //wordRepository = new WordRepository(application);
    }

    public LiveData<Word> getCurrentWord() {
        return currentWord;
    }

    public LiveData<List<String>> getAnswerOptions() {
        return answerOptions;
    }

    public LiveData<Integer> getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public LiveData<Integer> getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public LiveData<Integer> getWrongAnswerCount() {
        return wrongAnswerCount;
    }

    public LiveData<Boolean> getIsCorrectAnswer() {
        return isCorrectAnswer;
    }

    public LiveData<Boolean> getIsRoundCompleted() {
        return isRoundCompleted;
    }

    public LiveData<Boolean> getShowResultFooter() {
        return showResultFooter;
    }

//    public void startNewRound() {
//        isRoundCompleted.setValue(false);
//        loadRandomWord();
//        loadAnswerOptions();
//    }
//
//    private void loadRandomWord() {
//        //Word randomWord = wordRepository.getRandomWord();
//        //currentWord.setValue(randomWord);
//        //correctAnswerCount.setValue(randomWord.getCorrectAnswerCount());
//        //setValue(randomWord.getWrongAnswerCount());
//    }
//
//    private void loadAnswerOptions() {
//        Word word = currentWord.getValue();
//        if (word != null) {
//            //List<String> options = wordRepository.getRandomTranslations(word.getId());
//            int index = new Random().nextInt(4);
//            //options.add(index, word.getTranslation());
//
//            //answerOptions.setValue(options);
//            correctAnswerIndex.setValue(index);
//        }
//    }
//
//    public void checkAnswer(int selectedIndex) {
//        Word word = currentWord.getValue();
//        if (word == null) return;
//
//        int correct = word.getCorrectAnswerCount();
//        int wrong = word.getWrongAnswerCount();
//
//        //boolean isCorrect = selectedIndex == correctAnswerIndex.getValue();
//        //if (isCorrect) {
//            correct++;
//            correctAnswerCount.setValue(correct);
//        //} else {
//        //    wrong++;
//            wrongAnswerCount.setValue(wrong);
//        }
//
//        wordRepository.updateScore(word.getId(), correct, wrong);
//        isCorrectAnswer.setValue(isCorrect);
//        isRoundCompleted.setValue(true);
//        showResultFooter.setValue(true);
//    }
//
//    public void hideResultFooter() {
//        showResultFooter.setValue(false);
//    }
//
//    public String getWordForSpeech() {
//        Word word = currentWord.getValue();
//        return word != null ? word.getEnglishWord() : "";
//    }
}