package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

// ==================== MESSAGE LOG MODELS ====================

data class MessageLog(
    val id: String,
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("session_id")
    val sessionId: String?,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("message_type")
    val messageType: String, // 'user', 'bot', 'system', 'image', 'audio'
    @SerializedName("message_content")
    val messageContent: String?,
    val classification: String,
    val status: String, // 'success', 'error', 'pending'
    @SerializedName("response_time_ms")
    val responseTimeMs: Int?,
    @SerializedName("intent_detected")
    val intentDetected: String?,
    @SerializedName("confidence_score")
    val confidenceScore: Double?,
    val metadata: Map<String, Any>?,
    @SerializedName("created_at")
    val createdAt: String
)

data class MessageLogCreateRequest(
    @SerializedName("session_id")
    val sessionId: String? = null,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("message_type")
    val messageType: String,
    @SerializedName("message_content")
    val messageContent: String? = null,
    val classification: String = "general",
    val status: String = "success",
    @SerializedName("response_time_ms")
    val responseTimeMs: Int? = null,
    @SerializedName("intent_detected")
    val intentDetected: String? = null,
    @SerializedName("confidence_score")
    val confidenceScore: Double? = null,
    val metadata: Map<String, Any>? = null
)

data class MessageLogUpdateRequest(
    val classification: String? = null,
    val status: String? = null,
    @SerializedName("response_time_ms")
    val responseTimeMs: Int? = null,
    @SerializedName("intent_detected")
    val intentDetected: String? = null,
    @SerializedName("confidence_score")
    val confidenceScore: Double? = null,
    val metadata: Map<String, Any>? = null
)

data class MessageLogResponse(
    @SerializedName("message_log")
    val messageLog: MessageLog,
    val message: String,
    @SerializedName("triggers_executed")
    val triggersExecuted: List<String>? = null,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class MessageLogsListResponse(
    @SerializedName("message_logs")
    val messageLogs: List<MessageLog>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val filters: MessageLogFilters,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class MessageLogFilters(
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("session_id")
    val sessionId: String?,
    @SerializedName("message_type")
    val messageType: String?,
    val classification: String?,
    @SerializedName("intent_detected")
    val intentDetected: String?,
    val status: String?,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("end_date")
    val endDate: String?
)

data class RecentMessageLogsResponse(
    @SerializedName("message_logs")
    val messageLogs: List<MessageLog>,
    val total: Int,
    val minutes: Int,
    @SerializedName("cutoff_time")
    val cutoffTime: String,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class MessageLogDeleteResponse(
    val deleted: Boolean,
    @SerializedName("log_id")
    val logId: String,
    val message: String,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

// ==================== REALTIME STATS MODELS ====================

data class RealtimeStats(
    @SerializedName("total_messages")
    val totalMessages: Int,
    @SerializedName("unique_users")
    val uniqueUsers: Int,
    @SerializedName("avg_response_time_ms")
    val avgResponseTimeMs: Double,
    @SerializedName("success_rate")
    val successRate: Double,
    @SerializedName("by_type")
    val byType: Map<String, Int>,
    @SerializedName("by_classification")
    val byClassification: Map<String, Int>,
    @SerializedName("by_intent")
    val byIntent: Map<String, Int>,
    @SerializedName("time_window_minutes")
    val timeWindowMinutes: Int,
    @SerializedName("cutoff_time")
    val cutoffTime: String,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class UserMessageStats(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("total_messages")
    val totalMessages: Int,
    @SerializedName("by_type")
    val byType: Map<String, Int>,
    @SerializedName("by_intent")
    val byIntent: Map<String, Int>,
    @SerializedName("avg_confidence_score")
    val avgConfidenceScore: Double,
    @SerializedName("period_days")
    val periodDays: Int,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

// ==================== WEBSOCKET MODELS ====================

data class WebSocketMessage(
    val type: String, // "initial_data", "new_message_log", "message_log_updated", etc.
    val data: Any?,
    val total: Int? = null,
    val message: String? = null,
    val timestamp: String
)

data class WebSocketCommand(
    val action: String, // "subscribe", "get_recent", "get_by_user"
    @SerializedName("company_id")
    val companyId: String? = null,
    val minutes: Int? = null,
    @SerializedName("user_id")
    val userId: String? = null
)

