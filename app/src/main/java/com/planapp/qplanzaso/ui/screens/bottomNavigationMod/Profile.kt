package com.planapp.qplanzaso.ui.screens.bottomNavigationMod

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.model.Usuario
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.TimestampTypeAdapter
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
    val eventos by eventoViewModel.eventos.collectAsState()
    val loading by eventoViewModel.loading.collectAsState()

    // 🔹 Cargar eventos del usuario cuando esté disponible
    LaunchedEffect(usuario) {
        usuario?.uid?.let { uid ->
            eventoViewModel.cargarEventosDelUsuario(uid)
        }
    }

    if (usuario == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryColor)
        }
    } else {
        PerfilContenido(
            usuario = usuario!!,
            eventos = eventos,
            loading = loading,
            onCrearEvento = { navController.navigate("NewEventScreen") },
            onCerrarSesion = { usuarioViewModel.cerrarSesion() },
            navController = navController
        )
    }

    Spacer(Modifier.height(32.dp))

    Button(
        onClick = {
            usuarioViewModel.cerrarSesion()
            // 🔹 Navega a la pantalla de login o inicial
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Limpia el backstack
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
    ) {
        Text("Cerrar sesión", color = Color.White)
    }
}

@Composable
fun PerfilContenido(
    usuario: Usuario,
    eventos: List<Evento>,
    loading: Boolean,
    onCrearEvento: () -> Unit,
    onCerrarSesion: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // TopBar
        QTopBar(navController = navController, title = "Mi perfil")

        // 🧍 Imagen de perfil
        AsyncImage(
            model = usuario.fotoPerfil.ifEmpty { "https://cdn-icons-png.flaticon.com/512/3135/3135715.png" },
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🧾 Nombre y correo
        Text(
            text = usuario.nombre,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.CenterHorizontally)
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

        Spacer(modifier = Modifier.height(16.dp))

        // 🟠 Botón crear evento
        Button(
            onClick = onCrearEvento,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Crear evento", fontSize = 17.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 📅 Lista de eventos
        Text(
            text = "Tus próximos eventos",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (loading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else if (eventos.isEmpty()) {
            Text(
                text = "No tienes eventos próximos",
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        } else {
            eventos.forEach { evento ->
                EventoCard(evento = evento, navController = navController)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🚪 Botón cerrar sesión
        TextButton(
            onClick = onCerrarSesion,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Cerrar sesión", color = Color.Red, fontSize = 15.sp)
        }
    }
}

@Composable
fun EventoCard(evento: Evento, navController: NavController) {
    val sdf = remember { SimpleDateFormat("dd MMM", Locale("es", "ES")) }
    val fechaTexto = try {
        evento.fechaInicio?.toDate()?.let { sdf.format(it) } ?: "Sin fecha"
    } catch (e: Exception) {
        "Sin fecha"
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = evento.imagenUrl ?: evento.imagen,
                contentDescription = evento.nombre,
                modifier = Modifier
                    .width(100.dp)
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
                        maxLines = 1
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(fechaTexto, color = Color.Gray, fontSize = 13.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            evento.direccion ?: "Sin dirección",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }

                // 🟠 Botón Editar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val gson = GsonBuilder()
                                .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                                .create()
                            val eventoJson = gson.toJson(evento)
                            val encodedJson = URLEncoder.encode(eventoJson, StandardCharsets.UTF_8.toString())
                            navController.navigate("EditEventScreen/${encodedJson}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Editar", fontSize = 13.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
