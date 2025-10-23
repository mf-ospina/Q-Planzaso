package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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

// ---- Patrocinadores de ejemplo ----
val sponsors = listOf(
    "Coca-Cola", "Google", "Adidas", "Sony", "Netflix",
    "Umbrella Corp", "Samsung", "Nvidia", "Apple"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEvent(navController: NavController, encodedJson: String?) {
    // ✅ LÓGICA REAL para decodificar el JSON del evento
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
    val eventImageId = runCatching { R.drawable.img2 }.getOrDefault(android.R.drawable.ic_menu_gallery)

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
                // 🔹 Barra superior
                QTopBar(navController = navController, title = "Evento")

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título y botón de favorito
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = evento.nombre ?: "Sin título",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { /* Acción de favorito */ },
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

                    Spacer(modifier = Modifier.height(2.dp))

                    // Fecha y Ubicación
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp), // espacio entre fecha y ubicación
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        val fechaTexto = evento.fechaInicio?.let {
                            SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
                                .format(it.toDate())
                        } ?: "Fecha no especificada"


                        //  Fecha
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Fecha",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = fechaTexto,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        //  Ubicación
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Lugar",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.width(6.dp))
                            /*Text(
                                text = evento.direccion ?: evento.ciudad ?: "Ubicación no especificada",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )*/
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    // Imagen del evento

                    Image(
                        painter = painterResource(id = eventImageId),
                        contentDescription = "Foto del evento",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )

                    // Categorías
                    Column(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    ) {
                        Text(
                            text = "Categorías",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        evento.categoriasIds?.let { categorias ->
                            CategorySection(categories = categorias)
                        } ?: Text(
                            text = "No hay categorías disponibles.",
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descripción
                    Column(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = evento.descripcion ?: "No hay descripción disponible.",
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        // Horarios
                        Text("Horarios", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Fecha

                            val fechaTexto = evento.fechaInicio?.let {
                                SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES")).format(it.toDate())
                            } ?: "Fecha no especificada"

                            Column {
                                Text(
                                    text = fechaTexto,
                                    fontSize = 15.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Horas
                            /*Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = evento.hora,
                                    fontSize = 15.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }*/

                        }

                        Spacer(modifier = Modifier.height(26.dp))

                        Text(
                            text = "Ubicación",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Imagen placeholder de mapa
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ubicacion), // Imagen temporal
                                contentDescription = "Mapa placeholder",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )

                            // Overlay decorativo
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = 0.25f))
                            )

                            Text(
                                text = "Mapa no disponible aún",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        //Organizadores
                        Text(
                            text = "Organizadores",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Alcaldía de Bogotá",
                            fontSize = 16.sp, color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(26.dp))


                        //Patrocinadores
                        Text(
                            text = "Patrocinadores",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            sponsors.forEach { sponsor ->
                                SponsorChip(text = sponsor)
                            }
                        }

                        Spacer(modifier = Modifier.height(26.dp))

                        // Calificación
                        Text(
                            text = "Calificación",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))


                        //lamada del componente - RatingBar
                        RatingBar(initialRating = userRating, onRatingChanged = { newRating ->
                            userRating = newRating
                            // opcional: llamar a ViewModel para persistir
                        })

                        Spacer(modifier = Modifier.height(24.dp))


                        /// Botnes de descargar y comparit
                        //
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconActionButton(
                                icon = Icons.Default.Share,
                                onClick = { shareEvent(context, evento) }
                            )
                            Spacer(modifier = Modifier.width(24.dp))
                            IconActionButton(
                                icon = Icons.Default.Download,
                                onClick = { downloadEventImage(context, R.drawable.img2) }
                            )
                        }

                        // Eventos relacionados
                        Text("Eventos Relacionados", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.img2),
                            contentDescription = "Evento relacionado",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(bottom = 80.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("¡Registro exitoso!") },
                        text = { Text("Ahora estás inscrito al evento") },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) { Text("Aceptar") }
                        }
                    )
                }
            }
        } else {
            //  Fallback si falla el decode del JSON
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
            .padding(4.dp), // separa ligeramente cada chip
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

//botones de descargar y compartir
@Composable
fun IconActionButton(icon: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .background(Color(0xFFFFF4E5), shape = RoundedCornerShape(16.dp))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFFA94D),
            modifier = Modifier.size(28.dp)
        )
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

fun downloadEventImage(context: Context, imageResId: Int) {
    // Por ahora mostramos un mensaje simulado:
    Toast.makeText(context, "Descargando imagen del evento...", Toast.LENGTH_SHORT).show()
}
