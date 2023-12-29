package cc.loac.kalo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cc.loac.kalo.ui.theme.MyTypography

/**
 * 标签组件
 * @param text 标签显示文字
 * @param modifier [Modifier]
 * @param textColor 标签文字颜色
 * @param onClick 标签点击事件
 */
@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = textColor,
            style = MyTypography.labelSmall,
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp)
        )
    }
}