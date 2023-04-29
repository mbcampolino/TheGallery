package com.codecampos.thegallery.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AppUtils {

    fun dateToShow(date: Long): String {
        return DateTimeFormatter.ofPattern("dd MMMM yyyy").format(
            Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        )
    }

}