package com.example.depositcalculator.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Общий ViewModel, привязанный к Activity (scope)
    val viewModel: DepositViewModel = viewModel()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onCalculate = { navController.navigate("step1") },
                onHistory = { navController.navigate("history") },
                onExit = { /* завершение процесса */ }
            )
        }
        composable("step1") {
            Step1Screen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate("step2") }
            )
        }
        composable("step2") {
            Step2Screen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCalculate = { navController.navigate("result") }
            )
        }
        composable("result") {
            ResultScreen(
                viewModel = viewModel,
                onSave = {
                    viewModel.saveCalculation()
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onItemClick = { id -> navController.navigate("historyDetail/$id") }
            )
        }
        composable(
            "historyDetail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            HistoryDetailScreen(
                viewModel = viewModel,
                id = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}