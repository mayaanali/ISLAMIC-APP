package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Screen
import com.example.ui.theme.AlabasterSand
import com.example.ui.theme.DesertGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeumorphicBox
import com.example.ui.theme.NeumorphicInsetBox
import com.example.ui.theme.SlateBlue

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
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        NeumorphicBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            elevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .border(1.dp, SlateBlue.copy(alpha = 0.12f), RoundedCornerShape(28.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { screen ->
                        val isSelected = currentRoute == screen.route

                        val iconTint by animateColorAsState(
                            targetValue = if (isSelected) SlateBlue else SlateBlue.copy(alpha = 0.45f),
                            label = "IconTint"
                        )

                        if (isSelected) {
                            NeumorphicInsetBox(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { onNavigate(screen.route) },
                                shape = CircleShape,
                                glowColor = EmeraldGreen.copy(alpha = 0.3f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(EmeraldGreen.copy(alpha = 0.20f), CircleShape)
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
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
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { onNavigate(screen.route) }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
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
    }
}

