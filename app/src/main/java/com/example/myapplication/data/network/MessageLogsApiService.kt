package com.example.myapplication.data.network

import com.example.myapplication.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface MessageLogsApiService {

    // ==================== CRUD OPERATIONS ====================

    @POST("api/message-logs/")
    suspend fun createMessageLog(
        @Body request: MessageLogCreateRequest,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<MessageLogResponse>

    @GET("api/message-logs/")
    suspend fun getMessageLogs(
        @Query("user_id") userId: String? = null,
        @Query("session_id") sessionId: String? = null,
        @Query("message_type") messageType: String? = null,
        @Query("classification") classification: String? = null,
        @Query("intent_detected") intentDetected: String? = null,
        @Query("status") status: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("limit") limit: Int? = 100,
        @Query("offset") offset: Int? = 0,
        @Query("company_id") companyId: String? = null,
        @Query("notify_websocket") notifyWebsocket: Boolean? = true,
        @Header("Authorization") token: String
    ): Response<MessageLogsListResponse>

    @GET("api/message-logs/recent")
    suspend fun getRecentMessageLogs(
        @Query("minutes") minutes: Int? = 30,
        @Query("limit") limit: Int? = 50,
        @Query("company_id") companyId: String? = null,
        @Query("notify_websocket") notifyWebsocket: Boolean? = true,
        @Header("Authorization") token: String
    ): Response<RecentMessageLogsResponse>

    @GET("api/message-logs/{log_id}")
    suspend fun getMessageLogById(
        @Path("log_id") logId: String,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<MessageLogResponse>

    @PATCH("api/message-logs/{log_id}")
    suspend fun updateMessageLog(
        @Path("log_id") logId: String,
        @Body request: MessageLogUpdateRequest,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<MessageLogResponse>

    @DELETE("api/message-logs/{log_id}")
    suspend fun deleteMessageLog(
        @Path("log_id") logId: String,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<MessageLogDeleteResponse>

    // ==================== ANALYTICS & STATS ====================

    @GET("api/message-logs/stats/realtime")
    suspend fun getRealtimeStats(
        @Query("minutes") minutes: Int? = 5,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<RealtimeStats>

    @GET("api/message-logs/stats/by-user/{user_id}")
    suspend fun getUserMessageStats(
        @Path("user_id") userId: String,
        @Query("days") days: Int? = 7,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<UserMessageStats>
}

