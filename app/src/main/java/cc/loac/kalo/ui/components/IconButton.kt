package cc.loac.kalo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import cc.loac.kalo.ui.theme.LARGE_MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE

/**
 * 操作按钮组件
 * @param modifier Modifier
 * @param painter 图标
 * @param text 按钮文本
 * @param color 文本图标颜色
 * @param shape Shape
 * @param onClick 点击事件
 */
@Composable
fun MyIconButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    text: String,
    color: Color = MaterialTheme.colorScheme.secondary,
    shape: Shape = CardDefaults.shape,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clip(shape)
            .clickable { onClick() }
    ) {
        Row {
            Icon(
                painter = painter,
                contentDescription = text,
                tint = color,
                modifier = Modifier
                    .padding(MIDDLE_MIDDLE)
                    .size(LARGE_MIDDLE)
            )
            Text(
                text = text,
                modifier = Modifier.padding(
                    top = MIDDLE_MIDDLE,
                    bottom = MIDDLE_MIDDLE,
                    end = MIDDLE_MIDDLE
                ),
                color = color
            )
        }
    }
}