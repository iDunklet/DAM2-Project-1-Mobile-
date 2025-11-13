package com.example.mobile

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateDeserializer : JsonDeserializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun deserialize(json: JsonElement, typeOfT: java.lang.reflect.Type, context: JsonDeserializationContext): Date {
        return try {
            dateFormat.parse(json.asString) ?: throw JsonParseException("No se pudo parsear la fecha")
        } catch (e: Exception) {
            throw JsonParseException("Error parseando la fecha: ${json.asString}", e)
        }
    }
}
