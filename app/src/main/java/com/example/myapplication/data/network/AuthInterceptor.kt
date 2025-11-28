package com.example.myapplication.data.network

import android.content.Context
import android.util.Log
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.models.RefreshTokenRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 🔐 Interceptor para manejar autenticación y renovación automática de tokens
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    private val TAG = "AuthInterceptor"
    private val tokenManager = TokenManager(context)

    // 🔒 Flag para evitar múltiples intentos de renovación simultáneos
    private var isRefreshing = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // ✅ No interceptar la petición de refresh token para evitar loop infinito
        if (originalRequest.url.encodedPath.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        // Intentar la petición original
        var response = chain.proceed(originalRequest)

        // Si obtenemos 401 (Unauthorized) y el mensaje es "Token expirado"
        if (response.code == 401 && !isRefreshing) {
            val responseBody = response.peekBody(Long.MAX_VALUE).string()

            if (responseBody.contains("Token expirado", ignoreCase = true)) {
                Log.d(TAG, "⚠️ Token expirado detectado, intentando renovar...")
                response.close()

                // Intentar renovar el token
                val newToken = runBlocking {
                    refreshAccessToken()
                }

                if (newToken != null) {
                    // Reintentar la petición original con el nuevo token
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()

                    Log.d(TAG, "🔄 Reintentando petición con nuevo token")
                    response = chain.proceed(newRequest)
                } else {
                    Log.e(TAG, "❌ No se pudo renovar el token - limpiando sesión")
                    // ✅ Limpiar tokens para forzar re-login
                    runBlocking {
                        tokenManager.clearTokens()
                    }
                }
            }
        }

        return response
    }

    private suspend fun refreshAccessToken(): String? {
        // ✅ Evitar múltiples intentos simultáneos
        if (isRefreshing) {
            Log.d(TAG, "⏭️ Ya hay un refresh en progreso, saltando...")
            return null
        }

        isRefreshing = true
        return try {
            val refreshToken = tokenManager.getRefreshToken().first()
            if (refreshToken.isNullOrEmpty()) {
                Log.e(TAG, "❌ No hay refresh token disponible")
                return null
            }

            Log.d(TAG, "🔄 Renovando access token...")

            // Obtener la base URL y cliente básico desde RetrofitClient
            val baseUrl = "http://192.168.1.13:8000/" // Usar la misma base URL

            // Crear cliente HTTP básico sin interceptores
            val basicClient = okhttp3.OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            // Crear una instancia temporal del API sin el interceptor para evitar loop infinito
            val tempRetrofit = retrofit2.Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(basicClient)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()

            val authApi = tempRetrofit.create(AuthApiService::class.java)
            val request = RefreshTokenRequest(refreshToken)
            val response = authApi.refreshToken(request)

            if (response.isSuccessful && response.body() != null) {
                val refreshResponse = response.body()!!
                val newAccessToken = refreshResponse.accessToken
                val newRefreshToken = refreshResponse.refreshToken ?: refreshToken
                val companyId = refreshResponse.user?.companyId

                // Guardar los nuevos tokens
                tokenManager.saveTokens(newAccessToken, newRefreshToken, companyId)
                Log.d(TAG, "✅ Token renovado exitosamente")

                newAccessToken
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "❌ Error renovando token: $errorBody")

                // ✅ Si el refresh token también expiró, limpiar todo
                if (response.code() == 401) {
                    Log.e(TAG, "❌ Refresh token también expiró - limpiando sesión")
                    tokenManager.clearTokens()
                }
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción renovando token: ${e.message}", e)
            null
        } finally {
            isRefreshing = false
        }
    }
}

