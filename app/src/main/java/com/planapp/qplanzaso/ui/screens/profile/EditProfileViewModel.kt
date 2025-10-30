// app/src/main/java/com/planapp/qplanzaso/ui/screens/profile/EditProfileViewModel.kt
package com.planapp.qplanzaso.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val repo: ProfileRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(
        auth.currentUser.let { u ->
            val display = u?.displayName.orEmpty().trim()
            val partes = display.split(" ", limit = 2)
            ProfileFormState(
                nombre = partes.getOrNull(0).orEmpty(),
                telefono = "",
                ubicacion = "",
                bio = ""
            )
        }
    )
    val state: StateFlow<ProfileFormState> = _state

    init {
        viewModelScope.launch {
            // Escucha Firestore y actualiza el estado
            repo.data.collect { fromDb ->
                _state.value = fromDb
            }
        }
    }

    fun update(newState: ProfileFormState) {
        _state.value = newState
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            repo.save(_state.value)
            onSaved()
        }
    }
}
