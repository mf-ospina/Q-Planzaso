// app/src/main/java/com/planapp/qplanzaso/ui/screens/profile/NotificationSettingsScreen.kt
package com.planapp.qplanzaso.ui.screens.profile
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import com.planapp.qplanzaso.ui.theme.PrimaryColor

@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    state: NotificationPrefs,
    onChange: (NotificationPrefs) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Atrás",
                        tint = PrimaryColor          // flecha naranja
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Notificaciones",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryColor            // título naranja
                )
            }
        }

        // Canales
        item { SectionTitle("Canales") }
        item {
            SettingSwitch("Push", "Avisos en el dispositivo", state.push) { checked ->
                onChange(state.copy(push = checked))
            }
        }
        /*item {
            SettingSwitch("Correo", "Resumen y avisos por e-mail", state.email) { checked ->
                onChange(state.copy(email = checked))
            }
        }*/
        item { HorizontalDivider() }

        // Preferencias
        /*item { SectionTitle("Preferencias de alerta") }
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
        item { HorizontalDivider() }*/

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
        /*item { SectionTitle("Categorías") }
        item {
            SettingSwitch(
                "Eventos cercanos",
                "Recomendados según tus intereses",
                state.nearEvents
            ) { c ->
                onChange(state.copy(nearEvents = c))
            }
        }*/
        item {
            SettingSwitch(
                "Recordatorios",
                "Asistencias, check-ins, cambios",
                state.reminders
            ) { c ->
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
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryColor
                    )
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("Guardar")
                }
            }
        }

        item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = DarkGrayText.copy(alpha = 0.8f)   // texto se va al gris oscuro de la app
    )
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = DarkGrayText
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = DarkGrayText.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryColor,
                checkedTrackColor = PrimaryColor.copy(alpha = 0.4f)
            )
        )
    }
}

@Composable
private fun TimePickerRow(
    enabled: Boolean,
    time: String,
    onTime: (String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Parseamos el "HH:mm" que viene de prefs
    val initialHour = time.take(2).toIntOrNull() ?: 8
    val initialMinute = time.takeLast(2).toIntOrNull() ?: 0

    // 👇 Importante: atar el remember a `time`
    var selectedHour by remember(time) { mutableStateOf(initialHour) }
    var selectedMinute by remember(time) { mutableStateOf(initialMinute) }

    val timeText = remember(selectedHour, selectedMinute) {
        "%02d:%02d".format(selectedHour, selectedMinute)
    }

    fun openTimePicker() {
        if (!enabled) return

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
                val newTime = "%02d:%02d".format(hourOfDay, minute)
                onTime(newTime)  // 👉 aquí ya lo mandas al ViewModel
            },
            selectedHour,
            selectedMinute,
            true // formato 24h
        ).show()
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = timeText,
            onValueChange = { /* readOnly */ },
            readOnly = true,
            enabled = enabled,
            label = { Text("Hora del resumen") },
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = enabled) { openTimePicker() },
            trailingIcon = {
                TextButton(
                    enabled = enabled,
                    onClick = { openTimePicker() }
                ) {
                    Text("Cambiar")
                }
            }
        )
    }
}
