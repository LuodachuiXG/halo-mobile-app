package cc.loac.kalo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import cc.loac.kalo.ui.mShimmer
import cc.loac.kalo.ui.theme.LARGE
import cc.loac.kalo.ui.theme.MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE
import cc.loac.kalo.ui.theme.SMALL

@Composable
fun ShimmerCard() {
    Column {
        for (i in 1..5) {
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LARGE * 3)
                    .padding(SMALL)
                    .clip(CardDefaults.shape)
                    .mShimmer(true)
            ) {

            }
        }
    }
}