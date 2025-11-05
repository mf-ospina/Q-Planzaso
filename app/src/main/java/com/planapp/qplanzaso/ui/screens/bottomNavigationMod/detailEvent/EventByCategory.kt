package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.ui.components.SearchComponent
import com.planapp.qplanzaso.ui.viewModel.EventoViewModel
import com.planapp.qplanzaso.ui.components.EventCard
import com.google.firebase.Timestamp
import com.google.gson.GsonBuilder
import com.planapp.qplanzaso.ui.components.QTopBar
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.TimestampTypeAdapter

val FilterButtonColor = Color(0xFFFFA500).copy(alpha = 0.8f)
val FilterTextColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventByCategory(
    navController: NavHostController,
    categoryId: String,
    categoryName: String? = null
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }

    // 1. Obtener el ViewModel y Observar los estados específicos de categoría
    val eventoViewModel: EventoViewModel = viewModel()
    val eventosDeCategoria by eventoViewModel.eventosPorCategoria.collectAsStateWithLifecycle()
    val loadingState by eventoViewModel.loadingCategoria.collectAsStateWithLifecycle()
    val errorState by eventoViewModel.errorCategoria.collectAsStateWithLifecycle()

    // 2. Cargar los datos al entrar en la pantalla
    LaunchedEffect(key1 = categoryId) {
        eventoViewModel.cargarEventosPorCategoria(categoryId)
    }

    // Decodificar el nombre de la categoría para el título
    val decodedCategoryName = remember(categoryName) {
        categoryName?.let {
            java.net.URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
        } ?: "Eventos"
    }

    val searchPlaceholder = remember(decodedCategoryName) {
        "Buscar en: $decodedCategoryName"
    }

    // Filtrar la lista de eventos
    val eventosFiltrados by remember(eventosDeCategoria, searchText) {
        derivedStateOf {
            if (searchText.isBlank()) {
                eventosDeCategoria
            } else {
                val query = searchText.trim().lowercase()
                eventosDeCategoria.filter { evento ->
                    val nombreBusqueda = evento.nombre?.lowercase() ?: ""
                    val descripcionBusqueda = evento.descripcion?.lowercase() ?: ""
                    nombreBusqueda.contains(query) || descripcionBusqueda.contains(query)
                }
            }
        }
    }


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // TopBar
            QTopBar(navController = navController, title = decodedCategoryName )

            Spacer(modifier = Modifier.height(1.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ){
            // 3. Barra de búsqueda
            SearchComponent(
                modifier = Modifier.fillMaxWidth(),
                searchText = searchText,
                onSearchTextChanged = { searchText = it },
                placeholderText = searchPlaceholder
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Fila: Texto "Ordenar:" y Botón "Filtros (3)"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ordenar:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                FilterButton(
                    count = 3,
                    onClick = { showFilterSheet = true } // Mostrar el sheet
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            OrderSelector()
            Spacer(modifier = Modifier.height(12.dp))
            Divider(thickness = 1.dp, color = Color.LightGray)

            // 5. Lista de Eventos Filtrados por Categoría
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (loadingState) {
                    item {
                        Box(modifier = Modifier.fillParentMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (errorState != null) {
                    item {
                        Text(
                            text = "Error de carga: ${errorState!!}",
                            modifier = Modifier.fillParentMaxWidth().padding(32.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else if (eventosFiltrados.isNotEmpty()) {
                    items(
                        items = eventosFiltrados,
                        key = { evento -> evento.id ?: evento.nombre ?: System.currentTimeMillis() }
                    ) { evento ->
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                            EventCard(evento = evento) {
                                // Lógica de navegación al detalle
                                val gson = GsonBuilder()
                                    .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                                    .create()

                                val eventoJson = gson.toJson(evento)
                                val encodedJson = URLEncoder.encode(eventoJson, StandardCharsets.UTF_8.toString())
                                navController.navigate("detailEvent/${encodedJson}")
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "No se encontraron eventos para esta búsqueda.",
                            modifier = Modifier.fillParentMaxWidth().padding(32.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

    // ModalBottomSheet para llamar a AdvancedFiltersScreen
    if (showFilterSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        )
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight(0.95f),
        ) {
            AdvancedFiltersScreen(
                onDismiss = { showFilterSheet = false }
            )
        }
    }}
}

// ----------------------------------------------------------------------
// Componente OrderSelector (sin cambios)
// ----------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSelector() {
    val sortingOptions = listOf("Relevancia", "Fecha (Próximos)", "Precio (Menor a Mayor)", "A-Z")
    var isExpanded by remember { mutableStateOf(false) }
    var selectedOption by rememberSaveable { mutableStateOf(sortingOptions[0]) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Ordenar por",
                    modifier = Modifier.menuAnchor()
                )
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color(0xFFF0F0F0),
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            sortingOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}


// ----------------------------------------------------------------------
// Componente Reutilizable para el botón de Filtros (sin cambios)
// ----------------------------------------------------------------------

@Composable
fun FilterButton(
    count: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = FilterButtonColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Filtros ($count)",
            color = FilterTextColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventByCategory() {
    val mockNavController = rememberNavController()
    EventByCategory(navController = mockNavController, categoryId = "demo", categoryName = "Concierto%20de%20Rock")
}