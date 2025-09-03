package com.example.learnwordstrainer.ui.mainscreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.flowWithLifecycle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.navigation.AppNavigation
import com.example.learnwordstrainer.service.BubbleService
import com.example.learnwordstrainer.ui.base.BaseActivity
import com.example.learnwordstrainer.ui.mainscreen.compose.components.PermissionDialog
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme
import com.example.learnwordstrainer.ui.widget.UpdateWidgetWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val isGranted = Settings.canDrawOverlays(this)
        viewModel.onEvent(MainScreenEvent.OnPermissionResult(PermissionDialogType.OVERLAY, isGranted))
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onEvent(MainScreenEvent.OnPermissionResult(PermissionDialogType.NOTIFICATION, isGranted))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onEvent(MainScreenEvent.OnCreate)
        }
        scheduleWidgetUpdates()

        setContent {
            val currentTheme by viewModel.themeMode.collectAsState()
            val dialogState by viewModel.permissionDialogState.collectAsState()

            LaunchedEffect(lifecycle) {
                viewModel.effect
                    .flowWithLifecycle(lifecycle)
                    .onEach { effect ->
                        when (effect) {
                            is MainScreenEffect.RequestPermission -> requestPermission(effect.type)
                            is MainScreenEffect.StartService -> startService(effect.type)
                        }
                    }.launchIn(this)
            }

            LearnWordsTrainerTheme(themeMode = currentTheme) {
                AppNavigation(mainViewModel = viewModel)

                dialogState?.let { state ->
                    val dialogDetails = when (state.type) {
                        PermissionDialogType.NOTIFICATION ->
                            Triple(
                                stringResource(R.string.request_notification_permission_dialog_title),
                                stringResource(R.string.request_notification_permission_dialog_text),
                                Icons.Default.Notifications
                            )
                        PermissionDialogType.OVERLAY ->
                            Triple(
                                stringResource(R.string.request_overlay_permission_dialog_title),
                                stringResource(R.string.request_overlay_permission_dialog_text),
                                Icons.Default.AddCircle
                            )
                    }

                    PermissionDialog(
                        onDismiss = {
                            viewModel.onEvent(MainScreenEvent.OnPermissionDialogDismiss(state.type, state.dismissForever))
                        },
                        onConfirmation = {
                            viewModel.onEvent(MainScreenEvent.OnPermissionDialogConfirm(state.type))
                        },
                        dialogTitle = dialogDetails.first,
                        dialogText = dialogDetails.second,
                        icon = dialogDetails.third,
                        isDismissForever = state.dismissForever,
                        onDismissForeverChange = { isChecked ->
                            viewModel.onEvent(MainScreenEvent.OnDismissForeverChanged(state.type, isChecked))
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(MainScreenEvent.OnResume)
    }

    private fun requestPermission(type: PermissionDialogType) {
        when (type) {
            PermissionDialogType.NOTIFICATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            PermissionDialogType.OVERLAY -> {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri())
                overlayPermissionLauncher.launch(intent)
            }
        }
    }

    private fun startService(type: ServiceType) {
        // Створюємо Intent? (nullable Intent)
        val intent: Intent? = when (type) {
            ServiceType.BUBBLE -> Intent(this, BubbleService::class.java)

            // Замість TODO() просто повертаємо null. Код не впаде.
            ServiceType.NOTIFICATION -> null
        }

        // Запускаємо сервіс, тільки якщо intent не є null
        intent?.let {
            startService(it)
        }
    }

    private fun scheduleWidgetUpdates() {
        val workManager = WorkManager.getInstance(applicationContext)

        val periodicRequest = PeriodicWorkRequestBuilder<UpdateWidgetWorker>(
            1, TimeUnit.HOURS
        ).addTag("periodic_update").build()

        workManager.enqueueUniquePeriodicWork(
            "WordWidgetUpdateWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicRequest
        )
    }

}