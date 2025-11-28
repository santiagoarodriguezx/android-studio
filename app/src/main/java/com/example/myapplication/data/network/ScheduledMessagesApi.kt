package com.example.myapplication.data.network

import com.example.myapplication.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 📅 API para Mensajes Programados
 */
interface ScheduledMessagesApi {

    /**
     * Programar un mensaje para envío futuro
     */
    @POST("api/scheduled-messages/")
    suspend fun scheduleMessage(
        @Header("Authorization") token: String,
        @Body request: ScheduleMessageRequest
    ): Response<ScheduleMessageResponse>

    /**
     * Generar mensaje con IA (solo preview, no programa)
     */
    @POST("api/scheduled-messages/generate-message")
    suspend fun generateMessagePreview(
        @Header("Authorization") token: String,
        @Body request: GenerateMessageRequest
    ): Response<GenerateMessageResponse>

    /**
     * Obtener lista de mensajes programados
     */
    @GET("api/scheduled-messages/")
    suspend fun getScheduledMessages(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ScheduledMessagesListResponse>

    /**
     * Obtener detalle de un mensaje programado específico
     */
    @GET("api/scheduled-messages/{message_id}")
    suspend fun getScheduledMessage(
        @Header("Authorization") token: String,
        @Path("message_id") messageId: String
    ): Response<SingleScheduledMessageResponse>

    /**
     * Actualizar un mensaje programado (solo si está pending)
     */
    @PATCH("api/scheduled-messages/{message_id}")
    suspend fun updateScheduledMessage(
        @Header("Authorization") token: String,
        @Path("message_id") messageId: String,
        @Body request: UpdateScheduledMessageRequest
    ): Response<UpdateScheduledMessageResponse>

    /**
     * Cancelar un mensaje programado
     */
    @DELETE("api/scheduled-messages/{message_id}")
    suspend fun cancelScheduledMessage(
        @Header("Authorization") token: String,
        @Path("message_id") messageId: String
    ): Response<CancelScheduledMessageResponse>

    /**
     * Enviar un mensaje programado AHORA (sin esperar la hora programada)
     */
    @POST("api/scheduled-messages/{message_id}/send-now")
    suspend fun sendMessageNow(
        @Header("Authorization") token: String,
        @Path("message_id") messageId: String
    ): Response<SendNowResponse>
}

