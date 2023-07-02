package com.robowatch.notifications

data class PushNotification(
    val data: NotificationData,
    val to: String
)