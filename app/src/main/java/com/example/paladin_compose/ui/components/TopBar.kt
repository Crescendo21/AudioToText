// TopBar.kt
package com.example.paladin_compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

@Composable
fun TopBar(
    title: String = "Rec your audio",
    backgroundColor: Color = Color(0xFF08D9D6),
    contentColor: Color = Color(0xFFEAEAEA),
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onSettingsClick),
            tint = contentColor
        )

        Text(
            text = title,
            color = contentColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Try",
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(end = 4.dp) // Ajout d'un padding à gauche du texte "Try"
            )

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 4.dp)
                    .background(color = contentColor, shape = CircleShape),
                contentAlignment = Alignment.Center // Centrer le contenu dans la Box
            ) {
                Text(
                    text = "3",
                    color = Color(0xFF252A34),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
