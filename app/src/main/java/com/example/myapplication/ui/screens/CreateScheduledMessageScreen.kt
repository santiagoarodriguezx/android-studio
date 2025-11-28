package com.example.myapplication.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.ui.components.ErrorBanner
import com.example.myapplication.ui.components.SuccessBanner
import com.example.myapplication.ui.theme.*
import com.example.myapplication.utils.hideKeyboardOnTap
import com.example.myapplication.viewmodel.ScheduledMessagesState
import com.example.myapplication.viewmodel.ScheduledMessagesViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 📝 Pantalla para crear mensajes programados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScheduledMessageScreen(
    viewModel: ScheduledMessagesViewModel,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val generatedMessage by viewModel.generatedMessage.collectAsState()

    var messageMode by remember { mutableStateOf(MessageMode.MANUAL) }
    var recipients by remember { mutableStateOf("") }
    var messageContent by remember { mutableStateOf("") }
    var aiPrompt by remember { mutableStateOf("") }
    var aiContext by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf("10:00") }
    var timezone by remember { mutableStateOf("America/Bogota") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }
    var dateTimeErrorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Observar el mensaje generado
    LaunchedEffect(generatedMessage) {
        if (generatedMessage != null) {
            messageContent = generatedMessage!!
            isGenerating = false
        }
    }

    // Observar si el mensaje fue programado
    LaunchedEffect(state) {
        when (state) {
            is ScheduledMessagesState.MessageScheduled -> {
                successMessage = "✅ Mensaje programado exitosamente"
                kotlinx.coroutines.delay(1500)
                onSuccess()
            }
            is ScheduledMessagesState.Error -> {
                errorMessage = (state as ScheduledMessagesState.Error).message
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

                    Text(
                        "Programar Mensaje",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = OnSurface
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .hideKeyboardOnTap(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Selector de modo
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 0.08f),
                        ambientColor = Color.Black.copy(alpha = 0.05f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
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
                            Text("✨", fontSize = 18.sp)
                        }
                        Text(
                            "Tipo de mensaje",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = OnSurface
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceVariant.copy(alpha = 0.6f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ModeSelectorButton(
                            selected = messageMode == MessageMode.MANUAL,
                            onClick = { messageMode = MessageMode.MANUAL },
                            label = "✍️ Manual",
                            modifier = Modifier.weight(1f)
                        )
                        ModeSelectorButton(
                            selected = messageMode == MessageMode.AI,
                            onClick = { messageMode = MessageMode.AI },
                            label = "🤖 Con IA",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Destinatarios
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 0.08f),
                        ambientColor = Color.Black.copy(alpha = 0.05f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Success.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📱", fontSize = 18.sp)
                        }
                        Text(
                            "Destinatarios",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = OnSurface
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = recipients,
                        onValueChange = { recipients = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Números de WhatsApp") },
                        placeholder = { Text("573001234567, 573007654321") },
                        supportingText = {
                            Text(
                                "Separados por comas",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.People,
                                null,
                                tint = Primary
                            )
                        },
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Contenido del mensaje
            AnimatedContent(
                targetState = messageMode,
                label = "message_mode"
            ) { mode ->
                when (mode) {
                    MessageMode.MANUAL -> ManualMessageInput(
                        messageContent = messageContent,
                        onMessageChange = { messageContent = it }
                    )

                    MessageMode.AI -> AIMessageInput(
                        aiPrompt = aiPrompt,
                        aiContext = aiContext,
                        generatedMessage = messageContent,
                        isGenerating = isGenerating,
                        onPromptChange = { aiPrompt = it },
                        onContextChange = { aiContext = it },
                        onGenerateClick = {
                            isGenerating = true
                            viewModel.generateMessagePreview(aiPrompt, aiContext)
                        },
                        onEditGenerated = { messageContent = it }
                    )
                }
            }

            // Fecha y hora
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.Black.copy(alpha = 0.08f),
                        ambientColor = Color.Black.copy(alpha = 0.05f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = Surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Warning.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📅", fontSize = 18.sp)
                        }
                        Text(
                            "Fecha y Hora de Envío",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = OnSurface
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón de fecha
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    spotColor = Color.Black.copy(alpha = 0.05f)
                                )
                                .clickable { showDatePicker = true },
                            shape = RoundedCornerShape(14.dp),
                            color = if (selectedDate != null) Primary.copy(alpha = 0.1f) else SurfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = if (selectedDate != null) Primary else OnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    selectedDate?.let { formatDate(it) } ?: "Fecha",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = if (selectedDate != null) Primary else OnSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        // Botón de hora
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    spotColor = Color.Black.copy(alpha = 0.05f)
                                )
                                .clickable { showTimePicker = true },
                            shape = RoundedCornerShape(14.dp),
                            color = Primary.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    selectedTime,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = Primary
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = timezone,
                        onValueChange = { timezone = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Zona horaria") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Public,
                                null,
                                tint = Primary
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = SurfaceVariant,
                            focusedBorderColor = Primary,
                            unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                            focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Botón programar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = if (isFormValid(recipients, messageContent, aiPrompt, messageMode, selectedDate) && state !is ScheduledMessagesState.Loading) 6.dp else 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = Primary.copy(alpha = 0.3f),
                        ambientColor = Primary.copy(alpha = 0.1f)
                    )
                    .clickable(
                        enabled = isFormValid(recipients, messageContent, aiPrompt, messageMode, selectedDate) && state !is ScheduledMessagesState.Loading,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        val recipientsList = recipients
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        val scheduledFor = formatScheduledDateTime(selectedDate, selectedTime)

                        // Validar que la fecha sea futura
                        val scheduledDateTime = parseScheduledDateTime(selectedDate, selectedTime)
                        val now = Calendar.getInstance()

                        if (scheduledDateTime.timeInMillis <= now.timeInMillis) {
                            dateTimeErrorMessage = "⚠️ La fecha y hora deben ser futuras. Por favor selecciona una fecha/hora posterior a ${formatDateTime(now.timeInMillis)}"
                            return@clickable
                        }

                        dateTimeErrorMessage = null

                        if (messageMode == MessageMode.MANUAL) {
                            viewModel.scheduleManualMessage(
                                recipients = recipientsList,
                                messageContent = messageContent,
                                scheduledFor = scheduledFor,
                                timezone = timezone
                            )
                        } else {
                            viewModel.scheduleAIMessage(
                                recipients = recipientsList,
                                aiPrompt = aiPrompt,
                                scheduledFor = scheduledFor,
                                timezone = timezone,
                                aiContext = aiContext.ifEmpty { null }
                            )
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                color = if (isFormValid(recipients, messageContent, aiPrompt, messageMode, selectedDate) && state !is ScheduledMessagesState.Loading) {
                    Primary
                } else {
                    OnSurfaceVariant.copy(alpha = 0.3f)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state is ScheduledMessagesState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isFormValid(recipients, messageContent, aiPrompt, messageMode, selectedDate)) Color.White else OnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Programar Mensaje",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (isFormValid(recipients, messageContent, aiPrompt, messageMode, selectedDate)) Color.White else OnSurfaceVariant
                            )
                        }
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

            // Mostrar error general
            if (errorMessage != null) {
                ErrorBanner(
                    message = errorMessage!!,
                    onDismiss = { errorMessage = null }
                )
            }

            // Mostrar error de fecha/hora
            if (dateTimeErrorMessage != null) {
                ErrorBanner(
                    message = dateTimeErrorMessage!!,
                    onDismiss = { dateTimeErrorMessage = null }
                )
            }
        }
    }

    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Primary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = OnSurfaceVariant)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker iOS Style
    if (showTimePicker) {
        IOSStyleTimePicker(
            initialTime = selectedTime,
            onDismiss = { showTimePicker = false },
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
            }
        )
    }
}

@Composable
fun ManualMessageInput(
    messageContent: String,
    onMessageChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = Surface
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💬", fontSize = 18.sp)
                }
                Text(
                    "Mensaje",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = OnSurface
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = messageContent,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                label = { Text("Escribe tu mensaje") },
                placeholder = { Text("🎉 Promoción especial...") },
                maxLines = 10,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = SurfaceVariant,
                    focusedBorderColor = Primary,
                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "${messageContent.length}/1000",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (messageContent.length > 1000) Error else OnSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun AIMessageInput(
    aiPrompt: String,
    aiContext: String,
    generatedMessage: String,
    isGenerating: Boolean,
    onPromptChange: (String) -> Unit,
    onContextChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onEditGenerated: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = Surface
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                                colors = listOf(GradientStart.copy(alpha = 0.3f), GradientEnd.copy(alpha = 0.3f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 18.sp)
                }
                Text(
                    "Generar con IA",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = OnSurface
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = aiPrompt,
                onValueChange = onPromptChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("¿Qué mensaje quieres?") },
                placeholder = { Text("Mensaje promocional para Black Friday con 50% descuento") },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = SurfaceVariant,
                    focusedBorderColor = Primary,
                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = aiContext,
                onValueChange = onContextChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contexto adicional (opcional)") },
                placeholder = { Text("Tono formal, productos tecnológicos...") },
                maxLines = 2,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = SurfaceVariant,
                    focusedBorderColor = Primary,
                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f)
                )
            )

            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(
                        elevation = if (aiPrompt.length >= 10 && !isGenerating) 4.dp else 1.dp,
                        shape = RoundedCornerShape(14.dp),
                        spotColor = GradientEnd.copy(alpha = 0.2f)
                    )
                    .clickable(
                        enabled = aiPrompt.length >= 10 && !isGenerating,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onGenerateClick() },
                shape = RoundedCornerShape(14.dp),
                color = if (aiPrompt.length >= 10 && !isGenerating) {
                    Primary
                } else {
                    OnSurfaceVariant.copy(alpha = 0.3f)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (aiPrompt.length >= 10) Color.White else OnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Generar Mensaje",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (aiPrompt.length >= 10) Color.White else OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (generatedMessage.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))

                Text(
                    "✨ Mensaje generado",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Primary
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = generatedMessage,
                    onValueChange = onEditGenerated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    label = { Text("Puedes editar el mensaje") },
                    maxLines = 8,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Primary.copy(alpha = 0.3f),
                        focusedBorderColor = Primary,
                        unfocusedContainerColor = Primary.copy(alpha = 0.05f),
                        focusedContainerColor = Primary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}

enum class MessageMode {
    MANUAL, AI
}

@Composable
fun ModeSelectorButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .shadow(
                elevation = if (selected) 3.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (selected) Primary.copy(alpha = 0.2f) else Color.Transparent
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Surface else Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (selected) Primary else OnSurfaceVariant
            )
        }
    }
}

@Composable
fun IOSStyleTimePicker(
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val timeParts = initialTime.split(":")
    var selectedHour by remember { mutableStateOf(timeParts[0].toIntOrNull() ?: 10) }
    var selectedMinute by remember { mutableStateOf(timeParts[1].toIntOrNull() ?: 0) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.15f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(24.dp),
            color = Surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "Cancelar",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Text(
                        "Seleccionar Hora",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = OnSurface
                    )

                    TextButton(
                        onClick = {
                            val hour = selectedHour.toString().padStart(2, '0')
                            val minute = selectedMinute.toString().padStart(2, '0')
                            onTimeSelected("$hour:$minute")
                        }
                    ) {
                        Text(
                            "OK",
                            color = Primary,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Time Display
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Primary.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    color = Primary.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            selectedHour.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 56.sp
                            ),
                            color = Primary
                        )
                        Text(
                            ":",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 56.sp
                            ),
                            color = Primary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(
                            selectedMinute.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 56.sp
                            ),
                            color = Primary
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Hour and Minute Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hour Selector
                    TimePickerColumn(
                        modifier = Modifier.weight(1f),
                        label = "Hora",
                        range = 0..23,
                        selectedValue = selectedHour,
                        onValueChange = { selectedHour = it }
                    )

                    // Minute Selector
                    TimePickerColumn(
                        modifier = Modifier.weight(1f),
                        label = "Minuto",
                        range = 0..59,
                        selectedValue = selectedMinute,
                        onValueChange = { selectedMinute = it }
                    )
                }
            }
        }
    }
}

@Composable
fun TimePickerColumn(
    modifier: Modifier = Modifier,
    label: String,
    range: IntRange,
    selectedValue: Int,
    onValueChange: (Int) -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = OnSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Simple scrollable column with buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                range.forEach { value ->
                    val isSelected = value == selectedValue
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .shadow(
                                elevation = if (isSelected) 2.dp else 0.dp,
                                shape = RoundedCornerShape(10.dp),
                                spotColor = Primary.copy(alpha = 0.2f)
                            )
                            .clickable { onValueChange(value) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Primary else Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                value.toString().padStart(2, '0'),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.White else OnSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun isFormValid(
    recipients: String,
    messageContent: String,
    aiPrompt: String,
    mode: MessageMode,
    selectedDate: Long?
): Boolean {
    return recipients.isNotBlank() &&
            selectedDate != null &&
            when (mode) {
                MessageMode.MANUAL -> messageContent.isNotBlank() && messageContent.length <= 1000
                MessageMode.AI -> aiPrompt.length >= 10
            }
}

private fun formatDate(millis: Long): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(Date(millis))
}

private fun formatScheduledDateTime(dateMillis: Long?, time: String): String {
    if (dateMillis == null) return ""

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis

    val timeParts = time.split(":")
    if (timeParts.size == 2) {
        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    // Usar formato ISO 8601 con zona horaria
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    return format.format(calendar.time)
}

private fun parseScheduledDateTime(dateMillis: Long?, time: String): Calendar {
    val calendar = Calendar.getInstance()
    if (dateMillis != null) {
        calendar.timeInMillis = dateMillis
    }

    val timeParts = time.split(":")
    if (timeParts.size == 2) {
        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    return calendar
}

private fun formatDateTime(millis: Long): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return format.format(Date(millis))
}

