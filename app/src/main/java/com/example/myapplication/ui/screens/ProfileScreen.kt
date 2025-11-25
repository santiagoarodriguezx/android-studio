package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
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
import com.example.myapplication.viewmodel.AuthViewModel
import java.util.Calendar

/**
 * Pantalla de Perfil - Diseño Moderno Minimalista
 *
 * Características:
 * - Fondo blanco limpio
 * - Tarjetas con sombras suaves
 * - Bordes redondeados
 * - Gradientes sutiles
 * - Encabezado con saludo personalizado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSecurity: () -> Unit = {},
    onNavigateToDevices: () -> Unit = {},
    onNavigateToLoginHistory: () -> Unit = {},
    onLogout: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    // Cargar datos del usuario
    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    // Determinar saludo según la hora
    val greeting = remember {
        val hour = try {
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        } catch (e: Exception) {
            12
        }
        when (hour) {
            in 0..11 -> "Buenos días"
            in 12..17 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header moderno con gradiente suave
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
            ) {
                Column {
                    // Botón de retroceso
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Saludo y foto de perfil
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar con gradiente
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.name?.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Saludo y nombre
                        Column {
                            Text(
                                text = greeting,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = currentUser?.name ?: "Usuario",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentUser?.email ?: "",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección: Información de la cuenta
                ProfileSection(title = "Información de la Cuenta") {
                    currentUser?.let { user ->
                        ProfileInfoCard(
                            icon = Icons.Outlined.Badge,
                            title = "Rol",
                            value = user.role ?: "Usuario",
                            iconColor = MaterialTheme.colorScheme.primary
                        )

                        ProfileInfoCard(
                            icon = Icons.Outlined.Business,
                            title = "Company ID",
                            value = user.companyId ?: "No asignado",
                            iconColor = MaterialTheme.colorScheme.secondary
                        )

                        ProfileInfoCard(
                            icon = Icons.Outlined.Email,
                            title = "Email",
                            value = user.email,
                            iconColor = Color(0xFF2196F3),
                            verified = user.emailVerified
                        )
                    }
                }

                // Sección: Seguridad
                ProfileSection(title = "Seguridad") {
                    ProfileActionCard(
                        icon = Icons.Outlined.Security,
                        title = "Autenticación de Dos Factores",
                        subtitle = if (currentUser?.twoFactorEnabled == true)
                            "Activado" else "Desactivado",
                        iconColor = if (currentUser?.twoFactorEnabled == true)
                            Color(0xFF4CAF50) else Color(0xFFFF9800),
                        onClick = onNavigateToSecurity
                    )

                    ProfileActionCard(
                        icon = Icons.Outlined.Devices,
                        title = "Dispositivos Confiables",
                        subtitle = "Gestionar dispositivos",
                        iconColor = Color(0xFF2196F3),
                        onClick = onNavigateToDevices
                    )

                    ProfileActionCard(
                        icon = Icons.Outlined.History,
                        title = "Historial de Acceso",
                        subtitle = "Ver actividad reciente",
                        iconColor = Color(0xFF9C27B0),
                        onClick = onNavigateToLoginHistory
                    )
                }

                // Sección: Más opciones
                ProfileSection(title = "Más Opciones") {
                    ProfileActionCard(
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        title = "Ayuda y Soporte",
                        subtitle = "Centro de ayuda",
                        iconColor = Color(0xFF607D8B),
                        onClick = { /* TODO */ }
                    )

                    ProfileActionCard(
                        icon = Icons.Outlined.Info,
                        title = "Acerca de",
                        subtitle = "Versión 1.0.0",
                        iconColor = Color(0xFF795548),
                        onClick = { /* TODO */ }
                    )
                }

                // Botón de cerrar sesión
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Componente: Sección del perfil con título
 */
@Composable
private fun ProfileSection(
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

/**
 * Componente: Card de información (no clicable)
 */
@Composable
private fun ProfileInfoCard(
    icon: ImageVector,
    title: String,
    value: String,
    iconColor: Color,
    verified: Boolean = false
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
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (verified) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verificado",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Componente: Card de acción (clicable)
 */
@Composable
private fun ProfileActionCard(
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

