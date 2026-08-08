package com.example.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Screen

@Composable
fun PixelBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Pixel Pill Container
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .border(width = 2.5.dp, color = PixelDarkBorder, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item 1: Guided Path / Dashboard (Mosque Icon)
                val isDashboard = currentRoute == Screen.Dashboard.route
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isDashboard) PixelGoldAccent else Color.Transparent)
                        .clickable { onNavigate(Screen.Dashboard.route) },
                    contentAlignment = Alignment.Center
                ) {
                    PixelMosqueIcon(modifier = Modifier.size(24.dp))
                }

                // Item 2: Purity / Focus (Heart with Crescent)
                val isFocus = currentRoute == Screen.Focus.route
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isFocus) PixelGoldAccent else Color.Transparent)
                        .clickable { onNavigate(Screen.Focus.route) },
                    contentAlignment = Alignment.Center
                ) {
                    PixelHeartIcon(modifier = Modifier.size(26.dp), scaleFactor = 0.6f)
                }

                // Item 3: Apps / App Shield (Kaaba Box Icon)
                val isApps = currentRoute == Screen.Apps.route
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isApps) PixelGoldAccent else Color.Transparent)
                        .clickable { onNavigate(Screen.Apps.route) },
                    contentAlignment = Alignment.Center
                ) {
                    PixelKaabaIcon(modifier = Modifier.size(24.dp))
                }

                // Item 4: Settings / Engine (Gear Icon)
                val isPermissions = currentRoute == Screen.Permissions.route
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isPermissions) PixelGoldAccent else Color.Transparent)
                        .clickable { onNavigate(Screen.Permissions.route) },
                    contentAlignment = Alignment.Center
                ) {
                    PixelGearIcon(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
