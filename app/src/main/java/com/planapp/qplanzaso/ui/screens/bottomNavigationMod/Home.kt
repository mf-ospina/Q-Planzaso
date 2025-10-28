package com.planapp.qplanzaso.ui.screens.bottomNavigationMod

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.planapp.qplanzaso.R
import com.planapp.qplanzaso.ui.components.CarouselView
import com.planapp.qplanzaso.ui.screens.home.CategoriaViewModel
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import com.planapp.qplanzaso.ui.components.EventCard
import com.google.firebase.Timestamp
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.TimestampTypeAdapter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class Plan(
    val id: Int,
    val title: String,
    val imageUrl: String
)

val samplePlans = listOf(
    Plan(1, "Evento en la ciudad", "https://hotelregency.com.co/wp-content/uploads/2023/01/11-ENE-BLOG-REGENCY_BANNER-GENERAL-915x515.jpg"),
    Plan(2, "Reunión comunitaria", "https://latiquetera.com/img/upload/nu_6ssjfizms8_565146960579861.jpg"),
    Plan(3, "Jornada cultural", "https://tuboleta.com/sites/default/files/2025-08/TheMills_imagen%20de%20eventos_900x800_72dpi.png"),
    Plan(4, "Actividad recreativa", "https://cartagenamusicfestival.com/wp-content/uploads/2021/06/WhatsApp-Image-2021-06-21-at-6.56.30-PM.jpeg"),
    Plan(5, "Encuentro local", "https://bogota.gov.co/sites/default/files/2025-10/planes-en-bogota-10-eventos-recomendamos-de-bienal-de-arte-2025.jpg")
)

private val colorMusica = Color(0xFFB39DDB)
private val colorDeportes = Color(0xFFE57373)
private val colorComedia = Color(0xFF81C7F0)
private val colorArte = Color(0xFFF5D07A)
private val colorDefault = Color(0xFFB0BEC5)

private fun getColorForCategory(categoryName: String): Color {
    return when (categoryName.lowercase()) {
        "música" -> colorMusica
        "deportes" -> colorDeportes
        "comedia" -> colorComedia
        "arte" -> colorArte
        "rock" -> Color(0xFF8E8E8E)
        else -> colorDefault
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val categoriaViewModel: CategoriaViewModel = viewModel()
    val categoriaState by categoriaViewModel.uiState.collectAsStateWithLifecycle()

    val eventoViewModel: EventoViewModel = viewModel()
    val todosLosEventos by eventoViewModel.eventos.collectAsStateWithLifecycle()
    val loadingState by eventoViewModel.loading.collectAsStateWithLifecycle()
    val errorState by eventoViewModel.error.collectAsStateWithLifecycle()

    var searchQuery by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        eventoViewModel.cargarDatosIniciales()
    }

    val eventosFiltrados by remember(todosLosEventos, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                todosLosEventos
            } else {
                val query = searchQuery.trim().lowercase()
                todosLosEventos.filter { evento ->
                    val nombreBusqueda = evento.nombre?.lowercase() ?: ""
                    val descripcionBusqueda = evento.descripcion?.lowercase() ?: ""
                    nombreBusqueda.contains(query) || descripcionBusqueda.contains(query)
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (loadingState && todosLosEventos.isEmpty()) {
            CircularProgressIndicator()
        } else if (errorState != null) {
            Text(text = stringResource(R.string.error_general) + ": ${errorState}")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
                // SearchBar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.buscar_eventos)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            items(categoriaState.categorias) { categoria ->
                                val color = getColorForCategory(categoria.nombre)
                                CategoryBox(
                                    name = categoria.nombre,
                                    color = color,
                                    onClick = {
                                        val encodedCategoryName = URLEncoder.encode(
                                            categoria.nombre,
                                            StandardCharsets.UTF_8.toString()
                                        )

                                        navController.navigate("EventByCategory/${categoria.id}/${encodedCategoryName}")
                                    }
                                )
                            }
                        }
                    }
                    item {
                        CarouselView(
                            items = samplePlans,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) { _, plan ->
                            PlanCarouselItem(plan = plan)
                        }
                    }

                    item {
                        val titleText = if (searchQuery.isNotEmpty())
                            "Resultados de Búsqueda (${eventosFiltrados.size})"
                        else
                            stringResource(R.string.todos_eventos)

                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    if (eventosFiltrados.isNotEmpty()) {
                        items(
                            items = eventosFiltrados,
                            key = { evento -> evento.id ?: evento.nombre ?: System.currentTimeMillis() }
                        ) { evento ->

                            Box(modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .height(140.dp)) {
                                EventCard(evento = evento) {

                                    val gson = GsonBuilder()
                                        .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                                        .create()

                                    val eventoJson = gson.toJson(evento)
                                    val encodedJson = URLEncoder.encode(eventoJson, StandardCharsets.UTF_8.toString())
                                    navController.navigate("detailEvent/${encodedJson}")
                                }
                            }
                        }
                    } else if (!loadingState && searchQuery.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.sin_resultados) + " para '$searchQuery'.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    } else if (!loadingState) {
                        item {
                            Text(
                                text = stringResource(R.string.no_eventos),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanCarouselItem(plan: Plan) {
    Card(
        modifier = Modifier
            .fillMaxSize()

            .clip(RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = 32.dp,
                bottomEnd = 32.dp
            )),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        AsyncImage(
            model = plan.imageUrl,
            contentDescription = plan.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
@Composable
fun CategoryBox(
    name: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(color, shape = RoundedCornerShape(22.dp)), // Más redondeado (píldora)
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp) // Más ancho
        )
    }
}