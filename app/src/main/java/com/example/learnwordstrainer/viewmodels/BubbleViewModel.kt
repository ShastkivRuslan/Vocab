package com.example.learnwordstrainer.viewmodels;

import com.example.learnwordstrainer.model.BubblePosition;
import com.example.learnwordstrainer.repository.BubbleRepository;

/**
 * ViewModel for the floating bubble, managing its position and state
 */
public class BubbleViewModel {
    private final BubbleRepository repository;
    private BubblePosition currentPosition;

    public BubbleViewModel(BubbleRepository repository) {
        this.repository = repository;
    }

    /**
     * Load the saved position from the repository
     */
    public BubblePosition loadSavedPosition() {
        currentPosition = repository.loadPosition();
        return currentPosition;
    }

    /**
     * Save the current position to the repository
     */
    public void saveBubblePosition(int x, int y) {
        repository.savePosition(x, y);
        currentPosition = new BubblePosition(x, y);
    }

    /**
     * Зберігає розмір бульбашки
     * @param sizeDp розмір у dp
     */
    public void saveBubbleSize(int sizeDp) {
        repository.setBubbleSize(sizeDp);
    }

    /**
     * Отримує збережений розмір бульбашки
     * @return розмір у dp
     */
    public int getSavedBubbleSize() {
        return repository.getBubbleSize();
    }


}