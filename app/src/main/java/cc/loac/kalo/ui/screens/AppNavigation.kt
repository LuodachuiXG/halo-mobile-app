package cc.loac.kalo.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cc.loac.kalo.ui.screens.home.HomeScreen
import cc.loac.kalo.ui.screens.login.LoginScreen
import cc.loac.kalo.ui.screens.me.MeScreen


/**
 * 封装底部导航栏 Item 的密封类
 * @param route 导航到的页面
 * @param name Item 名称
 * @param icon Item 图标
 */
private sealed class Screen(
    val route: AppScreen,
    val name: String,
    val icon: ImageVector
) {
    object Me: Screen(AppScreen.ME, "我", Icons.Default.Person)
    object Home: Screen(AppScreen.HOME, "主页", Icons.Default.Home)
}

/**
 * App 页面导航及底部导航栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 底部导航栏页面项
    val item = listOf(
        Screen.Home,
        Screen.Me
    )

    // 获取导航的当前路由
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRout = navBackStackEntry?.destination?.route

    Scaffold (
        bottomBar = {
            // 不是登录页面才显示底部导航栏
            if (currentRout != AppScreen.LOGIN.route) {
                NavigationBar {
                    item.forEach {
                        NavigationBarItem(
                            selected = currentRout == it.route.route,
                            onClick = {
                                navController.navigate(it.route.route) {
                                    // 清空栈内到 popUpTo ID 之间的所有 Item
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        // 保存页面状态
                                        saveState = true
                                        inclusive = true
                                    }

                                    // 避免多次点击产生多个实例
                                    launchSingleTop = true
                                    // 再次点击之前的 Item 时恢复状态
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = it.icon,
                                    contentDescription = it.name
                                )
                            },
                            label = {
                                Text(it.name)
                            }
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = AppScreen.LOGIN.route,
            modifier = Modifier.padding(it)
        ) {
            // 登录页面
            composable(AppScreen.LOGIN.route) {
                LoginScreen(navController)
            }

            // 主页
            composable(AppScreen.HOME.route) {
                HomeScreen(navController)
            }

            // “我” 页面
            composable(AppScreen.ME.route) {
                MeScreen(navController)
            }
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