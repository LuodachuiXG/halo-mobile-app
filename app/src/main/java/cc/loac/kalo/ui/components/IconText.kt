package cc.loac.kalo.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE
import cc.loac.kalo.ui.theme.MyTypography
import cc.loac.kalo.ui.theme.VERY_SMALL

/**
 * 图标文字组件
 * @param painter 矢量图标
 * @param text 文字
 * @param modifier [Modifier]
 */
@Composable
fun IconText(
    painter: Painter,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(MIDDLE_MIDDLE)
                .padding(top = VERY_SMALL / 2),
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.secondary,
            style = MyTypography.labelMedium,
            modifier = Modifier.padding(start = VERY_SMALL / 2)
        )
    }
}