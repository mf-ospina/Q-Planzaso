package com.planapp.qplanzaso.ui.screens.home // Asegúrate que este paquete coincida con tu proyecto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.planapp.qplanzaso.data.repository.CategoriaRepository
import com.planapp.qplanzaso.model.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Clase para representar el estado de la UI
data class CategoriaUiState(
    val categorias: List<Categoria> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class CategoriaViewModel : ViewModel() {
    private val categoriaRepository = CategoriaRepository()
    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()
    init {
        cargarCategorias()
    }

    private fun cargarCategorias() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val categoriasActivas = categoriaRepository.obtenerCategoriasActivas()
                _uiState.update {
                    it.copy(categorias = categoriasActivas, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error al cargar categorías: ${e.message}", isLoading = false)
                }
            }
        }
    }
}