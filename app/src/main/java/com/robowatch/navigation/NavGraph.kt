@file:Suppress("DEPRECATION")

package com.robowatch.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.robowatch.screen.WelcomeScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.robowatch.ui.theme.CustomNavigationBarTheme
import androidx.compose.material.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.robowatch.bottomnav.BottomNav
import com.robowatch.screen.LoginScreen
import com.robowatch.screen.SignupScreen

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    val robotWorkingStatus = remember { mutableStateOf(false) }
    val intruderDetected = remember { mutableStateOf(0) }
    val batteryStatus = remember { mutableStateOf(0) }
    val robotMovementStatus = remember { mutableStateOf(0) }
    val streamingURL = remember {mutableStateOf("")}

    // Read the working status from Firebase Realtime Database and update the state
    val database = FirebaseDatabase.getInstance()
    var reference = database.getReference("")

    val valueEventListener = remember {
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val workingStatus = dataSnapshot.child("RobotStatus/WorkingStatus").getValue(Boolean::class.java)
                val intruderStatus = dataSnapshot.child("IntruderDetect/IntruderDetected").getValue(Int::class.java)
                val batteryLevel = dataSnapshot.child("RobotStatus/BatteryStatus").getValue(Int::class.java)
                val movementStatus = dataSnapshot.child("RobotStatus/ControlMovement").getValue(Int::class.java)
                val streamingUrl = dataSnapshot.child("RobotStatus/Url").getValue((String::class.java))

                robotWorkingStatus.value = workingStatus ?: false
                intruderDetected.value = intruderStatus ?: 0
                batteryStatus.value = batteryLevel ?: 0
                robotMovementStatus.value = movementStatus ?: 1
                streamingURL.value = streamingUrl ?: ""
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
                robotWorkingStatus.value = false
                intruderDetected.value = 0
                batteryStatus.value = 0
                robotMovementStatus.value = 0
                streamingURL.value = ""
            }
        }
    }

    DisposableEffect(Unit) {
        reference.addValueEventListener(valueEventListener)

        onDispose {
            reference.removeEventListener(valueEventListener)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
            Screen.Home.route
        } else {
            startDestination
        }
    ) {
        composable(route = Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }
        composable(route = Screen.Auth.route){
            LoginScreen(navController = navController)
        }
        composable(route = Screen.AuthS.route){
            SignupScreen(navController = navController)
        }
        composable(route = Screen.Home.route) {
            CustomNavigationBarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BottomNav(navController, reference, robotWorkingStatus , intruderDetected, batteryStatus, robotMovementStatus, streamingURL)
                }
            }
        }
    }
}
