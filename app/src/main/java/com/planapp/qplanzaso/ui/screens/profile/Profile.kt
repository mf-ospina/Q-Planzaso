package com.planapp.qplanzaso.ui.screens.profile

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Profile(
    modifier: Modifier = Modifier,
    state: ProfileFormState,                 // <- viene de ProfileEntry
    onBack: () -> Unit = {},
    onEdit: (ProfileFormState) -> Unit = {},
    onOpenSettings: () -> Unit = {}          // <- nuevo callback
) { var form by remember(state) { mutableStateOf(state) }
    val today = remember { LocalDate.now() }
    val orange = MaterialTheme.colorScheme.primary

    // Datos del usuario desde Firestore
    //val form = state

    // Listas de eventos dummy locales (puedes conectar a tu fuente real luego)
    val allEvents = remember { mutableStateListOf<EventUi>() }
    val myEventsMaster = remember { mutableStateListOf<EventUi>() }

    val upcoming by derivedStateOf { allEvents.filter { it.date >= today }.sortedBy { it.date } }
    val past by derivedStateOf { allEvents.filter { it.date < today }.sortedByDescending { it.date } }
    val myEvents by derivedStateOf { myEventsMaster.sortedBy { it.date } }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }
    val selectionMode by derivedStateOf { selectedIds.isNotEmpty() }
    var showConfirm by remember { mutableStateOf(false) }

    fun clearSelection() { selectedIds = emptySet() }
    fun toggleSelection(id: Long) { selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id }
    fun deleteSelected() {
        when (pagerState.currentPage) {
            0, 1 -> allEvents.removeAll { it.id in selectedIds }
            2     -> myEventsMaster.removeAll { it.id in selectedIds }
        }
        clearSelection()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { if (selectionMode) clearSelection() else onBack() }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
            }
            Text(
                "Mi perfil",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header usuario
            item {
                Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                            Text("${form.nombre} ${form.apellido}", fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Star, null, tint = orange, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(form.ubicacion, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        FilledTonalIconButton(onClick = { onEdit(form) }) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Editar")
                        }
                    }
                }
            }

            // Tabs + pager
            item {
                Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
                    Column {
                        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                            Tab(
                                selected = pagerState.currentPage == 0,
                                onClick = { scope.launch { pagerState.animateScrollToPage(0) }; clearSelection() },
                                icon = { Icon(Icons.AutoMirrored.Rounded.List, contentDescription = "Próximos") },
                                text = { Text("Próximos") }
                            )
                            Tab(
                                selected = pagerState.currentPage == 1,
                                onClick = { scope.launch { pagerState.animateScrollToPage(1) }; clearSelection() },
                                icon = { Icon(Icons.Rounded.Event, contentDescription = "Pasados") },
                                text = { Text("Pasados") }
                            )
                            Tab(
                                selected = pagerState.currentPage == 2,
                                onClick = { scope.launch { pagerState.animateScrollToPage(2) }; clearSelection() },
                                icon = { Icon(Icons.Rounded.Favorite, contentDescription = "Mis eventos") },
                                text = { Text("Mis eventos") }
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 220.dp)
                                .padding(bottom = 12.dp)
                        ) { page ->
                            val (title, data, canCreateHere) = when (page) {
                                0 -> Triple("Tus próximos eventos", upcoming, false)
                                1 -> Triple("Eventos pasados", past, false)
                                else -> Triple("Mis eventos", myEvents, true)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (selectionMode) {
                                        IconButton(onClick = { showConfirm = true }) {
                                            Icon(
                                                Icons.Rounded.Delete,
                                                contentDescription = "Eliminar seleccionados",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                if (data.isEmpty()) {
                                    val emptyMsg = when (page) {
                                        0 -> "Aquí se mostrarán tus eventos futuros."
                                        1 -> "Aquí se mostrarán tus eventos pasados."
                                        else -> "Aquí se mostrarán tus eventos creados."
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            emptyMsg,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    data.forEach { e ->
                                        val selected = e.id in selectedIds
                                        EventCard(
                                            e = e,
                                            selected = selected,
                                            selectionEnabled = true,
                                            selectionMode = selectionMode,
                                            onLongPress = { toggleSelection(e.id) },
                                            onTapWhenSelecting = { toggleSelection(e.id) }
                                        )
                                    }
                                }

                                if (canCreateHere && data.isEmpty()) {
                                    Button(
                                        onClick = { /* TODO crear */ },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) { Text("Crear +") }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(56.dp)) }
        }
    }

    if (showConfirm) {
        val count = selectedIds.size
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Eliminar ${if (count == 1) "evento" else "eventos"}") },
            text = { Text("¿Seguro que deseas eliminar $count ${if (count == 1) "evento seleccionado" else "eventos seleccionados"}?") },
            confirmButton = {
                TextButton(onClick = { deleteSelected(); showConfirm = false }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancelar") } },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/* --------- Modelos y UI ---------- */

private data class EventUi(
    val id: Long,
    val title: String,
    val date: LocalDate,
    val place: String,
    val displayDate: String
)

@Composable
private fun EventCard(
    e: EventUi,
    selected: Boolean,
    selectionEnabled: Boolean,
    selectionMode: Boolean,
    onLongPress: () -> Unit,
    onTapWhenSelecting: () -> Unit
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val containerColor =
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else MaterialTheme.colorScheme.surface

    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(selectionEnabled, selectionMode, selected) {
                detectTapGestures(
                    onLongPress = { if (selectionEnabled) onLongPress() },
                    onTap = { if (selectionEnabled && selectionMode) onTapWhenSelecting() }
                )
            }
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(e.title, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(e.displayDate, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(12.dp))
                Text(e.place, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
