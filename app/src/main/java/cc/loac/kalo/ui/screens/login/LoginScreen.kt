package cc.loac.kalo.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.loac.kalo.data.models.MyResponse
import cc.loac.kalo.data.models.PublicKey
import cc.loac.kalo.data.repositories.LoginRepository
import cc.loac.kalo.ui.theme.aliFontFamily
import cc.loac.kalo.ui.components.MAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    var url by remember { mutableStateOf("https://loac.cc") }
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("123456") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Title("Kalo")
        Inputs(
            urlValue = url,
            onUrlChange = { url = it },
            usernameValue = username,
            onUsernameChange = { username = it },
            passwordValue = password,
            onPasswordChange = { password = it }
        )
        LoginBtn(url, username, password, loginViewModel)
    }
}

/**
 * Kalo 大字标题
 * @param title 标题
 */
@Composable
private fun Title(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 68.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontFamily = aliFontFamily,
            fontSize = 130.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * 输入框组
 * @param urlValue Halo 站点地址数据
 * @param onUrlChange Halo 站点地址改变事件
 * @param usernameValue 用户名数据
 * @param onUsernameChange 用户名改变事件
 * @param passwordValue 密码数据
 * @param onPasswordChange 密码改变事件
 */
@Composable
private fun Inputs(urlValue: String,
                   onUrlChange: (String) -> Unit,
                   usernameValue: String,
                   onUsernameChange: (String) -> Unit,
                   passwordValue: String,
                   onPasswordChange: (String) -> Unit,) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        Input(
            label = "Halo 站点地址",
            placeholder = "输入你的 Halo 站点地址",
            value = urlValue,
            onValueChange = onUrlChange
        )

        Input(
            label = "用户名",
            placeholder = "Halo 用户名",
            value = usernameValue,
            onValueChange = onUsernameChange
        )

        Input(
            label = "密码",
            placeholder = "Halo 密码",
            value = passwordValue,
            onValueChange = onPasswordChange,
            isPassword = true
        )
    }
}

/**
 * 输入框
 * @param label 输入框标签
 * @param placeholder 输入框占位符（提示）
 * @param value 输入框值
 * @param onValueChange 输入框内容改变事件
 * @param isPassword 是否是密码框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Input(label: String,
                  placeholder: String,
                  value: String,
                  onValueChange: (String) -> Unit,
                  isPassword: Boolean = false) {
    OutlinedTextField(
        label = { Text(label) },
        placeholder = { Text(placeholder)},
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    )
}

/**
 * 登录按钮
 * @param url Halo 站点地址
 * @param username Halo 用户名
 * @param password Halo 密码
 * @param loginViewModel ViewModel
 */
@Composable
private fun LoginBtn(
    url: String,
    username: String,
    password: String,
    loginViewModel: LoginViewModel
) {
    // 是否显示有空白信息对话框
    var showEmptyAlert by remember { mutableStateOf(false) }
    // 是否显示获取 Public Key 成功的对话框
    var showGetPublicKeyAlert by remember { mutableStateOf(false) }

    // 获取到的 Public Key
    val publicKeyRes by loginViewModel.publicKey.collectAsState()
    Column (
        modifier = Modifier.padding(start = 30.dp, end = 30.dp)
    ) {
        Button(
            onClick = {
                // 有空白信息
                if (url.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    showEmptyAlert = true
                    return@Button
                }

                // 尝试获取 Public-Key
                loginViewModel.getPublicKey()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("登录")
        }
    }

    // 显示有空白信息对话框话框
    if (showEmptyAlert) {
        MAlert(title = "登录失败", text = "请将信息填写完整") {
            showEmptyAlert = false
        }
    }

    // Public Key 获取成功
    if (publicKeyRes.isNotNone()) {
        showGetPublicKeyAlert = true
    }

    // 显示 Public Key 获取成功对话框
    if (showGetPublicKeyAlert) {
        var title: String
        var text: String
        if (publicKeyRes.isSuccessful()) {
            title = "获取 Public-Key 成功"
            text = publicKeyRes.data?.base64Format ?: "None"
        } else {
            title = "获取 Public-Key 失败"
            text = publicKeyRes.errMsg
        }

        MAlert(title = title, text = text) {
            showGetPublicKeyAlert = false
            publicKeyRes.clear()
        }
    }
}

/**
 * 登录界面的 ViewModel
 */
class LoginViewModel : ViewModel() {
    // 初始化数据操作类
    private val loginRepository = LoginRepository()


    // Public-Key
    private val _publicKey = MutableStateFlow(MyResponse<PublicKey>())
    val publicKey: StateFlow<MyResponse<PublicKey>> = _publicKey

    /**
     * 获取 Public-Key
     */
    fun getPublicKey() {
        viewModelScope.launch {
            val publicKey = loginRepository.getPublicKey()
            _publicKey.value = publicKey
        }
    }
}