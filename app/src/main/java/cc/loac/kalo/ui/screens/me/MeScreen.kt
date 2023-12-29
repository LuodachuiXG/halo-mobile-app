package cc.loac.kalo.ui.screens.me

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cc.loac.kalo.R
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.UserInfo
import cc.loac.kalo.data.repositories.UserRepo
import cc.loac.kalo.network.handle
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.MyIconButton
import cc.loac.kalo.ui.components.UserProfileCard
import cc.loac.kalo.ui.screens.AppScreen
import cc.loac.kalo.ui.theme.MIDDLE_MIDDLE
import cc.loac.kalo.ui.theme.SMALL
import cc.loac.kalo.ui.theme.VERY_SMALL
import kotlinx.coroutines.launch

/**
 * “我”页面
 */
@Composable
fun MeScreen(
    navController: NavController,
    meViewModel: MeViewModel = viewModel()
) {
    Column(
        modifier = Modifier.padding(SMALL)
    ) {
        // 顶部用户信息栏（头像、显示名称、邮箱、身份）
        MeScreenUserInfoCard(meViewModel)
        // 操作按钮
        MeImageButtons(navController)
    }
}

/**
 * 用户信息卡片
 */
@Composable
private fun MeScreenUserInfoCard(meViewModel: MeViewModel) {
    // 只执行一次不参与重组
    LaunchedEffect(Unit) {
        // 获取用户资料
        meViewModel.getUserProfile()
    }

    // 用户信息实体类
    var userInfo by remember {
        mutableStateOf(UserInfo())
    }

    var showAlert by remember { mutableStateOf(false) }
    var showAlertMessage = ""
    if (showAlert) {
        "用户资料获取失败，$showAlertMessage".Alert {
            showAlert = false
        }
    }

    // 用户信息状态改变
    LaunchedEffect(meViewModel.userProfile.value) {
        meViewModel.userProfile.handle(
            success = {
                // 用户信息获取成功
                userInfo = it
            },
            failure = {
                showAlertMessage = it
                showAlert = true
            }
        )
    }

    // 用户信息卡片组件
    UserProfileCard(userInfo = userInfo)
}

/**
 * 操作选项按钮
 */
@Composable
private fun MeImageButtons(navController: NavController) {
    Column(
        modifier = Modifier.padding(top = MIDDLE_MIDDLE)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            MyIconButton(
                painter = painterResource(id = R.drawable.plugin),
                text = "插件",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = VERY_SMALL)
            ) {
                // 跳转到插件页面
                navController.navigate(AppScreen.PLUGIN.route)
            }

            MyIconButton(
                painter = painterResource(id = R.drawable.user),
                text = "用户",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = VERY_SMALL)
            )
        }

        Row(
            modifier = Modifier.padding(top = MIDDLE_MIDDLE)
        ) {
            MyIconButton(
                painter = painterResource(id = R.drawable.frame),
                text = "概述",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = VERY_SMALL)
            )

            MyIconButton(
                painter = painterResource(id = R.drawable.setting),
                text = "设置",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = VERY_SMALL)
            )
        }
    }
}

/**
 * “我”页面 ViewModel
 */
class MeViewModel: ViewModel() {
    // 初始化用户数据操作类
    private val userRepo = UserRepo()

    private val _userProfile = mutableStateOf(MyResponse<UserInfo>())
    val userProfile: State<MyResponse<UserInfo>> = _userProfile

    /**
     * 获取用户资料
     */
    fun getUserProfile() {
        viewModelScope.launch {
            _userProfile.value = userRepo.getUserProfile()
        }
    }
}