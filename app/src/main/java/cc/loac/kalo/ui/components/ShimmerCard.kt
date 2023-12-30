package cc.loac.kalo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import cc.loac.kalo.ui.mShimmer
import cc.loac.kalo.ui.theme.LARGE_MIDDLE
import cc.loac.kalo.ui.theme.SMALL

/**
 * 纵向排列的列表骨架屏
 * @param count 列表项数量
 */
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    count: Int = 5
) {
    Column (
        modifier = Modifier.fillMaxHeight()
    ) {
        for (i in 1..count) {
            Card (
                modifier = modifier
                    .fillMaxWidth()
                    .height(LARGE_MIDDLE * 3)
                    .clip(CardDefaults.shape)
                    .padding(bottom = SMALL)
                    .mShimmer(true)
            ) {}
        }
    }
}