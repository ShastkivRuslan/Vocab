package com.example.learnwordstrainer.viewmodels;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.learnwordstrainer.model.ThemeMode;
import com.example.learnwordstrainer.repository.ThemeRepository;
import com.example.learnwordstrainer.repository.WordRepository;

public class MainViewModel extends AndroidViewModel {
    private final WordRepository wordRepository;
    private final ThemeRepository themeRepository;

    private final MutableLiveData<Integer> totalWordsCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> learnedPercentage = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> currentTheme = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        wordRepository = new WordRepository(application);
        themeRepository = new ThemeRepository(application);

        // Завантаження збереженої теми
        currentTheme.setValue(themeRepository.getThemeMode());

        // Початкове завантаження статистики
        loadStatistics();
    }

    public LiveData<Integer> getTotalWordsCount() {
        return totalWordsCount;
    }

    public LiveData<Integer> getLearnedPercentage() {
        return learnedPercentage;
    }

    public LiveData<Integer> getCurrentTheme() {
        return currentTheme;
    }

    public int getCurrentThemeValue() {
        return currentTheme.getValue() != null ? currentTheme.getValue() : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }

    public void loadStatistics() {
        int total = wordRepository.getWordCount();
        int learned = wordRepository.getLearnedWordsCount();

        totalWordsCount.setValue(total);

        int percentage = total > 0 ? (learned * 100) / total : 0;
        learnedPercentage.setValue(percentage);
    }

    public void setTheme(ThemeMode themeMode) {
        int mode;
        switch (themeMode) {
            case LIGHT:
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case DARK:
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case SYSTEM:
            default:
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }

        themeRepository.saveThemeMode(mode);
        currentTheme.setValue(mode);
    }
}