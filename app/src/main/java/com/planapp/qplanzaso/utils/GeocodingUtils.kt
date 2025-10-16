package com.planapp.qplanzaso.utils

import android.content.Context
import android.location.Geocoder
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Utilidades para convertir entre direcciones y coordenadas.
 * Se ejecuta en corrutinas (IO) para no bloquear la UI.
 */
object GeocodingUtils {

    suspend fun obtenerCoordenadasDesdeDireccion(context: Context, direccion: String): GeoPoint? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val resultados = geocoder.getFromLocationName(direccion, 1)
                if (!resultados.isNullOrEmpty()) {
                    val loc = resultados.first()
                    GeoPoint(loc.latitude, loc.longitude)
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun obtenerDireccionDesdeCoordenadas(context: Context, lat: Double, lon: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val resultados = geocoder.getFromLocation(lat, lon, 1)
                resultados?.firstOrNull()?.getAddressLine(0)
            } catch (e: Exception) {
                null
            }
        }
    }
}
