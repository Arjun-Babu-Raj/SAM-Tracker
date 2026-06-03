package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.AnalyticsScreen
import com.example.ui.BaselineScreen
import com.example.ui.ChildProfileScreen
import com.example.ui.FollowupScreen
import com.example.ui.HomeScreen
import com.example.ui.MainViewModel
import com.example.ui.RecordsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SamTrackerApp()
        }
      }
    }
  }
}

@Composable
fun SamTrackerApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToBaseline = { navController.navigate("baseline") },
                onNavigateToFollowup = { navController.navigate("followup") },
                onNavigateToRecords = { navController.navigate("records") {
                    popUpTo("home") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                } },
                onNavigateToAnalytics = { navController.navigate("analytics") {
                    popUpTo("home") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                } }
            )
        }
        composable("records") {
            RecordsScreen(
                viewModel = viewModel,
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                } },
                onNavigateToAnalytics = { navController.navigate("analytics") {
                    popUpTo("home") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                } },
                onNavigateToProfile = { studyId ->
                    navController.navigate("childProfile/$studyId")
                }
            )
        }
        composable("childProfile/{studyId}") { backStackEntry ->
            val studyId = backStackEntry.arguments?.getString("studyId") ?: ""
            ChildProfileScreen(
                studyId = studyId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("analytics") {
            AnalyticsScreen(
                viewModel = viewModel,
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                } }
            )
        }
        composable("baseline") {
            BaselineScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("followup") {
            FollowupScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
