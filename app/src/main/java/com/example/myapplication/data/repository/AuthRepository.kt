package com.example.myapplication.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.models.*
import com.example.myapplication.data.network.RetrofitClient
import com.example.myapplication.utils.DeviceUtils
import kotlinx.coroutines.flow.first
import java.util.*

class AuthRepository(
    private val tokenManager: TokenManager,
    private val context: Context
) {

    private val api = RetrofitClient.authApi
    private val TAG = "AuthRepository"

    private suspend fun getDeviceFingerprint(): String {
        // Intentar obtener el fingerprint existente
        val existingFingerprint = tokenManager.getDeviceFingerprint().first()

        if (!existingFingerprint.isNullOrEmpty()) {
            Log.d(TAG, "📱 Usando fingerprint existente (storage): ${existingFingerprint.take(16)}... (len=${existingFingerprint.length})")
            return existingFingerprint
        }

        // Si no existe, generar uno nuevo y guardarlo
        val newFingerprint = DeviceUtils.generateDeviceFingerprint(context)
        tokenManager.saveDeviceFingerprint(newFingerprint)
        Log.d(TAG, "📱 Nuevo fingerprint generado y guardado (local): ${newFingerprint.take(16)}... (len=${newFingerprint.length})")
        return newFingerprint
    }

    // Nueva función de ayuda (debug) para comprobar si ya hay fingerprint guardado
    suspend fun isFingerprintStored(): Boolean {
        val existing = tokenManager.getDeviceFingerprint().first()
        return !existing.isNullOrEmpty()
    }

    private fun getDeviceName(): String {
        return DeviceUtils.getDeviceName()
    }

    private suspend fun getAuthHeader(): String {
        val token = tokenManager.getAccessToken().first()
        if (token.isNullOrEmpty()) {
            Log.e(TAG, "❌ No hay token de acceso disponible")
            throw IllegalStateException("No hay token de acceso disponible")
        }
        Log.d(TAG, "✅ Token recuperado para header: ${token.take(50)}...")
        return "Bearer $token"
    }

    // ==================== AUTH OPERATIONS ====================

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(
                email = email,
                password = password,
                deviceFingerprint = getDeviceFingerprint(),
                deviceName = getDeviceName()
            )

            // Log explícito indicando que el request contiene device_fingerprint (muestra parcial por seguridad)
            val fingerprint = request.deviceFingerprint
            Log.d(TAG, "🔐 Intentando login para: $email - enviando device_fingerprint: ${fingerprint?.take(16) ?: "null"}... (len=${fingerprint?.length ?: 0})")
            val response = api.login(request)

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                Log.d(TAG, "📥 Respuesta login - requires2FA: ${loginResponse.requires2FA}, accessToken: ${loginResponse.accessToken?.take(50)}")

                // ✅ Guardar tokens si el login fue exitoso (tiene accessToken y no requiere 2FA)
                if (!loginResponse.accessToken.isNullOrEmpty() && !loginResponse.requires2FA) {
                    loginResponse.accessToken?.let { accessToken ->
                        loginResponse.refreshToken?.let { refreshToken ->
                            val companyId = loginResponse.user?.companyId
                            Log.d(TAG, "💾 Guardando tokens después de login exitoso${if (companyId != null) " con company_id: $companyId" else ""}")
                            tokenManager.saveTokens(accessToken, refreshToken, companyId)
                            tokenManager.saveUserEmail(email)
                        }
                    }
                } else if (loginResponse.requires2FA) {
                    Log.d(TAG, "🔐 Login requiere 2FA, no se guardan tokens aún")
                } else {
                    Log.w(TAG, "⚠️ Login exitoso pero no hay accessToken")
                }

                Result.success(loginResponse)
            } else {
                Log.e(TAG, "❌ Error en login: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción en login: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        name: String,
        companyId: String? = null,
        phone: String? = null
    ): Result<RegisterResponse> {
        return try {
            val request = RegisterRequest(email, password, name, companyId, phone)
            val response = api.register(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error en registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmail(token: String): Result<Unit> {
        return try {
            val request = VerifyEmailRequest(token)
            val response = api.verifyEmail(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Token inválido o expirado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verify2FA(email: String, code: String): Result<LoginResponse> {
        return try {
            val request = Verify2FARequest(
                email = email,
                code = code,
                deviceFingerprint = getDeviceFingerprint()
            )

            Log.d(TAG, "🔐 Verificando 2FA para: $email - enviando device_fingerprint: ${request.deviceFingerprint?.take(16)}... (len=${request.deviceFingerprint?.length})")
            val response = api.verify2FA(request)

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                // ✅ Guardar tokens si accessToken existe (verificación exitosa)
                if (!loginResponse.accessToken.isNullOrEmpty()) {
                    loginResponse.accessToken?.let { accessToken ->
                        loginResponse.refreshToken?.let { refreshToken ->
                            val companyId = loginResponse.user?.companyId
                            Log.d(TAG, "💾 Guardando tokens después de verificación 2FA${if (companyId != null) " con company_id: $companyId" else ""}")
                            tokenManager.saveTokens(accessToken, refreshToken, companyId)
                            tokenManager.saveUserEmail(email)
                        }
                    }
                }

                Result.success(loginResponse)
            } else {
                Result.failure(Exception("Código 2FA inválido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyDevice(token: String): Result<Unit> {
        return try {
            val request = VerifyDeviceRequest(token)
            val response = api.verifyDevice(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Token de dispositivo inválido o expirado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendVerification(email: String, type: String = "device_verification"): Result<Unit> {
        return try {
            val request = ResendVerificationRequest(email, type)
            val response = api.resendVerification(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al reenviar verificación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enable2FA(): Result<Enable2FAResponse> {
        return try {
            val response = api.enable2FA(getAuthHeader())

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al habilitar 2FA"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun disable2FA(): Result<Unit> {
        return try {
            val response = api.disable2FA(getAuthHeader())

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al deshabilitar 2FA"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            val request = ForgotPasswordRequest(email)
            val response = api.requestPasswordReset(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al solicitar reset"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            val request = ResetPasswordRequest(token, newPassword)
            val response = api.resetPassword(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Token inválido o expirado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = tokenManager.getRefreshToken().first()
                ?: return Result.failure(Exception("No refresh token"))

            Log.d(TAG, "🔄 Refrescando access token...")
            val request = RefreshTokenRequest(refreshToken)
            val response = api.refreshToken(request)

            if (response.isSuccessful && response.body() != null) {
                val refreshResponse = response.body()!!
                val newAccessToken = refreshResponse.accessToken
                val newRefreshToken = refreshResponse.refreshToken ?: refreshToken
                val companyId = refreshResponse.user?.companyId

                Log.d(TAG, "✅ Token refrescado exitosamente${if (companyId != null) " con company_id: $companyId" else ""}")
                tokenManager.saveTokens(newAccessToken, newRefreshToken, companyId)
                Result.success(newAccessToken)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "❌ Error refrescando token: $errorBody")
                Result.failure(Exception(errorBody ?: "Error al refrescar token"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción refrescando token: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            api.logout(getAuthHeader())
            tokenManager.clearTokens()
            Result.success(Unit)
        } catch (e: Exception) {
            // Limpiar tokens localmente aunque falle la API
            tokenManager.clearTokens()
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<UserInfo> {
        return try {
            val response = api.getCurrentUser(getAuthHeader())

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error obteniendo usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLoginHistory(limit: Int = 20): Result<List<LoginAttempt>> {
        return try {
            val response = api.getLoginHistory(getAuthHeader(), limit)

            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Error obteniendo historial"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrustedDevices(): Result<List<TrustedDevice>> {
        return try {
            val response = api.getTrustedDevices(getAuthHeader())

            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Error obteniendo dispositivos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun revokeTrustedDevice(deviceId: String): Result<Unit> {
        return try {
            val response = api.revokeTrustedDevice(deviceId, getAuthHeader())

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error revocando dispositivo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken().first() != null
    }

    suspend fun clearTokens() {
        Log.d(TAG, "🧹 Limpiando tokens...")
        tokenManager.clearTokens()
    }
}
