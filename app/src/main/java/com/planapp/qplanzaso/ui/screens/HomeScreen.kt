package com.planapp.qplanzaso.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding // 👈 Import necesario
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.data.repository.CategoriaRepository
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.Home
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.NavItem
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.Profile
import com.planapp.qplanzaso.ui.theme.BackgroundColor
import com.planapp.qplanzaso.ui.theme.DarkButton
import com.planapp.qplanzaso.ui.theme.LightButton
import com.planapp.qplanzaso.ui.theme.LightSelector
import com.planapp.qplanzaso.ui.theme.PrimaryColor

@Composable
fun HomeScreen(navController: NavController) {
    val navItemList = listOf(
        NavItem(label = "Calendario", icon = Icons.Default.CalendarMonth, route = "calendar"),
        NavItem(label = "Inicio", icon = Icons.Default.Home, route = "home"),
        NavItem(label = "Perfil", icon = Icons.Default.PeopleAlt, route = "profile")
    )

    var selectedItemIndex by remember { mutableStateOf(1) }
    val categoria = CategoriaRepository()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(), // 👈 protege contenido del notch si es necesario
        bottomBar = {
            NavigationBar(
                modifier = Modifier,
                tonalElevation = 8.dp,
                containerColor = Color.White
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    val selected = selectedItemIndex == index

                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedItemIndex = index },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label,
                                tint = if (selected) PrimaryColor else Color.Gray
                            )
                        },
                        label = {
                            Text(text = navItem.label)
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryColor,
                            selectedTextColor = PrimaryColor,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = LightSelector
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        ContentScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    // 👇 NO aplicamos bottom padding
                ),
            selectedItemIndex = selectedItemIndex,
            navController = navController
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedItemIndex: Int,
    navController: NavController
) {
    when (selectedItemIndex) {
        0 -> Calendar(modifier = modifier)
        1 -> Home(modifier = modifier, navController = navController)
        2 -> Profile(modifier = modifier, navController = navController)
    }
}

@Composable
fun Calendar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pantalla de Calendario")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}
