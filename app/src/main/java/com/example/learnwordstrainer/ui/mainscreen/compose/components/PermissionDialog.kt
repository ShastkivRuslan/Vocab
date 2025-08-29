package com.example.learnwordstrainer.ui.mainscreen.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learnwordstrainer.ui.theme.LearnWordsTrainerTheme

@Composable
fun PermissionDialog(
    onConfirmation: () -> Unit,
    onDismiss: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    isDismissForever: Boolean,
    onDismissForeverChange: (Boolean) -> Unit
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Permission Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Column {
                Text(text = dialogText)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isDismissForever,
                        onCheckedChange = onDismissForeverChange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Не показувати більше")
                }
            }
        },
        onDismissRequest = {
            onDismiss()
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

                    onDismiss()
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
            onConfirmation = {},
            onDismiss = {},
            dialogTitle = "Дозвіл на сповіщення",
            dialogText = "Нам потрібен цей дозвіл, щоб надсилати вам нагадування для вивчення слів.",
            icon = Icons.Default.Notifications,
            isDismissForever = true,
            onDismissForeverChange = {}
        )
    }
}
