package com.robowatch

data class ProfileEntry(val name: String, val value: String)

fun getProfileEntries(): List<ProfileEntry> {
    return listOf(ProfileEntry("Email", "robowatch@gmail.com"),
        ProfileEntry("Twitter", "@robo_watch"),
        ProfileEntry("Phone", "00010001"))
}