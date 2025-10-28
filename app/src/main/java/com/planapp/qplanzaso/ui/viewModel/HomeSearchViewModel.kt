package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planapp.qplanzaso.data.repository.EventoRepository
import com.planapp.qplanzaso.model.Evento
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeSearchViewModel(
    private val eventoRepository: EventoRepository = EventoRepository()
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _searchResults = MutableStateFlow<List<Evento>>(emptyList())
    val searchResults: StateFlow<List<Evento>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    //  Bandera para saber si ya se hizo la carga inicial
    private val _isInitialLoadDone = MutableStateFlow(false)
    val isInitialLoadDone: StateFlow<Boolean> = _isInitialLoadDone

    private var searchJob: Job? = null

    init {
        //  Cargar todos los eventos al iniciar el ViewModel
        loadAllEventsInitial()
    }

    private fun loadAllEventsInitial() {
        if (_isInitialLoadDone.value) return // Evitar doble carga

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Usamos obtenerEventos para llenar la lista al inicio
                val allEvents = eventoRepository.obtenerEventos()
                _searchResults.value = allEvents
                _isInitialLoadDone.value = true
            } catch (e: Exception) {
                println("Error cargando eventos iniciales: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchTextChanged(newText: String) {
        _searchText.value = newText

        searchJob?.cancel()

        if (newText.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(300L)
                performSearch(newText)
            }
        } else {
            // Si el texto se borra, volvemos a mostrar la lista inicial
            loadAllEventsInitial()
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = eventoRepository.buscarEventosPorTexto(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}