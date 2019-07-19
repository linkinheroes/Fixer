package pl.janowicz.fixer.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @ToJson
    fun toJson(date: Date): String {
        return dateFormat.format(date)
    }

    @FromJson
    fun fromJson(text: String): Date {
        return dateFormat.parse(text)
    }
}