package com.shastkiv.vocab.ui.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle = lifecycleRegistry
    override val savedStateRegistry = savedStateRegistryController.savedStateRegistry

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        savedStateRegistryController.performRestore(null)
    }

    fun create() {
        if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }
    }

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

    fun destroy() {
        if (lifecycleRegistry.currentState != Lifecycle.State.DESTROYED) {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }
    }

    fun isActive(): Boolean {
        return lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.CREATED) &&
                lifecycleRegistry.currentState != Lifecycle.State.DESTROYED
    }

    fun getCurrentState(): Lifecycle.State {
        return lifecycleRegistry.currentState
    }
}