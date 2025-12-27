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
import com.shastkiv.vocab.ui.common.compose.AboutBubbleContent
import com.shastkiv.vocab.ui.common.compose.OverlayPermissionAlert
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

            AboutBubbleContent()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OverlayPermissionAlert()

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

@Preview(showBackground = true)
@Composable
private fun Preview() {
    OverlayContent(
        onSkipPressed = {},
        onBackPressed = {}
    )
}