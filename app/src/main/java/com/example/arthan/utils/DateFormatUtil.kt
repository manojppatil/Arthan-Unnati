package com.example.arthan.utils

import java.text.SimpleDateFormat
import java.util.*

class DateFormatUtil {

    companion object{
        fun currentTime(format: String): String {
            return formatDate(Calendar.getInstance().time, format)
        }

        fun formatDate(date: Date, strFormat: String): String {
            val format = SimpleDateFormat(strFormat, Locale.getDefault())
            return format.format(date)
        }
    }



}