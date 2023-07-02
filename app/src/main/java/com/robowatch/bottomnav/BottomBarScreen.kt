package com.robowatch.bottomnav

import com.robowatch.R

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: Int,
    val icon_focused: Int
) {

    // for home
    object Home: BottomBarScreen(
        route = "home",
        title = "Home",
        icon = R.drawable.ic_bottom_home,
        icon_focused = R.drawable.ic_bottom_home_focused
    )

    // for control
    object Report: BottomBarScreen(
        route = "report",
        title = "Control",
        icon = R.drawable.ic_bottom_control_filled,
        icon_focused = R.drawable.ic_bottom_control_filled
    )

    // for scheduled patrols
    object Patrols: BottomBarScreen(
        route = "patrols",
        title = "Scheduled Patrols",
        icon = R.drawable.ic_bottom_patrols_filled,
        icon_focused = R.drawable.ic_bottom_patrols_filled
    )

    // for data log
    object Log: BottomBarScreen(
        route = "log",
        title = "Historical Data and Analytics",
        icon = R.drawable.ic_bottom_report,
        icon_focused = R.drawable.ic_bottom_report_focused
    )

    // for report
    object Profile: BottomBarScreen(
        route = "profile",
        title = "Profile",
        icon = R.drawable.ic_bottom_profile,
        icon_focused = R.drawable.ic_bottom_profile_focused
    )
}
