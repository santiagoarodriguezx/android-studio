package com.example.myapplication.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.models.*
import com.example.myapplication.data.repository.AgentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 🤖 ViewModel para gestión de Agentes de IA
 */
class AgentsViewModel(context: Context) : ViewModel() {
    private val TAG = "AgentsVM"
    private val repository = AgentsRepository(context)
    private val tokenManager = TokenManager(context)

    // Estado de la UI
    private val _state = MutableStateFlow<AgentsState>(AgentsState.Loading)
    val state: StateFlow<AgentsState> = _state.asStateFlow()

    // Lista de agentes
    private val _agents = MutableStateFlow<List<Agent>>(emptyList())
    val agents: StateFlow<List<Agent>> = _agents.asStateFlow()

    // Agente seleccionado
    private val _selectedAgent = MutableStateFlow<Agent?>(null)
    val selectedAgent: StateFlow<Agent?> = _selectedAgent.asStateFlow()

    // Resultado de test
    private val _testResult = MutableStateFlow<AgentTestResponse?>(null)
    val testResult: StateFlow<AgentTestResponse?> = _testResult.asStateFlow()

    // Filtros
    private val _showOnlyActive = MutableStateFlow(true)
    val showOnlyActive: StateFlow<Boolean> = _showOnlyActive.asStateFlow()

    init {
        loadAgents()
    }

    /**
     * Cargar todos los agentes
     */
    fun loadAgents(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!forceRefresh && _agents.value.isNotEmpty()) {
                    _state.value = AgentsState.Success
                    return@launch
                }

                _state.value = AgentsState.Loading
                val token = tokenManager.getAccessToken().first() ?: run {
                    _state.value = AgentsState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = repository.getAgents(
                    token = token,
                    isActive = if (_showOnlyActive.value) true else null
                )

                result.fold(
                    onSuccess = { response ->
                        _agents.value = response.agents.sortedBy { it.orderPriority }
                        _state.value = AgentsState.Success
                        Log.d(TAG, "✅ ${response.count} agentes cargados")
                    },
                    onFailure = { error ->
                        _state.value = AgentsState.Error(error.message ?: "Error desconocido")
                        Log.e(TAG, "❌ Error cargando agentes: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = AgentsState.Error(e.message ?: "Error desconocido")
                Log.e(TAG, "❌ Error: ${e.message}", e)
            }
        }
    }

    /**
     * Obtener un agente por ID
     */
    fun loadAgent(agentId: String) {
        viewModelScope.launch {
            try {
                _state.value = AgentsState.Loading
                val token = tokenManager.getAccessToken().first() ?: run {
                    _state.value = AgentsState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = repository.getAgent(token, agentId)

                result.fold(
                    onSuccess = { agent ->
                        _selectedAgent.value = agent
                        _state.value = AgentsState.Success
                        Log.d(TAG, "✅ Agente cargado: ${agent.name}")
                    },
                    onFailure = { error ->
                        _state.value = AgentsState.Error(error.message ?: "Error desconocido")
                        Log.e(TAG, "❌ Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = AgentsState.Error(e.message ?: "Error desconocido")
                Log.e(TAG, "❌ Error: ${e.message}", e)
            }
        }
    }

    /**
     * Crear un nuevo agente
     */
    fun createAgent(
        name: String,
        description: String,
        agentPrompt: String,
        orderPriority: Int,
        model: String = "gemini-2.0-flash-exp",
        temperature: Float = 0.7f,
        maxTokens: Int = 1000,
        fallbackMessage: String? = null
    ) {
        viewModelScope.launch {
            try {
                _state.value = AgentsState.Loading
                val token = tokenManager.getAccessToken().first() ?: run {
                    _state.value = AgentsState.Error("No hay token de autenticación")
                    return@launch
                }

                val request = AgentCreateRequest(
                    name = name.lowercase().replace(" ", "_"),
                    description = description,
                    agentPrompt = agentPrompt,
                    orderPriority = orderPriority,
                    model = model,
                    temperature = temperature,
                    maxTokens = maxTokens,
                    fallbackMessage = fallbackMessage
                )

                val result = repository.createAgent(token, request)

                result.fold(
                    onSuccess = { agent ->
                        _state.value = AgentsState.AgentCreated(agent)
                        loadAgents(forceRefresh = true)
                        Log.d(TAG, "✅ Agente creado: ${agent.name}")
                    },
                    onFailure = { error ->
                        _state.value = AgentsState.Error(error.message ?: "Error desconocido")
                        Log.e(TAG, "❌ Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = AgentsState.Error(e.message ?: "Error desconocido")
                Log.e(TAG, "❌ Error: ${e.message}", e)
            }
        }
    }

    /**
     * Actualizar un agente
     */
    fun updateAgent(
        agentId: String,
        description: String? = null,
        agentPrompt: String? = null,
        orderPriority: Int? = null,
        model: String? = null,
        temperature: Float? = null,
        maxTokens: Int? = null,
        fallbackMessage: String? = null
    ) {
        viewModelScope.launch {
            try {
                _state.value = AgentsState.Loading
                val token = tokenManager.getAccessToken().first() ?: run {
                    _state.value = AgentsState.Error("No hay token de autenticación")
                    return@launch
                }

                val request = AgentUpdateRequest(
                    description = description,
                    agentPrompt = agentPrompt,
                    orderPriority = orderPriority,
                    model = model,
                    temperature = temperature,
                    maxTokens = maxTokens,
                    fallbackMessage = fallbackMessage
                )

                val result = repository.updateAgent(token, agentId, request)

                result.fold(
                    onSuccess = { agent ->
                        _state.value = AgentsState.AgentUpdated(agent)
                        loadAgents(forceRefresh = true)
                        Log.d(TAG, "✅ Agente actualizado")
                    },
                    onFailure = { error ->
                        _state.value = AgentsState.Error(error.message ?: "Error desconocido")
                        Log.e(TAG, "❌ Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = AgentsState.Error(e.message ?: "Error desconocido")
                Log.e(TAG, "❌ Error: ${e.message}", e)
            }
        }
    }

    /**
     * Eliminar un agente
     */
    fun deleteAgent(agentId: String) {
        viewModelScope.launch {
            try {
                _state.value = AgentsState.Loading
                val token = tokenManager.getAccessToken().first() ?: run {
                    _state.value = AgentsState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = repository.deleteAgent(token, agentId)

                result.fold(
                    onSuccess = { message ->
                        _state.value = AgentsState.AgentDeleted(message)
                        loadAgents(forceRefresh = true)
                        Log.d(TAG, "✅ Agente eliminado")
                    },
                    onFailure = { error ->
                        _state.value = AgentsState.Error(error.message ?: "Error desconocido")
                        Log.e(TAG, "❌ Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = AgentsState.Error(e.message ?: "Error desconocido")
                Log.e(TAG, "❌ Error: ${e.message}", e)
            }
        }
    }

    /**
     * Activar/Desactivar un agente
     */
    fun toggleAgentStatus(agentId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            try {
                _state.value = AgentsState.Loading
                val token = tokenManager.getAccessToken().first() ?: run {
                    _state.value = AgentsState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = if (currentStatus) {
                    repository.deactivateAgent(token, agentId)
                } else {
                    repository.activateAgent(token, agentId)
                }

                result.fold(
                    onSuccess = { agent ->
                        _state.value = AgentsState.Success
                        loadAgents(forceRefresh = true)
                        Log.d(TAG, "✅ Estado del agente actualizado")
                    },
                    onFailure = { error ->
                        _state.value = AgentsState.Error(error.message ?: "Error desconocido")
                        Log.e(TAG, "❌ Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = AgentsState.Error(e.message ?: "Error desconocido")
                Log.e(TAG, "❌ Error: ${e.message}", e)
            }
        }
    }

    /**
     * Recargar agentes desde el backend
     */
    fun reloadAgents() {
        viewModelScope.launch {
            try {
                _state.value = AgentsState.Loading
                val token = tokenManager.getAccessToken().first() ?: run {
                    _state.value = AgentsState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = repository.reloadAgents(token)

                result.fold(
                    onSuccess = { response ->
                        _state.value = AgentsState.AgentsReloaded(response.message)
                        loadAgents(forceRefresh = true)
                        Log.d(TAG, "✅ ${response.message}")
                    },
                    onFailure = { error ->
                        _state.value = AgentsState.Error(error.message ?: "Error desconocido")
                        Log.e(TAG, "❌ Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = AgentsState.Error(e.message ?: "Error desconocido")
                Log.e(TAG, "❌ Error: ${e.message}", e)
            }
        }
    }

    /**
     * Probar un agente
     */
    fun testAgent(agentId: String, message: String) {
        viewModelScope.launch {
            try {
                _state.value = AgentsState.Testing
                val token = tokenManager.getAccessToken().first() ?: run {
                    _state.value = AgentsState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = repository.testAgent(token, agentId, message)

                result.fold(
                    onSuccess = { response ->
                        _testResult.value = response
                        _state.value = AgentsState.TestCompleted(response)
                        Log.d(TAG, "✅ Test completado")
                    },
                    onFailure = { error ->
                        _state.value = AgentsState.Error(error.message ?: "Error desconocido")
                        Log.e(TAG, "❌ Error: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _state.value = AgentsState.Error(e.message ?: "Error desconocido")
                Log.e(TAG, "❌ Error: ${e.message}", e)
            }
        }
    }

    /**
     * Cambiar filtro de agentes activos
     */
    fun toggleShowOnlyActive() {
        _showOnlyActive.value = !_showOnlyActive.value
        loadAgents(forceRefresh = true)
    }

    /**
     * Limpiar resultado de test
     */
    fun clearTestResult() {
        _testResult.value = null
    }

    /**
     * Limpiar agente seleccionado
     */
    fun clearSelectedAgent() {
        _selectedAgent.value = null
    }

    /**
     * Limpiar estado
     */
    fun clearState() {
        _state.value = AgentsState.Success
    }
}

/**
 * Estados de la UI
 */
sealed class AgentsState {
    object Loading : AgentsState()
    object Testing : AgentsState()
    object Success : AgentsState()
    data class Error(val message: String) : AgentsState()
    data class AgentCreated(val agent: Agent) : AgentsState()
    data class AgentUpdated(val agent: Agent) : AgentsState()
    data class AgentDeleted(val message: String) : AgentsState()
    data class AgentsReloaded(val message: String) : AgentsState()
    data class TestCompleted(val result: AgentTestResponse) : AgentsState()
}

