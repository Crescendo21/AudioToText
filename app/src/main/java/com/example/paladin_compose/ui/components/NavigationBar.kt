package com.example.paladin_compose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.md_theme_light_background
import com.example.compose.md_theme_light_onPrimary

@Composable
fun CustomNavigationBar(
    containerColor: Color = md_theme_light_background,
    contentColor: Color = md_theme_light_onPrimary,
    height: androidx.compose.ui.unit.Dp,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(25.dp),
        color = containerColor,
        elevation = 4.dp
    ) {
        BottomNavigation(
            backgroundColor = containerColor,
            contentColor = contentColor,
            elevation = 0.dp,
            content = content
        )
    }
}

@Composable
fun NavIconWithLabel(iconResource: Int, labelText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Ajouté
    ) {
        val iconPainter = painterResource(id = iconResource)
        Image(
            painter = iconPainter,
            contentDescription = labelText,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = labelText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption
        )
    }
}

