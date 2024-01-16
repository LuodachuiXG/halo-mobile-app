package cc.loac.kalo.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import com.zj.shimmer.ShimmerConfig
import com.zj.shimmer.shimmer

/**
 * 自定义骨架屏颜色
 * @param visible 是否显示骨架屏
 * @param config 骨架屏配置[ShimmerConfig]，这里进行了自定义
 */
@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.mShimmer(
    visible: Boolean,
    config: ShimmerConfig = ShimmerConfig(
        contentColor = MaterialTheme.colorScheme.surfaceVariant,
        higLightColor = MaterialTheme.colorScheme.inverseOnSurface,
        duration = 500f
    )
) = shimmer(visible, config)