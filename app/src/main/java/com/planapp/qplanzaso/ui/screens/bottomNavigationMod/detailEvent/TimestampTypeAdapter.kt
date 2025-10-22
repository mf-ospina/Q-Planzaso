package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import com.google.firebase.Timestamp
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Adaptador personalizado para serializar y deserializar objetos Timestamp de Firebase
 */
class TimestampTypeAdapter : JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {

    override fun serialize(
        src: Timestamp?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return if (src == null) {
            JsonNull.INSTANCE
        } else {
            JsonObject().apply {
                addProperty("seconds", src.seconds)
                addProperty("nanoseconds", src.nanoseconds)
            }
        }
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Timestamp? {
        if (json == null || json.isJsonNull) {
            return null
        }

        return try {
            val jsonObject = json.asJsonObject
            val seconds = jsonObject.get("seconds")?.asLong ?: 0L
            val nanoseconds = jsonObject.get("nanoseconds")?.asInt ?: 0
            Timestamp(seconds, nanoseconds)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}