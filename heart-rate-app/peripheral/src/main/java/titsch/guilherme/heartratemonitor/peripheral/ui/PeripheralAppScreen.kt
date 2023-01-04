package titsch.guilherme.heartratemonitor.peripheral.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme
import titsch.guilherme.heartratemonitor.peripheral.ui.navigation.Home
import titsch.guilherme.heartratemonitor.peripheral.ui.navigation.PeripheralNavHost
import titsch.guilherme.heartratemonitor.peripheral.ui.navigation.bottomNavigationScreens
import titsch.guilherme.heartratemonitor.peripheral.ui.navigation.navigateSingleTopTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeripheralApp() {
    HeartRateMonitorTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen =
            bottomNavigationScreens.find { it.route == currentDestination?.route } ?: Home
        Scaffold(
            bottomBar = {
                NavigationBar {
                    bottomNavigationScreens.forEach { destination ->
                        val selected = destination == currentScreen
                        val icon =
                            if (selected) destination.selectedIcon else destination.selectableIcon
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    stringResource(id = destination.contentDescription)
                                )
                            },
                            selected = selected,
                            onClick = { navController.navigateSingleTopTo(destination.route) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            PeripheralNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
