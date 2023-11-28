package cc.loac.kalo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE
import cc.loac.kalo.ui.theme.SMALL
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

/**
 * 操作按钮组件
 * @param modifier Modifier
 * @param imageVector 图标
 * @param text 按钮文本
 * @param color 文本图标颜色
 * @param shape Shape
 * @param onClick 点击事件
 */
@Composable
fun ImageButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
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
                imageVector = imageVector,
                contentDescription = text,
                tint = color,
                modifier = Modifier.padding(MIDDLE_MIDDLE)
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