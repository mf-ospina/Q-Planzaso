package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent
import com.planapp.qplanzaso.ui.components.CommentModal
import androidx.compose.ui.platform.LocalInspectionMode
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
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.ui.components.CategorySection
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.components.RatingBar
import com.planapp.qplanzaso.ui.theme.DarkGrayText
import com.planapp.qplanzaso.ui.theme.LightButton
import com.planapp.qplanzaso.ui.theme.PrimaryColor
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
fun DetailEvent(navController: NavController, encodedJson: String?) {
    val eventoViewModel: EventoViewModel = viewModel()

    val eventoLive by eventoViewModel.eventoSeleccionado.collectAsState()

    val eventoInicial: Evento? = remember(encodedJson) {
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

// 🔹 LaunchedEffect para inicializar si el evento es favorito
                        LaunchedEffect(evento, usuarioId) {
                            if (evento != null && usuarioId != null) {
                                esFavorito = eventoViewModel.verificarSiEsFavorito(evento.id!!, usuarioId)
                            }
                        }

// Animación de escala (efecto “latido”)
                        val scale by animateFloatAsState(
                            targetValue = if (esFavorito) 1.3f else 1f,
                            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
                            label = "favoriteAnimation"
                        )

                        IconButton(
                            onClick = {
                                if (usuarioId == null || evento?.id == null) {
                                    Toast.makeText(contexto, "Debes iniciar sesión para marcar favoritos", Toast.LENGTH_SHORT).show()
                                    return@IconButton
                                }

                                // Lógica de toggle usando Coroutine
                                scope.launch {
                                    val actualmenteFavorito = eventoViewModel.verificarSiEsFavorito(evento.id!!, usuarioId)
                                    if (actualmenteFavorito) {
                                        eventoViewModel.eventoRepo.eliminarFavorito(evento.id!!, usuarioId)
                                        esFavorito = false
                                        Toast.makeText(contexto, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                                    } else {
                                        eventoViewModel.eventoRepo.agregarFavorito(evento.id!!, usuarioId)
                                        esFavorito = true
                                        Toast.makeText(contexto, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                                    }
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