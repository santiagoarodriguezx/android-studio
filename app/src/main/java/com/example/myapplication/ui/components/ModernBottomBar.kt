package com.example.myapplication.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*

/**
 * Barra de navegación inferior moderna con botón flotante
 *
 * Características:
 * - 4 elementos de navegación principales
 * - Botón flotante central para perfil
 * - Animaciones suaves
 * - Indicadores de selección
 */
@Composable
fun ModernBottomBar(
    selectedTab: BottomNavItem,
    onTabSelected: (BottomNavItem) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Barra de navegación principal
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp,
            tonalElevation = 3.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item 1: Dashboard
                BottomNavButton(
                    icon = if (selectedTab == BottomNavItem.Dashboard)
                        Icons.Filled.Dashboard
                    else
                        Icons.Outlined.Dashboard,
                    label = "Inicio",
                    isSelected = selectedTab == BottomNavItem.Dashboard,
                    onClick = { onTabSelected(BottomNavItem.Dashboard) }
                )

                // Item 2: Analytics
                BottomNavButton(
                    icon = if (selectedTab == BottomNavItem.Analytics)
                        Icons.Filled.BarChart
                    else
                        Icons.Outlined.BarChart,
                    label = "Analíticas",
                    isSelected = selectedTab == BottomNavItem.Analytics,
                    onClick = { onTabSelected(BottomNavItem.Analytics) }
                )

                // Espacio para el botón flotante
                Spacer(modifier = Modifier.width(80.dp))

                // Item 3: Messages
                BottomNavButton(
                    icon = if (selectedTab == BottomNavItem.Messages)
                        Icons.Filled.Message
                    else
                        Icons.Outlined.Message,
                    label = "Mensajes",
                    isSelected = selectedTab == BottomNavItem.Messages,
                    onClick = { onTabSelected(BottomNavItem.Messages) }
                )

                // Item 4: Settings
                BottomNavButton(
                    icon = if (selectedTab == BottomNavItem.Settings)
                        Icons.Filled.Settings
                    else
                        Icons.Outlined.Settings,
                    label = "Ajustes",
                    isSelected = selectedTab == BottomNavItem.Settings,
                    onClick = { onTabSelected(BottomNavItem.Settings) }
                )
            }
        }

        // Botón flotante central (Perfil)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
        ) {
            FloatingActionButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = Primary.copy(alpha = 0.5f)
                    ),
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Primary, Secondary)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Botón individual de la barra de navegación
 */
@Composable
private fun BottomNavButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) Primary else OnSurfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(300),
        label = "iconColor"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    Column(
        modifier = Modifier
            .width(72.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isSelected) {
                    Modifier.background(Primary.copy(alpha = 0.1f))
                } else {
                    Modifier
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = animatedColor,
                    modifier = Modifier.size(24.dp)
                )

                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = animatedColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Enum para los items de navegación
 */
enum class BottomNavItem {
    Dashboard,
    Analytics,
    Messages,
    Settings
}

