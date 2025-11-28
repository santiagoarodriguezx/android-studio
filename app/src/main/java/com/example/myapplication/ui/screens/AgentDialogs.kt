package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.data.models.Agent
import com.example.myapplication.ui.components.SuccessDialog
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.hideKeyboardOnTap
import com.example.myapplication.viewmodel.AgentsState
import com.example.myapplication.viewmodel.AgentsViewModel

/**
 * ✏️ Diálogo para editar un agente existente
 */
@Composable
fun EditAgentDialog(
    agent: Agent,
    viewModel: AgentsViewModel,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf(agent.description) }
    var agentPrompt by remember { mutableStateOf(agent.agentPrompt) }
    var orderPriority by remember { mutableStateOf(agent.orderPriority.toString()) }
    var model by remember { mutableStateOf(agent.model) }
    var temperature by remember { mutableStateOf(agent.temperature.toString()) }
    var maxTokens by remember { mutableStateOf(agent.maxTokens.toString()) }
    var fallbackMessage by remember { mutableStateOf(agent.fallbackMessage ?: "") }

    val state by viewModel.state.collectAsState()
    val isLoading = state is AgentsState.Loading
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Observar el estado de éxito
    LaunchedEffect(state) {
        if (state is AgentsState.AgentUpdated) {
            showSuccessDialog = true
        }
    }

    // Cerrar el diálogo principal cuando se cierra el de éxito
    if (showSuccessDialog) {
        SuccessDialog(
            message = "Agente actualizado correctamente",
            onDismiss = {
                showSuccessDialog = false
                onDismiss()
            }
        )
    }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                ),
            shape = RoundedCornerShape(24.dp),
            color = Surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(GradientStart, GradientEnd)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    "Editar Agente",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = OnSurface
                                )
                                Text(
                                    agent.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Primary
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            enabled = !isLoading
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = OnSurfaceVariant
                            )
                        }
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                        .hideKeyboardOnTap(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Descripción
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción *") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, null, tint = Primary)
                        },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    // Prompt del agente
                    OutlinedTextField(
                        value = agentPrompt,
                        onValueChange = { agentPrompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        label = { Text("Prompt del Agente *") },
                        leadingIcon = {
                            Icon(Icons.Default.Chat, null, tint = Primary)
                        },
                        supportingText = {
                            Text(
                                "Usa {message} y {history} en el prompt",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        maxLines = 6,
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    // Prioridad (solo si no es router)
                    if (agent.orderPriority != 1) {
                        OutlinedTextField(
                            value = orderPriority,
                            onValueChange = { if (it.all { c -> c.isDigit() }) orderPriority = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Prioridad *") },
                            leadingIcon = {
                                Icon(Icons.Default.Numbers, null, tint = Primary)
                            },
                            supportingText = {
                                Text(
                                    "Mayor prioridad = se ejecuta primero",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = SurfaceVariant,
                                focusedBorderColor = Primary,
                                unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                                focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Configuración avanzada
                    Text(
                        "⚙️ Configuración Avanzada",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = OnSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = temperature,
                            onValueChange = { temperature = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Temperature") },
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = SurfaceVariant,
                                focusedBorderColor = Primary,
                                unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                                focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                            )
                        )

                        OutlinedTextField(
                            value = maxTokens,
                            onValueChange = { if (it.all { c -> c.isDigit() }) maxTokens = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Max Tokens") },
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = SurfaceVariant,
                                focusedBorderColor = Primary,
                                unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                                focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Modelo
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Modelo") },
                        leadingIcon = {
                            Icon(Icons.Default.Psychology, null, tint = Primary)
                        },
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    // Mensaje de fallback
                    OutlinedTextField(
                        value = fallbackMessage,
                        onValueChange = { fallbackMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Mensaje de Fallback (opcional)") },
                        minLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }

                // Actions
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                viewModel.updateAgent(
                                    agentId = agent.id,
                                    description = if (description != agent.description) description else null,
                                    agentPrompt = if (agentPrompt != agent.agentPrompt) agentPrompt else null,
                                    orderPriority = orderPriority.toIntOrNull()?.takeIf { it != agent.orderPriority },
                                    model = if (model != agent.model) model else null,
                                    temperature = temperature.toFloatOrNull()?.takeIf { it != agent.temperature },
                                    maxTokens = maxTokens.toIntOrNull()?.takeIf { it != agent.maxTokens },
                                    fallbackMessage = if (fallbackMessage != (agent.fallbackMessage ?: "")) fallbackMessage.ifBlank { null } else null
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading && description.length >= 10 && agentPrompt.length >= 50,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 🎨 Diálogo para crear un nuevo agente
 */
@Composable
fun CreateAgentDialog(
    viewModel: AgentsViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var agentPrompt by remember { mutableStateOf("") }
    var orderPriority by remember { mutableStateOf("2") }
    var model by remember { mutableStateOf("gemini-2.0-flash-exp") }
    var temperature by remember { mutableStateOf("0.7") }
    var maxTokens by remember { mutableStateOf("1000") }
    var fallbackMessage by remember { mutableStateOf("") }

    val state by viewModel.state.collectAsState()
    val isLoading = state is AgentsState.Loading
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Observar el estado de éxito
    LaunchedEffect(state) {
        if (state is AgentsState.AgentCreated) {
            showSuccessDialog = true
        }
    }

    // Cerrar el diálogo principal cuando se cierra el de éxito
    if (showSuccessDialog) {
        SuccessDialog(
            message = "Agente creado correctamente",
            onDismiss = {
                showSuccessDialog = false
                onDismiss()
            }
        )
    }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                ),
            shape = RoundedCornerShape(24.dp),
            color = Surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(GradientStart, GradientEnd)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    "Crear Agente de IA",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = OnSurface
                                )
                                Text(
                                    "Configura un nuevo agente",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            enabled = !isLoading
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = OnSurfaceVariant
                            )
                        }
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                        .hideKeyboardOnTap(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nombre
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it.lowercase().replace(" ", "_") },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre del agente *") },
                        placeholder = { Text("productos, pago, soporte") },
                        leadingIcon = {
                            Icon(Icons.Default.Label, null, tint = Primary)
                        },
                        supportingText = {
                            Text(
                                "Lowercase sin espacios (ej: productos, pago)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    // Descripción
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción *") },
                        placeholder = { Text("Agente para gestionar consultas sobre productos") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, null, tint = Primary)
                        },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    // Prompt del agente
                    OutlinedTextField(
                        value = agentPrompt,
                        onValueChange = { agentPrompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        label = { Text("Prompt del Agente *") },
                        placeholder = { Text("Eres un experto en {message}...") },
                        leadingIcon = {
                            Icon(Icons.Default.Chat, null, tint = Primary)
                        },
                        supportingText = {
                            Text(
                                "Usa {message} y {history} en el prompt",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        maxLines = 6,
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    // Prioridad
                    OutlinedTextField(
                        value = orderPriority,
                        onValueChange = { if (it.all { c -> c.isDigit() }) orderPriority = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Prioridad *") },
                        placeholder = { Text("2 o mayor") },
                        leadingIcon = {
                            Icon(Icons.Default.Numbers, null, tint = Primary)
                        },
                        supportingText = {
                            Text(
                                "1 está reservado para el router. Usa 2+",
                                style = MaterialTheme.typography.bodySmall,
                                color = Warning
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    // Configuración avanzada
                    Text(
                        "⚙️ Configuración Avanzada",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = OnSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = temperature,
                            onValueChange = { temperature = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Temperature") },
                            placeholder = { Text("0.7") },
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = SurfaceVariant,
                                focusedBorderColor = Primary,
                                unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                                focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                            )
                        )

                        OutlinedTextField(
                            value = maxTokens,
                            onValueChange = { if (it.all { c -> c.isDigit() }) maxTokens = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Max Tokens") },
                            placeholder = { Text("1000") },
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = SurfaceVariant,
                                focusedBorderColor = Primary,
                                unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                                focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Modelo
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Modelo") },
                        leadingIcon = {
                            Icon(Icons.Default.Psychology, null, tint = Primary)
                        },
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    // Mensaje de fallback
                    OutlinedTextField(
                        value = fallbackMessage,
                        onValueChange = { fallbackMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Mensaje de Fallback (opcional)") },
                        placeholder = { Text("Lo siento, no pude procesar tu solicitud") },
                        minLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }

                // Actions
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                viewModel.createAgent(
                                    name = name,
                                    description = description,
                                    agentPrompt = agentPrompt,
                                    orderPriority = orderPriority.toIntOrNull() ?: 2,
                                    model = model,
                                    temperature = temperature.toFloatOrNull() ?: 0.7f,
                                    maxTokens = maxTokens.toIntOrNull() ?: 1000,
                                    fallbackMessage = fallbackMessage.ifBlank { null }
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading && name.isNotBlank() && description.length >= 10 && agentPrompt.length >= 50,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Crear Agente")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 🗑️ Diálogo para confirmar eliminación de agente
 */
@Composable
fun DeleteAgentDialog(
    agent: Agent,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Error.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                "¿Eliminar agente?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Estás a punto de eliminar el agente:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Error.copy(alpha = 0.05f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            agent.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Error
                        )
                        Text(
                            agent.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
                Text(
                    "Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Warning
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * 🔄 Diálogo para recargar agentes
 */
@Composable
fun ReloadAgentsDialog(
    viewModel: AgentsViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isLoading = state is AgentsState.Loading

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Success.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Sync,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                "Recargar Agentes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                "Esto recargará todos los agentes desde la base de datos. Es útil después de hacer cambios directos en la configuración.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = { viewModel.reloadAgents() },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Success
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Recargar")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}

