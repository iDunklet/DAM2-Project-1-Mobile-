package com.example.mobile.helpers

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateDeserializer : JsonDeserializer<Date> {

    // ---------------------------------------------------------
    //  Configuración de formatos de fecha
    // ---------------------------------------------------------
    private val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()), // ISO
        SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH)     // Jun 15, 2024 12:00:00 AM
    )

    // ---------------------------------------------------------
    //  Método principal de deserialización
    // ---------------------------------------------------------
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Date {
        val dateString = json.asString
        return parseDate(dateString)
    }

    // ---------------------------------------------------------
    //  Lógica de parseo de fecha
    // ---------------------------------------------------------
    private fun parseDate(dateString: String): Date {
        val parsedDate = tryParseWithFormats(dateString)
        return parsedDate ?: throwParseException(dateString)
    }

    private fun tryParseWithFormats(dateString: String): Date? {
        for (format in dateFormats) {
            val parsedDate = tryParseWithFormat(format, dateString)
            if (parsedDate != null) return parsedDate
        }
        return null
    }

    private fun tryParseWithFormat(format: SimpleDateFormat, dateString: String): Date? {
        return try {
            format.parse(dateString)
        } catch (exception: Exception) {
            logParseError(dateString, exception)
            null
        }
    }

    // ---------------------------------------------------------
    //  Manejo de errores
    // ---------------------------------------------------------
    private fun throwParseException(dateString: String): Nothing {
        throw JsonParseException("Error parseando la fecha: $dateString")
    }

    private fun logParseError(dateString: String, exception: Exception) {
        Log.d("DataManager", "Error parseando fecha: $dateString")
    }
}