package com.example.nbrbcurrency.utils

import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        fun getFormattedDate(date : Date) : String {
            val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH)
            return formatter.format(date)
        }

        fun getTomorrowDate(date: Date):String{
            val calendar = GregorianCalendar()
            calendar.time = date
            calendar.add(Calendar.DATE, 1)
            return getFormattedDate(calendar.time)
        }
    }
}