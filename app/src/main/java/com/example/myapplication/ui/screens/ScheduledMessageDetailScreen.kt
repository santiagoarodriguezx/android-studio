package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * 📄 Pantalla de detalle de mensaje programado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledMessageDetailScreen(
    messageId: String,
    viewModel: ScheduledMessagesViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val message by viewModel.selectedMessage.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) }
    var showSendNowDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(messageId) {
        viewModel.loadMessageDetail(messageId)
    }

    LaunchedEffect(state) {
        when (val currentState = state) {
            is ScheduledMessagesState.Success -> {
                val msg = currentState.message
                if (msg.contains("cancelado", ignoreCase = true)) {
                    successMessage = "✅ Mensaje cancelado exitosamente"
                    kotlinx.coroutines.delay(2000)
                    onNavigateBack()
                } else if (msg.contains("enviado", ignoreCase = true)) {
                    successMessage = "✅ $msg"
                    kotlinx.coroutines.delay(2000)
                    onNavigateBack()
                }
            }
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
                title = { Text("📄 Detalle del Mensaje") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state is ScheduledMessagesState.Loading && message == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                message != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Estado
                        StatusCard(message!!)

                        // Contenido del mensaje
                        MessageContentCard(message!!)

                        // Información de envío
                        ScheduleInfoCard(message!!)

                        // Destinatarios
                        RecipientsCard(message!!)

                        // Estadísticas (si está enviado)
                        if (message!!.status == ScheduledMessageStatus.SENT) {
                            StatisticsCard(message!!)
                        }

                        // Acciones
                        if (message!!.status == ScheduledMessageStatus.PENDING) {
                            ActionButtons(
                                onSendNow = { showSendNowDialog = true },
                                onEdit = { showEditDialog = true },
                                onCancel = { showCancelDialog = true },
                                isLoading = state is ScheduledMessagesState.Loading
                            )
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Mensaje no encontrado")
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

    // Diálogo de confirmación para cancelar
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            icon = { Icon(Icons.Default.Cancel, null, tint = Error) },
            title = { Text("Cancelar Mensaje") },
            text = { Text("¿Estás seguro de que deseas cancelar este mensaje programado?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelScheduledMessage(messageId)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Cancelar Mensaje")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Volver")
                }
            }
        )
    }

    // Diálogo de confirmación para enviar ahora
    if (showSendNowDialog) {
        AlertDialog(
            onDismissRequest = { showSendNowDialog = false },
            icon = { Icon(Icons.Default.Send, null, tint = Primary) },
            title = { Text("Enviar Ahora") },
            text = { Text("¿Quieres enviar este mensaje AHORA en lugar de esperar a la hora programada?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendMessageNow(messageId)
                        showSendNowDialog = false
                    }
                ) {
                    Text("Enviar Ahora")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSendNowDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun StatusCard(message: ScheduledMessage) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (message.status) {
                ScheduledMessageStatus.PENDING -> Color(0xFFFFA726).copy(alpha = 0.1f)
                ScheduledMessageStatus.SENT -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                ScheduledMessageStatus.FAILED -> Error.copy(alpha = 0.1f)
                ScheduledMessageStatus.CANCELLED -> Surface.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Estado",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        when (message.status) {
                            ScheduledMessageStatus.PENDING -> "⏳ Pendiente"
                            ScheduledMessageStatus.SENT -> "✅ Enviado"
                            ScheduledMessageStatus.FAILED -> "❌ Fallido"
                            ScheduledMessageStatus.CANCELLED -> "🚫 Cancelado"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (message.aiGenerated) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Primary.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Primary
                                )
                                Text("IA", style = MaterialTheme.typography.labelSmall, color = Primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageContentCard(message: ScheduledMessage) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "💬 Contenido del Mensaje",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Surface.copy(alpha = 0.5f)
            ) {
                Text(
                    message.messageContent,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (message.aiPrompt != null) {
                Spacer(Modifier.height(12.dp))

                Text(
                    "🤖 Prompt de IA:",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )

                Text(
                    message.aiPrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ScheduleInfoCard(message: ScheduledMessage) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📅 Información de Programación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            InfoRow(
                icon = Icons.Default.Schedule,
                label = "Programado para",
                value = formatDateTime(message.scheduledFor)
            )

            InfoRow(
                icon = Icons.Default.Public,
                label = "Zona horaria",
                value = message.timezone
            )

            if (message.sentAt != null) {
                InfoRow(
                    icon = Icons.Default.CheckCircle,
                    label = "Enviado el",
                    value = formatDateTime(message.sentAt)
                )
            }
        }
    }
}

@Composable
fun RecipientsCard(message: ScheduledMessage) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "📱 Destinatarios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        "${message.recipients.size}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            message.recipients.take(5).forEach { recipient ->
                Text(
                    "• $recipient",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            if (message.recipients.size > 5) {
                Text(
                    "... y ${message.recipients.size - 5} más",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun StatisticsCard(message: ScheduledMessage) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📊 Estadísticas de Envío",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = message.sentCount.toString(),
                    label = "Enviados",
                    color = Color(0xFF4CAF50)
                )

                StatItem(
                    value = message.failedCount.toString(),
                    label = "Fallidos",
                    color = Error
                )

                StatItem(
                    value = "${(message.sentCount.toFloat() / message.recipients.size * 100).toInt()}%",
                    label = "Tasa de éxito",
                    color = Primary
                )
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun ActionButtons(
    onSendNow: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    isLoading: Boolean
) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 18.sp)
                }
                Text(
                    "Acciones",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = OnSurface
                )
            }

            // Enviar Ahora
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Primary.copy(alpha = 0.3f)
                    )
                    .clickable(
                        enabled = !isLoading,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onSendNow() },
                shape = RoundedCornerShape(14.dp),
                color = if (isLoading) OnSurfaceVariant.copy(alpha = 0.3f) else Primary
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Enviar Ahora",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            // Cancelar Mensaje
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = Error.copy(alpha = 0.2f)
                    )
                    .clickable(
                        enabled = !isLoading,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onCancel() },
                shape = RoundedCornerShape(14.dp),
                color = if (isLoading) OnSurfaceVariant.copy(alpha = 0.3f) else Error.copy(alpha = 0.15f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Cancelar Mensaje",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )

        Column {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatDateTime(isoString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        date?.let { outputFormat.format(it) } ?: isoString
    } catch (e: Exception) {
        isoString
    }
}

