package com.example.myapplication.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.models.ScheduledMessage
import com.example.myapplication.data.models.ScheduledMessageStatus
import com.example.myapplication.ui.components.ErrorBanner
import com.example.myapplication.ui.components.SuccessBanner
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.ScheduledMessagesState
import com.example.myapplication.viewmodel.ScheduledMessagesViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 📅 Pantalla de Mensajes Programados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledMessagesScreen(
    viewModel: ScheduledMessagesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val messages by viewModel.messages.collectAsState()

    var selectedFilter by remember { mutableStateOf<ScheduledMessageStatus?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadScheduledMessages(refresh = true)
    }

    LaunchedEffect(selectedFilter) {
        viewModel.loadScheduledMessages(status = selectedFilter, refresh = true)
    }

    // Observar estados de éxito/error
    LaunchedEffect(state) {
        when (val currentState = state) {
            is ScheduledMessagesState.Error -> {
                errorMessage = currentState.message
            }
            else -> {}
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = { Text("📅 Mensajes Programados") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Filtro
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, "Filtrar")
                    }

                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos") },
                            onClick = {
                                selectedFilter = null
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("⏳ Pendientes") },
                            onClick = {
                                selectedFilter = ScheduledMessageStatus.PENDING
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("✅ Enviados") },
                            onClick = {
                                selectedFilter = ScheduledMessageStatus.SENT
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("❌ Fallidos") },
                            onClick = {
                                selectedFilter = ScheduledMessageStatus.FAILED
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🚫 Cancelados") },
                            onClick = {
                                selectedFilter = ScheduledMessageStatus.CANCELLED
                                showFilterMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
                icon = { Icon(Icons.Default.Add, "Crear") },
                text = { Text("Programar Mensaje") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state is ScheduledMessagesState.Loading && messages.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                messages.isEmpty() -> {
                    EmptyMessagesState(
                        onCreateClick = onNavigateToCreate
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { message ->
                            ScheduledMessageCard(
                                message = message,
                                onClick = { onNavigateToDetail(message.id) }
                            )
                        }
                    }
                }
            }

            // Mostrar mensaje de éxito
            if (successMessage != null) {
                SuccessBanner(
                    message = successMessage!!,
                    onDismiss = { successMessage = null }
                )
            }

            // Mostrar mensaje de error
            if (errorMessage != null) {
                ErrorBanner(
                    message = errorMessage!!,
                    onDismiss = { errorMessage = null }
                )
            }
        }
    }
}

@Composable
fun ScheduledMessageCard(
    message: ScheduledMessage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = message.status)

                if (message.aiGenerated) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "IA",
                            modifier = Modifier.size(16.dp),
                            tint = Primary
                        )
                        Text(
                            "IA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido del mensaje
            Text(
                text = message.messageContent,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Información de envío
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Destinatarios
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = OnSurfaceVariant
                    )
                    Text(
                        "${message.recipients.size} destinatarios",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }

                // Fecha programada
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = OnSurfaceVariant
                    )
                    Text(
                        formatDateTime(message.scheduledFor),
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Estadísticas de envío (si está enviado)
            if (message.status == ScheduledMessageStatus.SENT) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "✅ ${message.sentCount} enviados",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50)
                    )
                    if (message.failedCount > 0) {
                        Text(
                            "❌ ${message.failedCount} fallidos",
                            style = MaterialTheme.typography.labelSmall,
                            color = Error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: ScheduledMessageStatus) {
    val (emoji, text, color) = when (status) {
        ScheduledMessageStatus.PENDING -> Triple("⏳", "Pendiente", Color(0xFFFFA726))
        ScheduledMessageStatus.SENT -> Triple("✅", "Enviado", Color(0xFF4CAF50))
        ScheduledMessageStatus.FAILED -> Triple("❌", "Fallido", Color(0xFFF44336))
        ScheduledMessageStatus.CANCELLED -> Triple("🚫", "Cancelado", Color(0xFF9E9E9E))
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, style = MaterialTheme.typography.labelSmall)
            Text(
                text,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyMessagesState(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = OnSurfaceVariant
            )

            Text(
                "No hay mensajes programados",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurfaceVariant
            )

            Text(
                "Crea tu primer mensaje programado",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            Button(onClick = onCreateClick) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Programar Mensaje")
            }
        }
    }
}

private fun formatDateTime(isoString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        date?.let { outputFormat.format(it) } ?: isoString
    } catch (e: Exception) {
        isoString
    }
}

