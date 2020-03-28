package com.mmo.hackernews.util

import java.math.BigDecimal
import kotlin.math.round

object DateDisplayUtil {

    private const val TAG = "JSONUtil"

    //Takes time in seconds and returns it as a string in the highest possible unit
    fun compoundDuration(n: Long): String {
        if (n < 0) return "" // task doesn't ask for negative integers to be converted
        if (n == 0L) return "Just now"
        if (n in 86400..172799) return "Yesterday"

        val weeks  : Long
        val days   : Long
        val hours  : Long
        val minutes: Long
        val seconds: Long
        var divisor: Long = 7 * 24 * 60 * 60
        var rem    : Long
        var result = ""

        weeks = n / divisor
        rem   = n % divisor
        divisor /= 7
        days  = rem / divisor
        rem  %= divisor
        divisor /= 24
        hours = rem / divisor
        rem  %= divisor
        divisor /= 60
        minutes = rem / divisor
        seconds = rem % divisor

        if (weeks > 0)   return "${weeks}w"
        if (days > 1)    return "${days}d"
        if (hours > 0){
            when(minutes) {
                in 0..14 -> return "${hours}h"
                in 15..45 -> return "${hours}.5h"
                in 46..59 -> return "${hours+1L}h"
            }
        }
        if (minutes > 0) return "${minutes}m"
        if (seconds > 0) return "${seconds}s"
        else
            result = result.substring(0, result.length - 2)
        return result
    }
}