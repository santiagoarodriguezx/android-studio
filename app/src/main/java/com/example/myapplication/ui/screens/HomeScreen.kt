package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla Principal - Diseño Futurista estilo iOS
 * 
 * Dashboard con acceso rápido a todas las funcionalidades
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToMessageLogs: () -> Unit = {},
    onNavigateToProducts: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Animación de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
        viewModel.loadCurrentUser()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.03f),
                        Surface,
                        Secondary.copy(alpha = 0.02f)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header personalizado
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hola,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = currentUser?.name?.split(" ")?.firstOrNull() ?: "Usuario",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón de logout
                        IconButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Error.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Logout,
                                contentDescription = "Cerrar Sesión",
                                tint = Error
                            )
                        }

                        // Avatar clicable para ver perfil
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Primary, Secondary)
                                    )
                                )
                                .clickable { showProfileDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.name?.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Contenido principal
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                // Tarjeta de bienvenida
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -40 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn()
                    ) {
                        WelcomeCard(userName = currentUser?.name ?: "Usuario")
                    }
                }

                // Acciones Rápidas
                item {
                    Text(
                        text = "⚡ Acciones Rápidas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    QuickActionsRow(
                        onAnalyticsClick = onNavigateToAnalytics,
                        onProductsClick = onNavigateToProducts,
                        onMessagesClick = onNavigateToMessageLogs,
                        onSettingsClick = onNavigateToSettings
                    )
                }

                // Actividad Reciente (reemplaza "Progreso del mes")
                item {
                    Text(
                        text = "📊 Actividad Reciente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    RecentActivityScroll()
                }

                // Grid de funcionalidades principales
                item {
                    Text(
                        text = "Módulos Principales",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Analytics
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -100 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ) + fadeIn()
                    ) {
                        FeatureCard(
                            icon = Icons.Outlined.Analytics,
                            title = "Analytics Dashboard",
                            description = "Métricas y estadísticas en tiempo real",
                            gradient = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2)
                            ),
                            onClick = onNavigateToAnalytics
                        )
                    }
                }

                // Productos (NUEVO)
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { 100 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ) + fadeIn()
                    ) {
                        FeatureCard(
                            icon = Icons.Outlined.Inventory,
                            title = "Gestión de Productos",
                            description = "Administra tu inventario y productos",
                            gradient = listOf(
                                Color(0xFF4CAF50),
                                Color(0xFF8BC34A)
                            ),
                            onClick = onNavigateToProducts
                        )
                    }
                }

                // Message Logs
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -100 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ) + fadeIn()
                    ) {
                        FeatureCard(
                            icon = Icons.Outlined.Message,
                            title = "Message Logs",
                            description = "Historial de mensajes y conversaciones",
                            gradient = listOf(
                                Color(0xFFf093fb),
                                Color(0xFFf5576c)
                            ),
                            onClick = onNavigateToMessageLogs
                        )
                    }
                }

                // Información de cuenta
                item {
                    Text(
                        text = "Mi Cuenta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    AccountInfoCard(
                        email = currentUser?.email ?: "",
                        role = currentUser?.role ?: "Usuario",
                        emailVerified = currentUser?.emailVerified ?: false,
                        twoFactorEnabled = currentUser?.twoFactorEnabled ?: false,
                        onViewProfile = { showProfileDialog = true }
                    )
                }
            }
        }

        // Dialog de confirmación de logout
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                icon = {
                    Icon(
                        Icons.Outlined.Logout,
                        contentDescription = null,
                        tint = Error,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = { Text("Cerrar Sesión") },
                text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                showLogoutDialog = false
                                viewModel.logout()
                                onLogout()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Error
                        )
                    ) {
                        Text("Salir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Modal de perfil
        if (showProfileDialog) {
            ModalBottomSheet(
                onDismissRequest = { showProfileDialog = false },
                containerColor = Surface
            ) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = { showProfileDialog = false },
                    onLogout = {
                        showProfileDialog = false
                        showLogoutDialog = true
                    }
                )
            }
        }
    }
}

/**
 * Card de Bienvenida
 */
@Composable
private fun WelcomeCard(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Primary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bienvenido de vuelta,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Text(
                    text = userName.split(" ").firstOrNull() ?: "Usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gestiona tu cuenta y accede a todas las funcionalidades",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Outlined.WavingHand,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

/**
 * Card de Funcionalidad
 */
@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Abrir",
                tint = OnSurfaceVariant
            )
        }
    }
}

/**
 * Card de Información de Cuenta
 */
@Composable
private fun AccountInfoCard(
    email: String,
    role: String,
    emailVerified: Boolean,
    twoFactorEnabled: Boolean,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewProfile),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Ver más",
                    tint = OnSurfaceVariant
                )
            }

            // Email
            InfoRow(
                icon = Icons.Outlined.Email,
                label = "Email",
                value = email,
                verified = emailVerified
            )

            // Rol
            InfoRow(
                icon = Icons.Outlined.Badge,
                label = "Rol",
                value = role
            )

            // 2FA Status
            InfoRow(
                icon = Icons.Outlined.Security,
                label = "Autenticación 2FA",
                value = if (twoFactorEnabled) "Activada" else "Desactivada",
                valueColor = if (twoFactorEnabled) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }
    }
}

/**
 * Fila de información
 */
@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    verified: Boolean = false,
    valueColor: Color = OnSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(20.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = valueColor
                )
                if (verified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verificado",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Acciones Rápidas en fila horizontal
 */
@Composable
private fun QuickActionsRow(
    onAnalyticsClick: () -> Unit,
    onProductsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            icon = Icons.Outlined.Analytics,
            label = "Analytics",
            color = Color(0xFF667eea),
            onClick = onAnalyticsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Outlined.Inventory,
            label = "Productos",
            color = Color(0xFF4CAF50),
            onClick = onProductsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Outlined.Message,
            label = "Mensajes",
            color = Color(0xFFf093fb),
            onClick = onMessagesClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Outlined.Settings,
            label = "Ajustes",
            color = Color(0xFF90CAF9),
            onClick = onSettingsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Botón de acción rápida
 */
@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = OnSurface
            )
        }
    }
}

/**
 * Scroll horizontal de actividad reciente
 */
@Composable
private fun RecentActivityScroll() {
    val recentActivities = remember {
        listOf(
            ActivityItem(
                title = "Nuevo producto agregado",
                description = "Laptop HP ProBook 450",
                time = "Hace 2 horas",
                icon = Icons.Outlined.Inventory,
                color = Color(0xFF4CAF50)
            ),
            ActivityItem(
                title = "Analytics actualizado",
                description = "150 mensajes hoy",
                time = "Hace 5 horas",
                icon = Icons.Outlined.Analytics,
                color = Color(0xFF667eea)
            ),
            ActivityItem(
                title = "Mensaje recibido",
                description = "Usuario preguntó por stock",
                time = "Hace 1 día",
                icon = Icons.Outlined.Message,
                color = Color(0xFFf093fb)
            ),
            ActivityItem(
                title = "Stock actualizado",
                description = "20 productos modificados",
                time = "Hace 2 días",
                icon = Icons.Outlined.Sync,
                color = Color(0xFFFF9800)
            )
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        items(recentActivities) { activity ->
            ActivityCard(activity)
        }
    }
}

/**
 * Tarjeta de actividad reciente
 */
@Composable
private fun ActivityCard(activity: ActivityItem) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(activity.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = null,
                    tint = activity.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    maxLines = 1
                )
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = activity.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Modelo de datos para actividad
 */
private data class ActivityItem(
    val title: String,
    val description: String,
    val time: String,
    val icon: ImageVector,
    val color: Color
)
