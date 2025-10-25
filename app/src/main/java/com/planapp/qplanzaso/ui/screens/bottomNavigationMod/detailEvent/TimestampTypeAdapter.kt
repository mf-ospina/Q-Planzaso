package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import com.google.firebase.Timestamp
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * Gson TypeAdapter para (de)serializar com.google.firebase.Timestamp
 * Escribe el timestamp como número de segundos (long) y lee números o strings convertibles.
 */
class TimestampTypeAdapter : TypeAdapter<Timestamp?>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Timestamp?) {
        if (value == null) {
            out.nullValue()
            return
        }
        // Guardamos solo los segundos (puedes cambiar a milliseconds si lo prefieres)
        out.value(value.seconds)
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Timestamp? {
        return when (reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            JsonToken.NUMBER -> {
                val seconds = reader.nextLong()
                Timestamp(seconds, 0)
            }
            JsonToken.STRING -> {
                val s = reader.nextString()
                val seconds = s.toLongOrNull() ?: 0L
                Timestamp(seconds, 0)
            }
            else -> {
                // Si viene un objeto complejo u otro tipo inesperado, consumir y devolver null
                reader.skipValue()
                null
            }
        }
    }
}
