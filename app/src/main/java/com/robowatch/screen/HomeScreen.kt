package com.robowatch.screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.robowatch.R
import com.robowatch.notifications.NotificationData
import com.robowatch.notifications.PushNotification
import com.robowatch.notifications.RetrofitInstance
import com.robowatch.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(robotWorkingStatus: MutableState<Boolean>, intruderState: MutableState<Int>, batteryStatus: MutableState<Int>, robotMovementStatus: MutableState<Int>) {
    Surface(color = BackgroundColor, modifier = Modifier.fillMaxSize()) {
        Column {
            HeaderUI()
            RobotStatusCardUI(robotWorkingStatus.value)
            IntruderDetectedCardUI(intruderState.value)
            BatteryStatusCardUI(batteryStatus.value)
            RobotMovementCardUI(robotMovementStatus.value)
        }
    }

}

@Composable
fun HeaderUI() {
    val databaseReference = FirebaseDatabase.getInstance().reference
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userNameState = remember { mutableStateOf("") }

    // Retrieve user name from Realtime Database
    currentUser?.let { user ->
        val userReference = databaseReference.child("Users").child(user.uid)
        userReference.child("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                userNameState.value = userName ?: ""
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column() {
            Text(
                text = "Hello, ${userNameState.value}",
                fontFamily = Poppins,
                fontSize = 18.sp,
                color = PrimaryTextColor,
                fontWeight = FontWeight.Bold
            )
            Row() {
                Text(
                    text = "Let's manage your ",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = SubTextColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "RoboWatch",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = Orange,
                    fontWeight = FontWeight.Medium
                )
            }

        }

        Image(
            painter = painterResource(id = R.drawable.robo),
            contentDescription = "",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
        )

    }
}

@Composable
fun RobotStatusCardUI(robotWorkingStatus: Boolean) {

    val annotatedString1 =
            if (robotWorkingStatus) {
                AnnotatedString.Builder("ON")
                    .apply {
                        addStyle(
                            SpanStyle(
                                color = Color.Black,
                            ), 0, 2
                        )
                    }
                    .toAnnotatedString() // Convert AnnotatedString.Builder to AnnotatedString
            } else {
                AnnotatedString.Builder("OFF")
                    .apply {
                        addStyle(
                            SpanStyle(
                                color = Color.Black,
                            ), 0, 3
                        )
                    }
                    .toAnnotatedString() // Convert AnnotatedString.Builder to AnnotatedString
            }

    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp) // 22
            .padding(top = 5.dp)
            ,
        elevation = 0.dp,
        shape = Shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(
                    text = "Robot status",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = SubTextColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick_circle),
                        contentDescription = "",
                        tint = PrimaryColor,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = annotatedString1,
                        fontFamily = Poppins,
                        fontSize = 18.sp,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick =
                    {
                        val db = FirebaseDatabase.getInstance()
                        val docRef = db.getReference("RobotStatus/WorkingStatus")
                        docRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val existingValue = dataSnapshot.getValue(Boolean::class.java)

                                if (existingValue != null) {
                                    val newValue = !existingValue
                                    docRef.setValue(newValue)
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                // NOTHING
                            }
                        })
                    },
                    modifier = Modifier
                        .clip(Shapes.large)
                        .border(width = 0.dp, color = Color.Transparent, shape = Shapes.large),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = run {
                            if (robotWorkingStatus) {
                                "Turn Off"
                            } else {
                                "Turn On"
                            }
                        },
                        fontSize = 10.sp,
                        modifier = Modifier.align(alignment = CenterVertically),
                        fontFamily = Poppins
                    )
                }
            }
        }
    }
}

@Composable
fun IntruderDetectedCardUI(intruderState: Int)
{
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp) // 22
            .padding(top = 5.dp),
        elevation = 0.dp,
        shape = Shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(
                    text = "Intruder Deteced",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = SubTextColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick_circle),
                        contentDescription = "",
                        tint = PrimaryColor,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = run {
                            if (intruderState == 1) {
                                "YES"
                            }
                            else if(intruderState == 0) {
                                "NO"
                            } else {
                                "Error[code 01]"
                            }
                        },
                        fontFamily = Poppins,
                        fontSize = 18.sp,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.ExtraBold

                    )
                }
            }
        }
    }
}

@Composable
fun BatteryStatusCardUI(batteryStatus: Int)
{
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp) // 22
            .padding(top = 5.dp),
        elevation = 0.dp,
        shape = Shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(
                    text = "Battery Status",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = SubTextColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick_circle),
                        contentDescription = "",
                        tint = PrimaryColor,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$batteryStatus%",
                        fontFamily = Poppins,
                        fontSize = 18.sp,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun RobotMovementCardUI(robotMovementStatus: Int)
{
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp) // 22
            .padding(top = 5.dp),
        elevation = 0.dp,
        shape = Shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(
                    text = "Robot Movement",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = SubTextColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick_circle),
                        contentDescription = "",
                        tint = PrimaryColor,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = run {
                            if(robotMovementStatus == 1)
                                "Manual"
                            else
                                "Autonomous"
                        },
                        fontFamily = Poppins,
                        fontSize = 18.sp,
                        color = PrimaryTextColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val db = FirebaseDatabase.getInstance()
                        val docRef = db.getReference("RobotStatus/ControlMovement")
                        docRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val existingValue = dataSnapshot.getValue(Int::class.java)

                                if (existingValue != null) {
                                    val newValue = if (existingValue == 0) 1 else 0
                                    docRef.setValue(newValue)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle database error
                            }
                        })

                    },
                    modifier = Modifier
                        .clip(Shapes.large)
                        .border(width = 0.dp, color = Color.Transparent, shape = Shapes.large),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = run {
                            if (robotMovementStatus == 1) {
                                "Switch to Autonomous"
                            } else {
                                "Switch to Manual"
                            }
                        },
                        fontSize = 10.sp,
                        modifier = Modifier.align(alignment = CenterVertically),
                        fontFamily = Poppins
                    )
                }
            }
        }
    }
}
