package com.planapp.qplanzaso

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object StorageManager {
    private val storage = FirebaseStorage.getInstance().reference

    fun subirImagen(uri: Uri, path: String, callback: (String?) -> Unit) {
        val ref = storage.child(path)
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    callback(url.toString())
                }
            }
            .addOnFailureListener { callback(null) }
    }
}
