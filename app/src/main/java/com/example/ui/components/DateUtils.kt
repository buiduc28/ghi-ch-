package com.example.ui.components

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatNoteDate(timestamp: Long): String {
        val now = Calendar.getInstance()
        val noteTime = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isToday = now.get(Calendar.YEAR) == noteTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == noteTime.get(Calendar.DAY_OF_YEAR)

        val isYesterday = now.get(Calendar.YEAR) == noteTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - noteTime.get(Calendar.DAY_OF_YEAR) == 1

        val sameYear = now.get(Calendar.YEAR) == noteTime.get(Calendar.YEAR)

        return when {
            isToday -> {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                "Hôm nay, ${timeFormat.format(Date(timestamp))}"
            }
            isYesterday -> {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                "Hôm qua, ${timeFormat.format(Date(timestamp))}"
            }
            sameYear -> {
                val dateFormat = SimpleDateFormat("dd 'thg' MM, HH:mm", Locale("vi", "VN"))
                dateFormat.format(Date(timestamp))
            }
            else -> {
                val fullFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fullFormat.format(Date(timestamp))
            }
        }
    }
}
