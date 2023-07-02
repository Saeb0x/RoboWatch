package com.robowatch.screen

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.database.FirebaseDatabase
import com.robowatch.R
import com.robowatch.joystick.JoystickView
import com.robowatch.ui.theme.BackgroundColor
import com.robowatch.ui.theme.PrimaryColor
import com.robowatch.ui.theme.PrimaryTextColor

@Composable
fun ReportScreen(streamingURL: MutableState<String>) {
    val angleText = remember { mutableStateOf("0°") }
    val powerText = remember { mutableStateOf("0%") }
    val directionText = remember { mutableStateOf("Center") }
    val streamingurl = streamingURL.value

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column()
        {
            WebViewComponent(streamingurl)
            JoystickViewWrapper(
                onJoystickMoveListener1 = object : JoystickView.OnJoystickMoveListener {
                    override fun onValueChanged(angle: Int, power: Int, direction: Int) {
                        angleText.value = " $angle°"
                        powerText.value = " $power%"
                        directionText.value = when (direction) {
                            JoystickView.FRONT -> "Forward"
                            JoystickView.FRONT_RIGHT -> "Right-Forward"
                            JoystickView.RIGHT -> "Right"
                            JoystickView.RIGHT_BOTTOM -> "Right-Backward"
                            JoystickView.BOTTOM -> "Backward"
                            JoystickView.BOTTOM_LEFT -> "Left-Backward"
                            JoystickView.LEFT -> "Left"
                            JoystickView.LEFT_FRONT -> "Left-Forward"
                            else -> "Center"
                        }

                        val database = FirebaseDatabase.getInstance()
                        val joystickDataRef = database.getReference("JoystickCoor")
                        val switchControlMovement =
                            database.getReference("RobotStatus/ControlMovement")

                        val directionString = when (direction) {
                            1 -> "Left"
                            2 -> "Left-Forward"
                            3 -> "Forward"
                            4 -> "Right-Forward"
                            5 -> "Right"
                            6 -> "Right-Backward"
                            7 -> "Backward"
                            8 -> "Left-Backward"
                            else -> "Center"
                        }

                        val data = hashMapOf(
                            "angle" to angle,
                            "power" to power,
                            "directMapped" to directionString,
                            "direct" to direction
                        )
                        switchControlMovement.setValue(1)
                        joystickDataRef.setValue(data)
                    }
                }/*,
                onJoystickMoveListener2 = object : JoystickView.OnJoystickMoveListener {
                    override fun onValueChanged(angle: Int, power: Int, direction: Int) {
                        angleText.value = " $angle°"
                        powerText.value = " $power%"
                        directionText.value = when (direction) {
                            JoystickView.FRONT -> "Forward"
                            JoystickView.FRONT_RIGHT -> "Right-Forward"
                            JoystickView.RIGHT -> "Right"
                            JoystickView.RIGHT_BOTTOM -> "Right-Backward"
                            JoystickView.BOTTOM -> "Backward"
                            JoystickView.BOTTOM_LEFT -> "Left-Backward"
                            JoystickView.LEFT -> "Left"
                            JoystickView.LEFT_FRONT -> "Left-Forward"
                            else -> "Center"
                        }

                        val database = FirebaseDatabase.getInstance()
                        val joystickDataRef = database.getReference("JoystickCoorPTC")
                        val switchControlMovement = database.getReference("RobotStatus/ControlMovement")

                        val directionString = when (direction) {
                            1 -> "Left"
                            2 -> "Left-Forward"
                            3 -> "Forward"
                            4 -> "Right-Forward"
                            5 -> "Right"
                            6 -> "Right-Backward"
                            7 -> "Backward"
                            8 -> "Left-Backward"
                            else -> "Center"
                        }

                        val data = hashMapOf(
                            "angle" to angle,
                            "power" to power,
                            "directMapped" to directionString,
                            "direct" to direction
                        )
                        switchControlMovement.setValue(true)
                        joystickDataRef.setValue(data)
                    }
                }*/,
                angleText = angleText,
                powerText = powerText,
                directionText = directionText
            )
        }
    }
}

@Composable
fun JoystickViewWrapper(
    onJoystickMoveListener1: JoystickView.OnJoystickMoveListener,
    /*onJoystickMoveListener2: JoystickView.OnJoystickMoveListener,*/
    angleText: MutableState<String>,
    powerText: MutableState<String>,
    directionText: MutableState<String>,
) {

    AndroidView(
        factory = { context ->
            LinearLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Inflate the LinearLayout XML layout
                LayoutInflater.from(context).inflate(R.layout.joystick_layout, this, true)
            }
        },
        update = { layout ->
            val joystick1 = layout.findViewById<JoystickView>(R.id.joystickView)
            joystick1.setOnJoystickMoveListener(onJoystickMoveListener1, JoystickView.DEFAULT_LOOP_INTERVAL)

            val upButton = layout.findViewById<Button>(R.id.buttonUp)
            val leftButton = layout.findViewById<Button>(R.id.buttonLeft)
            val rightButton = layout.findViewById<Button>(R.id.buttonRight)
            val downButton = layout.findViewById<Button>(R.id.buttonDown)
            val resetButton = layout.findViewById<Button>(R.id.buttonReset)

            // Reference to the Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val buttonUpDataRef = database.getReference("ButtonsPTC/ButtonUp")
            val buttonDownDataRef = database.getReference("ButtonsPTC/ButtonDown")
            val buttonRightDataRef = database.getReference("ButtonsPTC/ButtonRight")
            val buttonLeftDataRef = database.getReference("ButtonsPTC/ButtonLeft")
            val buttonResetDataRef = database.getReference("ButtonsPTC/ButtonReset")

            // Set touch listeners for the buttons
            upButton.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    buttonUpDataRef.setValue(1)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    buttonUpDataRef.setValue(0)
                }
                false
            }

            leftButton.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    buttonLeftDataRef.setValue(1)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    buttonLeftDataRef.setValue(0)
                }
                false
            }

            rightButton.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    buttonRightDataRef.setValue(1)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    buttonRightDataRef.setValue(0)
                }
                false
            }

            downButton.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    buttonDownDataRef.setValue(1)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    buttonDownDataRef.setValue(0)
                }
                false
            }

            resetButton.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    buttonResetDataRef.setValue(1)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    buttonResetDataRef.setValue(0)
                }
                false
            }

            /*val joystick2 = view.findViewById<JoystickView>(R.id.joystickViewTwo)
            joystick2.setOnJoystickMoveListener(onJoystickMoveListener2, JoystickView.DEFAULT_LOOP_INTERVAL)*/
        }
    )
}

@Composable
fun WebViewComponent(streamingurl: String) {
    Box(
        modifier = Modifier
            .size(500.dp, 400.dp)
            .background(brush = Brush.linearGradient(
                colors = listOf(PrimaryColor, PrimaryTextColor),
                start = Offset.Zero,
                end = Offset.Infinite
            ))
            .padding(10.dp)
    ) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = WebViewClient()

                // Enable JavaScript
                settings.javaScriptEnabled = true

                loadUrl("${streamingurl}")

            }
        })
    }
}


