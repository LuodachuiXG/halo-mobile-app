package cc.loac.kalo.ui.components

import androidx.compose.foundation.background
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
import cc.loac.kalo.ui.theme.SMALL

@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    textColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit = {}
) {
    Surface(
        contentColor = backgroundColor,
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