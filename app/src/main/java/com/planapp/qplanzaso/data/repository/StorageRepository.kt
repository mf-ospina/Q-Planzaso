package com.planapp.qplanzaso.data.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository {

    private val storageRef = Firebase.storage.reference

    // 🔹 Subir imagen y devolver URL pública
    suspend fun subirImagenEvento(uri: Uri, eventoId: String): String {
        val nombreArchivo = "evento/$eventoId/${UUID.randomUUID()}.jpg" // 👈 singular
        val fileRef = storageRef.child(nombreArchivo)

        fileRef.putFile(uri).await()
        return fileRef.downloadUrl.await().toString()
    }

    // 🔹 Eliminar una imagen
    suspend fun eliminarImagenPorUrl(url: String) {
        val ref = Firebase.storage.getReferenceFromUrl(url)
        ref.delete().await()
    }

    // 🔹 Reemplazar imagen (elimina la anterior y sube la nueva)
    suspend fun reemplazarImagen(urlAntigua: String?, uriNueva: Uri, eventoId: String): String {
        urlAntigua?.let { eliminarImagenPorUrl(it) }
        return subirImagenEvento(uriNueva, eventoId)
    }
}