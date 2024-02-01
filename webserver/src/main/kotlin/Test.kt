package io.github.vibraplatform.webserver

import io.github.vibraplatform.common.snowflake.Snowflake
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun main() {
    val snowflake = Snowflake(6469376176427008)
    println(6469376176427008 shr 22)
    println(snowflake.value shr 22)
    println(snowflake.timeDifference.inWholeSeconds)
    println(snowflake.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()))
}