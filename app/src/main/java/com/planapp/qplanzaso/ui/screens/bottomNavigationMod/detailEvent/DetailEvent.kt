package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.CategorySection
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.components.RatingBar
import com.planapp.qplanzaso.ui.theme.LightButton
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

// ---- Ejemplo de patrocinadores ----
val sponsors = listOf(
    "Coca-Cola", "Google", "Adidas", "Sony", "Netflix",
    "Umbrella Corp", "Samsung", "Nvidia", "Apple"
)

// ---- Composable principal ----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEvent(navController: NavController, encodedJson: String?) {
    val evento: Evento? = remember(encodedJson) {
        encodedJson?.let {
            try {
                val gson = GsonBuilder()
                    .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                    .create()
                val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                gson.fromJson(decodedJson, Evento::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    var isRegistered by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            if (evento != null) {
                BottomAppBar(
                    modifier = Modifier.height(80.dp),
                    containerColor = Color.White
                ) {
                    Button(
                        onClick = {
                            if (!isRegistered) {
                                isRegistered = true
                                showDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRegistered) LightButton else PrimaryColor,
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
    ) { paddingValues ->
        if (evento != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                QTopBar(navController = navController, title = "Evento")
                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título y favorito
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = evento.nombre ?: "Sin título",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { /* Acción favorito */ },
                            modifier = Modifier.size(58.dp)
                        ) {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                modifier = Modifier.size(34.dp),
                                tint = Color.DarkGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fecha y Ubicación
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val fechaTexto = evento.fechaInicio?.let {
                            SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")).format(it.toDate())
                        } ?: "Fecha no especificada"

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Fecha", tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(fechaTexto, style = MaterialTheme.typography.bodyLarge, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Lugar", tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                evento.direccion ?: evento.ciudad ?: "Ubicación no especificada",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Imagen principal del evento
                    AsyncImage(
                        model = evento.imagenUrl,
                        contentDescription = "Foto del evento",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                        error = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Categorías
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text("Categorías", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        evento.categoriasIds?.let { CategorySection(categories = it) }
                            ?: Text("No hay categorías disponibles.", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descripción
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(evento.descripcion ?: "No hay descripción disponible.", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Patrocinadores
                    Text("Patrocinadores", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(start = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        sponsors.forEach { SponsorChip(text = it) }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calificación
                    Text("Calificación", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(start = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingBar(initialRating = userRating, onRatingChanged = { userRating = it })

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones compartir/descargar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconActionButton(icon = Icons.Default.Share) { shareEvent(context, evento) }
                        Spacer(modifier = Modifier.width(24.dp))
                        IconActionButton(icon = Icons.Default.Download) { downloadEventImage(context, evento.imagenUrl) }
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("¡Registro exitoso!") },
                        text = { Text("Ahora estás inscrito al evento") },
                        confirmButton = { TextButton(onClick = { showDialog = false }) { Text("Aceptar") } }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: No se pudo cargar la información del evento.")
            }
        }
    }
}

// ---- COMPONENTES REUTILIZADOS ----
@Composable
fun SponsorChip(text: String) {
    Surface(
        modifier = Modifier
            .border(1.dp, PrimaryColor, shape = MaterialTheme.shapes.small)
            .padding(4.dp),
        color = Color.Transparent,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp,
            color = PrimaryColor
        )
    }
}

@Composable
fun IconActionButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .background(Color(0xFFFFF4E5), shape = RoundedCornerShape(16.dp))
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFFFA94D), modifier = Modifier.size(28.dp))
    }
}

// ---- FUNCIONES DE ACCIÓN ----
fun shareEvent(context: Context, evento: Evento) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "¡No te pierdas ${evento.nombre}! 📅 ${evento.fechaInicio} en ${evento.ciudad}")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartir evento"))
}

fun downloadEventImage(context: Context, imageUrl: String?) {
    if (imageUrl.isNullOrEmpty()) {
        Toast.makeText(context, "No hay imagen para descargar", Toast.LENGTH_SHORT).show()
        return
    }
    Toast.makeText(context, "Descargando imagen desde Firebase...", Toast.LENGTH_SHORT).show()
}
