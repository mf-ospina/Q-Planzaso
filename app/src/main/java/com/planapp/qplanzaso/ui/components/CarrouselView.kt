package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable

fun <T> CarouselView(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (index: Int, item: T) -> Unit,
) {
    // Almacena el estado actual del carrusel, incluyendo el número de páginas.
    val pagerState = rememberPagerState(pageCount = { items.size })

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Muestra el carrusel horizontal.
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp
        ) { pageIndex ->
            // Renderiza el contenido definido por el usuario para cada página.
            itemContent(pageIndex, items[pageIndex])
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Muestra el indicador de posición del carrusel.
        PageIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
/** Indicador visual que muestra la página actual del carrusel. */
private fun PageIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    indicatorSize: Dp = 8.dp,
    spacing: Dp = 4.dp,
    activeColor: Color = Color.DarkGray,
    inactiveColor: Color = Color.LightGray
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { index ->
            val color = if (pagerState.currentPage == index) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}