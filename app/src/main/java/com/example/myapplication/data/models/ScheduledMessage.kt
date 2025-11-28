package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 📅 Modelos para Mensajes Programados
 */

// ==================== REQUEST MODELS ====================

data class ScheduleMessageRequest(
    @SerializedName("recipients")
    val recipients: List<String>,

    @SerializedName("scheduled_for")
    val scheduledFor: String, // ISO 8601 format

    @SerializedName("timezone")
    val timezone: String = "America/Bogota",

    @SerializedName("message_content")
    val messageContent: String? = null,

    @SerializedName("ai_prompt")
    val aiPrompt: String? = null,

    @SerializedName("ai_context")
    val aiContext: String? = null,

    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

data class GenerateMessageRequest(
    @SerializedName("prompt")
    val prompt: String,

    @SerializedName("context")
    val context: String? = null
)

data class UpdateScheduledMessageRequest(
    @SerializedName("message_content")
    val messageContent: String? = null,

    @SerializedName("scheduled_for")
    val scheduledFor: String? = null,

    @SerializedName("recipients")
    val recipients: List<String>? = null
)

// ==================== RESPONSE MODELS ====================

data class ScheduledMessage(
    @SerializedName("id")
    val id: String,

    @SerializedName("company_id")
    val companyId: String,

    @SerializedName("message_content")
    val messageContent: String,

    @SerializedName("recipients")
    val recipients: List<String>,

    @SerializedName("scheduled_for")
    val scheduledFor: String,

    @SerializedName("timezone")
    val timezone: String,

    @SerializedName("status")
    val status: ScheduledMessageStatus,

    @SerializedName("ai_generated")
    val aiGenerated: Boolean = false,

    @SerializedName("ai_prompt")
    val aiPrompt: String? = null,

    @SerializedName("ai_model")
    val aiModel: String? = null,

    @SerializedName("created_by")
    val createdBy: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("sent_at")
    val sentAt: String? = null,

    @SerializedName("sent_count")
    val sentCount: Int = 0,

    @SerializedName("failed_count")
    val failedCount: Int = 0,

    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

enum class ScheduledMessageStatus {
    @SerializedName("pending")
    PENDING,

    @SerializedName("sent")
    SENT,

    @SerializedName("failed")
    FAILED,

    @SerializedName("cancelled")
    CANCELLED
}

data class ScheduleMessageResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ScheduledMessageData
)

data class ScheduledMessageData(
    @SerializedName("id")
    val id: String,

    @SerializedName("message_content")
    val messageContent: String,

    @SerializedName("recipients_count")
    val recipientsCount: Int,

    @SerializedName("scheduled_for")
    val scheduledFor: String,

    @SerializedName("timezone")
    val timezone: String,

    @SerializedName("status")
    val status: ScheduledMessageStatus,

    @SerializedName("ai_generated")
    val aiGenerated: Boolean
)

data class GenerateMessageResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: GeneratedMessageData
)

data class GeneratedMessageData(
    @SerializedName("generated_message")
    val generatedMessage: String,

    @SerializedName("prompt")
    val prompt: String,

    @SerializedName("model")
    val model: String
)

data class ScheduledMessagesListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<ScheduledMessage>,

    @SerializedName("count")
    val count: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int
)

data class SingleScheduledMessageResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: ScheduledMessage
)

data class UpdateScheduledMessageResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ScheduledMessage
)

data class CancelScheduledMessageResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)

data class SendNowResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: Map<String, Any>
)

