package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.CategorySection
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.components.RatingBar
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import com.planapp.qplanzaso.ui.theme.LightButton
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import com.planapp.qplanzaso.ui.components.EventoMapView
import java.text.NumberFormat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import com.planapp.qplanzaso.utils.JsonNavHelper
import kotlinx.coroutines.launch
import java.net.URLDecoder



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEvent(navController: NavController, encodedJson: String?, eventoViewModel: EventoViewModel = viewModel() ) {
    val evento: Evento? = remember(encodedJson) {
        encodedJson?.let {
            try {
                val gson = GsonBuilder()
                    .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                    .create()

                // 🔹 Reemplaza solo los + por espacios sin decodificar toda la URL
                val fixedJson = it.replace("+", " ")
                gson.fromJson(fixedJson, Evento::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /*val evento: Evento? = remember(encodedJson) {
        encodedJson?.let {
            try {
                val gson = GsonBuilder()
                    .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                    .create()
                //  Reemplaza los "+" por espacios sin decodificar toda la URL
                val fixedJson = it.replace("+", " ")
                gson.fromJson(fixedJson, Evento::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }*/


    val context = LocalContext.current
    var userRating by remember { mutableStateOf(0) }

    val user = FirebaseAuth.getInstance().currentUser
    val usuarioId = user?.uid

    //Detectar si el usuario esta inscrito
    var isRegistered by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val contexto = LocalContext.current
    var esFavorito by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(evento) {
        if (evento != null && usuarioId != null) {
            // Verifica si el usuario está inscrito
            isRegistered = evento.inscritosIds.contains(usuarioId)
        }
    }


    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (evento != null) {
                Surface(
                    tonalElevation = 6.dp,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding() // deja espacio para la barra del sistema
                            .padding(horizontal = 16.dp, vertical = 10.dp), // añade margen interno
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                if (usuarioId == null) {
                                    Toast.makeText(context, "Debes iniciar sesión para inscribirte", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (!isRegistered && evento != null && evento.id != null) {
                                    eventoViewModel.inscribirseEnEvento(evento.id!!, usuarioId)
                                    isRegistered = true
                                    showDialog = true
                                } else {
                                    Toast.makeText(context, "Ya estás inscrito a este evento", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRegistered) Color(0xFFBDBDBD) else PrimaryColor,
                                contentColor = Color.White
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = if (isRegistered) "Inscrito" else "Inscribirse",
                                fontSize = 19.sp
                            )
                        }

                    }
                }
            }
        }

    ) { paddingValues ->
        if (evento != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                QTopBar(navController, title = "Evento")

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ------------------- TÍTULO -------------------
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = evento.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = DarkGrayText,
                            modifier = Modifier.weight(1f)
                        )

                        // ------------------- FAVORITO -------------------
                        val scope = rememberCoroutineScope()

                        // 🔹 Inicializar si el evento es favorito
                        LaunchedEffect(evento, usuarioId) {
                            if (evento != null && usuarioId != null) {
                                esFavorito =
                                    eventoViewModel.verificarSiEsFavorito(evento.id!!, usuarioId)
                            }
                        }

                        // ❤️ Animación suave tipo “latido”
                        val scale by animateFloatAsState(
                            targetValue = if (esFavorito) 1.2f else 1f,
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = LinearOutSlowInEasing
                            ),
                            label = "favoriteAnimation"
                        )

                        IconButton(
                            onClick = {
                                if (usuarioId == null || evento?.id == null) {
                                    Toast.makeText(
                                        contexto,
                                        "Debes iniciar sesión para marcar favoritos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@IconButton
                                }

                                scope.launch {
                                    // 🔹 Alterna el favorito de forma inmediata en UI
                                    eventoViewModel.toggleFavorito(evento, usuarioId)

                                    // 🔹 Actualiza el estado local para reflejar el cambio instantáneamente
                                    esFavorito = !esFavorito

                                    // 🔹 Mensaje visual rápido
                                    Toast.makeText(
                                        contexto,
                                        if (esFavorito) "Agregado a favoritos ❤️" else "Eliminado de favoritos 💔",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.scale(scale)
                        ) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = if (esFavorito) Color.Red else Color.LightGray,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(3.dp))

                    // ------------------- FECHA Y UBICACIÓN -------------------
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        // Fecha
                        val fechaTexto = evento.fechaInicio?.let {
                            try {
                                val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
                                sdf.format(it.toDate())
                            } catch (e: Exception) {
                                "Fecha no válida"
                            }
                        } ?: "Fecha no especificada"

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Fecha",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = fechaTexto,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Dirección
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Lugar",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = evento.direccion ?: "Ubicación no especificada",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // ------------------- IMAGEN -------------------
                    AsyncImage(
                        model = evento.imagenUrl?.takeIf { it.isNotEmpty() } ?: evento.imagen,
                        contentDescription = evento.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray), // 👈 ayuda a visualizar el contenedor
                    )
                    Spacer(modifier = Modifier.height(1.dp))

                    // ------------------- CATEGORÍAS -------------------
                    Text(text = "Categorías", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText,)
                    CategorySection(categories = evento.categoriasIds.map { it })

                    Spacer(modifier = Modifier.height(2.dp))

                    // ------------------- DESCRIPCIÓN -------------------
                    Text(text = "Descripción", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText,)
                    Text(
                        text = evento.descripcion,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // ------------------- HORARIOS -------------------
                    Text(text = "Fecha del evento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText,)

                    val formatoFechaHora = remember {
                        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("es", "ES"))
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E5)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Inicio
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Inicio",
                                    tint = Color(0xFFFFBA74)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Inicio",
                                        style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                                    )
                                    Text(
                                        evento.fechaInicio?.toDate()?.let { formatoFechaHora.format(it) }
                                            ?: "No especificado",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color(0xFF333333),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Fin
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = "Fin",
                                    tint = Color(0xFFFFBA74)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Fin",
                                        style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                                    )
                                    Text(
                                        evento.fechaFin?.toDate()?.let { formatoFechaHora.format(it) }
                                            ?: "No especificado",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color(0xFF333333),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // ------------------- PRECIO -------------------
                    val precioDouble = when (val p = evento.precio) {
                        is String -> p.toDoubleOrNull() ?: 0.0
                        is Number -> p.toDouble()
                        else -> 0.0
                    }

                    val textoPrecio = if (precioDouble <= 0.0) {
                        "Gratis"
                    } else {
                        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
                            maximumFractionDigits = 0
                        }.format(precioDouble)
                    }

                    Text(text = "Precio", fontWeight = FontWeight.Bold, fontSize = 20.sp,color = DarkGrayText,)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = "Precio",
                                tint = Color(0xFF4CAF50)
                            )
                            Column {
                                Text("Precio", style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray))
                                Text(
                                    textoPrecio,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = if (textoPrecio == "Gratis") Color(0xFF4CAF50) else Color(0xFF333333),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))


                    // ------------------- PATROCINADORES -------------------
                    Text(text = "Patrocinadores", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText,)

                    if (evento.patrocinadores.isNullOrEmpty()) {
                        Text(
                            text = "No hay patrocinadores registrados",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                        ) {
                            evento.patrocinadores.forEach { sponsor ->
                                SponsorChip(text = sponsor)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))

                    // ------------------- DIRECCIÓN Y MAPA -------------------
                    Text(text = "Dirección", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText,)
                    Text(
                        text = evento.direccion ?: "Dirección no disponible",
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EventoMapView(
                        lat = evento.ubicacion?.latitude,
                        lon = evento.ubicacion?.longitude,
                        nombreEvento = evento.nombre
                    )

                    // --- Calificación ---
                    Text("Calificación", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
                    Spacer(Modifier.height(8.dp))
                    RatingBar(initialRating = userRating, onRatingChanged = { userRating = it })

                    Spacer(Modifier.height(24.dp))

                    // --- Botones compartir / descargar ---
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconActionButton(icon = Icons.Default.Share) { shareEvent(context, evento) }
                        Spacer(Modifier.width(20.dp))
                        IconActionButton(icon = Icons.Default.Download) { downloadEventImage(context, evento.imagenUrl) }
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("¡Registro exitoso!") },
                        text = { Text("Te has inscrito correctamente al evento") },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) { Text("Aceptar") }
                        }
                    )
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se pudo cargar la información del evento", color = Color.Gray)
            }
        }
    }
}

// ---- CHIP DE PATROCINADOR ----
@Composable
fun SponsorChip(text: String) {
    Surface(
        modifier = Modifier
            .border(1.dp, PrimaryColor, RoundedCornerShape(50))
            .padding(4.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp,
            color = PrimaryColor
        )
    }
}

// ---- BOTONES DE ACCIÓN ----
@Composable
fun IconActionButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .background(Color(0xFFFFF4E5), RoundedCornerShape(16.dp))
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFFFA94D), modifier = Modifier.size(28.dp))
    }
}

// ---- FUNCIONES AUXILIARES ----
fun shareEvent(context: Context, evento: Evento) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "¡No te pierdas ${evento.nombre}! 📅 ${evento.fechaInicio} en ${evento.ciudad}"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartir evento"))
}

fun downloadEventImage(context: Context, imageUrl: String?) {
    if (imageUrl.isNullOrEmpty()) {
        Toast.makeText(context, "No hay imagen para descargar", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Descargando imagen desde Firebase...", Toast.LENGTH_SHORT).show()
    }
}