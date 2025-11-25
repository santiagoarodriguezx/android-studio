package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.AuthState
import com.example.myapplication.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla de Verificación de Dispositivo - Diseño Moderno
 *
 * Se muestra cuando el backend detecta un dispositivo nuevo
 * y requiere verificación por seguridad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceVerificationScreen(
    viewModel: AuthViewModel,
    email: String,
    password: String,
    verificationToken: String?,
    onVerificationSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var tokenInput by remember { mutableStateOf(verificationToken ?: "") }
    val authState by viewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    // Estado para reenvío de código
    var canResend by remember { mutableStateOf(true) }
    var resendCountdown by remember { mutableStateOf(0) }
    var showResendSuccess by remember { mutableStateOf(false) }

    // Animación de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    // Countdown para reenviar
    LaunchedEffect(resendCountdown) {
        if (resendCountdown > 0) {
            delay(1000L)
            resendCountdown--
        } else {
            canResend = true
        }
    }

    // Auto-ocultar mensaje de éxito
    LaunchedEffect(showResendSuccess) {
        if (showResendSuccess) {
            delay(3000L)
            showResendSuccess = false
        }
    }

    // Observar el estado de verificación
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                val message = (authState as AuthState.Success).message
                if (message.contains("reenviado", ignoreCase = true)) {
                    showResendSuccess = true
                    viewModel.resetAuthState()
                } else {
                    onVerificationSuccess()
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Primary.copy(alpha = 0.05f),
                        Background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Icono de seguridad con gradiente
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Warning.copy(alpha = 0.2f),
                                        Warning.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = "Seguridad",
                            modifier = Modifier.size(50.dp),
                            tint = Warning
                        )
                    }

                    Text(
                        text = "🔐 Dispositivo Nuevo",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Detectamos un inicio de sesión desde un dispositivo que no reconocemos",
                        fontSize = 15.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Card principal
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                        expandVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Warning.copy(alpha = 0.25f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Info del dispositivo
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Smartphone,
                                    contentDescription = "Dispositivo",
                                    tint = Secondary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column {
                                    Text(
                                        text = "Verificación Requerida",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Secondary
                                    )
                                    Text(
                                        text = "Por tu seguridad, verifica tu identidad",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Email info
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Código enviado a:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Primary.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Email,
                                        contentDescription = "Email",
                                        tint = Primary
                                    )
                                    Text(
                                        text = email,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Primary
                                    )
                                }
                            }
                        }

                        // Campo de verificación
                        OutlinedTextField(
                            value = tokenInput,
                            onValueChange = { tokenInput = it },
                            label = { Text("Código de Verificación") },
                            placeholder = { Text("Ingresa el código") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Key,
                                    contentDescription = "Token",
                                    tint = Primary
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = SurfaceVariant
                            )
                        )

                        // Mensaje de error
                        AnimatedVisibility(
                            visible = authState is AuthState.Error,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = ErrorContainer
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        tint = Error
                                    )
                                    Text(
                                        text = (authState as? AuthState.Error)?.message
                                            ?: "Error desconocido",
                                        color = Error,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        // Mensaje de éxito al reenviar
                        AnimatedVisibility(
                            visible = showResendSuccess,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = SuccessContainer
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = Success
                                    )
                                    Text(
                                        text = "Código reenviado exitosamente",
                                        color = Success,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón verificar
                        Button(
                            onClick = {
                                if (tokenInput.isNotEmpty()) {
                                    viewModel.verifyDevice(tokenInput, email, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = tokenInput.isNotEmpty() && authState !is AuthState.Loading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            )
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = OnPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.VerifiedUser,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        "Verificar Dispositivo",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Botón reenviar código
                        OutlinedButton(
                            onClick = {
                                if (canResend) {
                                    viewModel.resendVerification(email, "device_verification")
                                    canResend = false
                                    resendCountdown = 60 // 60 segundos
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = canResend && authState !is AuthState.Loading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    if (resendCountdown > 0)
                                        "Reenviar en ${resendCountdown}s"
                                    else
                                        "Reenviar Código",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Botón cancelar
                        TextButton(
                            onClick = onCancel,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Cancelar e Iniciar Sesión Nuevamente",
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info de seguridad
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 400))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Primary
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "💡 ¿Por qué es necesario?",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Primary
                            )
                            Text(
                                text = "Este paso adicional protege tu cuenta de accesos no autorizados desde dispositivos desconocidos. Una vez verificado, este dispositivo quedará registrado como confiable.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

