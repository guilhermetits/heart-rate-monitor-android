package titsch.guilherme.heartratemonitor.peripheral.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import titsch.guilherme.heartratemonitor.peripheral.R

interface PeripheralDestinations {
    val selectedIcon: ImageVector
    val selectableIcon: ImageVector
    val route: String
    val contentDescription: Int
}

/**
 * Rally app navigation destinations
 */
object Home : PeripheralDestinations {
    override val selectedIcon = Icons.Filled.Home
    override val selectableIcon = Icons.Outlined.Home
    override val route = "home"
    override val contentDescription: Int = R.string.home_screen_title
}

object Devices : PeripheralDestinations {
    override val selectedIcon = Icons.Filled.Build
    override val selectableIcon = Icons.Outlined.Build
    override val route = "devices"
    override val contentDescription: Int = R.string.devices_screen_title
}

val bottomNavigationScreens = listOf(Home, Devices)