package com.shastkiv.vocab.ui.initialsetup.compose.content

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.shastkiv.vocab.R
import com.shastkiv.vocab.ui.components.LiquidGlassCard
import com.shastkiv.vocab.ui.initialsetup.compose.components.OverlayPermissionDeniedBottomSheet
import com.shastkiv.vocab.ui.initialsetup.compose.components.SetupHeader
import com.shastkiv.vocab.ui.theme.customColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayContent(
    onSkipPressed: () -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var showDeniedSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val overlayLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(context)) {
            onSkipPressed()
        } else {
            showDeniedSheet = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            SetupHeader(
                onBackPressed = onBackPressed,
                title = stringResource(R.string.initial_setup_overlay_title),
                subTitle = stringResource(R.string.initial_setup_overlay_sub_title)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LiquidGlassCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = MaterialTheme.customColors.cardBackground,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.customColors.cardBorder,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Bubble",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.overlay_feature_efficiency_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.customColors.cardTitleText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.overlay_feature_efficiency_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.customColors.cardDescriptionText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            InfoPoint(
                icon = Icons.Default.AutoMode,
                title = stringResource(R.string.overlay_point_social_title),
                description = stringResource(R.string.overlay_point_social_description)
            )
            InfoPoint(
                icon = Icons.Default.AllInclusive,
                title = stringResource(R.string.overlay_point_no_switch_title),
                description = stringResource(R.string.overlay_point_no_switch_description)
            )
            InfoPoint(
                icon = Icons.Default.Translate,
                title = stringResource(R.string.overlay_point_context_title),
                description = stringResource(R.string.overlay_point_context_description)
            )
            InfoPoint(
                icon = Icons.Default.Psychology,
                title = stringResource(R.string.overlay_point_ai_title),
                description = stringResource(R.string.overlay_point_ai_description)
            )
            InfoPoint(
                icon = Icons.Default.AdsClick,
                title = stringResource(R.string.overlay_point_always_near_title),
                description = stringResource(R.string.overlay_point_always_near_description)
            )
            InfoPoint(
                icon = Icons.Default.HistoryEdu,
                title = stringResource(R.string.overlay_point_no_loss_title),
                description = stringResource(R.string.overlay_point_no_loss_description)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            LiquidGlassCard {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.SettingsSuggest,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.overlay_permission_footer_text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.customColors.cardTitleText
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = onSkipPressed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.overlay_skip_button))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:${context.packageName}".toUri()
                    )
                    overlayLauncher.launch(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    stringResource(R.string.overlay_grant_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    if (showDeniedSheet) {
        OverlayPermissionDeniedBottomSheet(
            onDismiss = {
                showDeniedSheet = false
                onSkipPressed()
            },
            onTryAgain = {
                showDeniedSheet = false
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:${context.packageName}".toUri()
                )
                overlayLauncher.launch(intent)
            },
            sheetState = sheetState
        )
    }
}

@Composable
fun InfoPoint(icon: ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    OverlayContent(
        onSkipPressed = {},
        onBackPressed = {}
    )
}