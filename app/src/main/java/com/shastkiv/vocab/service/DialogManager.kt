package com.shastkiv.vocab.service

import android.content.Context
import android.util.Log
import com.shastkiv.vocab.ui.addword.overlay.DialogViewManager
import com.shastkiv.vocab.ui.addword.shared.AddWordViewModel
import com.shastkiv.vocab.ui.lifecycle.OverlayLifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DialogManager - manages word dialog functionality using ViewModel Factory pattern.
 * Handles: dialog display, word processing, complex UI state management.
 *
 * Uses ViewModel Factory pattern because dialog has complex UI state management:
 * - Multiple UI states (Loading, Success, Error, SavingWord)
 * - Complex animations and transitions
 * - Coordination of multiple UseCase
 * - Reactive StateFlow for Compose UI
 * - Lifecycle-aware operations
 *
 * This pattern is justified for complex UI components that need:
 * sophisticated state management beyond simple CRUD operations.
 */
@Singleton
class DialogManager @Inject constructor(
    private val addWordViewModelFactory: AddWordViewModel.Factory
) {

    private var dialogViewManager: DialogViewManager? = null
    private val viewModel: AddWordViewModel by lazy {
        addWordViewModelFactory.create()
    }

    fun showDialog(
        context: Context,
        overlayLifecycleOwner: OverlayLifecycleOwner,
        initialText: String? = null
    ) {
        try {
            if (dialogViewManager == null) {
                dialogViewManager = DialogViewManager(
                    context = context,
                    overlayLifecycleOwner = overlayLifecycleOwner,
                    viewModel = viewModel
                )
            }

            dialogViewManager?.show(initialText)
            Log.d(TAG, "Dialog shown successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to show dialog", e)
        }
    }

    fun hideDialog() {
        try {
            dialogViewManager?.hide()
            viewModel.resetState()

            Log.d(TAG, "Dialog hidden successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to hide dialog", e)
        }
    }

    fun destroy() {
        Log.d(TAG, "Destroying DialogManager")

        try {
            hideDialog()
            dialogViewManager = null
        } catch (e: Exception) {
            Log.e(TAG, "Error during DialogManager destruction", e)
        }
    }

    companion object {
        private const val TAG = "DialogManager"
    }
}