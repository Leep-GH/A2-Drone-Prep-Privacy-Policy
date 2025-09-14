package com.appquests.droneprep.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.appquests.droneprep.ui.screens.HistoryScreen
import com.appquests.droneprep.ui.screens.HomeScreen
import com.appquests.droneprep.ui.screens.QuizScreen
import com.appquests.droneprep.ui.screens.ResultsScreen
import com.appquests.droneprep.ui.screens.ReviewScreen
import com.appquests.droneprep.ui.screens.SettingsScreen
import com.appquests.droneprep.ui.screens.StudyTopicsScreen
import com.appquests.droneprep.ui.screens.StudyReaderScreen
import com.appquests.droneprep.ui.screens.FlashcardsScreen
import com.appquests.droneprep.ui.screens.TopicPickerScreen
import com.appquests.droneprep.ui.screens.AcronymBusterScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("topicPicker") {
            TopicPickerScreen(navController = navController)
        }
        composable("quiz") {
            QuizScreen(navController = navController)
        }
        composable("results") {
            ResultsScreen(navController = navController)
        }
        composable("review") {
            ReviewScreen(navController = navController)
        }
        composable("history") {
            HistoryScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(_navController = navController)
        }
        composable("studyTopics") {
            StudyTopicsScreen(navController)
        }
        composable(
            route = "studyReader/{topic}",
            arguments = listOf(navArgument("topic") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicArg = backStackEntry.arguments?.getString("topic") ?: ""
            StudyReaderScreen(navController, topicArg)
        }
        composable("flashcards") {
            FlashcardsScreen(navController)
        }
        composable("acronymBuster") {
            AcronymBusterScreen(navController)
        }
    }
}
