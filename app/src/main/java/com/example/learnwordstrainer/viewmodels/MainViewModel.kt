package com.example.learnwordstrainer.viewmodels;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.learnwordstrainer.repository.ThemeRepository;
import com.example.learnwordstrainer.repository.WordRepository;

public class MainViewModel extends AndroidViewModel {
    private final WordRepository wordRepository;
    private final ThemeRepository themeRepository;

    private final MutableLiveData<Integer> totalWordsCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> learnedPercentage = new MutableLiveData<>(0);



    public MainViewModel(Application application) {
        super(application);
        wordRepository = new WordRepository(application);
        themeRepository = new ThemeRepository(application);

        // Застосування збереженої теми при старті
        AppCompatDelegate.setDefaultNightMode(themeRepository.getThemeMode());

        // Початкове завантаження статистики
        loadStatistics();
    }

    public LiveData<Integer> getTotalWordsCount() {
        return totalWordsCount;
    }

    public LiveData<Integer> getLearnedPercentage() {
        return learnedPercentage;
    }

    public void loadStatistics() {
        int total = wordRepository.getWordCount();
        int learned = wordRepository.getLearnedWordsCount();

        totalWordsCount.setValue(total);

        int percentage = total > 0 ? (learned * 100) / total : 0;
        learnedPercentage.setValue(percentage);
    }
}