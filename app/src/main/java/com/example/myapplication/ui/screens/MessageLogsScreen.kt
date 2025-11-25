package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.MessageLogsViewModel
import com.example.myapplication.viewmodel.MessageLogsState
import com.example.myapplication.data.models.MessageLog
import java.text.SimpleDateFormat
import java.util.*

/**
 * 💬 Message Logs Screen - Vista en Tiempo Real
 *
 * Muestra logs de mensajes con filtros, búsqueda y auto-refresh
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageLogsScreen(
    viewModel: MessageLogsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val logsState by viewModel.messageLogsState.collectAsState()
    val messageLogs by viewModel.recentLogs.collectAsState()
    val realtimeStats by viewModel.realtimeStats.collectAsState()

    var selectedMinutes by remember { mutableStateOf(30) }
    var showFilters by remember { mutableStateOf(false) }
    var autoRefresh by remember { mutableStateOf(true) }
    var selectedLog by remember { mutableStateOf<MessageLog?>(null) }

    // Auto-refresh cada 10 segundos
    LaunchedEffect(autoRefresh, selectedMinutes) {
        while (autoRefresh) {
            viewModel.getRecentMessageLogs(minutes = selectedMinutes)
            viewModel.getRealtimeStats(minutes = selectedMinutes)
            kotlinx.coroutines.delay(10000) // 10 segundos
        }
    }

    // Carga inicial
    LaunchedEffect(Unit) {
        viewModel.getRecentMessageLogs(minutes = selectedMinutes)
        viewModel.getRealtimeStats(minutes = selectedMinutes)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.History,
                            contentDescription = null,
                            tint = Primary
                        )
                        Text(
                            "Message Logs",
                            fontWeight = FontWeight.Bold
                        )

                        // Badge de auto-refresh
                        if (autoRefresh) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Success),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Toggle auto-refresh
                    IconButton(onClick = { autoRefresh = !autoRefresh }) {
                        Icon(
                            if (autoRefresh) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            "Auto-refresh",
                            tint = if (autoRefresh) Success else OnSurfaceVariant
                        )
                    }

                    // Filtros
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            Icons.Outlined.FilterList,
                            "Filtros",
                            tint = if (showFilters) Primary else OnSurfaceVariant
                        )
                    }

                    // Refrescar manual
                    IconButton(onClick = {
                        viewModel.getRecentMessageLogs(minutes = selectedMinutes)
                        viewModel.getRealtimeStats(minutes = selectedMinutes)
                    }) {
                        Icon(Icons.Outlined.Refresh, "Refrescar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            // Estadísticas en tiempo real
            realtimeStats?.let { stats ->
                RealtimeStatsBar(stats)
            }

            // Filtros expandibles
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FiltersSection(
                    selectedMinutes = selectedMinutes,
                    onMinutesChanged = {
                        selectedMinutes = it
                        viewModel.getRecentMessageLogs(minutes = it)
                    }
                )
            }

            // Lista de logs
            when {
                logsState is MessageLogsState.Loading && messageLogs.isEmpty() -> {
                    LoadingView()
                }

                logsState is MessageLogsState.Error && messageLogs.isEmpty() -> {
                    ErrorView(
                        message = (logsState as MessageLogsState.Error).message,
                        onRetry = {
                            viewModel.getRecentMessageLogs(minutes = selectedMinutes)
                        }
                    )
                }

                messageLogs.isEmpty() -> {
                    EmptyView()
                }

                else -> {
                    MessageLogsList(
                        logs = messageLogs,
                        isRefreshing = logsState is MessageLogsState.Loading,
                        onLogClick = { selectedLog = it }
                    )
                }
            }
        }
    }

    // Dialog de detalles
    selectedLog?.let { log ->
        LogDetailsDialog(
            log = log,
            onDismiss = { selectedLog = null },
            onDelete = {
                viewModel.deleteMessageLog(log.id)
                selectedLog = null
            }
        )
    }
}

@Composable
private fun RealtimeStatsBar(stats: com.example.myapplication.data.models.RealtimeStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Primary.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Outlined.Message,
                value = stats.totalMessages.toString(),
                label = "Mensajes",
                color = Primary
            )
            StatItem(
                icon = Icons.Outlined.People,
                value = stats.uniqueUsers.toString(),
                label = "Usuarios",
                color = Secondary
            )
            StatItem(
                icon = Icons.Outlined.Speed,
                value = "${stats.avgResponseTimeMs.toInt()}ms",
                label = "Resp.",
                color = Warning
            )
            StatItem(
                icon = Icons.Outlined.CheckCircle,
                value = "${stats.successRate.toInt()}%",
                label = "Éxito",
                color = Success
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = color
        )
        Text(
            label,
            fontSize = 11.sp,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun FiltersSection(
    selectedMinutes: Int,
    onMinutesChanged: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "⏱️ Ventana de Tiempo",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5, 15, 30, 60, 120).forEach { minutes ->
                    FilterChip(
                        selected = selectedMinutes == minutes,
                        onClick = { onMinutesChanged(minutes) },
                        label = {
                            Text(
                                if (minutes < 60) "${minutes}m" else "${minutes / 60}h",
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun MessageLogsList(
    logs: List<MessageLog>,
    isRefreshing: Boolean,
    onLogClick: (MessageLog) -> Unit
) {
    Column {
        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Primary
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(logs, key = { it.id }) { log ->
                MessageLogCard(
                    log = log,
                    onClick = { onLogClick(log) }
                )
            }
        }
    }
}

@Composable
private fun MessageLogCard(
    log: MessageLog,
    onClick: () -> Unit
) {
    val messageTypeColor = when (log.messageType) {
        "user" -> Primary
        "bot" -> Secondary
        "system" -> Warning
        else -> OnSurfaceVariant
    }

    val statusColor = when (log.status) {
        "success" -> Success
        "error" -> Error
        else -> Warning
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )

                    Text(
                        log.messageType.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = messageTypeColor
                    )

                    Text(
                        "•",
                        color = OnSurfaceVariant
                    )

                    Text(
                        log.userId.take(8),
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                }

                Text(
                    formatTime(log.createdAt),
                    fontSize = 11.sp,
                    color = OnSurfaceVariant
                )
            }

            // Contenido del mensaje
            if (!log.messageContent.isNullOrEmpty()) {
                Text(
                    log.messageContent,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = OnSurface
                )
            }

            // Footer con metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (log.intentDetected != null) {
                        MetadataChip(
                            icon = Icons.Outlined.Psychology,
                            text = log.intentDetected,
                            color = Secondary
                        )
                    }

                    if (log.responseTimeMs != null) {
                        MetadataChip(
                            icon = Icons.Outlined.Speed,
                            text = "${log.responseTimeMs}ms",
                            color = Warning
                        )
                    }
                }

                if (log.confidenceScore != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (log.confidenceScore > 0.7) Success else Warning
                        )
                        Text(
                            "${(log.confidenceScore * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (log.confidenceScore > 0.7) Success else Warning
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataChip(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = color
        )
        Text(
            text,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LogDetailsDialog(
    log: MessageLog,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.Info, null, tint = Primary)
                Text("Detalles del Log")
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DetailItem("ID", log.id)
                }
                item {
                    DetailItem("Usuario", log.userId)
                }
                item {
                    DetailItem("Tipo", log.messageType)
                }
                item {
                    DetailItem("Estado", log.status)
                }
                if (!log.messageContent.isNullOrEmpty()) {
                    item {
                        DetailItem("Contenido", log.messageContent)
                    }
                }
                if (log.intentDetected != null) {
                    item {
                        DetailItem("Intención", log.intentDetected)
                    }
                }
                if (log.confidenceScore != null) {
                    item {
                        DetailItem("Confianza", "${(log.confidenceScore * 100).toInt()}%")
                    }
                }
                if (log.responseTimeMs != null) {
                    item {
                        DetailItem("Tiempo Respuesta", "${log.responseTimeMs}ms")
                    }
                }
                item {
                    DetailItem("Creado", log.createdAt)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { showDeleteConfirm = true },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Error
                )
            ) {
                Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Eliminar")
            }
        }
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este log?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceVariant
        )
        Text(
            value,
            fontSize = 14.sp,
            color = OnSurface
        )
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = Primary)
            Text(
                "Cargando logs...",
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                "Error",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                message,
                textAlign = TextAlign.Center,
                color = OnSurfaceVariant
            )
            Button(onClick = onRetry) {
                Icon(Icons.Outlined.Refresh, null)
                Spacer(Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                Icons.Outlined.Inbox,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            Text(
                "Sin logs",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                "No hay mensajes en este período",
                textAlign = TextAlign.Center,
                color = OnSurfaceVariant
            )
        }
    }
}

private fun formatTime(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        date?.let { outputFormat.format(it) } ?: timestamp
    } catch (e: Exception) {
        timestamp
    }
}

