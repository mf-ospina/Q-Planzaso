package com.planapp.qplanzaso.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.planapp.qplanzaso.data.repository.CategoriaRepository
import com.planapp.qplanzaso.data.repository.ComentarioRepository
import com.planapp.qplanzaso.data.repository.EventoRepository
import com.planapp.qplanzaso.model.Categoria
import com.planapp.qplanzaso.model.ComentarioEvento
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.model.EventoStats
import com.planapp.qplanzaso.utils.GeocodingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.planapp.qplanzaso.data.repository.AsistenciaRepository
import com.planapp.qplanzaso.data.repository.InscripcionRepository
import com.planapp.qplanzaso.data.repository.StorageRepository
import com.planapp.qplanzaso.model.EventFormData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * ViewModel principal para manejar toda la lógica de los eventos:
 * - Exploración y descubrimiento (Parte 1)
 * - Gestión de eventos de organizador y usuario (Parte 2)
 * - Comentarios, calificaciones y ubicación (Parte extendida)
 */
class EventoViewModel(
    val eventoRepo: EventoRepository = EventoRepository(),
    private val categoriaRepo: CategoriaRepository = CategoriaRepository(),
    private val comentarioRepo: ComentarioRepository = ComentarioRepository(),
    private val inscripcionRepo: InscripcionRepository = InscripcionRepository(),
    private val asistenciaRepo: AsistenciaRepository = AsistenciaRepository(),
    private val storageRepo: StorageRepository = StorageRepository()


) : ViewModel() {

    // ------------------------------------------
    // 🔹 Estados principales
    // ------------------------------------------
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    //Favoritos
    private val _eventosFavoritos = MutableStateFlow<List<Evento>>(emptyList())
    val eventosFavoritos: StateFlow<List<Evento>> = _eventosFavoritos


    private val _eventoSeleccionado = MutableStateFlow<Evento?>(null)
    val eventoSeleccionado: StateFlow<Evento?> = _eventoSeleccionado

    private val _comentarios = MutableStateFlow<List<ComentarioEvento>>(emptyList())
    val comentarios: StateFlow<List<ComentarioEvento>> = _comentarios

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var lastCommentCursor: Timestamp? = null // para paginación

    // ------------------------------------------
    // 🔹 Filtrado por categoría (para pantalla de registro o descubrimiento)
    // ------------------------------------------
    private val _eventosPorCategoria = MutableStateFlow<List<Evento>>(emptyList())
    val eventosPorCategoria: StateFlow<List<Evento>> = _eventosPorCategoria

    private val _loadingCategoria = MutableStateFlow(false)
    val loadingCategoria: StateFlow<Boolean> = _loadingCategoria

    private val _errorCategoria = MutableStateFlow<String?>(null)
    val errorCategoria: StateFlow<String?> =_errorCategoria

    //Variables para campos de formulario de crear nuevo evento
    // Campos del formulario
    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var categoriasSeleccionadas by mutableStateOf<List<Categoria>>(emptyList())
    var fechaInicio by mutableStateOf<Timestamp?>(null)
    var fechaFin by mutableStateOf<Timestamp?>(null)
    var precio by mutableStateOf("")

    var patrocinadores by mutableStateOf<List<String>>(emptyList())

    var direccion by mutableStateOf("")
    var imagenUri by mutableStateOf<Uri?>(null)
    var ubicacionLatLng by mutableStateOf<LatLng?>(null)
    var direccionMapa by mutableStateOf<String?>(null)


    // ------------------------------------------
    // 🔹 Cargar datos iniciales (categorías, eventos)
    // ------------------------------------------
    fun cargarDatosIniciales() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _categorias.value = categoriaRepo.obtenerCategoriasActivas()
                _eventos.value = eventoRepo.obtenerEventos()
            } catch (e: Exception) {
                _error.value = "Error cargando datos iniciales: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ------------------------------------------
    // 🔹 Buscar y filtrar
    // ------------------------------------------
    fun buscarEventos(query: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _eventos.value = eventoRepo.buscarEventosPorTexto(query)
            } catch (e: Exception) {
                _error.value = "Error en búsqueda: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun aplicarFiltros(
        categoriasIds: List<String>? = null,
        fechaInicio: Timestamp? = null,
        fechaFin: Timestamp? = null,
        precioMax: Double? = null,
        ubicacionActual: GeoPoint? = null,
        maxDistanciaKm: Double? = null,
        soloVerificados: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _eventos.value = eventoRepo.filtrarEventos(
                    categoriasIds, fechaInicio, fechaFin,
                    precioMax, ubicacionActual, maxDistanciaKm, soloVerificados
                )
            } catch (e: Exception) {
                _error.value = "Error aplicando filtros: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ------------------------------------------
    // 🔹 CRUD de eventos
    // ------------------------------------------
    fun crearEvento(
        evento: Evento,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val eventoId = eventoRepo.crearEvento(evento)
                onSuccess(eventoId)
                cargarDatosIniciales()
            } catch (e: Exception) {
                val mensaje = "Error creando evento: ${e.message}"
                _error.value = mensaje
                onError(mensaje)
            } finally {
                _loading.value = false
            }
        }
    }

    // Funiones para mantener los datos en el formualrio de crear evento
    // ---------- FUNCIONES DE FORMULARIO ----------

    /** Guarda los datos del formulario en un objeto EventFormData */
    // Funiones para mantener los datos en el formualrio de crear evento
    // ---------- FUNCIONES DE FORMULARIO ----------

    /** Guarda los datos del formulario en un objeto EventFormData */
    fun toFormData(organizadorId: String): EventFormData? {
        val geoPoint = ubicacionLatLng?.let { GeoPoint(it.latitude, it.longitude) }
        if (nombre.isBlank() || descripcion.isBlank() || categoriasSeleccionadas.isEmpty() ||
            fechaInicio == null || fechaFin == null || direccion.isBlank() ||
            imagenUri == null || geoPoint == null
        ) return null

        return EventFormData(
            nombre = nombre,
            descripcion = descripcion,
            categoriaId = categoriasSeleccionadas.map { it.id },
            categoriaNombre = categoriasSeleccionadas.map { it.nombre },
            fechaInicio = fechaInicio!!,
            fechaFin = fechaFin!!,
            precio = precio.toDoubleOrNull() ?: 0.0,
            patrocinadores = patrocinadores,
            direccion = direccion,
            ubicacion = geoPoint,
            imagenUri = imagenUri,
            organizadorId = organizadorId
        )
    }

    /** Limpia el formulario después de crear un evento o cancelar */
    fun clearForm() {
        nombre = ""
        descripcion = ""
        categoriasSeleccionadas = emptyList()
        fechaInicio = null
        fechaFin = null
        precio = ""
        patrocinadores = emptyList()
        direccion = ""
        imagenUri = null
        ubicacionLatLng = null
        direccionMapa = null
    }

    // ------------------------------------------
// 🔹 Editar evento (actualizar datos existentes)
// ------------------------------------------
    fun actualizarEvento(
        eventoId: String,
        formData: EventFormData,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true

                // 1️⃣ Obtener el evento actual desde Firestore
                val eventoActual = eventoRepo.obtenerEvento(eventoId)
                if (eventoActual == null) {
                    onError("No se encontró el evento con id $eventoId")
                    _loading.value = false
                    return@launch
                }

                // 2️⃣ Construir un nuevo objeto Evento actualizado
                val geoPoint = formData.ubicacion
                val eventoActualizado = eventoActual.copy(
                    nombre = formData.nombre,
                    descripcion = formData.descripcion,
                    categoriasIds = formData.categoriaId,
                    fechaInicio = formData.fechaInicio,
                    fechaFin = formData.fechaFin,
                    precio = formData.precio,
                    patrocinadores = formData.patrocinadores,
                    direccion = formData.direccion,
                    ubicacion = geoPoint,
                    imagenUrl = eventoActual.imagenUrl, // se actualiza más abajo si hay nueva imagen
                )

                // 3️⃣ Subir nueva imagen si el usuario seleccionó una distinta
                val nuevaImagenUri = formData.imagenUri
                if (nuevaImagenUri != null) {
                    try {
                        val nuevaUrl = storageRepo.reemplazarImagen(
                            eventoActual.imagenUrl,
                            nuevaImagenUri,
                            eventoId
                        )
                        eventoRepo.actualizarCampoEvento(eventoId, "imagenUrl", nuevaUrl)
                    } catch (e: Exception) {
                        _error.value = "Error al actualizar la imagen: ${e.message}"
                    }
                }

                // 4️⃣ Guardar cambios del evento en Firestore
                eventoRepo.editarEvento(eventoActualizado)

                // 5️⃣ Refrescar el estado local
                _eventoSeleccionado.value = eventoActualizado
                cargarDatosIniciales()

                onSuccess()
            } catch (e: Exception) {
                val mensaje = "Error actualizando evento: ${e.message}"
                _error.value = mensaje
                onError(mensaje)
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun obtenerEventoPorId(eventoId: String): Evento? {
        return eventoRepo.obtenerEventoPorId(eventoId)
    }


    fun editarEvento(evento: Evento) {
        viewModelScope.launch {
            try {
                _loading.value = true
                eventoRepo.editarEvento(evento)
                cargarDatosIniciales()
            } catch (e: Exception) {
                _error.value = "Error editando evento: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun eliminarEvento(eventoId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                eventoRepo.eliminarEvento(eventoId)
                cargarDatosIniciales()
            } catch (e: Exception) {
                _error.value = "Error eliminando evento: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }


// ------------------------------------------
// 🔹 Favoritos (optimizado + sincronización global)
// ------------------------------------------

    private val _favoritosSync = MutableSharedFlow<Unit>(replay = 0)
    val favoritosSync = _favoritosSync.asSharedFlow()

    /**
     * Alterna el estado de favorito de un evento.
     * Aplica los cambios al instante en la UI y ejecuta la operación en Firestore en segundo plano.
     */
    fun toggleFavorito(evento: Evento, usuarioId: String) {
        viewModelScope.launch {
            try {
                val eventoId = evento.id ?: return@launch
                val favoritosActuales = _eventosFavoritos.value.toMutableList()
                val esFavorito = favoritosActuales.any { it.id == eventoId }

                if (esFavorito) {
                    // 🔹 1. Actualiza instantáneamente la lista local
                    _eventosFavoritos.value = favoritosActuales.filter { it.id != eventoId }

                    // 🔹 2. Lanza la eliminación real sin bloquear la UI
                    launch(Dispatchers.IO) {
                        try {
                            eventoRepo.eliminarFavorito(eventoId, usuarioId)
                        } catch (e: Exception) {
                            _error.value = "Error eliminando favorito: ${e.message}"
                        }
                    }
                } else {
                    // 🔹 1. Añade instantáneamente en la UI
                    _eventosFavoritos.value = favoritosActuales + evento

                    // 🔹 2. Luego lo guarda en Firestore sin bloquear la UI
                    launch(Dispatchers.IO) {
                        try {
                            eventoRepo.agregarFavorito(eventoId, usuarioId)
                        } catch (e: Exception) {
                            _error.value = "Error agregando favorito: ${e.message}"
                        }
                    }
                }

                // 🔹 3. Sincroniza el campo esFavorito en la lista global (si aplica)
                val favoritosIds = _eventosFavoritos.value.mapNotNull { it.id }.toSet()
                _eventos.value = _eventos.value.map { ev ->
                    ev.copy(esFavorito = favoritosIds.contains(ev.id))
                }

                // 🔹 4. Notifica a todas las pantallas que los favoritos cambiaron
                _favoritosSync.emit(Unit)

            } catch (e: Exception) {
                _error.value = "Error general al alternar favorito: ${e.message}"
            }
        }
    }

    /**
     * Verifica rápidamente si un evento es favorito.
     * Usa la caché local primero, y Firestore solo si es necesario.
     */
    suspend fun verificarSiEsFavorito(eventoId: String, usuarioId: String): Boolean {
        val favoritosLocales = _eventosFavoritos.value
        if (favoritosLocales.isNotEmpty()) {
            return favoritosLocales.any { it.id == eventoId }
        }
        return eventoRepo.esEventoFavorito(eventoId, usuarioId)
    }

    /**
     * Recarga la lista completa de favoritos desde Firestore y sincroniza con la lista global.
     */
    fun refrescarFavoritos(usuarioId: String) {
        viewModelScope.launch {
            try {
                val nuevosFavoritos = eventoRepo.obtenerEventosFavoritosPorUsuario(usuarioId)
                _eventosFavoritos.value = nuevosFavoritos

                val favoritosIds = nuevosFavoritos.mapNotNull { it.id }.toSet()
                _eventos.value = _eventos.value.map { evento ->
                    evento.copy(esFavorito = favoritosIds.contains(evento.id))
                }
            } catch (e: Exception) {
                _error.value = "Error refrescando favoritos: ${e.message}"
            }
        }
    }



    // ------------------------------------------
    // 🔹 Estadísticas
    // ------------------------------------------
    fun actualizarEstadisticas(eventoId: String, stats: EventoStats) {
        viewModelScope.launch {
            try {
                eventoRepo.actualizarEstadisticas(eventoId, stats)
            } catch (e: Exception) {
                _error.value = "Error actualizando estadísticas: ${e.message}"
            }
        }
    }

    // ------------------------------------------
    // 🔹 Comentarios con paginación
    // ------------------------------------------
    fun cargarComentarios(eventoId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val (lista, cursor) = comentarioRepo.obtenerComentariosPaginados(eventoId)
                _comentarios.value = lista
                lastCommentCursor = cursor
            } catch (e: Exception) {
                _error.value = "Error cargando comentarios: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun cargarMasComentarios(eventoId: String) {
        viewModelScope.launch {
            try {
                val (nuevos, cursor) = comentarioRepo.obtenerComentariosPaginados(eventoId, lastCommentCursor)
                if (nuevos.isNotEmpty()) {
                    _comentarios.value = _comentarios.value + nuevos
                    lastCommentCursor = cursor
                }
            } catch (e: Exception) {
                _error.value = "Error cargando más comentarios: ${e.message}"
            }
        }
    }

    fun agregarComentario(eventoId: String, comentario: ComentarioEvento, usuarioId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                comentarioRepo.crearComentario(eventoId, comentario.copy(usuarioId = usuarioId))
                if (comentario.calificacion > 0.0) {
                    eventoRepo.registrarCalificacion(eventoId, usuarioId, comentario.calificacion)
                }
                cargarComentarios(eventoId)
                _eventoSeleccionado.value = eventoRepo.obtenerEvento(eventoId)
            } catch (e: Exception) {
                _error.value = "Error agregando comentario: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun editarComentario(eventoId: String, comentario: ComentarioEvento, usuarioId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                comentarioRepo.editarComentario(eventoId, comentario)
                if (comentario.calificacion > 0.0) {
                    eventoRepo.registrarCalificacion(eventoId, usuarioId, comentario.calificacion)
                }
                cargarComentarios(eventoId)
                _eventoSeleccionado.value = eventoRepo.obtenerEvento(eventoId)
            } catch (e: Exception) {
                _error.value = "Error editando comentario: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun eliminarComentario(eventoId: String, comentarioId: String, usuarioId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                comentarioRepo.eliminarComentario(eventoId, comentarioId)
                eventoRepo.eliminarCalificacion(eventoId, usuarioId)
                cargarComentarios(eventoId)
                _eventoSeleccionado.value = eventoRepo.obtenerEvento(eventoId)
            } catch (e: Exception) {
                _error.value = "Error eliminando comentario: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun registrarCalificacion(eventoId: String, usuarioId: String, valor: Double) {
        viewModelScope.launch {
            try {
                eventoRepo.registrarCalificacion(eventoId, usuarioId, valor)
                _eventoSeleccionado.value = eventoRepo.obtenerEvento(eventoId)
            } catch (e: Exception) {
                _error.value = "Error registrando calificación: ${e.message}"
            }
        }
    }

    // ------------------------------------------
    // 🔹 Ubicación y geocodificación
    // ------------------------------------------
    fun actualizarUbicacionEventoSeleccionado(lat: Double, lon: Double) {
        val evento = _eventoSeleccionado.value
        if (evento != null) {
            val actualizado = evento.copy(ubicacion = GeoPoint(lat, lon))
            _eventoSeleccionado.value = actualizado
        }
    }

    fun calcularDistanciaAUsuario(ubicacionEvento: GeoPoint?, ubicacionUsuario: GeoPoint?): Double? {
        if (ubicacionEvento == null || ubicacionUsuario == null) return null
        val lat1 = ubicacionUsuario.latitude
        val lon1 = ubicacionUsuario.longitude
        val lat2 = ubicacionEvento.latitude
        val lon2 = ubicacionEvento.longitude
        return eventoRepo.calcularDistanciaKm(lat1, lon1, lat2, lon2)
    }

    // ✅ Función que usa GeocodingUtils.kt
    fun actualizarUbicacionDesdeDireccion(context: Context, direccion: String) {
        viewModelScope.launch {
            val geoPoint = withContext(Dispatchers.IO) {
                GeocodingUtils.obtenerCoordenadasDesdeDireccion(context, direccion)
            }
            geoPoint?.let {
                actualizarUbicacionEventoSeleccionado(it.latitude, it.longitude)
            } ?: run {
                _error.value = "No se pudo encontrar la ubicación para esa dirección."
            }
        }
    }

    suspend fun obtenerDireccionDesdeUbicacion(context: Context, lat: Double, lon: Double): String? {
        return withContext(Dispatchers.IO) {
            GeocodingUtils.obtenerDireccionDesdeCoordenadas(context, lat, lon)
        }
    }

    // ------------------------------------------
    // 🔹 Ubicación actual del usuario
    // ------------------------------------------
    fun actualizarUbicacionUsuario(geoPoint: GeoPoint) {
        val eventoActual = _eventoSeleccionado.value
        if (eventoActual != null) {
            val actualizado = eventoActual.copy(ubicacion = geoPoint)
            _eventoSeleccionado.value = actualizado
        }
    }


    // ------------------------------------------
    // 🔹 Compartir evento
    // ------------------------------------------
    fun compartirEvento(context: Context, evento: Evento) {
        val textoCompartir = buildString {
            append("🎉 ${evento.nombre}\n")
            append("📅 Fecha: ${evento.fechaInicio?.toDate() ?: "Sin fecha"}\n")
            evento.descripcion?.let { append("\n📝 $it\n") }
            evento.ubicacion?.let {
                append("\n📍 Ubicación: https://www.google.com/maps?q=${it.latitude},${it.longitude}\n")
            }
            append("\n¡Descúbrelo en QPlanzaso! 🔗")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, evento.nombre)
            putExtra(Intent.EXTRA_TEXT, textoCompartir)
        }

        val chooser = Intent.createChooser(intent, "Compartir evento con:")
        context.startActivity(chooser)
    }
    // ------------------------------------------
    // 🔹 Inscripciones
    // ------------------------------------------
    fun inscribirseEnEvento(eventoId: String, usuarioId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                inscripcionRepo.inscribirseEnEvento(eventoId, usuarioId)
                _eventoSeleccionado.value = eventoRepo.obtenerEvento(eventoId)
            } catch (e: Exception) {
                _error.value = "Error al inscribirse: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun cancelarInscripcion(eventoId: String, usuarioId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                inscripcionRepo.cancelarInscripcion(eventoId, usuarioId)
                _eventoSeleccionado.value = eventoRepo.obtenerEvento(eventoId)
            } catch (e: Exception) {
                _error.value = "Error cancelando inscripción: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun obtenerEventosInscritos(usuarioId: String, soloFuturos: Boolean = true) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val eventos = inscripcionRepo.obtenerEventosInscritos(usuarioId)
                val ahoraMillis = System.currentTimeMillis()

                _eventos.value = if (soloFuturos) {
                    eventos.filter { evento ->
                        val inicio = evento.fechaInicio
                        inicio != null && inicio.toDate().time > ahoraMillis
                    }
                } else {
                    eventos.filter { evento ->
                        val fin = evento.fechaFin
                        fin != null && fin.toDate().time < ahoraMillis
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al obtener eventos inscritos: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ------------------------------------------
    // 🔹 Asistencia real (Check-in)
    // ------------------------------------------
    fun registrarAsistencia(eventoId: String, usuarioId: String, lat: Double? = null, lon: Double? = null) {
        viewModelScope.launch {
            try {
                _loading.value = true
                asistenciaRepo.registrarAsistencia(eventoId, usuarioId, lat, lon)
                _eventoSeleccionado.value = eventoRepo.obtenerEvento(eventoId)
            } catch (e: Exception) {
                _error.value = "Error registrando asistencia: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun verificarAsistencia(eventoId: String, usuarioId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val yaAsistio = asistenciaRepo.verificarAsistencia(eventoId, usuarioId)
                callback(yaAsistio)
            } catch (e: Exception) {
                _error.value = "Error verificando asistencia: ${e.message}"
                callback(false)
            }
        }
    }

    // ------------------------------------------
    // 🔹 Subir Imagenes a storage
    // ------------------------------------------

    fun subirImagenEvento(uri: Uri, eventoId: String, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true

                // ⿡ Subir imagen y obtener URL
                val url = storageRepo.subirImagenEvento(uri, eventoId)

                // ⿢ Actualizar solo el campo imagenUrl del evento
                eventoRepo.actualizarCampoEvento(eventoId, "imagenUrl", url)

                // ⿣ Actualizar estado local
                val eventoActualizado = _eventoSeleccionado.value?.copy(imagenUrl = url)
                _eventoSeleccionado.value = eventoActualizado

                onSuccess(url)
            } catch (e: Exception) {
                _error.value = "Error subiendo imagen: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun reemplazarImagenEvento(urlAntigua: String?, uriNueva: Uri, eventoId: String, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true

                // 1) subir nueva imagen (puede lanzar)
                val nuevaUrl = storageRepo.reemplazarImagen(urlAntigua, uriNueva, eventoId)

                // 2) obtener y validar evento existente
                val eventoActual = eventoRepo.obtenerEvento(eventoId)
                if (eventoActual == null) {
                    _error.value = "Evento no encontrado para id: $eventoId"
                    return@launch
                }

                // 3) actualizar evento con nueva URL
                val eventoActualizado = eventoActual.copy(imagenUrl = nuevaUrl)
                eventoRepo.editarEvento(eventoActualizado)

                // 4) actualizar estado local y notificar
                _eventoSeleccionado.value = eventoActualizado
                onSuccess(nuevaUrl)

            } catch (e: Exception) {
                _error.value = "Error reemplazando imagen: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }


    fun eliminarImagenEvento(eventoId: String, imagenUrl: String?) {
        viewModelScope.launch {
            try {
                _loading.value = true

                // 1) si hay URL, eliminar del storage
                imagenUrl?.let { url ->
                    try {
                        storageRepo.eliminarImagenPorUrl(url)
                    } catch (e: Exception) {
                        // registrar error pero continuar con la actualización en Firestore
                        _error.value = "Advertencia: no se pudo eliminar fichero en Storage: ${e.message}"
                    }
                }

                // 2) obtener evento actual desde Firestore
                val eventoActual = eventoRepo.obtenerEvento(eventoId)
                if (eventoActual == null) {
                    _error.value = "Evento no encontrado: $eventoId"
                    return@launch
                }

                // 3) crear copia sin la URL de imagen y guardar
                val eventoActualizado = eventoActual.copy(imagenUrl = null)
                eventoRepo.editarEvento(eventoActualizado)

                // 4) actualizar estado local
                _eventoSeleccionado.value = eventoActualizado

            } catch (e: Exception) {
                _error.value = "Error eliminando imagen: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ------------------------------------------
    // 🔹 Logica Perfil
    // ------------------------------------------
    // Funcion Helper: dice si un evento ya finalizó
    fun esEventoFinalizado(evento: Evento): Boolean {
        val fin = evento.fechaFin
        return fin != null && fin < Timestamp.now()
    }

    // Cargar eventos del usuario (organizador)
    fun cargarEventosDelUsuario(usuarioId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _eventos.value = eventoRepo.obtenerEventosPorOrganizador(usuarioId)
            } catch (e: Exception) {
                _error.value = "Error cargando tus eventos: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun cargarEventosPorCategoria(categoriaId: String) {
        viewModelScope.launch {
            try {
                _loadingCategoria.value = true
                _eventosPorCategoria.value = eventoRepo.obtenerEventosPorCategoriaN(categoriaId)
            } catch (e: Exception) {
                _errorCategoria.value = "Error al cargar eventos de categoría: ${e.message}"
            } finally {
                _loadingCategoria.value = false
            }
        }
    }

    suspend fun crearEventoSuspend(evento: Evento): String? {
        return try {
            eventoRepo.crearEvento(evento)
        } catch (e: Exception) {
            _error.value = "Error creando evento: ${e.message}"
            null
        }
    }

    fun inscribirseEnEvento(eventoId: String) {
        viewModelScope.launch {
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            inscripcionRepo.inscribirseEnEvento(eventoId, usuarioId)

            // 🔹 Notificamos al CalendarioViewModel
            CalendarioViewModel().emitirRefresco()
        }
    }

    //nuevo evento//
    fun enviarCalificacionYComentario(
        eventoId: String,
        usuarioId: String,
        rating: Int,
        comment: String
    ) {
        viewModelScope.launch {
            try {
                // 1. Crear el objeto ComentarioEvento dentro del ViewModel (Lógica de Negocio)
                val nuevoComentario = ComentarioEvento(
                    id = eventoId,
                    usuarioId = usuarioId,
                    texto = comment, // Usa el campo correcto de tu modelo, yo usé 'contenido'
                    calificacion = rating.toDouble(),
                    fecha = Timestamp.now()
                    // Nota: Asegúrate de que los nombres de los campos (contenido/texto, fechaCreacion/fecha)
                    // coincidan con tu modelo ComentarioEvento real.
                )

                // 2. Usar la función existente agregarComentario
                agregarComentario(eventoId, nuevoComentario, usuarioId)

            } catch (e: Exception) {
                _error.value = "Error enviando calificación/comentario: ${e.message}"
            }
        }
    }

    suspend fun obtenerCalificacionDeUsuario(eventoId: String, usuarioId: String): Double {
        return try {
            // 🌟 Llama al nuevo método del repositorio 🌟
            val comentario = comentarioRepo.obtenerComentarioPorUsuario(eventoId, usuarioId)

            // Retorna la calificación (Double) o 0.0 si es nulo
            comentario?.calificacion ?: 0.0
        } catch (e: Exception) {
            // Log error si es necesario
            0.0
        }
    }
}