package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource // ⬅️ Import necesario
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.ui.components.QTopBar
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel

@Composable
fun AllCommentsScreen(
    navController: NavController,
    eventoId: String,
    eventoViewModel: EventoViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            // Usamos stringResource para obtener la traducción
            QTopBar(
                navController = navController,
                title = stringResource(id = R.string.all_comments_title)
            )
        },
        content = { paddingValues ->
            // Se usa paddingValues para que el contenido no quede debajo del TopBar
            CommentsSection(
                eventoId = eventoId,
                eventoViewModel = eventoViewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    )
}
