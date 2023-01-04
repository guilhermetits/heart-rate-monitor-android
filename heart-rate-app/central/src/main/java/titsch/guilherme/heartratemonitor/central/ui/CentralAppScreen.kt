package titsch.guilherme.heartratemonitor.central.ui

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
import titsch.guilherme.heartratemonitor.central.ui.navigation.CentralNavHost
import titsch.guilherme.heartratemonitor.central.ui.navigation.Home
import titsch.guilherme.heartratemonitor.central.ui.navigation.bottomNavigationScreens
import titsch.guilherme.heartratemonitor.central.ui.navigation.navigateSingleTopTo
import titsch.guilherme.heartratemonitor.core.theme.HeartRateMonitorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralApp() {
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
            CentralNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
