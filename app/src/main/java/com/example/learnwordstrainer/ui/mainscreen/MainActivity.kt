package com.example.learnwordstrainer.ui.mainscreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.flowWithLifecycle
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.service.BubbleService
import com.example.learnwordstrainer.service.NotificationService
import com.example.learnwordstrainer.ui.addword.AddWordActivity
import com.example.learnwordstrainer.ui.mainscreen.compose.MainScreen
import com.example.learnwordstrainer.ui.mainscreen.compose.components.PermissionDialog
import com.example.learnwordstrainer.ui.practice.PracticeActivity
import com.example.learnwordstrainer.ui.repetition.RepetitionActivity
import com.example.learnwordstrainer.ui.settings.SettingsActivity
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme
import com.example.learnwordstrainer.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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

        setContent {
            val currentTheme by viewModel.themeMode.collectAsState()
            var dialogState by remember { mutableStateOf<PermissionDialogType?>(null) }
            var shouldDismissForever by remember { mutableStateOf(false) }

            LaunchedEffect(lifecycle) {
                viewModel.effect
                    .flowWithLifecycle(lifecycle)
                    .onEach { effect ->
                        when (effect) {
                            is MainScreenEffect.Navigate -> handleNavigation(effect.route)
                            is MainScreenEffect.RequestPermission -> requestPermission(effect.type)
                            is MainScreenEffect.StartService -> startService(effect.type)
                            is MainScreenEffect.ShowPermissionDialog -> {
                                dialogState = effect.type
                                shouldDismissForever = false
                            }
                        }
                    }.launchIn(this)
            }

            LearnWordsTrainerTheme(themeMode = currentTheme) {
                MainScreen(
                    viewModel = viewModel,
                    onEvent = viewModel::onEvent
                )

                dialogState?.let { type ->
                    val dialogDetails = when (type) {
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
                            viewModel.onEvent(MainScreenEvent.OnPermissionDialogDismiss(type, shouldDismissForever))
                            dialogState = null
                            shouldDismissForever = false
                        },
                        onConfirmation = {
                            viewModel.onEvent(MainScreenEvent.OnPermissionDialogConfirm(type))
                            dialogState = null
                            shouldDismissForever = false
                        },
                        dialogTitle = dialogDetails.first,
                        dialogText = dialogDetails.second,
                        icon = dialogDetails.third,
                        isDismissForever = shouldDismissForever,
                        onDismissForeverChange = { shouldDismissForever = it }
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
        val intent = when (type) {
            ServiceType.NOTIFICATION -> {
                NotificationHelper.createNotificationChannel(this)
                Intent(this, NotificationService::class.java)
            }
            ServiceType.BUBBLE -> Intent(this, BubbleService::class.java)
        }
        startService(intent)
    }

    private fun handleNavigation(route: String) {
        val intent = when (route) {
            "settings" -> Intent(this, SettingsActivity::class.java)
            "add_word" -> Intent(this, AddWordActivity::class.java)
            "repetition" -> Intent(this, RepetitionActivity::class.java)
            "practice" -> Intent(this, PracticeActivity::class.java)
            "all_words" -> {
                Toast.makeText(this, "Екран 'Всі слова' в розробці", Toast.LENGTH_SHORT).show()
                null
            }
            else -> null
        }
        intent?.let(::startActivity)
    }
}