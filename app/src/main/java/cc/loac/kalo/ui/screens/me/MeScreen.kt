package cc.loac.kalo.ui.screens.me

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch

/**
 * “我”页面
 */
@Composable
fun MeScreen(
    navController: NavController,
    meViewModel: MeViewModel = viewModel()
) {
    val userProfile = meViewModel.userProfile

    val scope = rememberCoroutineScope()

    Column {
        Button(onClick = {
            scope.launch {
                meViewModel.getUserProfile()
            }
        }) {
            Text("Click Me")
        }
    }

    userProfile.handle(
        success = {
            Log.e("Test", it.user.toString())
        },
        failure = {
            Log.e("failed", it)
        }
    )
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