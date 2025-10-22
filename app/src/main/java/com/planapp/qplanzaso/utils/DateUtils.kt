package com.planapp.qplanzaso.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun timestampToMillis(ts: Timestamp?): Long? = ts?.toDate()?.time

fun millisToTimestamp(ms: Long?): Timestamp? = ms?.let { Timestamp(Date(it)) }

fun timestampToReadable(ts: Timestamp?, pattern: String = "dd/MM/yyyy HH:mm"): String {
    return ts?.toDate()?.let {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(it)
    } ?: "Sin fecha"
}
