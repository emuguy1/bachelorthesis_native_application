package de.num42.sharing

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun String.convertStringToDate(): Date? {
    return SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.US).parse(this)
}

fun Date.toTimeGoneByString():String{
    val currentDate = Date().nowUTC()

    val diff: Long = currentDate.time - this.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30
    val years = days / 365

    val ageText = if (years > 0) {
        "$years years ago"
    } else if(months > 0){
        "$months months ago"
    }
    else if(days > 0) {
        "$days days ago"
    }
    else{
        "$hours hours ago"
    }

    return ageText
}

fun Date.nowUTC(): Date {
    return Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))

}