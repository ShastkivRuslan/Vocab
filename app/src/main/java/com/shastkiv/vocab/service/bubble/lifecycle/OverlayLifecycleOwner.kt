package com.shastkiv.vocab.service.bubble.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Custom LifecycleOwner for overlay views that exist outside of the standard Activity/Fragment context.
 * It provides the necessary Lifecycle, ViewModelStore, and SavedStateRegistry for Compose and ViewModels
 * to function correctly in a system window.
 */
class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val viewModelStore = ViewModelStore()

    override val lifecycle: Lifecycle = lifecycleRegistry
    override val savedStateRegistry = savedStateRegistryController.savedStateRegistry

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        savedStateRegistryController.performRestore(null)
    }

    /**
     * Transitions the lifecycle to the CREATED state.
     */
    fun create() {
        if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }
    }

    /**
     * Moves the lifecycle to the STARTED state.
     */
    fun start() {
        when (lifecycleRegistry.currentState) {
            Lifecycle.State.INITIALIZED -> {
                lifecycleRegistry.currentState = Lifecycle.State.CREATED
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
            }
            Lifecycle.State.CREATED -> {
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
            }
            else -> { /* Already started or higher */ }
        }
    }

    /**
     * Moves the lifecycle to the RESUMED state, enabling UI interactions and animations.
     */
    fun resume() {
        when (lifecycleRegistry.currentState) {
            Lifecycle.State.INITIALIZED -> {
                lifecycleRegistry.currentState = Lifecycle.State.CREATED
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
                lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            }
            Lifecycle.State.CREATED -> {
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
                lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            }
            Lifecycle.State.STARTED -> {
                lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            }
            else -> { /* Already resumed or destroyed */ }
        }
    }

    /**
     * Safely destroys the lifecycle.
     * Uses handleLifecycleEvent to prevent IllegalStateException by ensuring
     * correct state transitions even if called before the view is fully initialized.
     */
    fun destroy() {
        val currentState = lifecycleRegistry.currentState
        if (currentState == Lifecycle.State.DESTROYED) return
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

        viewModelStore.clear()
    }
}