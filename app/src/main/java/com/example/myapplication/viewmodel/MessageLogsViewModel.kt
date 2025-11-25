package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.*
import com.example.myapplication.data.repository.MessageLogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MessageLogsState {
    object Idle : MessageLogsState()
    object Loading : MessageLogsState()
    data class Success(val message: String) : MessageLogsState()
    data class Error(val message: String) : MessageLogsState()
}

class MessageLogsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MessageLogsRepository(application)

    private val _messageLogsState = MutableStateFlow<MessageLogsState>(MessageLogsState.Idle)
    val messageLogsState: StateFlow<MessageLogsState> = _messageLogsState.asStateFlow()

    // ==================== MESSAGE LOGS ====================

    private val _messageLogs = MutableStateFlow<List<MessageLog>>(emptyList())
    val messageLogs: StateFlow<List<MessageLog>> = _messageLogs.asStateFlow()

    private val _totalLogs = MutableStateFlow(0)
    val totalLogs: StateFlow<Int> = _totalLogs.asStateFlow()

    fun getMessageLogs(
        userId: String? = null,
        sessionId: String? = null,
        messageType: String? = null,
        classification: String? = null,
        intentDetected: String? = null,
        status: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        limit: Int? = 100,
        offset: Int? = 0,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _messageLogsState.value = MessageLogsState.Loading

            repository.getMessageLogs(
                userId, sessionId, messageType, classification,
                intentDetected, status, startDate, endDate,
                limit, offset, companyId
            )
                .onSuccess { response ->
                    _messageLogs.value = response.messageLogs
                    _totalLogs.value = response.total
                    _messageLogsState.value = MessageLogsState.Success("Logs cargados exitosamente")
                }
                .onFailure { error ->
                    _messageLogsState.value = MessageLogsState.Error(
                        error.message ?: "Error al cargar logs"
                    )
                }
        }
    }

    private val _recentLogs = MutableStateFlow<List<MessageLog>>(emptyList())
    val recentLogs: StateFlow<List<MessageLog>> = _recentLogs.asStateFlow()

    fun getRecentMessageLogs(
        minutes: Int? = 30,
        limit: Int? = 50,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _messageLogsState.value = MessageLogsState.Loading

            repository.getRecentMessageLogs(minutes, limit, companyId)
                .onSuccess { response ->
                    _recentLogs.value = response.messageLogs
                    _messageLogsState.value = MessageLogsState.Success("Logs recientes cargados")
                }
                .onFailure { error ->
                    _messageLogsState.value = MessageLogsState.Error(
                        error.message ?: "Error al cargar logs recientes"
                    )
                }
        }
    }

    fun createMessageLog(
        request: MessageLogCreateRequest,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _messageLogsState.value = MessageLogsState.Loading

            repository.createMessageLog(request, companyId)
                .onSuccess { response ->
                    _messageLogsState.value = MessageLogsState.Success("Log creado exitosamente")
                    // Refrescar la lista
                    getRecentMessageLogs()
                }
                .onFailure { error ->
                    _messageLogsState.value = MessageLogsState.Error(
                        error.message ?: "Error al crear log"
                    )
                }
        }
    }

    fun deleteMessageLog(
        logId: String,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _messageLogsState.value = MessageLogsState.Loading

            repository.deleteMessageLog(logId, companyId)
                .onSuccess { response ->
                    _messageLogsState.value = MessageLogsState.Success("Log eliminado")
                    // Remover de la lista local
                    _messageLogs.value = _messageLogs.value.filter { it.id != logId }
                    _recentLogs.value = _recentLogs.value.filter { it.id != logId }
                }
                .onFailure { error ->
                    _messageLogsState.value = MessageLogsState.Error(
                        error.message ?: "Error al eliminar log"
                    )
                }
        }
    }

    // ==================== REALTIME STATS ====================

    private val _realtimeStats = MutableStateFlow<RealtimeStats?>(null)
    val realtimeStats: StateFlow<RealtimeStats?> = _realtimeStats.asStateFlow()

    fun getRealtimeStats(
        minutes: Int? = 5,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            repository.getRealtimeStats(minutes, companyId)
                .onSuccess { stats ->
                    _realtimeStats.value = stats
                }
                .onFailure { error ->
                    _messageLogsState.value = MessageLogsState.Error(
                        error.message ?: "Error al cargar estadísticas en tiempo real"
                    )
                }
        }
    }

    private val _userStats = MutableStateFlow<UserMessageStats?>(null)
    val userStats: StateFlow<UserMessageStats?> = _userStats.asStateFlow()

    fun getUserMessageStats(
        userId: String,
        days: Int? = 7,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _messageLogsState.value = MessageLogsState.Loading

            repository.getUserMessageStats(userId, days, companyId)
                .onSuccess { stats ->
                    _userStats.value = stats
                    _messageLogsState.value = MessageLogsState.Success("Estadísticas de usuario cargadas")
                }
                .onFailure { error ->
                    _messageLogsState.value = MessageLogsState.Error(
                        error.message ?: "Error al cargar estadísticas"
                    )
                }
        }
    }

    // ==================== UTILITY ====================

    fun resetState() {
        _messageLogsState.value = MessageLogsState.Idle
    }

    fun clearLogs() {
        _messageLogs.value = emptyList()
        _recentLogs.value = emptyList()
        _totalLogs.value = 0
    }

    fun clearStats() {
        _realtimeStats.value = null
        _userStats.value = null
    }
}

