package com.planapp.qplanzaso.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileFormState())
    val state: StateFlow<ProfileFormState> = _state

    init {
        viewModelScope.launch {
            // Lee el flujo en tiempo real del repo
            repo.data.collect { profile ->
                _state.value = profile
            }
        }
    }
}
