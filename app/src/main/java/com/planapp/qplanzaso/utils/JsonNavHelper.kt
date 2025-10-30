package com.planapp.qplanzaso.utils

import android.util.Base64
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.TimestampTypeAdapter

object JsonNavHelper {

    // 🔹 Hacemos visible el gson para funciones inline
    @PublishedApi
    internal val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
        .create()

    /** 🔹 Codifica cualquier objeto en Base64 */
    fun encode(obj: Any): String {
        val json = gson.toJson(obj)
        return Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    /** 🔹 Decodifica el JSON a un objeto del tipo indicado */
    inline fun <reified T> decode(encoded: String): T? {
        return try {
            val decoded = String(Base64.decode(encoded, Base64.NO_WRAP), Charsets.UTF_8)
            gson.fromJson(decoded, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

