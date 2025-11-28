package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.*
import com.example.myapplication.data.repository.ScheduledMessagesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 📅 Estados para Mensajes Programados
 */
sealed class ScheduledMessagesState {
    object Idle : ScheduledMessagesState()
    object Loading : ScheduledMessagesState()
    data class Success(val message: String) : ScheduledMessagesState()
    data class Error(val message: String) : ScheduledMessagesState()
    data class MessageScheduled(val data: ScheduledMessageData) : ScheduledMessagesState()
    data class MessageGenerated(val message: String) : ScheduledMessagesState()
}

/**
 * 📅 ViewModel para gestión de Mensajes Programados
 */
class ScheduledMessagesViewModel(
    private val repository: ScheduledMessagesRepository
) : ViewModel() {

    private val TAG = "ScheduledMessagesVM"

    // Estado general
    private val _state = MutableStateFlow<ScheduledMessagesState>(ScheduledMessagesState.Idle)
    val state: StateFlow<ScheduledMessagesState> = _state.asStateFlow()

    // Lista de mensajes programados
    private val _messages = MutableStateFlow<List<ScheduledMessage>>(emptyList())
    val messages: StateFlow<List<ScheduledMessage>> = _messages.asStateFlow()

    // Mensaje seleccionado
    private val _selectedMessage = MutableStateFlow<ScheduledMessage?>(null)
    val selectedMessage: StateFlow<ScheduledMessage?> = _selectedMessage.asStateFlow()

    // Mensaje generado con IA (preview)
    private val _generatedMessage = MutableStateFlow<String?>(null)
    val generatedMessage: StateFlow<String?> = _generatedMessage.asStateFlow()

    // Paginación
    private var currentOffset = 0
    private val pageSize = 50

    // ==================== PROGRAMAR MENSAJES ====================

    /**
     * Programar un mensaje manual
     */
    fun scheduleManualMessage(
        recipients: List<String>,
        messageContent: String,
        scheduledFor: String,
        timezone: String = "America/Bogota",
        metadata: Map<String, Any>? = null
    ) {
        viewModelScope.launch {
            _state.value = ScheduledMessagesState.Loading

            val result = repository.scheduleMessage(
                recipients = recipients,
                scheduledFor = scheduledFor,
                timezone = timezone,
                messageContent = messageContent,
                metadata = metadata
            )

            result.onSuccess { response ->
                Log.d(TAG, "✅ Mensaje programado: ${response.data.id}")
                _state.value = ScheduledMessagesState.MessageScheduled(response.data)
                // Recargar lista
                loadScheduledMessages()
            }.onFailure { error ->
                Log.e(TAG, "❌ Error programando mensaje: ${error.message}")
                _state.value = ScheduledMessagesState.Error(
                    error.message ?: "Error al programar mensaje"
                )
            }
        }
    }

    /**
     * Programar un mensaje generado con IA
     */
    fun scheduleAIMessage(
        recipients: List<String>,
        aiPrompt: String,
        scheduledFor: String,
        timezone: String = "America/Bogota",
        aiContext: String? = null,
        metadata: Map<String, Any>? = null
    ) {
        viewModelScope.launch {
            _state.value = ScheduledMessagesState.Loading

            val result = repository.scheduleMessage(
                recipients = recipients,
                scheduledFor = scheduledFor,
                timezone = timezone,
                aiPrompt = aiPrompt,
                aiContext = aiContext,
                metadata = metadata
            )

            result.onSuccess { response ->
                Log.d(TAG, "✅ Mensaje IA programado: ${response.data.id}")
                _state.value = ScheduledMessagesState.MessageScheduled(response.data)
                // Recargar lista
                loadScheduledMessages()
            }.onFailure { error ->
                Log.e(TAG, "❌ Error programando mensaje IA: ${error.message}")
                _state.value = ScheduledMessagesState.Error(
                    error.message ?: "Error al programar mensaje"
                )
            }
        }
    }

    /**
     * Generar preview de mensaje con IA (sin programar)
     */
    fun generateMessagePreview(prompt: String, context: String? = null) {
        viewModelScope.launch {
            _state.value = ScheduledMessagesState.Loading
            _generatedMessage.value = null

            val result = repository.generateMessagePreview(prompt, context)

            result.onSuccess { response ->
                Log.d(TAG, "✅ Mensaje generado: ${response.data.generatedMessage}")
                _generatedMessage.value = response.data.generatedMessage
                _state.value = ScheduledMessagesState.MessageGenerated(response.data.generatedMessage)
            }.onFailure { error ->
                Log.e(TAG, "❌ Error generando mensaje: ${error.message}")
                _state.value = ScheduledMessagesState.Error(
                    error.message ?: "Error al generar mensaje"
                )
            }
        }
    }

    // ==================== OBTENER MENSAJES ====================

    /**
     * Cargar lista de mensajes programados
     */
    fun loadScheduledMessages(
        status: ScheduledMessageStatus? = null,
        refresh: Boolean = false
    ) {
        viewModelScope.launch {
            _state.value = ScheduledMessagesState.Loading

            if (refresh) {
                currentOffset = 0
                _messages.value = emptyList()
            }

            val result = repository.getScheduledMessages(
                status = status,
                limit = pageSize,
                offset = currentOffset
            )

            result.onSuccess { response ->
                Log.d(TAG, "✅ ${response.count} mensajes cargados")

                if (refresh) {
                    _messages.value = response.data
                } else {
                    _messages.value = _messages.value + response.data
                }

                currentOffset += response.count
                _state.value = ScheduledMessagesState.Success("Mensajes cargados")
            }.onFailure { error ->
                Log.e(TAG, "❌ Error cargando mensajes: ${error.message}")
                _state.value = ScheduledMessagesState.Error(
                    error.message ?: "Error al cargar mensajes"
                )
            }
        }
    }

    /**
     * Cargar detalle de un mensaje específico
     */
    fun loadMessageDetail(messageId: String) {
        viewModelScope.launch {
            _state.value = ScheduledMessagesState.Loading

            val result = repository.getScheduledMessage(messageId)

            result.onSuccess { response ->
                Log.d(TAG, "✅ Mensaje cargado: ${response.data.id}")
                _selectedMessage.value = response.data
                _state.value = ScheduledMessagesState.Success("Mensaje cargado")
            }.onFailure { error ->
                Log.e(TAG, "❌ Error cargando mensaje: ${error.message}")
                _state.value = ScheduledMessagesState.Error(
                    error.message ?: "Error al cargar mensaje"
                )
            }
        }
    }

    // ==================== ACTUALIZAR MENSAJES ====================

    /**
     * Actualizar un mensaje programado
     */
    fun updateScheduledMessage(
        messageId: String,
        messageContent: String? = null,
        scheduledFor: String? = null,
        recipients: List<String>? = null
    ) {
        viewModelScope.launch {
            _state.value = ScheduledMessagesState.Loading

            val result = repository.updateScheduledMessage(
                messageId = messageId,
                messageContent = messageContent,
                scheduledFor = scheduledFor,
                recipients = recipients
            )

            result.onSuccess { response ->
                Log.d(TAG, "✅ Mensaje actualizado: ${response.data.id}")
                _selectedMessage.value = response.data
                _state.value = ScheduledMessagesState.Success("Mensaje actualizado")
                // Recargar lista
                loadScheduledMessages(refresh = true)
            }.onFailure { error ->
                Log.e(TAG, "❌ Error actualizando mensaje: ${error.message}")
                _state.value = ScheduledMessagesState.Error(
                    error.message ?: "Error al actualizar mensaje"
                )
            }
        }
    }

    /**
     * Cancelar un mensaje programado
     */
    fun cancelScheduledMessage(messageId: String) {
        viewModelScope.launch {
            _state.value = ScheduledMessagesState.Loading

            val result = repository.cancelScheduledMessage(messageId)

            result.onSuccess {
                Log.d(TAG, "✅ Mensaje cancelado: $messageId")
                _state.value = ScheduledMessagesState.Success("Mensaje cancelado")
                // Recargar lista
                loadScheduledMessages(refresh = true)
            }.onFailure { error ->
                Log.e(TAG, "❌ Error cancelando mensaje: ${error.message}")
                _state.value = ScheduledMessagesState.Error(
                    error.message ?: "Error al cancelar mensaje"
                )
            }
        }
    }

    /**
     * Enviar un mensaje programado ahora
     */
    fun sendMessageNow(messageId: String) {
        viewModelScope.launch {
            _state.value = ScheduledMessagesState.Loading

            val result = repository.sendMessageNow(messageId)

            result.onSuccess {
                Log.d(TAG, "✅ Mensaje enviado: $messageId")
                _state.value = ScheduledMessagesState.Success("Mensaje enviado")
                // Recargar lista
                loadScheduledMessages(refresh = true)
            }.onFailure { error ->
                Log.e(TAG, "❌ Error enviando mensaje: ${error.message}")
                _state.value = ScheduledMessagesState.Error(
                    error.message ?: "Error al enviar mensaje"
                )
            }
        }
    }

    // ==================== UTILIDADES ====================

    /**
     * Resetear estado
     */
    fun resetState() {
        _state.value = ScheduledMessagesState.Idle
    }

    /**
     * Limpiar mensaje generado
     */
    fun clearGeneratedMessage() {
        _generatedMessage.value = null
    }

    /**
     * Limpiar mensaje seleccionado
     */
    fun clearSelectedMessage() {
        _selectedMessage.value = null
    }
}

