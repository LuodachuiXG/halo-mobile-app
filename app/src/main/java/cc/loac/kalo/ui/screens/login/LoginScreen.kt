package cc.loac.kalo.ui.screens.login
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.loac.kalo.ui.theme.KaloTheme
import cc.loac.kalo.ui.theme.aliFontFamily

@Composable
fun LoginScreen() {
    var url by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Title()
        Inputs(
            urlValue = url,
            onUrlChange = { url = it },
            usernameValue = username,
            onUsernameChange = { username = it },
            passwordValue = password,
            onPasswordChange = { password = it }
        )
        LoginBtn {
            Toast.makeText(context, "你点击了登录", Toast.LENGTH_LONG).show()
        }
    }
}

/**
 * Kalo 大字标题
 */
@Composable
private fun Title() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 68.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kalo",
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
 */
@Composable
private fun LoginBtn(onBtnClick: () -> Unit) {
    Column (
        modifier = Modifier.padding(start = 30.dp, end = 30.dp)
    ) {
        Button(
            onClick = onBtnClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("登录")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KaloTheme {
        LoginScreen()
    }
}