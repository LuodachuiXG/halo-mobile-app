package cc.loac.kalo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


/**
 * 进度条对话框
 */
@Composable
fun String.progressAlert() {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(this)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(56.dp)
                )
            }
        },
        confirmButton = {}
    )
}


/**
 * 对话框
 * @param onConfirm 确定按钮点击事件
 */
@Composable
fun String.Alert(
    onConfirm: () -> Unit
) {
    this.Alert(
        title = "温馨提示",
        onConfirm = onConfirm
    )
}

/**
 * 对话框
 * @param title 标题
 * @param onConfirm 确定按钮点击事件
 */
@Composable
fun String.Alert(
    title: String,
    onConfirm: () -> Unit
) {
    MAlert(
        title = title,
        text = this,
        confirmText = "确定",
        dismissText = "",
        onConfirm = onConfirm,
        onDismiss = {},
        onDismissRequest = {}
    )
}


/**
 * 对话框
 * @param title 标题
 * @param text 内容
 * @param confirmText 确定按钮文字（为空不显示按钮）
 * @param dismissText 取消按钮文字（为空不显示按钮）
 * @param onConfirm 确认按钮点击事件
 * @param onDismiss 取消按钮点击事件
 * @param onDismissRequest 关闭对话框事件
 */
@Composable
fun MAlert(
    title: String = "",
    text: String = "",
    confirmText: String = "",
    dismissText: String = "",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (confirmText.isNotEmpty()) {
                TextButton(onClick = onConfirm) {
                    Text(text = confirmText)
                }
            }
        },
        dismissButton = {
            if (dismissText.isNotEmpty()) {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissText)
                }
            }
        }
    )
}