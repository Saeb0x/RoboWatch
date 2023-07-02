@file:Suppress("DEPRECATION")

package com.robowatch

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.robowatch.navigation.SetupNavGraph
import com.robowatch.notifications.FirebaseService
import com.robowatch.notifications.NotificationData
import com.robowatch.notifications.PushNotification
import com.robowatch.notifications.RetrofitInstance
import com.robowatch.ui.theme.RoboWatchTheme
import com.robowatch.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TOPIC = "/topics/myTopic2"

@ExperimentalAnimationApi
@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val TAG = "MainActivity"

    @Inject
    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        var eToken: String? = null
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                FirebaseService.token = token
                eToken = token
            }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        val db = FirebaseDatabase.getInstance()

        // Patrols Notification
        val robotStatusRef = db.getReference("RobotStatus/WorkingStatus")
        val schedulePatrolsRef = db.getReference("RobotStatus/ScheduledPatrols")
        var statusChanged = false

        robotStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val existingValue = dataSnapshot.getValue(Boolean::class.java)

                schedulePatrolsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(schedulePatrolsSnapshot: DataSnapshot) {
                        val schedulePatrolsValue = schedulePatrolsSnapshot.getValue(Boolean::class.java)

                        if (existingValue == false && !statusChanged && schedulePatrolsValue == true) {
                            statusChanged = true // Set the flag to true to prevent duplicate notifications

                            val notification = PushNotification(
                                NotificationData("[RoboWatch]: Scheduled Patrol Finished", "Your scheduled patrol has finished!"),
                                eToken ?: ""
                            )
                            sendNotification(notification)
                            schedulePatrolsRef.setValue(false)
                        } else if (existingValue == true) {
                            statusChanged = false // Reset the flag when the value changes back to false
                        }
                    }

                    override fun onCancelled(schedulePatrolsError: DatabaseError) {
                        Log.e(TAG, "Database Error: $schedulePatrolsError") // For debugging
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database Error: $error")
            }
        })


        // Intruder Detection Notification
        val intruderRef = db.getReference("IntruderDetect/IntruderDetected")
        var intruderDetected = false // Flag to track notification status

        intruderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val existingValue = dataSnapshot.getValue(Int::class.java)

                if (existingValue == 1 && !intruderDetected) {
                    intruderDetected = true // Set the flag to true to prevent duplicate notifications

                    val notification = PushNotification(
                        NotificationData("[RoboWatch]: Intruder Detected", "An intruder has been detected in your premise!"),
                        eToken ?: ""
                    )
                    sendNotification(notification)
                } else if (existingValue == 0) {
                    intruderDetected = false // Reset the flag when the value changes back to 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database Error: $error") // For debugging
            }
        })

        // Battery Status Notification
        val batteryStatusRef = db.getReference("RobotStatus/BatteryStatus")
        var lowBatteryNotified = false // Flag to track notification status

        batteryStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val batteryStatus = dataSnapshot.getValue(Double::class.java)

                if (batteryStatus != null && batteryStatus < 20.0 && !lowBatteryNotified) {
                    lowBatteryNotified = true // Set the flag to true to prevent duplicate notifications

                    val comment = "Low battery level! Please charge the robot."
                    val notification = PushNotification(
                        NotificationData("Battery Status", comment),
                        eToken ?: ""
                    )
                    sendNotification(notification)
                } else if (batteryStatus != null && batteryStatus >= 20.0) {
                    lowBatteryNotified = false // Reset the flag when the battery status increases above the threshold
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database Error: $error") // For debugging
            }
        })



        installSplashScreen().setKeepOnScreenCondition {
            !splashViewModel.isLoading.value
        }
        setContent {
            RoboWatchTheme {
                val screen by splashViewModel.startDestination
                val navController = rememberNavController()
                SetupNavGraph(navController = navController, startDestination = screen)
            }
        }
    }
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                //Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                //Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            //Log.e(TAG, e.toString())
        }
    }

}
