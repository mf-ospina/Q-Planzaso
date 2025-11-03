package com.planapp.qplanzaso.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.planapp.qplanzaso.data.repository.NotificacionRepository
import com.planapp.qplanzaso.model.Notificacion
import com.planapp.qplanzaso.model.TipoNotificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class NotificacionViewModel(
    private val notificacionRepo: NotificacionRepository = NotificacionRepository()
) : ViewModel() {

    // 🔹 Lista de notificaciones
    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    val notificaciones: StateFlow<List<Notificacion>> = _notificaciones

    // 🔹 Estado de carga
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // 🔹 Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // 🔹 Listener de notificaciones en tiempo real
    private var listener: ListenerRegistration? = null

    // 🔹 Cargar notificaciones del usuario
    fun cargarNotificaciones(usuarioId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val lista = notificacionRepo.obtenerNotificacionesUsuario(usuarioId)
                _notificaciones.value = lista.sortedByDescending { it.fecha?.toDate() }
            } catch (e: Exception) {
                _error.value = "Error al cargar notificaciones: ${e.localizedMessage ?: "Desconocido"}"
            } finally {
                _loading.value = false
            }
        }
    }

    // 🔹 Escuchar notificaciones en tiempo real
    fun escucharNotificacionesTiempoReal(usuarioId: String) {
        // Limpiar listener anterior si existe
        listener?.remove()
        val db = FirebaseFirestore.getInstance()
        listener = db.collection("notificaciones")
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _error.value = "Error al escuchar notificaciones: ${error.localizedMessage}"
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Notificacion::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _notificaciones.value = lista.sortedByDescending { it.fecha?.toDate() }
            }
    }

    // 🔹 Marcar como leída
    fun marcarComoLeida(id: String) {
        viewModelScope.launch {
            try {
                notificacionRepo.marcarComoLeida(id)
                _notificaciones.value = _notificaciones.value.map {
                    if (it.id == id) it.copy(leida = true) else it
                }
            } catch (e: Exception) {
                _error.value = "Error al marcar como leída: ${e.localizedMessage ?: "Desconocido"}"
            }
        }
    }

    // 🔹 Eliminar notificación
    fun eliminarNotificacion(id: String) {
        viewModelScope.launch {
            try {
                notificacionRepo.eliminarNotificacion(id)
                _notificaciones.value = _notificaciones.value.filterNot { it.id == id }
            } catch (e: Exception) {
                _error.value = "Error al eliminar: ${e.localizedMessage ?: "Desconocido"}"
            }
        }
    }

    // 🔹 Agregar recordatorios automáticos de un evento
    fun agregarRecordatoriosEvento(usuarioId: String, tituloEvento: String, fechaEvento: Date) {
        viewModelScope.launch {
            try {
                val diasAntes = listOf(7, 3, 1)
                val ahora = Date()
                diasAntes.forEach { dias ->
                    val calendario = Calendar.getInstance().apply { time = fechaEvento }
                    calendario.add(Calendar.DAY_OF_YEAR, -dias)
                    if (calendario.time.after(ahora)) {
                        val notificacion = Notificacion(
                            id = "${usuarioId}_${fechaEvento.time}_recordatorio_$dias",
                            usuarioId = usuarioId,
                            titulo = "Recordatorio: $tituloEvento",
                            mensaje = "Faltan $dias día(s) para tu evento: $tituloEvento",
                            fecha = Timestamp(calendario.time),
                            tipo = TipoNotificacion.RECORDATORIO,
                            leida = false
                        )
                        notificacionRepo.agregarNotificacion(notificacion)
                    }
                }
                cargarNotificaciones(usuarioId)
            } catch (e: Exception) {
                _error.value = "Error agregando recordatorios: ${e.localizedMessage ?: "Desconocido"}"
            }
        }
    }

    // 🔹 Refrescar manualmente
    fun refrescar(usuarioId: String) {
        cargarNotificaciones(usuarioId)
    }

    override fun onCleared() {
        super.onCleared()
        // 🔹 Limpiar listener al destruir el ViewModel
        listener?.remove()
    }
}
