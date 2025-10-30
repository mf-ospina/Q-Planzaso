// app/src/main/java/com/planapp/qplanzaso/ui/screens/profile/NotificationSettingsScreen.kt
package com.planapp.qplanzaso.ui.screens.profile

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    state: NotificationPrefs,
    onChange: (NotificationPrefs) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                }
                Spacer(Modifier.width(8.dp))
                Text("Notificaciones", style = MaterialTheme.typography.titleLarge)
            }
        }

        // Canales
        item { SectionTitle("Canales") }
        item {
            SettingSwitch("Push", "Avisos en el dispositivo", state.push) { checked ->
                onChange(state.copy(push = checked))
            }
        }
        item {
            SettingSwitch("Correo", "Resumen y avisos por e-mail", state.email) { checked ->
                onChange(state.copy(email = checked))
            }
        }
        item { HorizontalDivider() }

        // Preferencias
        item { SectionTitle("Preferencias de alerta") }
        item {
            SettingSwitch("Vibración", "Vibra al recibir", state.vibrate) { c ->
                onChange(state.copy(vibrate = c))
            }
        }
        item {
            SettingSwitch("Sonido", "Tono del sistema", state.sound) { c ->
                onChange(state.copy(sound = c))
            }
        }
        item {
            SettingSwitch("Prioridad alta", "Aparece como importante", state.priorityHigh) { c ->
                onChange(state.copy(priorityHigh = c))
            }
        }
        item { HorizontalDivider() }

        // Resumen diario
        item { SectionTitle("Resumen diario") }
        item {
            SettingSwitch("Enviar resumen", "Una vez al día", state.dailySummary) { c ->
                onChange(state.copy(dailySummary = c))
            }
        }
        item {
            TimePickerRow(
                enabled = state.dailySummary,
                time = state.summaryTime,
                onTime = { newTime -> onChange(state.copy(summaryTime = newTime)) }
            )
        }
        item { HorizontalDivider() }

        // Categorías
        item { SectionTitle("Categorías") }
        item {
            SettingSwitch("Eventos cercanos", "Recomendados según tus intereses", state.nearEvents) { c ->
                onChange(state.copy(nearEvents = c))
            }
        }
        item {
            SettingSwitch("Recordatorios", "Asistencias, check-ins, cambios", state.reminders) { c ->
                onChange(state.copy(reminders = c))
            }
        }

        // Acciones
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancelar") }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Guardar") }
            }
        }

        item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun TimePickerRow(
    enabled: Boolean,
    time: String,
    onTime: (String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val hours = (0..23).map { it.toString().padStart(2, '0') }
    val minutes = listOf("00", "15", "30", "45")
    var h = time.take(2).padStart(2, '0')
    var m = time.takeLast(2).padStart(2, '0')

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = "$h:$m",
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Hora") },
            modifier = Modifier.weight(1f),
            trailingIcon = {
                TextButton(enabled = enabled, onClick = {
                    val idx = hours.indexOf(h).coerceAtLeast(0)
                    h = hours[(idx + 1) % hours.size]
                    onTime("$h:$m")
                }) { Text("Hora") }
            }
        )
        Spacer(Modifier.width(8.dp))
        OutlinedButton(
            enabled = enabled,
            onClick = {
                val idx = minutes.indexOf(m).coerceAtLeast(0)
                m = minutes[(idx + 1) % minutes.size]
                onTime("$h:$m")
            },
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Min $m")
        }
    }
}