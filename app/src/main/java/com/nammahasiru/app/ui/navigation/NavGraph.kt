package com.nammahasiru.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nammahasiru.app.ui.screens.AddPlantScreen
import com.nammahasiru.app.ui.screens.DashboardScreen
import com.nammahasiru.app.ui.screens.FilteredPlantListScreen
import com.nammahasiru.app.ui.screens.MapScreen
import com.nammahasiru.app.ui.screens.PlantDetailScreen
import com.nammahasiru.app.ui.screens.PlantListScreen
import com.nammahasiru.app.ui.screens.SpeciesGuideScreen
import com.nammahasiru.app.ui.screens.SplashScreen
import com.nammahasiru.app.ui.screens.StatusUpdateScreen

sealed class Screen(val route: String) {
    data object Splash       : Screen("splash")
    data object Dashboard    : Screen("dashboard")
    data object AddPlant     : Screen("add_plant")
    data object PlantDetail  : Screen("plant_detail/{plantId}") {
        fun createRoute(plantId: Int) = "plant_detail/$plantId"
    }
    data object StatusUpdate : Screen("status_update/{plantId}") {
        fun createRoute(plantId: Int) = "status_update/$plantId"
    }
    data object TreeMap      : Screen("tree_map")
    data object SpeciesGuide : Screen("species_guide")
    data object PlantList    : Screen("plant_list")

    /**
     * Filtered list screen.
     * [filterType] = "total" | "alive" | "dead" | "pending"
     */
    data object FilteredList : Screen("filtered_list/{filterType}") {
        fun createRoute(filterType: String) = "filtered_list/$filterType"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {

        // ── Splash ────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Dashboard ─────────────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddPlant              = { navController.navigate(Screen.AddPlant.route) },
                onNavigateToMap         = { navController.navigate(Screen.TreeMap.route) },
                onNavigateToSpecies     = { navController.navigate(Screen.SpeciesGuide.route) },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                },
                // Stat card taps → filtered list
                onNavigateToTotal   = { navController.navigate(Screen.FilteredList.createRoute("total")) },
                onNavigateToAlive   = { navController.navigate(Screen.FilteredList.createRoute("alive")) },
                onNavigateToDead    = { navController.navigate(Screen.FilteredList.createRoute("dead")) },
                onNavigateToPending = { navController.navigate(Screen.FilteredList.createRoute("pending")) }
            )
        }

        // ── Add Plant ─────────────────────────────────────────────────────
        composable(Screen.AddPlant.route) {
            AddPlantScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Plant Detail ──────────────────────────────────────────────────
        composable(
            route     = Screen.PlantDetail.route,
            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: return@composable
            PlantDetailScreen(
                plantId               = plantId,
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToStatusUpdate = { id ->
                    navController.navigate(Screen.StatusUpdate.createRoute(id))
                }
            )
        }

        // ── Status Update ─────────────────────────────────────────────────
        composable(
            route     = Screen.StatusUpdate.route,
            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: return@composable
            StatusUpdateScreen(
                plantId        = plantId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Tree Map ──────────────────────────────────────────────────────
        composable(Screen.TreeMap.route) {
            MapScreen(
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                }
            )
        }

        // ── Species Guide ─────────────────────────────────────────────────
        composable(Screen.SpeciesGuide.route) {
            SpeciesGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Full Plant List (legacy entry from nav bar) ───────────────────
        composable(Screen.PlantList.route) {
            PlantListScreen(
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                }
            )
        }

        // ── Filtered Plant List (Alive / Dead / Pending / Total) ──────────
        composable(
            route     = Screen.FilteredList.route,
            arguments = listOf(navArgument("filterType") { type = NavType.StringType })
        ) { backStackEntry ->
            val filterType = backStackEntry.arguments?.getString("filterType") ?: "total"
            FilteredPlantListScreen(
                filterType            = filterType,
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                }
            )
        }
    }
}
