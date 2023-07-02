package com.robowatch.util

import androidx.annotation.DrawableRes
import com.robowatch.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    object First : OnBoardingPage(
        image = R.drawable.first,
        title = "Live Feed Stream",
        description = "With RoboWatch, you can remotely see what's happening in your home, office, or any other place you want to monitor!"
    )

    object Second : OnBoardingPage(
        image = R.drawable.second,
        title = "Manual Control",
        description = "With the Manual Control feature, you can take full control of your robot's movement and explore your environment in real-time!"
    )

    object Third : OnBoardingPage(
        image = R.drawable.third,
        title = "Alerts and Notifications",
        description = "With the Alerts and Notifications feature, you can receive real-time updates about what's happening in your environment!"
    )
}
