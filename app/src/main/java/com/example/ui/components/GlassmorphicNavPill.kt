package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.Screen

@Composable
fun GlassmorphicNavPill(
    currentRoute: String,
    items: List<Screen>,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Frosted Glass Container
        Surface(
            shape = CircleShape,
            color = Color(0xD9141624), // Semi-transparent deep indigo glass
            shadowElevation = 16.dp,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    shape = CircleShape
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF00CEC9) else Color.White.copy(alpha = 0.55f),
                        label = "IconTint"
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF00CEC9).copy(alpha = 0.18f)
                                else Color.Transparent
                            )
                            .clickable { onNavigate(screen.route) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
