package cc.loac.kalo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cc.loac.kalo.ui.theme.LARGE
import cc.loac.kalo.ui.theme.MIDDLE
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.ui.theme.VERY_SMALL

/**
 * 状态切换按钮
 * @param modifier [Modifier]
 * @param enable 按钮启用状态（展示不同文字、颜色）
 * @param enableText 启用时显示的文字
 * @param disableText 禁用时显示文字
 * @param isLoading 是否正在加载中
 * @param onClick 插件启用状态改变按钮点击事件（只有在非加载状态才可以触发）
 */
@Composable
fun SwitchButton(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    enableText: String,
    disableText: String,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Card (
        // 根据启用状态设置不同的按钮和文字颜色
        colors = CardDefaults.cardColors(
            containerColor = if (enable) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            contentColor = if (enable) {
                MaterialTheme.colorScheme.onError
            } else {
                MaterialTheme.colorScheme.onPrimary
            },
        ),
        modifier = modifier
            .clip(CardDefaults.shape)
            .clickable {
                if (!isLoading) {
                    onClick()
                }
            }
            .height(LARGE + SMALL + 2.dp)
    ) {
        if (isLoading) {
            // 加载中显示进度条
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = if (enable) {
                        MaterialTheme.colorScheme.onError
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                    modifier = Modifier.size(MIDDLE),
                    strokeWidth = VERY_SMALL - 2.dp
                )
            }
        } else {
            Text(
                text = if (enable) enableText else disableText,
                modifier = Modifier
                    .padding(SMALL)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}