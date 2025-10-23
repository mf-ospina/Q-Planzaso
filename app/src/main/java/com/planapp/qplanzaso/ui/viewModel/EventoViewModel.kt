package com.planapp.qplanzaso.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.planapp.qplanzaso.data.repository.CategoriaRepository
import com.planapp.qplanzaso.data.repository.ComentarioRepository
import com.planapp.qplanzaso.data.repository.EventoRepository
import com.planapp.qplanzaso.data.repository.VibraRepository
import com.planapp.qplanzaso.model.Categoria
import com.planapp.qplanzaso.model.ComentarioEvento
import com.planapp.qplanzaso.model.Evento
import com.planapp.qplanzaso.model.EventoStats
import com.planapp.qplanzaso.model.Vibra
import com.planapp.qplanzaso.utils.GeocodingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import com.planapp.qplanzaso.data.repository.InscripcionRepository
import android.util.Log // Asegúrate de tener este import para el log en caso de error

/**
 * ViewModel principal para manejar toda la lógica de los eventos:
 * - Exploración y descubrimiento (Parte 1)
 * - Gestión de eventos de organizador y usuario (Parte 2)
 * - Comentarios, calificaciones y ubicación (Parte extendida)
 */
class EventoViewModel(
    private val eventoRepo: EventoRepository = EventoRepository(),
    private val categoriaRepo: CategoriaRepository = CategoriaRepository(),
    private val vibraRepo: VibraRepository = VibraRepository(),
    private val comentarioRepo: ComentarioRepository = ComentarioRepository(),
    private val inscripcionRepo: InscripcionRepository = InscripcionRepository()
) : ViewModel() {

    // ------------------------------------------
    // 🔹 Estados principales
    // ------------------------------------------
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _vibras = MutableStateFlow<List<Vibra>>(emptyList())
    val vibras: StateFlow<List<Vibra>> = _vibras

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
    // 🚀 Estados para Eventos por Categoría (AÑADIDO)
    // ------------------------------------------
    private val _eventosPorCategoria = MutableStateFlow<List<Evento>>(emptyList())
    val eventosPorCategoria: StateFlow<List<Evento>> = _eventosPorCategoria

    private val _loadingCategoria = MutableStateFlow(false)
    val loadingCategoria: StateFlow<Boolean> = _loadingCategoria

    private val _errorCategoria = MutableStateFlow<String?>(null)
    val errorCategoria: StateFlow<String?> = _errorCategoria

    // ------------------------------------------
    // 🔹 Cargar datos iniciales (categorías, vibras, eventos)
    // ------------------------------------------
    fun cargarDatosIniciales() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _categorias.value = categoriaRepo.obtenerCategoriasActivas()
                _vibras.value = vibraRepo.obtenerVibrasActivas()
                _eventos.value = eventoRepo.obtenerEventos()
            } catch (e: Exception) {
                _error.value = "Error cargando datos iniciales: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ------------------------------------------
    // 🔍 Cargar Eventos por Categoría (AÑADIDO)
    // ------------------------------------------
    fun cargarEventosPorCategoria(categoryId: String) {
        viewModelScope.launch {
            _loadingCategoria.value = true
            _errorCategoria.value = null
            try {
                val eventos = eventoRepo.obtenerEventosPorCategoriaN(categoryId)
                _eventosPorCategoria.value = eventos
            } catch (e: Exception) {
                Log.e("EventoViewModel", "Error al cargar eventos por categoría $categoryId", e)
                _errorCategoria.value = "Error al cargar eventos: ${e.message}"
            } finally {
                _loadingCategoria.value = false
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
        vibras: List<String>? = null,
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
                    categoriasIds, vibras, fechaInicio, fechaFin,
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
    fun crearEvento(evento: Evento) {
        viewModelScope.launch {
            try {
                _loading.value = true
                eventoRepo.crearEvento(evento)
                cargarDatosIniciales()
            } catch (e: Exception) {
                _error.value = "Error creando evento: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
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
    // 🔹 Favoritos
    // ------------------------------------------
    fun agregarAFavoritos(eventoId: String, usuarioId: String) {
        viewModelScope.launch {
            try {
                eventoRepo.agregarFavorito(eventoId, usuarioId)
            } catch (e: Exception) {
                _error.value = "Error agregando a favoritos: ${e.message}"
            }
        }
    }

    fun eliminarDeFavoritos(eventoId: String, usuarioId: String) {
        viewModelScope.launch {
            try {
                eventoRepo.eliminarFavorito(eventoId, usuarioId)
            } catch (e: Exception) {
                _error.value = "Error eliminando de favoritos: ${e.message}"
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
                append("\n📍 Ubicación: https://www.google.com/maps?q=$${it.latitude},${it.longitude}\n")
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
}