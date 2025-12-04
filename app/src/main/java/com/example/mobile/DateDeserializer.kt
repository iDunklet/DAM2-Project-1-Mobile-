package com.example.mobile

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateDeserializer : JsonDeserializer<Date> {
    private val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()), // ISO
        SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH)     // Jun 15, 2024 12:00:00 AM
    )

    override fun deserialize(
        json: JsonElement,
        typeOfT: java.lang.reflect.Type,
        context: JsonDeserializationContext
    ): Date {
        val value = json.asString
        for (format in formats) {
            try {
                val parsed = format.parse(value)
                if (parsed != null) return parsed
            } catch (_: Exception) {
                // sigue probando con el siguiente formato
            }
        }
        throw JsonParseException("Error parseando la fecha: $value")
    }
}