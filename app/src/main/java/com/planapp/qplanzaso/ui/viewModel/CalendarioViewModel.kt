package com.planapp.qplanzaso.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planapp.qplanzaso.data.repository.InscripcionRepository
import com.planapp.qplanzaso.model.Evento
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel del módulo de calendario.
 * Controla:
 * - Carga de eventos del usuario autenticado.
 * - Agrupación por día, filtrado dinámico, y clasificación en pasados / próximos.
 * - Sincronización reactiva con Jetpack Compose mediante StateFlow.
 */
class CalendarioViewModel(
    private val inscripcionRepo: InscripcionRepository = InscripcionRepository()
) : ViewModel() {

    //canal de comunicación reactivo entre EventoViewModel y CalendarioViewModel usando MutableSharedFlow
    private val _refreshTrigger = MutableSharedFlow<Unit>()
    val refreshTrigger = _refreshTrigger.asSharedFlow()

    suspend fun forzarRecarga(usuarioId: String) {
        cargarEventosInscritos(usuarioId)
    }

    suspend fun emitirRefresco() {
        _refreshTrigger.emit(Unit)
    }

    // ────────────────────────────────
    // Estados observables
    // ────────────────────────────────
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate

    private val _todosEventosInscritos = MutableStateFlow<List<Evento>>(emptyList())
    val todosEventosInscritos: StateFlow<List<Evento>> = _todosEventosInscritos

    private val _eventosDelDia = MutableStateFlow<List<Evento>>(emptyList())
    val eventosDelDia: StateFlow<List<Evento>> = _eventosDelDia

    private val _eventosProximos = MutableStateFlow<List<Evento>>(emptyList())
    val eventosProximos: StateFlow<List<Evento>> = _eventosProximos

    private val _eventosPasados = MutableStateFlow<List<Evento>>(emptyList())
    val eventosPasados: StateFlow<List<Evento>> = _eventosPasados

    private val _eventosPorDia = MutableStateFlow<Map<String, List<Evento>>>(emptyMap())
    val eventosPorDia: StateFlow<Map<String, List<Evento>>> = _eventosPorDia

    private val dateFormatKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ────────────────────────────────
    // Cargar eventos desde Firestore
    // ────────────────────────────────
    fun cargarEventosInscritos(usuarioId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val lista = inscripcionRepo.obtenerEventosInscritos(usuarioId)
                _todosEventosInscritos.value = lista

                // Agrupar por fecha (clave yyyy-MM-dd)
                val agrupados = lista.groupBy { evento ->
                    evento.fechaInicio?.toDate()?.let { dateFormatKey.format(it) } ?: "sin_fecha"
                }
                _eventosPorDia.value = agrupados

                // Inicializar con el día actual
                actualizarListasParaFecha(_selectedDate.value)

            } catch (e: Exception) {
                _error.value = "Error al cargar eventos: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ────────────────────────────────
    // Cambiar fecha seleccionada
    // ────────────────────────────────
    fun seleccionarFecha(fecha: Date) {
        _selectedDate.value = fecha
        actualizarListasParaFecha(fecha)
    }

    // ────────────────────────────────
    // Actualizar listas (día / próximos / pasados)
    // ────────────────────────────────
    private fun actualizarListasParaFecha(fecha: Date) {
        val key = dateFormatKey.format(fecha)
        val eventosHoy = _eventosPorDia.value[key] ?: emptyList()
        _eventosDelDia.value = eventosHoy.sortedBy { it.fechaInicio?.toDate()?.time ?: 0L }

        val inicioDia = startOfDayMillis(fecha)
        val finDia = endOfDayMillis(fecha)

        // Próximos eventos (después de hoy)
        _eventosProximos.value = _todosEventosInscritos.value.filter { evento ->
            val inicio = evento.fechaInicio?.toDate()?.time ?: Long.MAX_VALUE
            inicio > finDia
        }.sortedBy { it.fechaInicio?.toDate()?.time ?: Long.MAX_VALUE }

        // Pasados (antes del día actual)
        _eventosPasados.value = _todosEventosInscritos.value.filter { evento ->
            val fin = evento.fechaFin?.toDate()?.time ?: (evento.fechaInicio?.toDate()?.time ?: 0L)
            fin < inicioDia
        }.sortedByDescending { it.fechaFin?.toDate()?.time ?: (it.fechaInicio?.toDate()?.time ?: 0L) }
    }

    // ────────────────────────────────
    // Helpers para manipular fechas
    // ────────────────────────────────
    private fun startOfDayMillis(date: Date): Long {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun endOfDayMillis(date: Date): Long {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    // ────────────────────────────────
    // Fechas con eventos
    // ────────────────────────────────
    fun obtenerFechasConEventos(): List<Date> {
        return _todosEventosInscritos.value.mapNotNull { it.fechaInicio?.toDate() }
    }

    // ────────────────────────────────
    // Validar si hay eventos en una fecha
    // ────────────────────────────────
    fun hayEventosEnFecha(fecha: Date): Boolean {
        val key = dateFormatKey.format(fecha)
        return _eventosPorDia.value[key]?.isNotEmpty() == true
    }

    // ────────────────────────────────
    // Limpieza de errores
    // ────────────────────────────────
    fun limpiarError() {
        _error.value = null
    }
}
