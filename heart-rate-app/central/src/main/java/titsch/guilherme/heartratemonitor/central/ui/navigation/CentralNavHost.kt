package titsch.guilherme.heartratemonitor.central.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import titsch.guilherme.heartratemonitor.central.ui.heartratelist.HeartRateListScreen
import titsch.guilherme.heartratemonitor.central.ui.home.HomeScreenRoute

@Composable
fun CentralNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(route = Home.route) {
            HomeScreenRoute()
        }
        composable(route = HeartRateList.route) {
            HeartRateListScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }