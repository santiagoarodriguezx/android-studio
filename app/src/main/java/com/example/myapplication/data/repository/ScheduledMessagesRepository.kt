package com.example.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.models.*
import com.example.myapplication.data.network.RetrofitClient
import com.example.myapplication.utils.ErrorUtils
import kotlinx.coroutines.flow.first

/**
 * 📅 Repositorio para gestión de Mensajes Programados
 */
class ScheduledMessagesRepository(private val context: Context) {

    private val TAG = "ScheduledMessagesRepo"
    private val api = RetrofitClient.scheduledMessagesApi
    private val tokenManager = TokenManager(context)

    private suspend fun getAuthHeader(): String {
        val token = tokenManager.getAccessToken().first()
        if (token.isNullOrEmpty()) {
            Log.e(TAG, "❌ No hay token de acceso disponible para ScheduledMessages")
            throw IllegalStateException("No hay token de acceso disponible")
        }
        Log.d(TAG, "✅ Token recuperado para ScheduledMessages: ${token.take(20)}...")
        return "Bearer $token"
    }

    // ==================== PROGRAMAR MENSAJES ====================

    /**
     * Programar un mensaje para envío futuro
     */
    suspend fun scheduleMessage(
        recipients: List<String>,
        scheduledFor: String,
        timezone: String = "America/Bogota",
        messageContent: String? = null,
        aiPrompt: String? = null,
        aiContext: String? = null,
        metadata: Map<String, Any>? = null
    ): Result<ScheduleMessageResponse> {
        return try {
            Log.d(TAG, "📅 Programando mensaje para ${recipients.size} destinatarios")

            val request = ScheduleMessageRequest(
                recipients = recipients,
                scheduledFor = scheduledFor,
                timezone = timezone,
                messageContent = messageContent,
                aiPrompt = aiPrompt,
                aiContext = aiContext,
                metadata = metadata
            )

            val response = api.scheduleMessage(getAuthHeader(), request)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Mensaje programado exitosamente: ${response.body()!!.data.id}")
                Result.success(response.body()!!)
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
            Log.e(TAG, "❌ Error programando mensaje: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Generar mensaje con IA (solo preview, no programa)
     */
    suspend fun generateMessagePreview(
        prompt: String,
        context: String? = null
    ): Result<GenerateMessageResponse> {
        return try {
            Log.d(TAG, "🤖 Generando preview de mensaje con IA")

            val request = GenerateMessageRequest(
                prompt = prompt,
                context = context
            )

            val response = api.generateMessagePreview(getAuthHeader(), request)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Mensaje generado: ${response.body()!!.data.generatedMessage.take(50)}...")
                Result.success(response.body()!!)
            } else {
                val error = "Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error generando mensaje: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==================== OBTENER MENSAJES ====================

    /**
     * Obtener lista de mensajes programados
     */
    suspend fun getScheduledMessages(
        status: ScheduledMessageStatus? = null,
        limit: Int = 50,
        offset: Int = 0
    ): Result<ScheduledMessagesListResponse> {
        return try {
            Log.d(TAG, "📋 Obteniendo mensajes programados (limit: $limit, offset: $offset)")

            val statusString = status?.name?.lowercase()
            val response = api.getScheduledMessages(
                token = getAuthHeader(),
                status = statusString,
                limit = limit,
                offset = offset
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ ${response.body()!!.count} mensajes obtenidos")
                Result.success(response.body()!!)
            } else {
                val error = "Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo mensajes: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener detalle de un mensaje programado específico
     */
    suspend fun getScheduledMessage(messageId: String): Result<SingleScheduledMessageResponse> {
        return try {
            Log.d(TAG, "📄 Obteniendo detalle del mensaje: $messageId")

            val response = api.getScheduledMessage(getAuthHeader(), messageId)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Mensaje obtenido: ${response.body()!!.data.id}")
                Result.success(response.body()!!)
            } else {
                val error = "Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo mensaje: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==================== ACTUALIZAR MENSAJES ====================

    /**
     * Actualizar un mensaje programado (solo si está pending)
     */
    suspend fun updateScheduledMessage(
        messageId: String,
        messageContent: String? = null,
        scheduledFor: String? = null,
        recipients: List<String>? = null
    ): Result<UpdateScheduledMessageResponse> {
        return try {
            Log.d(TAG, "✏️ Actualizando mensaje: $messageId")

            val request = UpdateScheduledMessageRequest(
                messageContent = messageContent,
                scheduledFor = scheduledFor,
                recipients = recipients
            )

            val response = api.updateScheduledMessage(getAuthHeader(), messageId, request)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Mensaje actualizado exitosamente")
                Result.success(response.body()!!)
            } else {
                val error = "Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error actualizando mensaje: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Cancelar un mensaje programado
     */
    suspend fun cancelScheduledMessage(messageId: String): Result<CancelScheduledMessageResponse> {
        return try {
            Log.d(TAG, "🚫 Cancelando mensaje: $messageId")

            val response = api.cancelScheduledMessage(getAuthHeader(), messageId)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Mensaje cancelado exitosamente")
                Result.success(response.body()!!)
            } else {
                val error = "Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cancelando mensaje: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Enviar un mensaje programado AHORA
     */
    suspend fun sendMessageNow(messageId: String): Result<SendNowResponse> {
        return try {
            Log.d(TAG, "🚀 Enviando mensaje ahora: $messageId")

            val response = api.sendMessageNow(getAuthHeader(), messageId)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Mensaje enviado exitosamente")
                Result.success(response.body()!!)
            } else {
                val error = "Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error enviando mensaje: ${e.message}", e)
            Result.failure(e)
        }
    }
}

