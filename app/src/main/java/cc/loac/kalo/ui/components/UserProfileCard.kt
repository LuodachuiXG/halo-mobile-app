package cc.loac.kalo.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.loac.kalo.data.models.User
import cc.loac.kalo.data.models.UserInfo
import cc.loac.kalo.data.models.UserSpec
import cc.loac.kalo.data.models.isEmpty
import cc.loac.kalo.ui.theme.LARGE
import cc.loac.kalo.ui.theme.LARGE_IMAGE
import cc.loac.kalo.ui.theme.LARGE_MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE
import cc.loac.kalo.ui.theme.MyTypography
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.ui.theme.VERY_SMALL
import coil.compose.AsyncImage
import com.zj.shimmer.shimmer

/**
 * 用户个人资料卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileCard(userInfo: UserInfo) {
    // 头像是否在加载中，用于控制头像是否显示骨架动画
    var avatarLoading by remember {
        mutableStateOf(true)
    }
    Card(modifier = Modifier
        .fillMaxWidth()
        // 是否显示骨架动画
        .shimmer(userInfo.isEmpty())) {
        Column(Modifier.padding(SMALL)) {
            Row {
                // 头像
                AsyncImage(
                    model = userInfo.user.spec.avatar,
                    contentDescription = "头像",
                    transform = {
                        // 头像加载完成后关闭骨架动画
                        avatarLoading = false
                        it
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(SMALL))
                        .size(LARGE_IMAGE)
                        .shimmer(avatarLoading)
                )

                Column(
                    modifier = Modifier
                        .padding(start = SMALL)
                        .height(LARGE_IMAGE)
                ) {
                    // 用户名
                    Text(
                        text = userInfo.user.spec.displayName,
                        style = MyTypography.titleMedium
                    )

                    // 邮箱
                    Row {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "邮箱",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(MIDDLE_MIDDLE + 2.dp)
                                .padding(top = VERY_SMALL),
                        )
                        Text(
                            text = userInfo.user.spec.email,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MyTypography.labelLarge
                        )
                    }


                    // 角色标签
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Tag(
                            text = if (userInfo.roles.isEmpty()) {
                                ""
                            } else {
                                userInfo.roles.first().metadata.annotations["rbac.authorization.halo.run/display-name"].toString()
                            }
                        )
                    }

                }

            }
        }
    }
}