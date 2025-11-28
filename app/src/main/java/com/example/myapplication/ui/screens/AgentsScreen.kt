package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.models.Agent
import com.example.myapplication.ui.components.ErrorBanner
import com.example.myapplication.ui.components.SuccessBanner
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.AgentsState
import com.example.myapplication.viewmodel.AgentsViewModel

/**
 * 🤖 Pantalla de Gestión de Agentes de IA - Diseño iOS Minimalista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentsScreen(
    viewModel: AgentsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val agents by viewModel.agents.collectAsState()
    val showOnlyActive by viewModel.showOnlyActive.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showReloadDialog by remember { mutableStateOf(false) }
    var agentToDelete by remember { mutableStateOf<Agent?>(null) }
    var agentToEdit by remember { mutableStateOf<Agent?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Observar cambios de estado para mostrar mensajes
    LaunchedEffect(state) {
        when (val currentState = state) {
            is AgentsState.AgentCreated -> {
                // No cerrar el diálogo aquí, lo maneja SuccessDialog
                successMessage = "✅ Agente '${currentState.agent.name}' creado exitosamente"
            }
            is AgentsState.AgentUpdated -> {
                // No cerrar el diálogo aquí, lo maneja SuccessDialog
                successMessage = "✅ Agente actualizado exitosamente"
            }
            is AgentsState.AgentDeleted -> {
                agentToDelete = null
                successMessage = currentState.message
            }
            is AgentsState.AgentsReloaded -> {
                showReloadDialog = false
                successMessage = currentState.message
            }
            is AgentsState.Error -> {
                // Cerrar todos los modales cuando hay error
                showCreateDialog = false
                agentToEdit = null
                showReloadDialog = false
                agentToDelete = null
                // Mostrar el mensaje de error
                errorMessage = currentState.message
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 2.dp,
                        spotColor = Color.Black.copy(alpha = 0.05f),
                        ambientColor = Color.Black.copy(alpha = 0.05f)
                    ),
                color = Surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Primary
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Agentes de IA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = OnSurface
                        )
                        Text(
                            "${agents.size} agentes configurados",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }

                    // Botón de filtro
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = CircleShape,
                                spotColor = if (showOnlyActive) Primary.copy(alpha = 0.3f) else Color.Transparent
                            )
                            .clickable { viewModel.toggleShowOnlyActive() },
                        shape = CircleShape,
                        color = if (showOnlyActive) Primary.copy(alpha = 0.15f) else SurfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (showOnlyActive) Icons.Default.FilterAlt else Icons.Outlined.FilterAltOff,
                                contentDescription = "Filtrar",
                                tint = if (showOnlyActive) Primary else OnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    // Botón de recargar
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(2.dp, CircleShape)
                            .clickable { showReloadDialog = true },
                        shape = CircleShape,
                        color = Success.copy(alpha = 0.15f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Recargar",
                                tint = Success,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state is AgentsState.Loading && agents.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            strokeWidth = 3.dp
                        )
                    }
                }

                agents.isEmpty() -> {
                    EmptyAgentsView(onCreateClick = { showCreateDialog = true })
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header con stats
                        item {
                            AgentsStatsCard(agents)
                        }

                        // Lista de agentes
                        items(agents) { agent ->
                            AgentCard(
                                agent = agent,
                                onEdit = { agentToEdit = agent },
                                onToggleStatus = { viewModel.toggleAgentStatus(agent.id, agent.isActive) },
                                onDelete = { agentToDelete = agent }
                            )
                        }

                        // Botón de crear agente al final
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(18.dp),
                                        spotColor = Primary.copy(alpha = 0.3f)
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { showCreateDialog = true },
                                shape = RoundedCornerShape(18.dp),
                                color = Primary
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Text(
                                            "Crear Nuevo Agente",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Espaciado final
                        item {
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                }
            }

            // Mostrar mensaje de éxito
            if (successMessage != null) {
                SuccessBanner(
                    message = successMessage!!,
                    onDismiss = {
                        successMessage = null
                        viewModel.clearState()
                    }
                )
            }

            // Mostrar mensaje de error
            if (errorMessage != null) {
                ErrorBanner(
                    message = errorMessage!!,
                    onDismiss = {
                        errorMessage = null
                        viewModel.clearState()
                    }
                )
            }
        }
    }

    // Dialogs
    if (showCreateDialog) {
        CreateAgentDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false }
        )
    }

    if (agentToEdit != null) {
        EditAgentDialog(
            agent = agentToEdit!!,
            viewModel = viewModel,
            onDismiss = { agentToEdit = null }
        )
    }

    if (showReloadDialog) {
        ReloadAgentsDialog(
            viewModel = viewModel,
            onDismiss = { showReloadDialog = false }
        )
    }

    if (agentToDelete != null) {
        DeleteAgentDialog(
            agent = agentToDelete!!,
            onConfirm = {
                viewModel.deleteAgent(agentToDelete!!.id)
                agentToDelete = null
            },
            onDismiss = { agentToDelete = null }
        )
    }
}

/**
 * Tarjeta de estadísticas de agentes
 */
@Composable
private fun AgentsStatsCard(agents: List<Agent>) {
    val activeCount = agents.count { it.isActive }
    val inactiveCount = agents.size - activeCount

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = Surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GradientStart.copy(alpha = 0.2f), GradientEnd.copy(alpha = 0.2f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📊", fontSize = 18.sp)
                }
                Text(
                    "Resumen",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = OnSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    label = "Total",
                    value = agents.size.toString(),
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Activos",
                    value = activeCount.toString(),
                    color = Success,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Inactivos",
                    value = inactiveCount.toString(),
                    color = OnSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = color.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Tarjeta de agente
 */
@Composable
private fun AgentCard(
    agent: Agent,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (expanded) 6.dp else 3.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(18.dp),
        color = Surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Icono con prioridad
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (agent.isActive) {
                                    Brush.linearGradient(
                                        colors = listOf(GradientStart, GradientEnd)
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(OnSurfaceVariant.copy(alpha = 0.3f), OnSurfaceVariant.copy(alpha = 0.2f))
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "#${agent.orderPriority}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            agent.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = OnSurface
                        )
                        Text(
                            agent.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            maxLines = if (expanded) Int.MAX_VALUE else 1
                        )
                    }
                }

                // Badge de estado
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (agent.isActive) Success.copy(alpha = 0.15f) else OnSurfaceVariant.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (agent.isActive) Success else OnSurfaceVariant)
                        )
                        Text(
                            if (agent.isActive) "Activo" else "Inactivo",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (agent.isActive) Success else OnSurfaceVariant
                        )
                    }
                }
            }

            // Contenido expandido
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = SurfaceVariant)

                    // Información técnica
                    InfoRow("Modelo", agent.model)
                    InfoRow("Temperature", agent.temperature.toString())
                    InfoRow("Max Tokens", agent.maxTokens.toString())

                    // Acciones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón editar
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onEdit() },
                            shape = RoundedCornerShape(12.dp),
                            color = Primary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Editar",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = Primary
                                )
                            }
                        }

                        // Botón toggle status
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onToggleStatus() },
                            shape = RoundedCornerShape(12.dp),
                            color = if (agent.isActive) Warning.copy(alpha = 0.15f) else Success.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (agent.isActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = if (agent.isActive) Warning else Success,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    if (agent.isActive) "Pausar" else "Activar",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = if (agent.isActive) Warning else Success
                                )
                            }
                        }

                        // Botón eliminar (solo si no es router)
                        if (agent.orderPriority != 1) {
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(2.dp, RoundedCornerShape(12.dp))
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { onDelete() },
                                shape = RoundedCornerShape(12.dp),
                                color = Error.copy(alpha = 0.15f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = OnSurface
        )
    }
}

/**
 * Vista cuando no hay agentes
 */
@Composable
private fun EmptyAgentsView(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("🤖", fontSize = 64.sp)
            Text(
                "No hay agentes configurados",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = OnSurface
            )
            Text(
                "Crea tu primer agente de IA",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Crear Agente")
            }
        }
    }
}


