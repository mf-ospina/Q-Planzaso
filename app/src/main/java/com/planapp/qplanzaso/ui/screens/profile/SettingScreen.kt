package com.planapp.qplanzaso.ui.screens.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val uid = auth.currentUser?.uid

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
            }
            Spacer(Modifier.width(8.dp))
            Text("Configuración", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(8.dp))

        Text("Cuenta", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        SettingRow(
            title = "Información de perfil",
            subtitle = "Nombre, correo y más",
            leading = { Icon(Icons.Rounded.Person, contentDescription = null) },
            onClick = { navController.navigateUp() } // vuelve un nivel a EditProfile
        )
        HorizontalDivider()

        Text(
            "Notificaciones",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
        SettingRow(
            title = "Preferencias de notificación",
            subtitle = "Silencio, resumen diario, categorías",
            leading = { Icon(Icons.Rounded.Notifications, contentDescription = null) },
            onClick = { navController.navigate("notif_settings") }
        )
        HorizontalDivider()

        Spacer(Modifier.height(24.dp))

        if (errorMsg != null) {
            Text(
                errorMsg!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isDeleting && uid != null,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = BorderStroke(1.dp, SolidColor(MaterialTheme.colorScheme.error))
        ) {
            Icon(Icons.Rounded.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (isDeleting) "Eliminando…" else "Eliminar cuenta")
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteDialog = false },
            title = { Text("¿Eliminar cuenta?") },
            text = { Text("Esta acción es irreversible. Se borrarán tus datos y tu cuenta.") },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting && uid != null,
                    onClick = {
                        if (uid == null) {
                            errorMsg = "Sesión no válida. Inicia sesión de nuevo."
                            showDeleteDialog = false
                            return@TextButton
                        }
                        isDeleting = true
                        errorMsg = null
                        // Proceso de borrado
                        scope.launch {
                            try {
                                // 1) Borra subcolecciones si tienes (ejemplo: "preferencias" y "dispositivos")
                                //    Si no las usas, comenta esta sección.
                                runCatching {
                                    val userDoc = db.collection("usuarios").document(uid)
                                    val batch = db.batch()
                                    val prefs = userDoc.collection("preferencias").get().await()
                                    prefs.documents.forEach { batch.delete(it.reference) }
                                    val dispositivos = userDoc.collection("dispositivos").get().await()
                                    dispositivos.documents.forEach { batch.delete(it.reference) }
                                    batch.commit().await()
                                }

                                // 2) Borra (o deja vacío) el doc principal
                                //    Si prefieres no borrarlo, podrías setear flags tipo "deletedAt"
                                db.collection("usuarios").document(uid).delete().await()

                                // 3) (Opcional) Si usabas otro lado para referenciar al user, limpialo aquí (SetOptions.merge() en colecciones globales)
                                // db.collection("estadisticas").document(uid).set(mapOf("activo" to false), SetOptions.merge()).await()

                                // 4) Elimina la cuenta de Firebase Auth (puede requerir re-login reciente)
                                auth.currentUser?.delete()?.await()

                                showDeleteDialog = false
                                // 5) Navega limpio al login
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                    launchSingleTop = true
                                }
                            } catch (e: Exception) {
                                val needsRecentLogin =
                                    (e is FirebaseAuthException && e.errorCode == "ERROR_REQUIRES_RECENT_LOGIN")
                                errorMsg = if (needsRecentLogin) {
                                    "Por seguridad, inicia sesión nuevamente para eliminar la cuenta."
                                } else {
                                    "No se pudo eliminar la cuenta: ${e.message ?: "error desconocido"}"
                                }
                            } finally {
                                isDeleting = false
                            }
                        }
                    }
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Sí, eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(enabled = !isDeleting, onClick = { showDeleteDialog = false }) { Text("No") }
            }
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    leading: @Composable RowScope.() -> Unit,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leading()
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
