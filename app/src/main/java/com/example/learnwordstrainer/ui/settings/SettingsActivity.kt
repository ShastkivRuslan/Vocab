package com.example.learnwordstrainer.ui.settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.domain.model.ThemeMode
import com.example.learnwordstrainer.ui.bubblesettings.BubbleSettingsActivity
import com.example.learnwordstrainer.ui.settings.compose.SettingsScreen
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    private val viewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            val currentThemeMode by viewModel.currentTheme.collectAsState()
            LearnWordsTrainerTheme(
                themeMode = currentThemeMode
            ) {
                SettingsScreen(
                    onBackPressed = {
                      finish()
                    },
                    onThemeClick = {
                        showThemeDialog()
                    },
                    onLanguageClick = {
                        //todo: show language settings
                        Toast.makeText(this@SettingsActivity, "Language is pressed", Toast.LENGTH_SHORT).show()
                    },
                    onBubbleSettingsClick = {
                        val intent = Intent(this@SettingsActivity, BubbleSettingsActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                            },
                    onNotificationClick = {
                        //todo: show notification settings
                        Toast.makeText(this@SettingsActivity, "Notification is pressed", Toast.LENGTH_SHORT).show()
                    },
                    onAboutClick = {
                        //todo: show about
                        Toast.makeText(this@SettingsActivity, "About is pressed", Toast.LENGTH_SHORT).show()

                    }
                )
            }
        }
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Системна", "Світла", "Темна")
        val currentThemeValue = viewModel.currentTheme.value
        var selectedTheme = 0

        when (currentThemeValue) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {}
            AppCompatDelegate.MODE_NIGHT_NO -> selectedTheme = 1
            AppCompatDelegate.MODE_NIGHT_YES -> selectedTheme = 2
        }

        AlertDialog.Builder(this)
            .setTitle("Виберіть тему")
            .setSingleChoiceItems(themes, selectedTheme) { dialog: DialogInterface, which: Int ->
                val selectedThemeMode = when (which) {
                    1 -> ThemeMode.LIGHT
                    2 -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                viewModel.setTheme(selectedThemeMode)
                dialog.dismiss()
            }
            .show()
    }
}
