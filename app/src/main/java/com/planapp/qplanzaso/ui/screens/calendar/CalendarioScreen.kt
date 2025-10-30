package com.planapp.qplanzaso.ui.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.planapp.qplanzaso.ui.screens.calendar.components.CalendarMonthView
import com.planapp.qplanzaso.ui.screens.calendar.components.EventItem
import com.planapp.qplanzaso.ui.viewModel.CalendarioViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*


import com.google.firebase.Timestamp
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent.TimestampTypeAdapter



@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTitleClick: (YearMonth) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(imageVector = Icons.Filled.ArrowBackIos, contentDescription = "Mes anterior")
        }

        // 🔹 Al hacer click sobre el título, abrimos el diálogo de selección
        Text(
            text = currentMonth.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clickable {
                onTitleClick(currentMonth)
            }
        )

        IconButton(onClick = onNextMonth) {
            Icon(imageVector = Icons.Filled.ArrowForwardIos, contentDescription = "Mes siguiente")
        }
    }
}

@Composable
fun MesAnioDialog(
    currentMonth: YearMonth,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val meses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    val currentYear = currentMonth.year
    val years = (currentYear - 1)..(currentYear + 1)

    var selectedMonth by remember { mutableStateOf(currentMonth.monthValue) }
    var selectedYear by remember { mutableStateOf(currentMonth.year) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar mes y año") },
        text = {
            Column {
                DropdownSelector(
                    label = "Mes",
                    options = meses,
                    selectedIndex = selectedMonth - 1,
                    onSelectedIndexChange = { selectedMonth = it + 1 }
                )

                Spacer(modifier = Modifier.height(8.dp))

                DropdownSelector(
                    label = "Año",
                    options = years.map { it.toString() },
                    selectedIndex = years.indexOf(selectedYear),
                    onSelectedIndexChange = { selectedYear = years.elementAt(it) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedYear, selectedMonth) }) {
                Text("Ir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Box {
            OutlinedTextField(
                value = options[selectedIndex],
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                enabled = false
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSelectedIndexChange(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    navController: NavController,
    viewModel: CalendarioViewModel = viewModel()
) {
    val eventosPorDia by viewModel.eventosPorDia.collectAsState()
    val fechaSeleccionada by viewModel.selectedDate.collectAsState()
    val eventosDelDia by viewModel.eventosDelDia.collectAsState()
    val eventosProximos by viewModel.eventosProximos.collectAsState()
    val eventosPasados by viewModel.eventosPasados.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val scope = rememberCoroutineScope()

    // ✅ Usamos java.time (desugaring activado)
    val currentMonth = YearMonth.now()
    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(24),
        endMonth = currentMonth.plusMonths(24),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.MONDAY
    )

    // 🔹 Cargar eventos
    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            viewModel.cargarEventosInscritos(uid)
        }
    }

    MaterialTheme(
        colorScheme = lightColorScheme() // para forzar el tema claro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                MesAnioDialog(
                    currentMonth = calendarState.firstVisibleMonth.yearMonth,
                    onDismiss = { showDialog = false },
                    onConfirm = { year, month ->
                        showDialog = false
                        scope.launch {
                            calendarState.animateScrollToMonth(YearMonth.of(year, month))
                        }
                    }
                )
            }

            CalendarHeader(
                currentMonth = calendarState.firstVisibleMonth.yearMonth,
                onPrevMonth = {
                    scope.launch {
                        calendarState.animateScrollToMonth(
                            calendarState.firstVisibleMonth.yearMonth.minusMonths(1)
                        )
                    }
                },
                onNextMonth = {
                    scope.launch {
                        calendarState.animateScrollToMonth(
                            calendarState.firstVisibleMonth.yearMonth.plusMonths(1)
                        )
                    }
                },
                onTitleClick = { showDialog = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Vista del calendario
            CalendarMonthView(
                calendarState = calendarState,
                eventosPorDia = eventosPorDia,
                fechaSeleccionada = fechaSeleccionada,
                onDayClick = { viewModel.seleccionarFecha(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Título dinámico
            val today = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val isHoy = dateFormat.format(today) == dateFormat.format(fechaSeleccionada)
            val titulo = if (isHoy) "Eventos para hoy" else "Eventos para este día"

            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // 🔹 Lista de eventos del día seleccionado
            when {
                error != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error ?: "Error desconocido", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid != null) viewModel.cargarEventosInscritos(uid)
                        }) {
                            Text("Reintentar")
                        }
                    }
                }

                loading -> CircularProgressIndicator()
                eventosDelDia.isEmpty() -> {
                    Text("No tienes eventos asignados para este día.", color = Color.Gray)
                }

                else -> {
                    eventosDelDia.forEach { ev ->
                        EventItem(evento = ev) {
                            val gson = GsonBuilder()
                                .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                                .create()
                            val json = gson.toJson(ev)
                            val encoded = URLEncoder.encode(json, "UTF-8")
                            navController.navigate("detailEvent/$encoded")

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Próximos eventos
            Text("Eventos próximos", style = MaterialTheme.typography.titleMedium)
            if (eventosProximos.isEmpty()) {
                Text("No hay eventos próximos.", color = Color.Gray)
            } else {
                eventosProximos.forEach { ev ->
                    EventItem(evento = ev) {
                        val gson = GsonBuilder()
                            .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                            .create()
                        val json = gson.toJson(ev)
                        val encoded = URLEncoder.encode(json, "UTF-8")
                        navController.navigate("detailEvent/$encoded")

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Pasados
            Text("Eventos pasados", style = MaterialTheme.typography.titleMedium)
            if (eventosPasados.isEmpty()) {
                Text("No hay eventos pasados.", color = Color.Gray)
            } else {
                eventosPasados.forEach { ev ->
                    EventItem(evento = ev) {
                        val gson = GsonBuilder()
                            .registerTypeAdapter(Timestamp::class.java, TimestampTypeAdapter())
                            .create()
                        val json = gson.toJson(ev)
                        val encoded = URLEncoder.encode(json, "UTF-8")
                        navController.navigate("detailEvent/$encoded")

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
