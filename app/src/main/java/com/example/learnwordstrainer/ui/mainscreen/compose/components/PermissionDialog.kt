package com.example.learnwordstrainer.ui.mainscreen.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.learnwordstrainer.R
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme

@Composable
fun PermissionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Permission Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Продовжити")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Відмовитись")
            }
        }
    )
}

@Preview(showBackground = true, name = "Permission Dialog Preview")
@Composable
fun PermissionDialogPreview() {
    LearnWordsTrainerTheme {
        PermissionDialog(
            onDismissRequest = {},
            onConfirmation = {},
            dialogTitle = "Дозвіл на сповіщення",
            dialogText = "Нам потрібен цей дозвіл, щоб надсилати вам нагадування для вивчення слів.",
            icon = Icons.Default.Notifications
        )
    }
}
