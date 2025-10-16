package com.planapp.qplanzaso.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.Home
import com.planapp.qplanzaso.ui.screens.bottomNavigationMod.NavItem
import com.planapp.qplanzaso.ui.screens.profile.Profile
import com.planapp.qplanzaso.ui.screens.profile.ProfileFormState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    startTab: String? = null
) {
    val navItemList = listOf(
        NavItem(label = "Calendario", icon = Icons.Default.CalendarMonth, route = "calendar"),
        NavItem(label = "Inicio", icon = Icons.Default.Home, route = "home"),
        NavItem(label = "Perfil", icon = Icons.Default.PeopleAlt, route = "profile")
    )

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(1) }

    LaunchedEffect(startTab) {
        selectedItemIndex = when (startTab) {
            "calendar" -> 0
            "home" -> 1
            "profile" -> 2
            else -> selectedItemIndex
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        icon = { Icon(imageVector = navItem.icon, contentDescription = navItem.label) },
                        label = { Text(text = navItem.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            selectedItemIndex = selectedItemIndex,
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedItemIndex: Int,
    navController: NavController
) {
    when (selectedItemIndex) {
        0 -> Calendar(modifier = modifier)
        1 -> Home(modifier = modifier, navController = navController)
        2 -> Profile(
            modifier = modifier,
            onBack = {
                navController.navigate("home?tab=home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            },
            onEdit = { form: ProfileFormState ->
                navController.currentBackStackEntry?.savedStateHandle?.set("profile_form", form)
                navController.navigate("edit_profile")
            }
        )
    }
}

@Composable
fun Calendar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pantalla de Calendario")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}
