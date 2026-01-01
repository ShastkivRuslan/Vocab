package dev.shastkiv.vocab.ui.mainscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.shastkiv.vocab.navigation.AppNavigation
import dev.shastkiv.vocab.service.bubble.BubbleService
import dev.shastkiv.vocab.ui.base.BaseActivity
import dev.shastkiv.vocab.ui.theme.LearnWordsTrainerTheme
import dev.shastkiv.vocab.ui.widget.UpdateWidgetWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        scheduleWidgetUpdates()

        setContent {
            val currentTheme by viewModel.themeMode.collectAsState()

            LaunchedEffect(lifecycle) {
                viewModel.effect
                    .flowWithLifecycle(lifecycle)
                    .onEach { effect ->
                        when (effect) {
                            is MainScreenEffect.StartService -> startService(effect.type)
                        }
                    }.launchIn(this)
            }

            LearnWordsTrainerTheme(themeMode = currentTheme) {
                AppNavigation(mainViewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun startService(type: ServiceType) {
        val intent: Intent? = when (type) {
            ServiceType.BUBBLE -> Intent(this, BubbleService::class.java)
            ServiceType.NOTIFICATION -> null
        }
        intent?.let { startService(it) }
    }

    private fun scheduleWidgetUpdates() {
        val workManager = WorkManager.getInstance(applicationContext)

        val periodicRequest = PeriodicWorkRequestBuilder<UpdateWidgetWorker>(
            1, TimeUnit.HOURS
        ).addTag("periodic_update").build()

        workManager.enqueueUniquePeriodicWork(
            "WordWidgetUpdateWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )
    }

}