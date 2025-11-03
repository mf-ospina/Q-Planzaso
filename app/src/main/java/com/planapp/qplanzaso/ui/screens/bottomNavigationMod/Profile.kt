package com.planapp.qplanzaso.ui.screens.bottomNavigationMod

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.model.Usuario
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.components.SearchComponent
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.TimestampTypeAdapter
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.ui.viewModel.UsuarioViewModel
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Profile(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    eventoViewModel: EventoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val usuario by usuarioViewModel.usuario.collectAsState()
    val eventosCreados by eventoViewModel.eventos.collectAsState()
    val eventosInscritosState by eventoViewModel.eventosInscritos.collectAsState()
    val loading by eventoViewModel.loading.collectAsState()

    // 🔹 Cargar eventos creados e inscritos
    LaunchedEffect(usuario) {
        usuario?.uid?.let { uid ->
            eventoViewModel.cargarEventosDelUsuario(uid) // eventos creados
            eventoViewModel.obtenerEventosInscritos(uid) // eventos inscritos
        }
    }

    if (usuario == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryColor)
        }
    } else {
        PerfilContenido(
            usuario = usuario!!,
            eventosCreados = eventosCreados,
            eventosInscritos = eventosInscritosState,
            loading = loading,
            onCrearEvento = { navController.navigate("NewEventScreen") },
            onCerrarSesion = {
                usuarioViewModel.cerrarSesion()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            navController = navController,
            eventoViewModel = eventoViewModel
        )
    }
}

@Composable
fun PerfilContenido(
    usuario: Usuario,
    eventosCreados: List<Evento>,
    eventosInscritos: List<Evento>,
    loading: Boolean,
    onCrearEvento: () -> Unit,
    onCerrarSesion: () -> Unit,
    navController: NavController,
    eventoViewModel: EventoViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf("Creados", "Inscritos")
    val tabIcons = listOf(Icons.Default.CalendarToday, Icons.Default.ConfirmationNumber)

    val eventosFiltrados = remember(searchQuery, selectedTabIndex, eventosCreados, eventosInscritos) {
        val baseList = if (selectedTabIndex == 0) eventosCreados else eventosInscritos
        if (searchQuery.isBlank()) baseList
        else baseList.filter {
            it.nombre.contains(searchQuery, ignoreCase = true) ||
                    (it.direccion?.contains(searchQuery, ignoreCase = true) ?: false)
        }
    }


    val usuarioId = usuario.uid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        QTopBar(navController = navController, title = "Mi perfil", showBackButton = false)

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = usuario.fotoPerfil.ifEmpty { "https://cdn-icons-png.flaticon.com/512/3135/3135715.png" },
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onCerrarSesion,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión", tint = Color.Gray)
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = usuario.nombre,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = DarkGrayText
        )
        Text(
            text = usuario.correo,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (usuario.verified) {
            Text(
                text = "Cuenta verificada ✅",
                color = Color(0xFF4CAF50),
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onCrearEvento,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Crear evento", tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Crear evento", fontSize = 17.sp, color = Color.White)
        }

        Spacer(Modifier.height(20.dp))

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = PrimaryColor,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = PrimaryColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    selectedContentColor = PrimaryColor,
                    unselectedContentColor = Color.Gray,
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = tabIcons[index], contentDescription = title, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(title)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        SearchComponent(
            searchText = searchQuery,
            onSearchTextChanged = { searchQuery = it },
            placeholderText = "Buscar evento..."
        )

        Spacer(Modifier.height(18.dp))

        when {
            loading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }

            eventosFiltrados.isEmpty() -> Text(
                text = if (selectedTabIndex == 0)
                    "No tienes eventos creados"
                else
                    "No estás inscrito en ningún evento",
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (selectedTabIndex == 0) {
                    eventosFiltrados.forEach { evento ->
                        EventoCard(evento = evento, navController = navController, eventoViewModel = eventoViewModel, editable = true, usuarioId = usuarioId)
                    }
                } else {
                    eventosFiltrados.forEach { evento ->
                        EventoCard(evento = evento, navController = navController, eventoViewModel = eventoViewModel, editable = false, usuarioId = usuarioId)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun EventoCard(
    evento: Evento,
    navController: NavController,
    eventoViewModel: EventoViewModel,
    editable: Boolean = true,
    usuarioId: String // 🔹 nuevo parámetro
) {
    val sdf = remember { SimpleDateFormat("dd MMM", Locale("es", "ES")) }
    val fechaTexto = try { evento.fechaInicio?.toDate()?.let { sdf.format(it) } ?: "Sin fecha" } catch (e: Exception) { "Sin fecha" }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) } // 🔹 diálogo cancelar inscripción

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable {
                val gson = GsonBuilder().registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter()).create()
                val eventoJson = gson.toJson(evento)
                val encodedJson = URLEncoder.encode(eventoJson, StandardCharsets.UTF_8.toString())
                navController.navigate("detailEvent/$encodedJson")
            }
    ) {
        Row(Modifier.fillMaxSize()) {
            AsyncImage(
                model = evento.imagenUrl ?: evento.imagen,
                contentDescription = evento.nombre,
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = evento.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF222222),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(fechaTexto, color = Color.Gray, fontSize = 13.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            evento.direccion ?: "Sin dirección",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (editable) {
                        IconButton(onClick = {
                            val gson = GsonBuilder().registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter()).create()
                            val eventoJson = gson.toJson(evento)
                            val encodedJson = URLEncoder.encode(eventoJson, StandardCharsets.UTF_8.toString())
                            navController.navigate("EditEventScreen/$encodedJson")
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar evento", tint = PrimaryColor)
                        }

                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar evento", tint = Color.Red)
                        }
                    } else {
                        // 🔹 Botón cancelar inscripción
                        IconButton(onClick = { showCancelDialog = true }) {
                            Icon(Icons.Default.Cancel, contentDescription = "Cancelar inscripción", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    // 🔹 AlertDialog cancelar inscripción
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar inscripción", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGrayText) },
            text = { Text("¿Deseas cancelar tu inscripción a este evento?", fontSize = 14.sp, color = DarkGrayText) },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    evento.id?.let {
                        eventoViewModel.cancelarInscripcion(it, usuarioId)
                    }
                }) {
                    Text("Cancelar inscripción", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Volver", color = PrimaryColor, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // 🔹 AlertDialog eliminar evento (solo editable)
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar evento", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGrayText) },
            text = { Text("¿Estás segura de que deseas eliminar este evento? Esta acción no se puede deshacer.", fontSize = 14.sp, color = DarkGrayText) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    evento.id?.let { eventoViewModel.eliminarEvento(it) }
                }) { Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = PrimaryColor, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}
