package dev.shastkiv.vocab.ui.initialsetup.compose.content

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import dev.shastkiv.vocab.R
import dev.shastkiv.vocab.ui.common.compose.AboutBubbleContent
import dev.shastkiv.vocab.ui.common.compose.OverlayPermissionAlert
import dev.shastkiv.vocab.ui.initialsetup.compose.components.OverlayPermissionDeniedBottomSheet
import dev.shastkiv.vocab.ui.initialsetup.compose.components.SetupHeader

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