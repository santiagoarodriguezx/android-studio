package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
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

// Definir los colores personalizados
private val Primary = Color(0xFF6750A4) // Morado Material You
private val OnSurface = Color(0xFF1C1B1F) // Negro para texto

/**
 * Pantalla de Configuración - Diseño Moderno
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.1f),
                                Color.White
                            )
                        )
                    )
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Primary
                        )
                    }

                    Text(
                        text = "Configuración",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }
            }

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección: Apariencia
                SettingsSection(title = "Apariencia") {
                    // Tema oscuro/claro
                    SettingsSwitchCard(
                        icon = if (isDarkMode) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                        title = "Tema Oscuro",
                        subtitle = if (isDarkMode) "Activado" else "Desactivado",
                        iconColor = if (isDarkMode) Color(0xFF9C27B0) else Color(0xFFFFA726),
                        checked = isDarkMode,
                        onCheckedChange = onThemeChange
                    )

                    SettingsActionCard(
                        icon = Icons.Outlined.Palette,
                        title = "Color de Acento",
                        subtitle = "Morado",
                        iconColor = Primary,
                        onClick = { /* TODO: Color picker */ }
                    )
                }

                // Sección: Notificaciones
                SettingsSection(title = "Notificaciones") {
                    var notificationsEnabled by remember { mutableStateOf(true) }
                    var soundEnabled by remember { mutableStateOf(true) }
                    var vibrationEnabled by remember { mutableStateOf(false) }

                    SettingsSwitchCard(
                        icon = Icons.Outlined.Notifications,
                        title = "Notificaciones Push",
                        subtitle = "Recibir alertas en tiempo real",
                        iconColor = Color(0xFF2196F3),
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )

                    SettingsSwitchCard(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = "Sonido",
                        subtitle = "Reproducir sonido de notificaciones",
                        iconColor = Color(0xFF4CAF50),
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )

                    SettingsSwitchCard(
                        icon = Icons.Outlined.Vibration,
                        title = "Vibración",
                        subtitle = "Vibrar al recibir notificaciones",
                        iconColor = Color(0xFFFF9800),
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }

                // Sección: Privacidad
                SettingsSection(title = "Privacidad y Seguridad") {
                    SettingsActionCard(
                        icon = Icons.Outlined.Lock,
                        title = "Cambiar Contraseña",
                        subtitle = "Actualizar credenciales",
                        iconColor = Color(0xFFE91E63),
                        onClick = { /* TODO */ }
                    )

                    SettingsActionCard(
                        icon = Icons.Outlined.Security,
                        title = "Autenticación de Dos Factores",
                        subtitle = "Protege tu cuenta",
                        iconColor = Color(0xFF9C27B0),
                        onClick = { /* TODO */ }
                    )

                    SettingsActionCard(
                        icon = Icons.Outlined.PrivacyTip,
                        title = "Privacidad",
                        subtitle = "Gestionar datos personales",
                        iconColor = Color(0xFF607D8B),
                        onClick = { /* TODO */ }
                    )
                }

                // Sección: Acerca de
                SettingsSection(title = "Acerca de") {
                    SettingsActionCard(
                        icon = Icons.Outlined.Info,
                        title = "Versión",
                        subtitle = "1.0.0 (Build 1)",
                        iconColor = Color(0xFF795548),
                        onClick = { }
                    )

                    SettingsActionCard(
                        icon = Icons.Outlined.Description,
                        title = "Términos y Condiciones",
                        subtitle = "Leer términos de uso",
                        iconColor = Color(0xFF00BCD4),
                        onClick = { /* TODO */ }
                    )

                    SettingsActionCard(
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        title = "Ayuda y Soporte",
                        subtitle = "Centro de ayuda",
                        iconColor = Color(0xFF4CAF50),
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ver más",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitchCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        )
    }
}