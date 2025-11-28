package com.example.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.models.*
import com.example.myapplication.data.network.AgentsApi
import com.example.myapplication.data.network.AuthInterceptor
import com.example.myapplication.utils.ErrorUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * 🤖 Repositorio para gestión de Agentes de IA
 */
class AgentsRepository(private val context: Context) {
    private val TAG = "AgentsRepository"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor(context))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.13:8000/api/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: AgentsApi = retrofit.create(AgentsApi::class.java)

    /**
     * Obtener todos los agentes
     */
    suspend fun getAgents(
        token: String,
        isActive: Boolean? = null,
        orderBy: String = "order_priority",
        limit: Int = 50,
        offset: Int = 0
    ): Result<AgentsListResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 GET /agents/ - Obteniendo agentes...")
            val response = api.getAgents("Bearer $token", isActive, orderBy, limit, offset)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "✅ ${body.count} agentes obtenidos")
                    Result.success(body)
                } else {
                    Log.e(TAG, "❌ Respuesta vacía o no exitosa")
                    Result.failure(Exception("Error obteniendo agentes"))
                }
            } else {
                val errorMsg = "Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener un agente por ID
     */
    suspend fun getAgent(token: String, agentId: String): Result<Agent> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 GET /agents/$agentId")
            val response = api.getAgent("Bearer $token", agentId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "✅ Agente obtenido: ${body.agent.name}")
                    Result.success(body.agent)
                } else {
                    Result.failure(Exception("Error obteniendo agente"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Crear un nuevo agente
     */
    suspend fun createAgent(token: String, request: AgentCreateRequest): Result<Agent> =
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 POST /agents/ - Creando agente: ${request.name}")
            val response = api.createAgent("Bearer $token", request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "✅ Agente creado: ${body.agent.name}")
                    Result.success(body.agent)
                } else {
                    Result.failure(Exception("Error creando agente"))
                }
            } else {
                val errorMsg = ErrorUtils.extractErrorMessage(
                    response.errorBody()?.string(),
                    response.code(),
                    response.message()
                )
                Log.e(TAG, "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Actualizar un agente
     */
    suspend fun updateAgent(
        token: String,
        agentId: String,
        request: AgentUpdateRequest
    ): Result<Agent> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 PATCH /agents/$agentId - Actualizando agente")
            val response = api.updateAgent("Bearer $token", agentId, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "✅ Agente actualizado")
                    Result.success(body.agent)
                } else {
                    Result.failure(Exception("Error actualizando agente"))
                }
            } else {
                val errorMsg = ErrorUtils.extractErrorMessage(
                    response.errorBody()?.string(),
                    response.code(),
                    response.message()
                )
                Log.e(TAG, "❌ $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Eliminar un agente
     */
    suspend fun deleteAgent(token: String, agentId: String): Result<String> =
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 DELETE /agents/$agentId")
            val response = api.deleteAgent("Bearer $token", agentId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "✅ Agente eliminado")
                    Result.success(body.message)
                } else {
                    Result.failure(Exception("Error eliminando agente"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Activar un agente
     */
    suspend fun activateAgent(token: String, agentId: String): Result<Agent> =
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 POST /agents/$agentId/activate")
            val response = api.activateAgent("Bearer $token", agentId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.agent != null) {
                    Log.d(TAG, "✅ Agente activado")
                    Result.success(body.agent)
                } else {
                    Result.failure(Exception("Error activando agente"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Desactivar un agente
     */
    suspend fun deactivateAgent(token: String, agentId: String): Result<Agent> =
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 POST /agents/$agentId/deactivate")
            val response = api.deactivateAgent("Bearer $token", agentId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.agent != null) {
                    Log.d(TAG, "✅ Agente desactivado")
                    Result.success(body.agent)
                } else {
                    Result.failure(Exception("Error desactivando agente"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Recargar agentes
     */
    suspend fun reloadAgents(token: String): Result<AgentReloadResponse> =
        withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 POST /agents/reload")
            val response = api.reloadAgents("Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "✅ Agentes recargados")
                    Result.success(body)
                } else {
                    Result.failure(Exception("Error recargando agentes"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Probar un agente
     */
    suspend fun testAgent(
        token: String,
        agentId: String,
        message: String
    ): Result<AgentTestResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📡 GET /agents/$agentId/test")
            val response = api.testAgent("Bearer $token", agentId, message)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success) {
                    Log.d(TAG, "✅ Agente probado exitosamente")
                    Result.success(body)
                } else {
                    Result.failure(Exception("Error probando agente"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }
}

