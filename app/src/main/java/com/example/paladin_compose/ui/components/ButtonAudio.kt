package com.example.compose.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecordButton(isRecording: Boolean, isPaused: Boolean, onClick: () -> Unit) {    val buttonSize = 212.dp
    val circleColors = listOf(
        Color(0xFF08D9D6).copy(alpha = 0.2f),
        Color(0xFF08D9D6).copy(alpha = 0.3f),
        Color(0xFF08D9D6).copy(alpha = 0.8f)
    )
    val circleSizes = listOf(212.dp, 139.dp, 115.dp)

    val isPressed = remember { mutableStateOf(false) }
    val pressedAnimationProgress = animateFloatAsState(if (isPressed.value) 1f else 0f).value

    Box(
        modifier = Modifier
            .size(buttonSize)
            .clickable(
                onClick = {
                    isPressed.value = !isPressed.value
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(buttonSize)) {
            val center = Offset(size.width / 2, size.height / 2)

            for (i in circleSizes.indices) {
                val radius = circleSizes[i].toPx() / 2
                drawCircle(
                    color = circleColors[i],
                    radius = radius,
                    center = center
                )
            }
        }

        Icon(
            imageVector = if (isRecording && !isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = "Record Icon",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}
