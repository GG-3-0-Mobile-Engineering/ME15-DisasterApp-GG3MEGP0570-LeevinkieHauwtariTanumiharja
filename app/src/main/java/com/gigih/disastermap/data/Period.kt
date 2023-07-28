package com.gigih.disastermap.data

enum class Period(val timeInSeconds: Long) {
    ONE_WEEK(604800),
    FIVE_DAYS(432000),
    TODAY(86400)
}