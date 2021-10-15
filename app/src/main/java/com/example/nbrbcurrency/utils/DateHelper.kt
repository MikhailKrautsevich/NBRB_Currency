package com.example.nbrbcurrency.utils

import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        fun getDateForRetrofit(date : Date) : String {
            val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH)
            return formatter.format(date)
        }

        fun getTomorrowDateForRetrofit(date: Date):String{
            val calendar = GregorianCalendar()
            calendar.time = date
            calendar.add(Calendar.DATE, 1)
            return getDateForRetrofit(calendar.time)
        }

        fun getDateForTextView(date: Date):String{
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            return formatter.format(date)
        }

        fun getTomorrowDateForTextView(date: Date):String{
            val calendar = GregorianCalendar()
            calendar.time = date
            calendar.add(Calendar.DATE, 1)
            return getDateForTextView(calendar.time)
        }
    }
}