package com.planapp.qplanzaso.ui.screens.profile

import android.os.Build
import androidx.annotation.RequiresApi
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

private data class PreferenceGroup(
    val id: Long = System.nanoTime(),
    val title: String,
    val chips: MutableList<String>
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditProfileScreen(
    state: ProfileFormState,
    onChange: (ProfileFormState) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var form by remember(state) { mutableStateOf(state) }
    var age by remember { mutableStateOf("XX") }
    var password by remember { mutableStateOf("*******") }

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
                    "${form.nombre.firstOrNull() ?: 'U'}${form.apellido.firstOrNull() ?: 'N'}",
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${form.nombre} ${form.apellido}", style = MaterialTheme.typography.titleMedium)
            }
            FilledTonalIconButton(onClick = onOpenSettings) {
                Icon(Icons.Rounded.Settings, contentDescription = "Configuración")
            }
        }

        Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoRow("Nombre", form.nombre) {
                    fieldDialog = EditDialog("Nombre", form.nombre) {
                        form = form.copy(nombre = it); onChange(form)
                    }
                }
                InfoRow("Apellido", form.apellido) {
                    fieldDialog = EditDialog("Apellido", form.apellido) {
                        form = form.copy(apellido = it); onChange(form)
                    }
                }
                InfoRow("Correo", form.email) {
                    fieldDialog = EditDialog("Correo", form.email, keyboard = KeyboardType.Email) {
                        form = form.copy(email = it); onChange(form)
                    }
                }
                InfoRow("Teléfono", form.telefono) {
                    fieldDialog = EditDialog("Teléfono", form.telefono, keyboard = KeyboardType.Phone) {
                        form = form.copy(telefono = it); onChange(form)
                    }
                }
                InfoRow("Ubicación", form.ubicacion) {
                    fieldDialog = EditDialog("Ubicación", form.ubicacion) {
                        form = form.copy(ubicacion = it); onChange(form)
                    }
                }
                InfoRow("Bio", form.bio) {
                    fieldDialog = EditDialog("Bio", form.bio) {
                        form = form.copy(bio = it); onChange(form)
                    }
                }
                InfoRow("Edad", age) {
                    fieldDialog = EditDialog("Edad", age, keyboard = KeyboardType.Number) {
                        age = it.filter(Char::isDigit)
                    }
                }
                InfoRow("Contraseña", "********") {
                    fieldDialog = EditDialog(
                        "Contraseña",
                        initial = if (password == "*******") "" else password,
                        keyboard = KeyboardType.Password,
                        isPassword = true
                    ) { password = it }
                }
            }
        }

        Text(
            "Mis preferencias",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        prefGroups.forEach { group ->
            PreferenceGroupCard(
                group = group,
                onMoreClick = { menuExpandedFor = group.id },
                menuExpanded = menuExpandedFor == group.id,
                onDismissMenu = { menuExpandedFor = null },
                onEdit = {
                    editingGroupId = group.id
                    selectedCategory = group.title
                    selectedSubs = group.chips.toSet()
                    subPickerOpen = true
                    menuExpandedFor = null
                },
                onDelete = {
                    prefGroups.removeAll { it.id == group.id }
                    menuExpandedFor = null
                }
            )
        }

        Button(
            onClick = {
                editingGroupId = null
                selectedCategory = null
                selectedSubs = emptySet()
                catPickerOpen = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Añadir preferencia")
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

    if (catPickerOpen) {
        var picked by remember { mutableStateOf(selectedCategory ?: "") }
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
                        val picked = current.toList()
                        val existingIdx = prefGroups.indexOfFirst { it.id == editingGroupId }
                        if (picked.isNotEmpty()) {
                            if (existingIdx >= 0) {
                                prefGroups[existingIdx] = prefGroups[existingIdx].copy(
                                    title = cat,
                                    chips = picked.toMutableList()
                                )
                            } else {
                                prefGroups.add(PreferenceGroup(title = cat, chips = picked.toMutableList()))
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

@Composable
private fun PreferenceGroupCard(
    group: PreferenceGroup,
    onMoreClick: () -> Unit,
    menuExpanded: Boolean,
    onDismissMenu: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(group.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Box {
                    IconButton(onClick = onMoreClick) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "Más")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = onDismissMenu) {
                        DropdownMenuItem(text = { Text("Editar") }, onClick = onEdit)
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                            onClick = onDelete
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(group.chips) { chip ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            chip,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
