package com.planapp.qplanzaso.ui.screens.bottomNavigationMod

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.planapp.qplanzaso.ui.components.CarouselView
import com.planapp.qplanzaso.ui.screens.home.CategoriaViewModel

// (Datos de ejemplo y PlanCarouselItem se mantienen igual)
data class Plan(val id: Int, val title: String)
val samplePlans = listOf(
    Plan(1, "Concierto Acústico"),
    Plan(2, "Feria Gastronómica"),
    Plan(3, "Cine al Aire Libre"),
    Plan(4, "Taller de Pintura"),
    Plan(5, "Yoga en el Parque")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel: CategoriaViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isActive by rememberSaveable { mutableStateOf(false) }

    val categoryColors = listOf(
        Color(0xFFF44336), Color(0xFF2196F3), Color(0xFFFF9800),
        Color(0xFF4CAF50), Color(0xFF9C27B0), Color(0xFF795548)
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()
            state.error != null -> Text(text = "Error: ${state.error}")
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .verticalScroll(rememberScrollState()),
                 
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Q'Planzaso",
                        
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        // AJUSTE: Reducimos un poco el padding superior para un mejor balance
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    )
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { isActive = false },
                        active = isActive,
                        onActiveChange = { it },
                        placeholder = { Text("Buscar planes y eventos...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        content = { /* ... */ },
                       
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                       
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp), 
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        items(state.categorias) { categoria ->
                            val color = categoryColors[state.categorias.indexOf(categoria) % categoryColors.size]
                            CategoryBox(
                                name = categoria.nombre,
                                color = color,
                                onClick = {
                                    navController.navigate("DetailEvent/${categoria.id}")
                                }
                            )
                        }
                    }

                    Text(
                        text = "Planes Destacados",
                        style = MaterialTheme.typography.titleLarge,
                        // AJUSTE: Padding consistente con los demás elementos
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    CarouselView(
                        items = samplePlans,
                        modifier = Modifier
                            .fillMaxWidth()
                           
                            .height(180.dp)
                    ) { index, plan ->
                        val color = categoryColors[index % categoryColors.size]
                        PlanCarouselItem(plan = plan, color = color)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


@Composable
fun PlanCarouselItem(plan: Plan, color: Color) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = plan.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun CategoryBox(
    name: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .size(width = 90.dp, height = 50.dp) // AJUSTE: Un poco más ancho para que el texto respire
            .background(color, shape = RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}
