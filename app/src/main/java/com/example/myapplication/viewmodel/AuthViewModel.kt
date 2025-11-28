package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.LoginResponse
import com.example.myapplication.data.models.RegisterResponse
import com.example.myapplication.data.models.UserInfo
import com.example.myapplication.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    data class LoginSuccess(val response: LoginResponse) : AuthState()
    data class RegisterSuccess(val response: RegisterResponse) : AuthState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // 🔒 Flag para evitar múltiples llamadas simultáneas
    private var isCheckingLoginStatus = false

    init {
        checkLoginStatus()
    }

    /**
     * Verificar el estado de login
     * Se llama automáticamente al iniciar y puede llamarse manualmente cuando sea necesario
     */
    fun checkLoginStatus() {
        // ✅ Evitar llamadas concurrentes
        if (isCheckingLoginStatus) {
            Log.d(TAG, "⏭️ Ya hay una verificación de login en progreso, saltando...")
            return
        }

        viewModelScope.launch {
            isCheckingLoginStatus = true
            try {
                val hasToken = repository.isLoggedIn()
                _isLoggedIn.value = hasToken

                if (hasToken) {
                    // ✅ Solo cargar usuario si realmente tenemos un token válido
                    try {
                        loadCurrentUser()
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error cargando usuario: ${e.message}")
                        // Si falla, probablemente el token expiró
                        _isLoggedIn.value = false
                        _currentUser.value = null
                        repository.clearTokens()
                    }
                } else {
                    // Si no hay token, limpiar el estado
                    _currentUser.value = null
                    _authState.value = AuthState.Idle
                }
            } finally {
                isCheckingLoginStatus = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.login(email, password)

            result.onSuccess { response ->
                Log.d(TAG, "🔍 Procesando respuesta de login:")
                Log.d(TAG, "  - requiresDeviceVerification: ${response.requiresDeviceVerification}")
                Log.d(TAG, "  - requires2FA: ${response.requires2FA}")
                Log.d(TAG, "  - accessToken: ${if(response.accessToken.isNullOrEmpty()) "null/empty" else "presente (${response.accessToken.length} chars)"}")
                Log.d(TAG, "  - success: ${response.success}")
                Log.d(TAG, "  - message: ${response.message}")

                when {
                    response.requiresDeviceVerification -> {
                        Log.d(TAG, "✉️ Dispositivo nuevo detectado - requiere verificación")
                        _authState.value = AuthState.LoginSuccess(response)
                    }
                    response.requires2FA -> {
                        Log.d(TAG, "🔐 Requiere código 2FA")
                        _authState.value = AuthState.LoginSuccess(response)
                    }
                    !response.accessToken.isNullOrEmpty() -> {
                        Log.d(TAG, "✅ Login exitoso - accessToken presente")
                        _isLoggedIn.value = true
                        loadCurrentUser()
                        _authState.value = AuthState.Success("Login exitoso")
                    }
                    response.success -> {
                        Log.d(TAG, "✅ Login exitoso - success=true")
                        _isLoggedIn.value = true
                        loadCurrentUser()
                        _authState.value = AuthState.Success("Login exitoso")
                    }
                    else -> {
                        Log.e(TAG, "❌ Login falló - ninguna condición de éxito cumplida")
                        _authState.value = AuthState.Error(response.message ?: "Error desconocido")
                    }
                }
            }.onFailure { error ->
                Log.e(TAG, "❌ Error en login: ${error.message}", error)
                _authState.value = AuthState.Error(error.message ?: "Error de conexión")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        companyId: String? = null,
        phone: String? = null
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.register(email, password, name, companyId, phone)

            result.onSuccess { response ->
                _authState.value = AuthState.RegisterSuccess(response)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error en registro")
            }
        }
    }

    fun verify2FA(email: String, code: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.verify2FA(email, code)

            result.onSuccess { response ->
                // ✅ Verificar si el access_token existe (login exitoso)
                if (!response.accessToken.isNullOrEmpty()) {
                    loadCurrentUser()
                    _authState.value = AuthState.Success("Verificación 2FA exitosa")
                } else {
                    _authState.value = AuthState.Error(response.message ?: "Código inválido")
                }
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error en verificación")
            }
        }
    }

    fun verifyDevice(token: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.verifyDevice(token)

            result.onSuccess {
                // Dispositivo verificado, intentar login nuevamente
                login(email, password)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al verificar dispositivo")
            }
        }
    }

    fun resendVerification(email: String, type: String = "device_verification") {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.resendVerification(email, type)

            result.onSuccess {
                _authState.value = AuthState.Success("Código reenviado exitosamente")
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al reenviar código")
            }
        }
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.requestPasswordReset(email)

            result.onSuccess {
                _authState.value = AuthState.Success(
                    "Si el email existe, recibirás instrucciones para resetear tu contraseña"
                )
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al enviar email")
            }
        }
    }

    fun resetPassword(token: String, newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.resetPassword(token, newPassword)

            result.onSuccess {
                _authState.value = AuthState.Success("Contraseña actualizada exitosamente")
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Token inválido")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // ✅ Verificar si hay tokens antes de intentar logout
            val hasTokens = repository.isLoggedIn()

            if (hasTokens) {
                // Si hay tokens, hacer logout en el servidor
                repository.logout()
            } else {
                // Si no hay tokens, solo limpiar el estado local
                Log.d(TAG, "⚠️ No hay tokens - solo limpiando estado local")
            }

            // ✅ SIEMPRE limpiar el estado local sin importar el resultado
            _isLoggedIn.value = false
            _currentUser.value = null
            _authState.value = AuthState.Idle

            Log.d(TAG, "🚪 Logout completado - isLoggedIn: ${_isLoggedIn.value}")
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val result = repository.getCurrentUser()

                result.onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    Log.d(TAG, "✅ Usuario cargado: ${user.email}")
                }.onFailure { error ->
                    Log.e(TAG, "❌ Error obteniendo usuario actual: ${error.message}")
                    // ✅ Si falla (token expirado, etc.), limpiar sesión
                    _isLoggedIn.value = false
                    _currentUser.value = null
                    // Limpiar tokens para evitar intentos repetidos
                    repository.clearTokens()
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción cargando usuario: ${e.message}")
                _isLoggedIn.value = false
                _currentUser.value = null
                repository.clearTokens()
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

