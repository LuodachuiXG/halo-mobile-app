package cc.loac.kalo.ui.screens.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cc.loac.kalo.ui.screens.AppScreen

/**
 * 封装底部导航栏 Item 的密封类
 * @param route 导航到的页面
 * @param resourceId Item 名称
 * @param icon Item 图标
 */
private sealed class Screen(
    val route: AppScreen,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {

}



/**
 * 登录成功后显示的底部导航栏页面
 */
@Composable
fun BottomNavigationScreen() {

}