// ui/screens/profile/NotificationSettingsViewModel.kt
package com.planapp.qplanzaso.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationSettingsViewModel(
    private val repo: NotificationPrefsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationPrefs())
    val state: StateFlow<NotificationPrefs> = _state

    init {
        viewModelScope.launch {
            repo.data.collect { prefs -> _state.value = prefs }
        }
    }

    // para onChange = vm::update
    fun update(new: NotificationPrefs) {
        _state.value = new
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            repo.save(_state.value)
            onSaved()
        }
    }
}
