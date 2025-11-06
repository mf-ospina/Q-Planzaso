package com.planapp.qplanzaso
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
//import com.planapp.qplanzaso.work.TestWorker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.MapsInitializer
import com.planapp.qplanzaso.ui.theme.QPlanzasoTheme
import com.planapp.qplanzaso.ui.navigation.AppNavigation
import com.planapp.qplanzaso.utils.NotificationHelper
import com.planapp.qplanzaso.work.DailySummaryScheduler
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DailySummaryScheduler.scheduleFromPrefs(this)
        Log.d("MyApp", "Firebase inicializado correctamente ✅")
        enableEdgeToEdge()


        NotificationHelper.createNotificationChannel(this)

        // 🔹 TEST: ejecutar un worker 10 segundos después de abrir la app
        /*val request = OneTimeWorkRequestBuilder<TestWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(request)*/

        enableEdgeToEdge()
        setContent {
            QPlanzasoTheme {
                AppNavigation(modifier = Modifier)
            }
        }

        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST) { renderer ->
            Log.d("MAPS", "Google Maps renderer used: $renderer")
        }
    }
}

