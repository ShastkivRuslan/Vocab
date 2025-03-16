package com.example.learnwordstrainer.viewmodels;

import com.example.learnwordstrainer.model.BubblePosition;
import com.example.learnwordstrainer.repository.BubblePositionRepository;

/**
 * ViewModel for the floating bubble, managing its position and state
 */
public class BubbleViewModel {
    private final BubblePositionRepository repository;
    private BubblePosition currentPosition;

    public BubbleViewModel(BubblePositionRepository repository) {
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
}