package com.planapp.qplanzaso.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.planapp.qplanzaso.ui.theme.DarkButton
import com.planapp.qplanzaso.ui.theme.DarkGrayText

@Composable
fun QTopBarInicio(
    navController: NavController,
    modifier: Modifier = Modifier,
    title: String = "Planzaso",
    showBackButton: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // Botón de volver (si aplica)
        if (showBackButton) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = "❮",
                    fontSize = 18.sp,
                    color = DarkGrayText,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Logo + título centrado
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp, color = DarkGrayText)) {
                    append("Q")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkButton, fontSize = 32.sp)) {
                    append("´")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, color = DarkGrayText)) {
                    append("  $title")
                }
            },
            modifier = Modifier.align(Alignment.Center)
        )

        // 🔹 Botones al lado derecho
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { navController.navigate("favoritos") }
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favoritos",
                    tint = Color(0xFFFF5C8D)
                )
            }

            IconButton(
                onClick = { navController.navigate("notificaciones") }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQTopBarInicio() {
    QTopBarInicio(navController = rememberNavController())
}
