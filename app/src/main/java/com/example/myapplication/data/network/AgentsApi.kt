package com.example.myapplication.data.network

import com.example.myapplication.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 🤖 API para gestión de Agentes de IA
 */
interface AgentsApi {

    /**
     * Listar todos los agentes de la compañía
     */
    @GET("agents/")
    suspend fun getAgents(
        @Header("Authorization") token: String,
        @Query("is_active") isActive: Boolean? = null,
        @Query("order_by") orderBy: String = "order_priority",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<AgentsListResponse>

    /**
     * Obtener un agente por ID
     */
    @GET("agents/{agent_id}")
    suspend fun getAgent(
        @Header("Authorization") token: String,
        @Path("agent_id") agentId: String
    ): Response<AgentResponse>

    /**
     * Crear un nuevo agente
     */
    @POST("agents/")
    suspend fun createAgent(
        @Header("Authorization") token: String,
        @Body request: AgentCreateRequest
    ): Response<AgentResponse>

    /**
     * Actualizar un agente existente
     */
    @PATCH("agents/{agent_id}")
    suspend fun updateAgent(
        @Header("Authorization") token: String,
        @Path("agent_id") agentId: String,
        @Body request: AgentUpdateRequest
    ): Response<AgentResponse>

    /**
     * Eliminar un agente
     */
    @DELETE("agents/{agent_id}")
    suspend fun deleteAgent(
        @Header("Authorization") token: String,
        @Path("agent_id") agentId: String
    ): Response<AgentActionResponse>

    /**
     * Activar un agente
     */
    @POST("agents/{agent_id}/activate")
    suspend fun activateAgent(
        @Header("Authorization") token: String,
        @Path("agent_id") agentId: String
    ): Response<AgentActionResponse>

    /**
     * Desactivar un agente
     */
    @POST("agents/{agent_id}/deactivate")
    suspend fun deactivateAgent(
        @Header("Authorization") token: String,
        @Path("agent_id") agentId: String
    ): Response<AgentActionResponse>

    /**
     * Recargar todos los agentes
     */
    @POST("agents/reload")
    suspend fun reloadAgents(
        @Header("Authorization") token: String
    ): Response<AgentReloadResponse>

    /**
     * Probar un agente con un mensaje
     */
    @GET("agents/{agent_id}/test")
    suspend fun testAgent(
        @Header("Authorization") token: String,
        @Path("agent_id") agentId: String,
        @Query("message") message: String
    ): Response<AgentTestResponse>
}

