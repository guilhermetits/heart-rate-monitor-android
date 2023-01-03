package titsch.guilherme.heartratemonitor.central.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import titsch.guilherme.heartratemonitor.central.R

interface CentralDestinations {
    val selectedIcon: ImageVector
    val selectableIcon: ImageVector
    val route: String
    val contentDescription: Int
}

/**
 * Rally app navigation destinations
 */
object Home : CentralDestinations {
    override val selectedIcon = Icons.Filled.Home
    override val selectableIcon = Icons.Outlined.Home
    override val route = "home"
    override val contentDescription: Int = R.string.home_screen_title
}

object HeartRateList : CentralDestinations {
    override val selectedIcon = Icons.Filled.Favorite
    override val selectableIcon = Icons.Outlined.FavoriteBorder
    override val route = "heart_rate_list"
    override val contentDescription: Int = R.string.heart_rate_screen_title
}

val bottomNavigationScreens = listOf(Home, HeartRateList)