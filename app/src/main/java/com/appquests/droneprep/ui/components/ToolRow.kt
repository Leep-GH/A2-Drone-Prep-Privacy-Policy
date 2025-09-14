package com.appquests.droneprep.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.appquests.droneprep.ui.design.DS
import com.appquests.droneprep.ui.design.Palette

@Composable
fun ToolRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        color = Palette.Card,
        shape = RoundedCornerShape(DS.RadiusCard),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(horizontal = DS.SpaceLg, vertical = DS.SpaceMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon = icon, tint = iconTint, bgTint = iconTint)
            Spacer(Modifier.width(DS.SpaceLg))
            Column {
                Text(
                    title,
                    color = Palette.TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    subtitle,
                    color = Palette.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}