package com.planapp.qplanzaso.ui.screens.bottomNavigationMod.detailEvent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.planapp.qplanzaso.ui.components.RatingBar
import com.planapp.qplanzaso.ui.theme.PrimaryColor
import com.planapp.qplanzaso.R

@Composable
fun RatingCommentSection(
    initialRating: Int = 0,
    hasUserRated: Boolean,
    onRatingChange: (Int) -> Unit = {},
    onCommentChange: (String) -> Unit = {},
    onSendRating: (Int, String) -> Unit = { _, _ -> }
) {
    var selectedRating by remember { mutableStateOf(initialRating.coerceIn(0, 5)) }
    var commentText by remember { mutableStateOf("") }

    val showInputArea = selectedRating > 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (hasUserRated) {
            // Ya calificó. Muestra la calificación final.
            Text(
                stringResource(id = R.string.rating_thank_you),
                style = MaterialTheme.typography.titleMedium
            )
            RatingBar(initialRating = initialRating, onRatingChanged = {})

        } else {
            // Formulario activo.
            Text(
                text = if (selectedRating == 0)
                    stringResource(id = R.string.rating_select_star)
                else
                    stringResource(id = R.string.rating_your_rating),
                style = MaterialTheme.typography.titleMedium
            )

            // --- 1. RatingBar ---
            RatingBar(
                initialRating = selectedRating,
                onRatingChanged = { newRating ->
                    selectedRating = newRating
                    onRatingChange(newRating)
                }
            )

            // --- 2. Comentarios y Botón Condicional ---
            if (showInputArea) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = {
                        commentText = it
                        onCommentChange(it)
                    },
                    label = { Text(stringResource(id = R.string.rating_comment_label)) },
                    placeholder = { Text(stringResource(id = R.string.rating_comment_placeholder)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    singleLine = false,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onSendRating(selectedRating, commentText) },
                        enabled = selectedRating > 0,
                        modifier = Modifier.wrapContentWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        Text(stringResource(id = R.string.rating_send_button), fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingCommentSectionPreview() {
    Column {
        Text(
            stringResource(id = R.string.rating_preview_not_rated),
            modifier = Modifier.padding(16.dp)
        )
        RatingCommentSection(initialRating = 0, hasUserRated = false)

        Divider(Modifier.padding(vertical = 16.dp))

        Text(
            stringResource(id = R.string.rating_preview_rated),
            modifier = Modifier.padding(16.dp)
        )
        RatingCommentSection(initialRating = 4, hasUserRated = true)
    }
}
