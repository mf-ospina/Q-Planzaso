package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Timestamp // Se asume que este import es necesario para la lógica de filtros

// ----------------------------------------------------------------------
//  Colores personalizados para la UI
// ----------------------------------------------------------------------

val PlanzasoPrimary = Color(0xFFE9652D) // Naranja principal para botones
val PlanzasoSelectedChipBackground = Color(0xFFD3D3D3) // Fondo gris claro para chips seleccionados
val PlanzasoChipSelectedText = Color.Black
val PlanzasoChipUnselectedBackground = Color(0xFFF0F0F0)

val PlanzasoVibraTranquilo = Color(0xFFADD8E6)
val PlanzasoVibraFiesta = Color(0xFFB1F7B1).copy(red = 0.7f, green = 0.7f, blue = 0.3f)
val PlanzasoVibraCultural = Color(0xFFF7B1C6)
val PlanzasoCategoriaConcierto = Color(0xFFF7B1C6)
val PlanzasoCategoriaTeatro = Color(0xFFAEC6E4)
val PlanzasoCategoriaGastronomia = Color(0xFFE9652D)
val PlanzasoSliderActive = PlanzasoPrimary
val PlanzasoSliderInactive = Color(0xFFE0E0E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AdvancedFiltersScreen(
    onDismiss: () -> Unit,
    // Aquí puedes añadir parámetros para pasar el estado inicial o el ViewModel.
) {

    var selectedDate by remember { mutableStateOf("Hoy") }
    var selectedPrice by remember { mutableStateOf("Gratis") }
    var distanceRange by remember { mutableStateOf(5f) } // 1km a 10km (0f..9f => 1km..10km)
    var selectedVibra by remember { mutableStateOf(listOf("Tranquilo")) }
    var selectedCategories by remember { mutableStateOf(listOf("Concierto")) }
    var showOnlyVerified by remember { mutableStateOf(false) }

    // ➡Lógica para aplicar filtros y recolectar el estado ⬅
    val applyFiltersAction: () -> Unit = {

        // Ejemplo de recolección de datos:
        val filters = mapOf(
            "fecha" to selectedDate,
            "precio" to selectedPrice,
            "distancia" to "${distanceRange.toInt()}km",
            "vibra" to selectedVibra,
            "categorias" to selectedCategories,
            "verificados" to showOnlyVerified
        )
        println("Filtros aplicados: $filters")

        onDismiss() // Cierra el modal después de aplicar
    }

    // ➡️ Lógica para reiniciar filtros ⬅️
    val resetFiltersAction: () -> Unit = {
        selectedDate = "Hoy"
        selectedPrice = "Gratis"
        distanceRange = 5f
        selectedVibra = emptyList()
        selectedCategories = emptyList()
        showOnlyVerified = false
        println("Filtros Reiniciados")
    }

    Scaffold(
        topBar = {
            // Header con el título y el botón de retroceso que cierra el ModalBottomSheet
            TopAppBar(
                title = { Text("Filtros avanzados", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver/Cerrar")
                    }
                }
            )
        },
        bottomBar = {
            // Barra inferior con los botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón Reiniciar
                Button(
                    onClick = resetFiltersAction,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlanzasoPrimary)
                ) {
                    Text("Reiniciar", color = Color.White, fontSize = 16.sp)
                }
                // Botón Aplicar Filtros
                Button(
                    onClick = applyFiltersAction,
                    modifier = Modifier.weight(1f).height(50.dp),
                    // Usar un tono ligeramente más claro, replicando la imagen
                    colors = ButtonDefaults.buttonColors(containerColor = PlanzasoPrimary.copy(alpha = 0.85f))
                ) {
                    Text("Aplicar Filtros", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla: Filtros agrupados
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()) // Permite el scroll de la lista de filtros
        ) {

            // --- FILTRO: Fecha ---
            FilterSectionTitle(title = "Fecha")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DateChip(text = "Hoy", isSelected = selectedDate == "Hoy", onClick = { selectedDate = "Hoy" })
                DateChip(text = "Mañana", isSelected = selectedDate == "Mañana", onClick = { selectedDate = "Mañana" })
                DateDropdownChip(text = "Mañana", onClick = { /* Abrir selector de fechas */ })
            }
            HorizontalDivider()

            // --- FILTRO: Precio ---
            FilterSectionTitle(title = "Precio")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriceChip(text = "Gratis", isSelected = selectedPrice == "Gratis", onClick = { selectedPrice = "Gratis" })
                PriceChip(text = "Economico", isSelected = selectedPrice == "Economico", onClick = { selectedPrice = "Economico" })
                PriceChip(text = "Caro", isSelected = selectedPrice == "Caro", onClick = { selectedPrice = "Caro" })
            }
            HorizontalDivider()

            // --- FILTRO: Distancia ---
            FilterSectionTitle(title = "Distancia")
            DistanceSlider(currentDistance = distanceRange, onValueChange = { distanceRange = it })
            HorizontalDivider()

            // --- FILTRO: Vibra ---
            FilterSectionTitle(title = "Vibra")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VibraChip(text = "Tranquilo", isSelected = selectedVibra.contains("Tranquilo"), backgroundColor = PlanzasoVibraTranquilo, onClick = { toggleSelection(selectedVibra, "Tranquilo") { selectedVibra = it } })
                VibraChip(text = "Fiesta", isSelected = selectedVibra.contains("Fiesta"), backgroundColor = PlanzasoVibraFiesta, onClick = { toggleSelection(selectedVibra, "Fiesta") { selectedVibra = it } })
                VibraChip(text = "Cultural", isSelected = selectedVibra.contains("Cultural"), backgroundColor = PlanzasoVibraCultural, onClick = { toggleSelection(selectedVibra, "Cultural") { selectedVibra = it } })
            }
            HorizontalDivider()

            // --- FILTRO: Categorias ---
            FilterSectionTitle(title = "Categorias")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VibraChip(text = "Concierto", isSelected = selectedCategories.contains("Concierto"), backgroundColor = PlanzasoCategoriaConcierto, onClick = { toggleSelection(selectedCategories, "Concierto") { selectedCategories = it } })
                VibraChip(text = "Teatro", isSelected = selectedCategories.contains("Teatro"), backgroundColor = PlanzasoCategoriaTeatro, onClick = { toggleSelection(selectedCategories, "Teatro") { selectedCategories = it } })
                VibraChip(text = "Gastronomia", isSelected = selectedCategories.contains("Gastronomia"), backgroundColor = PlanzasoCategoriaGastronomia, onClick = { toggleSelection(selectedCategories, "Gastronomia") { selectedCategories = it } })
            }
            HorizontalDivider()

            // --- FILTRO: Mostrar solo eventos verificados (Switch) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mostrar solo eventos verificados",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                // Switch que controla el estado booleano
                Switch(
                    checked = showOnlyVerified,
                    onCheckedChange = { showOnlyVerified = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PlanzasoPrimary,
                        uncheckedThumbColor = Color.Gray.copy(alpha = 0.7f),
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ----------------------------------------------------------------------
// 📦 Componentes Reutilizables de apoyo
// ----------------------------------------------------------------------

@Composable
        /** Título para cada sección de filtros. */
fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
        /** Chip genérico para filtros de selección única (Fecha, Precio). */
fun DateChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PlanzasoSelectedChipBackground else PlanzasoChipUnselectedBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = PlanzasoChipSelectedText, fontSize = 14.sp)
    }
}

@Composable
        /** Chip para el filtro de precio (reutiliza el estilo de DateChip). */
fun PriceChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    DateChip(text = text, isSelected = isSelected, onClick = onClick)
}

@Composable
        /** Chip con icono desplegable para seleccionar fecha. */
fun DateDropdownChip(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PlanzasoChipUnselectedBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = PlanzasoChipSelectedText, fontSize = 14.sp)
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Desplegar", modifier = Modifier.size(20.dp), tint = Color.Black)
    }
}

@Composable
        /** Chip para filtros de multiselección (Vibra, Categorias). */
fun VibraChip(text: String, isSelected: Boolean, backgroundColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor.copy(alpha = if (isSelected) 1f else 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = PlanzasoChipSelectedText, fontSize = 14.sp)
    }
}

@Composable
        /** Slider para la selección de distancia (1km a 10km). */
fun DistanceSlider(currentDistance: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Slider(
            value = currentDistance,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8, // Permite valores enteros de 1 a 10
            colors = SliderDefaults.colors(thumbColor = PlanzasoSliderActive, activeTrackColor = PlanzasoSliderActive, inactiveTrackColor = PlanzasoSliderInactive)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "1 km", color = Color.Gray)
            Text(text = "${currentDistance.toInt()} km", fontWeight = FontWeight.Bold, color = PlanzasoPrimary)
            Text(text = "10 km", color = Color.Gray)
        }
    }
}

/** Función auxiliar para manejar la multiselección de chips (añadir/quitar elementos). */
fun toggleSelection(currentList: List<String>, item: String, onListUpdated: (List<String>) -> Unit) {
    val newList = if (currentList.contains(item)) {
        currentList.filter { it != item }
    } else {
        currentList + item
    }
    onListUpdated(newList)
}

@Composable
        /** Divisor horizontal estilizado. */
fun HorizontalDivider() {
    Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFE0E0E0))
}
@Preview(showBackground = true)
@Composable
fun PreviewAdvancedFiltersScreen() {
    // El NavController no se usa, pero se mantiene para simular el contexto
    AdvancedFiltersScreen(onDismiss = {})
}