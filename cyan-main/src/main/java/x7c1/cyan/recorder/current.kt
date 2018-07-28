package x7c1.cyan.recorder

import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE
import java.util.Calendar.MILLISECOND
import java.util.Calendar.SECOND

fun current(): String {
    val calendar = Calendar.getInstance()
    val fields = listOf(
        HOUR_OF_DAY,
        MINUTE,
        SECOND
    )
    val hms = fields.map(calendar::get).joinToString(
        separator = ":",
        transform = { String.format("%02d", it) }
    )
    val msec = String.format("%03d", calendar.get(MILLISECOND)).take(2)
    return "$hms.$msec"
}
