package com.example.myapplication.data.repository

import com.example.myapplication.data.models.*
import com.example.myapplication.data.network.RetrofitClient
import com.example.myapplication.data.local.TokenManager
import android.content.Context

class MessageLogsRepository(private val context: Context) {

    private val api = RetrofitClient.messageLogsApi
    private val tokenManager = TokenManager(context)

    private fun getAuthHeader(): String {
        val token = tokenManager.getAccessToken()
        return "Bearer $token"
    }

    // ==================== CRUD OPERATIONS ====================

    suspend fun createMessageLog(
        request: MessageLogCreateRequest,
        companyId: String? = null
    ): Result<MessageLogResponse> {
        return try {
            val response = api.createMessageLog(
                request = request,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessageLogs(
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
        companyId: String? = null,
        notifyWebsocket: Boolean? = true
    ): Result<MessageLogsListResponse> {
        return try {
            val response = api.getMessageLogs(
                userId = userId,
                sessionId = sessionId,
                messageType = messageType,
                classification = classification,
                intentDetected = intentDetected,
                status = status,
                startDate = startDate,
                endDate = endDate,
                limit = limit,
                offset = offset,
                companyId = companyId,
                notifyWebsocket = notifyWebsocket,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentMessageLogs(
        minutes: Int? = 30,
        limit: Int? = 50,
        companyId: String? = null,
        notifyWebsocket: Boolean? = true
    ): Result<RecentMessageLogsResponse> {
        return try {
            val response = api.getRecentMessageLogs(
                minutes = minutes,
                limit = limit,
                companyId = companyId,
                notifyWebsocket = notifyWebsocket,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessageLogById(
        logId: String,
        companyId: String? = null
    ): Result<MessageLogResponse> {
        return try {
            val response = api.getMessageLogById(
                logId = logId,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMessageLog(
        logId: String,
        request: MessageLogUpdateRequest,
        companyId: String? = null
    ): Result<MessageLogResponse> {
        return try {
            val response = api.updateMessageLog(
                logId = logId,
                request = request,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMessageLog(
        logId: String,
        companyId: String? = null
    ): Result<MessageLogDeleteResponse> {
        return try {
            val response = api.deleteMessageLog(
                logId = logId,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== ANALYTICS & STATS ====================

    suspend fun getRealtimeStats(
        minutes: Int? = 5,
        companyId: String? = null
    ): Result<RealtimeStats> {
        return try {
            val response = api.getRealtimeStats(
                minutes = minutes,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserMessageStats(
        userId: String,
        days: Int? = 7,
        companyId: String? = null
    ): Result<UserMessageStats> {
        return try {
            val response = api.getUserMessageStats(
                userId = userId,
                days = days,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

