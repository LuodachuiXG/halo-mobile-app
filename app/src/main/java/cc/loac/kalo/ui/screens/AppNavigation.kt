package cc.loac.kalo.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.loac.kalo.ui.screens.login.LoginScreen

/**
 * App 页面导航
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.LOGIN.route
    ) {
        // 登录页面
        composable(AppScreen.LOGIN.route) {
            LoginScreen()
        }
    }
}

/**
 * 所有页面枚举类
 */
enum class AppScreen(val route: String) {
    LOGIN("login"),
    ME("me"),
    HOME("home")
}