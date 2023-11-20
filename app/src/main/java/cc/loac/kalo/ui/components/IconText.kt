package cc.loac.kalo.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE
import cc.loac.kalo.ui.theme.MyTypography
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.ui.theme.VERY_SMALL

/**
 * 图标文字组件
 * @param imageVector 矢量图标
 * @param text 文字
 * @param modifier [Modifier]
 */
@Composable
fun IconText(
    imageVector: ImageVector,
    text: String,
    modifier: Modifier
) {
    Row(
        modifier = modifier
    ) {
        Icon(
            imageVector = imageVector,
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