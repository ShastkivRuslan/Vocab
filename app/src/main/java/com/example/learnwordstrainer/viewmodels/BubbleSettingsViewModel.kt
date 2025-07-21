package com.example.learnwordstrainer.viewmodels;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.learnwordstrainer.repository.BubbleRepository;
import com.example.learnwordstrainer.service.BubbleService;

public class BubbleSettingsViewModel extends AndroidViewModel {
    private BubbleRepository bubbleRepository;

    private final MutableLiveData<Boolean> isBubbleEnabled = new MutableLiveData<>();
    private final MutableLiveData<Integer> bubbleSize = new MutableLiveData<>();

    public BubbleSettingsViewModel(@NonNull Application application) {
        super(application);

        bubbleRepository = new BubbleRepository(application);
        isBubbleEnabled.setValue(bubbleRepository.isBubbleEnabled());
        bubbleSize.setValue(bubbleRepository.getBubbleSize());
    }

    public LiveData<Integer> getBubbleSize() {
        return bubbleSize;
    }

    public LiveData<Boolean> getIsBubbleEnabled() {
        return isBubbleEnabled;
    }

    public void toggleBubbleSwitch() {
        boolean newState = bubbleRepository.toggleBubble();
        isBubbleEnabled.setValue(newState);

        // Керуємо сервісом залежно від стану
        if (newState) {
            startBubbleService();
        } else {
            stopBubbleService();
        }
    }

    private void startBubbleService() {
        try {
            Intent serviceIntent = new Intent(getApplication(), BubbleService.class);
            getApplication().startForegroundService(serviceIntent);
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Failed to start bubble service", e);
        }
    }

    private void stopBubbleService() {
        Intent serviceIntent = new Intent(getApplication(), BubbleService.class);
        getApplication().stopService(serviceIntent);
    }

    /**
     * Зберігає розмір бульбашки
     * @param sizeDp розмір у dp
     */
    public void saveBubbleSize(int sizeDp) {
        bubbleRepository.setBubbleSize(sizeDp);
        bubbleSize.setValue(sizeDp);
    }

    /**
     * Отримує збережений розмір бульбашки
     * @return розмір у dp
     */
    public int getSavedBubbleSize() {
        return bubbleRepository.getBubbleSize();
    }

}
