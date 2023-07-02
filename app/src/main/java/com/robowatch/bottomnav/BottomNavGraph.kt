package com.robowatch.bottomnav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.database.DatabaseReference
import com.robowatch.screen.HomeScreen
import com.robowatch.screen.LogScreen
import com.robowatch.screen.PatrolsScreen
import com.robowatch.screen.ProfileScreen
import com.robowatch.screen.ReportScreen

@Composable
fun BottomNavGraph(
    navController2: NavHostController,
    navController: NavHostController,
    dbRef: DatabaseReference,
    robotWorkingStatus: MutableState<Boolean>, intruderState: MutableState<Int>, batteryStatus: MutableState<Int>, robotMovementStatus: MutableState<Int>, streamingURL: MutableState<String>
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route)
        {
            HomeScreen(robotWorkingStatus, intruderState, batteryStatus, robotMovementStatus)
        }
        composable(route = BottomBarScreen.Report.route)
        {
            ReportScreen(streamingURL)
        }
        composable(route = BottomBarScreen.Patrols.route)
        {
            PatrolsScreen()
        }
        composable(route = BottomBarScreen.Log.route) {
            LogScreen(dbRef)
        }
        composable(route = BottomBarScreen.Profile.route)
        {
            ProfileScreen(navController2)
        }
    }
}