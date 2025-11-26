package com.manaira.supmanaira.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.manaira.supmanaira.ui.home.HomeScreen
import com.manaira.supmanaira.ui.registros.RegistrosScreen
import com.manaira.supmanaira.ui.itens.ItensScreen
import com.manaira.supmanaira.ui.scanner.ScannerScreen

sealed class AppRoute(val route: String) {
    object Home : AppRoute("home")
    object Registros : AppRoute("registros")
    object Itens : AppRoute("itens/{registroId}") {
        fun createRoute(id: Int) = "itens/$id"
    }
    object Scanner : AppRoute("scanner/{registroId}") {
        fun createRoute(id: Int) = "scanner/$id"
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route
    ) {
        composable(AppRoute.Home.route) {
            HomeScreen(navController)
        }
        composable(AppRoute.Registros.route) {
            RegistrosScreen(navController)
        }
        composable(AppRoute.Itens.route) { backStack ->
            val registroId = backStack.arguments?.getString("registroId")?.toInt() ?: 0
            ItensScreen(navController, registroId)
        }
        composable(AppRoute.Scanner.route) { backStack ->
            val registroId = backStack.arguments?.getString("registroId")?.toInt() ?: 0
            ScannerScreen(navController, registroId)
        }
    }
}
