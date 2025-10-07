package com.planapp.qplanzaso

import android.app.Application
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Conecta al emulador de Firebase cuando estás en modo debug
        if (BuildConfig.DEBUG) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.useEmulator("10.0.2.2", 8080)

            // 🔑 Configurar Auth
            val auth = FirebaseAuth.getInstance()
            auth.useEmulator("10.0.2.2", 9099)

            // 📦 Configurar Storage
            val storage = Firebase.storage
            storage.useEmulator("10.0.2.2", 9199)
        }
    }
}
