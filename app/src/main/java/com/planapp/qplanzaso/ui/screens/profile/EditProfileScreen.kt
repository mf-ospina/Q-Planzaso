package com.planapp.qplanzaso.ui.screens.profile

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

object Teclado {
    val correo: KeyboardType
        get() = KeyboardType.Email
}

private data class PreferenceGroup(
    val id: Long = System.nanoTime(),
    val title: String,
    val chips: MutableList<String>
)

@Composable
fun EditProfileScreen(
    state: ProfileFormState,
    onChange: (ProfileFormState) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    // 🔐 NUEVO: callback para cambiar contraseña (conéctalo a FirebaseAuth en el Entry/VM)
    onChangePassword: (current: String, newPass: String) -> Unit = { _, _ -> }
) {
    val scroll = rememberScrollState()

    data class EditDialog(
        val title: String,
        val initial: String,
        val keyboard: KeyboardType = KeyboardType.Text,
        val isPassword: Boolean = false,
        val onConfirm: (String) -> Unit
    )
    var fieldDialog by remember { mutableStateOf<EditDialog?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    // 🔐 NUEVO: diálogo de cambio de contraseña
    var showPwdDialog by remember { mutableStateOf(false) }

    val prefGroups = remember { mutableStateListOf<PreferenceGroup>() }
    val catalog = remember {
        mapOf(
            "Cocina" to listOf("Italiana","Mexicana","Japonesa","China","India","Mediterránea","Vegana",
                "Vegetariana","Parrilla","Pastelería","Panadería","Mariscos","Colombiana"),
            "Música" to listOf("Pop","Rock","Indie","Hip-Hop","R&B","Electrónica","House","Techno",
                "Reggaetón","Salsa","Bachata","Vallenato","Clásica","Jazz","Blues","Folk"),
            "Deporte" to listOf("Fútbol","Baloncesto","Ciclismo","Running","Natación","Tenis","Pádel","Triatlón",
                "Gimnasio","CrossFit","Yoga","Pilates","Escalada","Senderismo"),
            "Videojuegos" to listOf("Acción","Aventura","RPG","MMO","MOBA","Shooter","Simulación","Estrategia",
                "Indie","Deportes","Carreras","Lucha","Survival","Sandbox"),
            "Belleza" to listOf("Skincare","Maquillaje","Peinados","Uñas","Fragancias","Barbería","K-Beauty",
                "Cuidado corporal","Dermocosmética"),
            "Cine" to listOf("Acción","Aventura","Comedia","Drama","Sci-Fi","Fantasía","Terror",
                "Suspenso","Animación","Documental","Romance","Crimen")
        )
    }

    var catPickerOpen by remember { mutableStateOf(false) }
    var subPickerOpen by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedSubs by remember { mutableStateOf<Set<String>>(emptySet()) }
    var editingGroupId by remember { mutableStateOf<Long?>(null) }
    var menuExpandedFor by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
            }
            Text(
                "Editar perfil",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Box(Modifier.size(48.dp))
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${state.nombre.firstOrNull() ?: 'U'}",
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(state.nombre, style = MaterialTheme.typography.titleMedium)
            }
            FilledTonalIconButton(onClick = onOpenSettings) {
                Icon(Icons.Rounded.Settings, contentDescription = "Configuración")
            }
        }

        Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow("Nombre", state.nombre) {
                    fieldDialog = EditDialog("Nombre", state.nombre) { nuevo ->
                        onChange(state.copy(nombre = nuevo))
                    }
                }
                InfoRow("Correo", state.correo) {
                    fieldDialog = EditDialog("Correo", state.correo, keyboard = KeyboardType.Email) { nuevo ->
                        onChange(state.copy(correo = nuevo))
                    }
                }
                InfoRow("Teléfono", state.telefono) {
                    fieldDialog = EditDialog("Teléfono", state.telefono, keyboard = KeyboardType.Phone) { nuevo ->
                        onChange(state.copy(telefono = nuevo))
                    }
                }
                InfoRow("Ubicación", state.ubicacion) {
                    fieldDialog = EditDialog("Ubicación", state.ubicacion) { nuevo ->
                        onChange(state.copy(ubicacion = nuevo))
                    }
                }
                InfoRow("Bio", state.bio) {
                    fieldDialog = EditDialog("Bio", state.bio) { nuevo ->
                        onChange(state.copy(bio = nuevo))
                    }
                }

                // 🔐 NUEVO: acceso al cambio de contraseña
                InfoRow("Contraseña", "••••••••") {
                    showPwdDialog = true
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Cancelar") }

            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Guardar") }
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }

    // Diálogo genérico de edición de texto
    fieldDialog?.let { d ->
        var text by remember(d) { mutableStateOf(d.initial) }
        AlertDialog(
            onDismissRequest = { fieldDialog = null },
            title = { Text("Editar ${d.title}") },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = d.keyboard),
                    visualTransformation = if (d.isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        if (d.isPassword) {
                            TextButton(onClick = { showPassword = !showPassword }) {
                                Text(if (showPassword) "Ocultar" else "Ver")
                            }
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { d.onConfirm(text.trim()); fieldDialog = null }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { fieldDialog = null }) { Text("Cancelar") } },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 🔐 NUEVO: Diálogo para cambiar contraseña
    if (showPwdDialog) {
        ChangePasswordDialog(
            onDismiss = { showPwdDialog = false },
            onConfirm = { current, newPass ->
                onChangePassword(current, newPass)
                showPwdDialog = false
            }
        )
    }

    // (Picker de categorías/subcategorías igual que antes)
    var picked by remember { mutableStateOf(selectedCategory ?: "") }
    if (catPickerOpen) {
        AlertDialog(
            onDismissRequest = { catPickerOpen = false },
            title = { Text("Selecciona una categoría") },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    catalog.keys.sorted().forEach { cat ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = picked == cat, onClick = { picked = cat })
                            Spacer(Modifier.width(8.dp))
                            Text(cat)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (picked.isNotBlank()) {
                            selectedCategory = picked
                            selectedSubs = emptySet()
                            catPickerOpen = false
                            subPickerOpen = true
                        }
                    }
                ) { Text("Siguiente") }
            },
            dismissButton = { TextButton(onClick = { catPickerOpen = false }) { Text("Cancelar") } },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (subPickerOpen) {
        val cat = selectedCategory ?: ""
        val options = catalog[cat].orEmpty()
        var current by remember(cat) { mutableStateOf(selectedSubs) }
        AlertDialog(
            onDismissRequest = { subPickerOpen = false },
            title = { Text("Subcategorías de $cat") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    options.forEach { opt ->
                        val checked = current.contains(opt)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { isChecked ->
                                    current = if (isChecked) current + opt else current - opt
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(opt)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val pickedSubs = current.toList()
                        val existingIdx = prefGroups.indexOfFirst { it.id == editingGroupId }
                        if (pickedSubs.isNotEmpty()) {
                            if (existingIdx >= 0) {
                                prefGroups[existingIdx] = prefGroups[existingIdx].copy(
                                    title = cat,
                                    chips = pickedSubs.toMutableList()
                                )
                            } else {
                                prefGroups.add(PreferenceGroup(title = cat, chips = pickedSubs.toMutableList()))
                            }
                        }
                        subPickerOpen = false
                    }
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { subPickerOpen = false }) { Text("Cancelar") } },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/* ---- helpers ---- */

@Composable
private fun InfoRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$label :",
            modifier = Modifier.width(100.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = onClick) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Editar $label")
        }
    }
}

/* 🔐 NUEVO: Diálogo específico para cambio de contraseña */
@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (current: String, newPass: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var new1 by remember { mutableStateOf("") }
    var new2 by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }

    val tooShort = new1.isNotEmpty() && new1.length < 8
    val mismatch = new2.isNotEmpty() && new1 != new2
    val canConfirm = current.isNotBlank() && new1.length >= 8 && new1 == new2

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    label = { Text("Contraseña actual") },
                    singleLine = true,
                    visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = new1,
                    onValueChange = { new1 = it },
                    label = { Text("Nueva contraseña (min 8)") },
                    singleLine = true,
                    isError = tooShort,
                    supportingText = {
                        if (tooShort) Text("Demasiado corta.")
                    },
                    visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = new2,
                    onValueChange = { new2 = it },
                    label = { Text("Confirmar nueva") },
                    singleLine = true,
                    isError = mismatch,
                    supportingText = {
                        if (mismatch) Text("No coincide.")
                    },
                    visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation()
                )
                TextButton(onClick = { show = !show }) {
                    Text(if (show) "Ocultar" else "Ver")
                }
            }
        },
        confirmButton = {
            TextButton(enabled = canConfirm, onClick = { onConfirm(current, new1) }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        shape = RoundedCornerShape(16.dp)
    )
}