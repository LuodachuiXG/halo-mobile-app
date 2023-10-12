package cc.loac.kalo.ui.screens.me


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.UserInfo
import cc.loac.kalo.data.repositories.ConfigKey
import cc.loac.kalo.data.repositories.ConfigRepo
import cc.loac.kalo.data.repositories.UserRepo
import cc.loac.kalo.network.handle
import cc.loac.kalo.ui.components.Alert
import cc.loac.kalo.ui.components.UserProfileCard
import cc.loac.kalo.ui.theme.SMALL
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
        MeScreenUserInfoCard(meViewModel)
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

    // 用户信息状态，获取成功后此变量状态改变
    val userInfoState = meViewModel.userProfile

    // 用户信息状态改变
    userInfoState.handle(
        success = {
            // 用户信息获取成功
            userInfo = it
        },
        failure = {
            "用户资料获取失败".Alert {}
        }
    )

    // 用户信息卡片组件
    UserProfileCard(userInfo = userInfo)
}

/**
 * “我”页面 ViewModel
 */
class MeViewModel: ViewModel() {
    // 初始化用户数据操作类
    private val userRepo = UserRepo(ConfigRepo.get(ConfigKey.HALO_URL))

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