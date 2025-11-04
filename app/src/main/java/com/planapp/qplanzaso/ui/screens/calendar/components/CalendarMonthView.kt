package com.planapp.qplanzaso.ui.screens.calendar.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Composable
fun CalendarMonthView(
    calendarState: CalendarState,
    eventosPorDia: Map<String, List<Any>>,
    fechaSeleccionada: Date,
    onDayClick: (Date) -> Unit
) {
    val dateFormatKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    HorizontalCalendar(
        state = calendarState,
        dayContent = { day ->
            val dayDate = day.date.toJavaDate()
            val key = dateFormatKey.format(dayDate)
            val tieneEventos = eventosPorDia.containsKey(key)
            DayCell(
                day = day,
                isSelected = dateFormatKey.format(fechaSeleccionada) == key,
                isToday = Calendar.getInstance().let { c ->
                    dateFormatKey.format(c.time) == dateFormatKey.format(dayDate)
                },
                tieneEventos = tieneEventos,
                onClick = { onDayClick(dayDate) }
            )
        }
    )
}

@Composable
private fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean,
    tieneEventos: Boolean,
    onClick: () -> Unit
) {
    val purple = Color(0xFF6A1B9A)

    // 🔹 Animaciones suaves para color y tamaño
    val animatedColor by animateColorAsState(
        targetValue = when {
            isSelected -> PrimaryColor
            isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else -> Color.Transparent
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
    )

    val animatedSize by animateDpAsState(
        targetValue = if (isSelected) 44.dp else 36.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
    )

    // 🔹 Animación de rebote con escala
    val scale = remember { Animatable(1f) }

    // Se dispara cuando cambia el estado seleccionado
    LaunchedEffect(isSelected) {
        if (isSelected) {
            scale.animateTo(
                0.88f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            scale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    // 🔹 Efecto de elevación (sombra)
    val elevation = if (isSelected) 6.dp else 0.dp

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(44.dp)
            .scale(scale.value)
            .clickable(enabled = day.position == DayPosition.MonthDate) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (day.position == DayPosition.MonthDate) {
            Surface(
                shape = CircleShape,
                color = animatedColor,
                shadowElevation = elevation
            ) {
                Box(
                    modifier = Modifier
                        .size(animatedSize)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> Color.White
                            isToday -> MaterialTheme.colorScheme.primary
                            else -> Color.Black
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 🔹 Indicador de evento (punto morado)
            if (tieneEventos) {
                Box(
                    modifier = Modifier
                        .offset(y = 28.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(purple)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

/** Extension LocalDate -> java.util.Date */
private fun LocalDate.toJavaDate(): Date {
    val zdt = this.atStartOfDay(ZoneId.systemDefault())
    return Date.from(zdt.toInstant())
}
