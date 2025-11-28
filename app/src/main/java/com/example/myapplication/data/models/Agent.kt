package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

/**
 * 🤖 Modelos para Agentes de IA
 */

// ==================== REQUEST MODELS ====================

data class AgentCreateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("agent_prompt")
    val agentPrompt: String,

    @SerializedName("order_priority")
    val orderPriority: Int,

    @SerializedName("model")
    val model: String = "gemini-2.0-flash-exp",

    @SerializedName("temperature")
    val temperature: Float = 0.7f,

    @SerializedName("max_tokens")
    val maxTokens: Int = 1000,

    @SerializedName("fallback_message")
    val fallbackMessage: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true,

    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

data class AgentUpdateRequest(
    @SerializedName("description")
    val description: String? = null,

    @SerializedName("agent_prompt")
    val agentPrompt: String? = null,

    @SerializedName("order_priority")
    val orderPriority: Int? = null,

    @SerializedName("model")
    val model: String? = null,

    @SerializedName("temperature")
    val temperature: Float? = null,

    @SerializedName("max_tokens")
    val maxTokens: Int? = null,

    @SerializedName("fallback_message")
    val fallbackMessage: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean? = null,

    @SerializedName("metadata")
    val metadata: Map<String, Any>? = null
)

// ==================== RESPONSE MODELS ====================

data class Agent(
    @SerializedName("id")
    val id: String,

    @SerializedName("company_id")
    val companyId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("agent_prompt")
    val agentPrompt: String,

    @SerializedName("order_priority")
    val orderPriority: Int,

    @SerializedName("model")
    val model: String,

    @SerializedName("temperature")
    val temperature: Float,

    @SerializedName("max_tokens")
    val maxTokens: Int,

    @SerializedName("fallback_message")
    val fallbackMessage: String?,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("metadata")
    val metadata: Map<String, Any>?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("created_by")
    val createdBy: String?
)

data class AgentsListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("agents")
    val agents: List<Agent>,

    @SerializedName("count")
    val count: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int,

    @SerializedName("company_id")
    val companyId: String,

    @SerializedName("timestamp")
    val timestamp: String
)

data class AgentResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("agent")
    val agent: Agent,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("timestamp")
    val timestamp: String
)

data class AgentTestResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("agent_name")
    val agentName: String,

    @SerializedName("message_sent")
    val messageSent: String,

    @SerializedName("response")
    val response: String,

    @SerializedName("usage")
    val usage: Map<String, Any>?,

    @SerializedName("timestamp")
    val timestamp: String
)

data class AgentReloadResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("agents_count")
    val agentsCount: Int,

    @SerializedName("timestamp")
    val timestamp: String
)

data class AgentActionResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("agent")
    val agent: Agent? = null
)

