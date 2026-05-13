package com.nammahasiru.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.nammahasiru.app.ui.screens.RegisterScreen
import com.nammahasiru.app.ui.screens.SignInScreen
import com.nammahasiru.app.ui.screens.SpeciesGuideScreen
import com.nammahasiru.app.ui.screens.SplashScreen
import com.nammahasiru.app.ui.screens.StatusUpdateScreen
import com.nammahasiru.app.ui.screens.WelcomeScreen
import com.nammahasiru.app.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    data object Splash       : Screen("splash")
    data object Welcome      : Screen("welcome")
    data object SignIn       : Screen("sign_in")
    data object Register     : Screen("register")
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

    // Single shared AuthViewModel for all auth screens
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {

        // ── Splash ────────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onLoggedIn = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNotLoggedIn = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Welcome ───────────────────────────────────────────────────────────
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignIn   = { navController.navigate(Screen.SignIn.route) },
                onRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // ── Sign In ───────────────────────────────────────────────────────────
        composable(Screen.SignIn.route) {
            SignInScreen(
                viewModel           = authViewModel,
                onLoginSuccess      = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Register ──────────────────────────────────────────────────────────
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel          = authViewModel,
                onRegisterSuccess  = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Dashboard ─────────────────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddPlant              = { navController.navigate(Screen.AddPlant.route) },
                onNavigateToMap         = { navController.navigate(Screen.TreeMap.route) },
                onNavigateToSpecies     = { navController.navigate(Screen.SpeciesGuide.route) },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                },
                onNavigateToTotal   = { navController.navigate(Screen.FilteredList.createRoute("total")) },
                onNavigateToAlive   = { navController.navigate(Screen.FilteredList.createRoute("alive")) },
                onNavigateToDead    = { navController.navigate(Screen.FilteredList.createRoute("dead")) },
                onNavigateToPending = { navController.navigate(Screen.FilteredList.createRoute("pending")) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Add Plant ─────────────────────────────────────────────────────────
        composable(Screen.AddPlant.route) {
            AddPlantScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Plant Detail ──────────────────────────────────────────────────────
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

        // ── Status Update ─────────────────────────────────────────────────────
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

        // ── Tree Map ──────────────────────────────────────────────────────────
        composable(Screen.TreeMap.route) {
            MapScreen(
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                }
            )
        }

        // ── Species Guide ─────────────────────────────────────────────────────
        composable(Screen.SpeciesGuide.route) {
            SpeciesGuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Full Plant List ───────────────────────────────────────────────────
        composable(Screen.PlantList.route) {
            PlantListScreen(
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToPlantDetail = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                }
            )
        }

        // ── Filtered Plant List (Alive / Dead / Pending / Total) ──────────────
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

