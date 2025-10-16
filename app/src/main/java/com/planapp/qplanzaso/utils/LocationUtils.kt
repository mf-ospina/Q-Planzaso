package com.planapp.qplanzaso.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

object LocationUtils {

    const val LOCATION_PERMISSION_REQUEST_CODE = 1001

    // ✅ Solicita permisos de ubicación (si no los tiene)
    fun solicitarPermisosUbicacion(activity: Activity): Boolean {
        val permisoFine = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val permisoCoarse = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)

        return if (permisoFine != PackageManager.PERMISSION_GRANTED || permisoCoarse != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            false
        } else {
            true
        }
    }

    // ✅ Obtener ubicación actual (usa FusedLocationProviderClient)
    @SuppressLint("MissingPermission")
    suspend fun obtenerUbicacionActual(activity: Activity): GeoPoint? {
        val fusedClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)

        return try {
            val location = fusedClient.lastLocation.await()
            if (location != null) {
                GeoPoint(location.latitude, location.longitude)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
