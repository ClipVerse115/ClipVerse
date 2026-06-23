package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.HotPink
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlassyCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color(0x33ffffff),
    backgroundColor: Color = Color(0x1AFFFFFF),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundGlow")
    
    val angle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Angle1"
    )

    val angle2 by infiniteTransition.animateFloat(
        initialValue = PI.toFloat(),
        targetValue = 3 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Angle2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicBlack)
    ) {
        // Glowing animated space blobs
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(90.dp)
        ) {
            val width = size.width
            val height = size.height

            if (width > 0 && height > 0) {
                // Purple Blob moving in a circle
                val c1X = (width / 2) + (cos(angle1) * (width * 0.3f))
                val c1Y = (height / 2) + (sin(angle1) * (height * 0.2f))
                drawGlowCircle(
                    center = Offset(c1X, c1Y),
                    radius = width * 0.5f,
                    color = CyberPurple.copy(alpha = 0.15f)
                )

                // Hot Pink Blob moving oppositely
                val c2X = (width / 2) + (cos(angle2) * (width * 0.25f))
                val c2Y = (height / 3) + (sin(angle2) * (height * 0.3f))
                drawGlowCircle(
                    center = Offset(c2X, c2Y),
                    radius = width * 0.45f,
                    color = HotPink.copy(alpha = 0.12f)
                )

                // Cyber Cyan Blob in bottom center
                drawGlowCircle(
                    center = Offset(width * 0.5f, height * 0.85f),
                    radius = width * 0.4f,
                    color = CyberCyan.copy(alpha = 0.08f)
                )
            }
        }

        // Overlay with a subtle dark grid / noise mesh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CosmicBlack.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
            content()
        }
    }
}

private fun DrawScope.drawGlowCircle(center: Offset, radius: Float, color: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color, Color.Transparent),
            center = center,
            radius = radius
        ),
        center = center,
        radius = radius
    )
}
