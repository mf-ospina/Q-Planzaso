package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent
import com.planapp.qplanzaso.ui.components.CommentModal
import androidx.compose.ui.platform.LocalInspectionMode
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import androidx.lifecycle.viewmodel.compose.viewModel
import com.planapp.qplanzaso.model.ComentarioEvento
import com.planapp.qplanzaso.ui.components.CommentSection
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel


// ---- Ejemplo de patrocinadores ----
val sponsors = listOf(
    "Coca-Cola", "Google", "Adidas", "Sony", "Netflix",
    "Umbrella Corp", "Samsung", "Nvidia", "Apple"
)

// ---- Composable principal ----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailEvent(navController: NavController, encodedJson: String?) {
    val eventoViewModel: EventoViewModel = viewModel()

    val eventoLive by eventoViewModel.eventoSeleccionado.collectAsState()

    val eventoInicial: Evento? = remember(encodedJson) {
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

    val evento = eventoLive ?: eventoInicial

    // Definir el usuario y su ID
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid // Esto será un String?

    var isRegistered by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }

    // userRating
    val userRating by eventoViewModel.calificacionUsuario.collectAsState()
    val context = LocalContext.current
    var showCommentModal by remember { mutableStateOf(false) }

    var comentarioAEditar by remember { mutableStateOf<ComentarioEvento?>(null) }

    // Observa cambios en el evento o usuario logueado y carga la calificación
    LaunchedEffect(key1 = evento?.id, key2 = currentUserId) {
        // Primero verificamos el usuario
        if (currentUserId != null) {
            evento?.id?.let { eventoIdNoNulo ->
                eventoViewModel.cargarCalificacionUsuario(eventoIdNoNulo, currentUserId)
            } ?: run {
                eventoViewModel.limpiarCalificacionUsuario()
            }
        } else {
            eventoViewModel.limpiarCalificacionUsuario()
        }
    }

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
                            modifier = Modifier.weight(1f),
                            color = DarkGrayText
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
                        Text("Categorías", fontWeight = FontWeight.Bold, fontSize = 20.sp, color= DarkGrayText)
                        Spacer(modifier = Modifier.height(8.dp))
                        evento.categoriasIds?.let { CategorySection(categories = it) }
                            ?: Text("No hay categorías disponibles.", fontSize = 16.sp, color = DarkGrayText, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descripción
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkGrayText)
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Promedio General",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DarkGrayText
                        )

                        val promedio = evento.calificacionPromedio ?: 0.0
                        val conteo = evento.calificacionesCount ?: 0L
                        val promedioStr = String.format(Locale.US, "%.1f", promedio)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Promedio",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "$promedioStr ($conteo votos)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Calificación
                    Text("Tu Calificación", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(start = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingBar(
                        rating = userRating ?: 0,
                        onRatingChanged = { newRating ->

                            if (userRating != null) {
                                Toast.makeText(context, "Ya has calificado este evento", Toast.LENGTH_SHORT).show()
                                return@RatingBar
                            }

                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val uid = currentUser?.uid
                            if (uid != null) {

                                evento.id?.let { eventoIdNoNulo ->
                                    val valorDouble = newRating.toDouble()
                                    eventoViewModel.registrarCalificacion(
                                        eventoId = eventoIdNoNulo,
                                        usuarioId = uid,
                                        valor = valorDouble
                                    )

                                    Toast.makeText(context, "¡Calificación registrada!", Toast.LENGTH_SHORT).show()

                                    println("El UID del usuario es: $uid")
                                    println("El evento seleccionado del usuario es: $eventoIdNoNulo")
                                    println("Estrellas seleccionadas: $newRating")

                                } ?: run {
                                    // Esto se ejecuta si 'evento.id' SÍ era nulo
                                    Toast.makeText(context, "⚠️ Error: El ID del evento es nulo.", Toast.LENGTH_SHORT).show()
                                    println(" EventoId es null")
                                }

                            } else {
                                // Esto se ejecuta si 'uid' es nulo (usuario no logueado)
                                Toast.makeText(context, "⚠️ Error: Debes iniciar sesión para calificar.", Toast.LENGTH_SHORT).show()
                                println("UID es null")
                            }
                        },
                        isReadOnly = false,
                        starSize = 36.dp,
                        starColor = Color(0xFFFFC107) // El color dorado
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
                    val isPreview = LocalInspectionMode.current

                    // Solo mostramos la sección de comentarios si NO estamos en un Preview
                    if (!isPreview) {
                        evento.id?.let { idNoNulo ->
                            CommentSection(
                                eventoId = idNoNulo,
                                viewModel = eventoViewModel,
                                onStartEdit = { comentarioParaEditar ->
                                    comentarioAEditar = comentarioParaEditar
                                    showCommentModal = true
                                }
                            )
                        }
                    }
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

                        Spacer(modifier = Modifier.width(24.dp))

                        // --- Botón de Comentario ---
                        IconActionButton(icon = Icons.Default.Comment) {
                            comentarioAEditar = null //
                            showCommentModal = true
                        }
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


        if (showCommentModal) {
            CommentModal(
                showDialog = true,
                onDismissRequest = {
                    showCommentModal = false
                    comentarioAEditar = null
                },
                initialComentario = comentarioAEditar,
                onAddComment = { comentarioDelModal ->

                    val uid = currentUserId
                    val eventoId = evento?.id
                    val userName = auth.currentUser?.displayName ?: "Usuario" // <-- Nombre no llega

                    if (uid == null || eventoId == null) {
                        Toast.makeText(context, "Error: No se pudo publicar", Toast.LENGTH_SHORT).show()
                        return@CommentModal // Salimos si no hay IDs
                    }

                    if (comentarioAEditar == null) {
                        eventoViewModel.agregarComentario(
                            eventoId = eventoId,
                            comentario = comentarioDelModal.copy(nombre = userName),
                            usuarioId = uid
                        )
                        Toast.makeText(context, "Comentario publicado", Toast.LENGTH_SHORT).show()

                    } else {
                        val comentarioActualizado = comentarioAEditar!!.copy(
                            texto = comentarioDelModal.texto,
                            calificacion = comentarioDelModal.calificacion
                        )
                        eventoViewModel.editarComentario(
                            eventoId = eventoId,
                            comentario = comentarioActualizado,
                            usuarioId = uid
                        )
                        Toast.makeText(context, "Comentario actualizado", Toast.LENGTH_SHORT).show()
                    }

                    // Cierra y resetea
                    showCommentModal = false
                    comentarioAEditar = null
                }
            )
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailEventPreview() {
    val navController = rememberNavController()

    // Evento de ejemplo para mostrar en el preview
    val eventoDemo = Evento(
        nombre = "Festival de Música 2025",
        descripcion = "Un evento lleno de música, comida y diversión para toda la familia.",
        ciudad = "Medellín",
        direccion = "Parque Norte",
        categoriasIds = listOf("Música", "Cultura", "Entretenimiento"),
        imagenUrl = "https://picsum.photos/800/400",
        fechaInicio = com.google.firebase.Timestamp.now(),
        calificacionPromedio = 4.5,
        calificacionesCount = 120
    )

    // Convertimos el evento a JSON codificado para pasarlo al composable
    val gson = GsonBuilder()
        .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
        .create()
    val json = gson.toJson(eventoDemo)
    val encodedJson = java.net.URLEncoder.encode(json, StandardCharsets.UTF_8.toString())

    DetailEvent(navController = navController, encodedJson = encodedJson)
}