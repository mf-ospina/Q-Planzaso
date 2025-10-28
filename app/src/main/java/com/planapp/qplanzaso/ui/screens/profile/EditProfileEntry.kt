// app/src/main/java/com/planapp/qplanzaso/ui/screens/profile/EditProfileEntry.kt
package com.planapp.qplanzaso.ui.screens.profile

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditProfileEntry(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
        ?: run { navController.popBackStack(); return }

    val vm: EditProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = ProfileRepository(uid, FirebaseFirestore.getInstance())
                return EditProfileViewModel(repo) as T
            }
        }
    )

    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    EditProfileScreen(
        state = state,
        onChange = vm::update,

        // Guardar perfil -> vuelve a tab "profile"
        onSave = {
            vm.save {
                navController.navigate("home?tab=profile") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },

        // Flecha / Cancelar -> mismo retorno consistente
        onBack = {
            navController.navigate("home?tab=profile") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
        },

        onOpenSettings = { navController.navigate("settings") },

        // ⬇️ NUEVO: cambio de contraseña
        onChangePassword = { current, newPass ->
            scope.launch {
                try {
                    val user = auth.currentUser ?: error("Sesión inválida.")
                    val email = user.email ?: error("Tu cuenta no tiene email asociado.")

                    // Reautenticación obligatoria
                    val cred = EmailAuthProvider.getCredential(email, current)
                    user.reauthenticate(cred).await()

                    // Actualiza contraseña
                    user.updatePassword(newPass).await()

                    Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                } catch (e: FirebaseAuthException) {
                    val msg = when (e.errorCode) {
                        "ERROR_WRONG_PASSWORD" -> "Contraseña actual incorrecta."
                        "ERROR_REQUIRES_RECENT_LOGIN" -> "Por seguridad, inicia sesión de nuevo."
                        else -> "Error de autenticación: ${e.errorCode}"
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message ?: "Error al cambiar la contraseña", Toast.LENGTH_LONG).show()
                }
            }
        }
    )
}
