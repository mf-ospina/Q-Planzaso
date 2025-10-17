package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.ui.components.CategorySection
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.components.RatingBar
import com.planapp.qplanzaso.ui.theme.BackgroundColor
import com.planapp.qplanzaso.ui.theme.DarkButton
import com.planapp.qplanzaso.ui.theme.LightButton
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import kotlin.random.Random

// --- Modelo y datos de ejemplo ---
data class EventDetail(
    val id: String,
    val title: String,
    val date: String,
    val location: String,
    val description: String
)

val sampleEventDetail = EventDetail(
    id = "1",
    title = "Concierto Filarmónico de Bogota",
    date = "14 Oct, 2025",
    location = "Movistar Arena, Bogotá",
    description = "Disfruta de una noche mágica con la orquesta filarmónica interpretando piezas clásicas y contemporáneas. Un evento imperdible para los amantes de la buena música.",
    /*sponsors = listOf(
        "Coca-Cola", "Google", "Adidas", "Sony", "Netflix",
        "Umbrella Corp", "Samsung", "Nvidia", "Apple")*/
)
//debe implemnetarse este array en la logica que traerá de la base  de datos
val sponsors = listOf(
    "Coca-Cola", "Google", "Adidas", "Sony", "Netflix",
    "Umbrella Corp", "Samsung", "Nvidia", "Apple"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEvent(navController: NavController, id: String?) {
    // Por ahora, usamos los datos de ejemplo.
    // En el futuro, usarías el 'id' para cargar los datos reales desde un ViewModel.
    val event = sampleEventDetail

    var isRegistered by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(0) } //calificacion

    // ✅ Fuera del Composable: definimos los IDs de imágenes seguros
    val eventImageId = runCatching { R.drawable.img2 }.getOrDefault(android.R.drawable.ic_menu_gallery)
    Scaffold(
        bottomBar = {
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
        //Contenido principal

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(Color.White)
        ) {
            // TopBar
            QTopBar(navController = navController, title = "Evento")

            Spacer(modifier = Modifier.height(4.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título y botón de favorito
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { /* Acción de favorito */ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.FavoriteBorder,
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
                            text = event.date,
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
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }


                Spacer(modifier = Modifier.height(2.dp))

                Image(
                    painter = painterResource(id = eventImageId),
                    contentDescription = "Foto del evento",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                        .clip(RoundedCornerShape(20.dp)) //  Bordes
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )


                Spacer(modifier = Modifier.height(2.dp))

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

                    CategorySection(
                        categories = listOf("Concierto", "Teatro", "Gastronomía", "Arte", "Tecnología")
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
                        text = event.description,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = Color.Gray,
                        fontWeight =  FontWeight.SemiBold
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
                        Column {
                            Text(
                                text = "4 y 5 de Octubre",
                                fontSize = 15.sp,
                                color = Color.Gray,
                                fontWeight =  FontWeight.SemiBold
                            )
                        }

                        // Horas
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "11:00 a.m.",
                                fontSize = 15.sp,
                                color = Color.Gray,
                                fontWeight =  FontWeight.SemiBold
                            )
                            Text(
                                text = "8:00 p.m.",
                                fontSize = 15.sp,
                                color = Color.Gray,
                                fontWeight =  FontWeight.SemiBold
                            )
                        }

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
                        fontWeight =  FontWeight.SemiBold
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
                        text ="Calificación",
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
                    text = { Text("Ahora estas inscrito al evento") },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun SponsorChip(text: String) {
    Surface(
        modifier = Modifier
            .border(width = 1.dp, color = PrimaryColor, shape = MaterialTheme.shapes.small)
            .padding(4.dp), // separa ligeramente cada chip
        color = Color.Transparent,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp,
            color = PrimaryColor
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewDetailEvent() {
    val navController = rememberNavController()
    DetailEvent(navController = navController, id = "1")
}
