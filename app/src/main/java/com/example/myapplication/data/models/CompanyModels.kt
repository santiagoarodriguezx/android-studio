package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

/**
 * 🏢 Modelos para gestión de Companies
 */

/**
 * Modelo de Company (Compañía)
 */
data class Company(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("sector") val sector: String? = null,
    @SerializedName("website") val website: String? = null,
    @SerializedName("logo_url") val logoUrl: String? = null,
    @SerializedName("billing_email") val billingEmail: String? = null,
    @SerializedName("tax_id") val taxId: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("subscription_plan") val subscriptionPlan: String = "basic",
    @SerializedName("subscription_status") val subscriptionStatus: String = "active",
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

/**
 * Respuesta de obtener una compañía
 */
data class CompanyResponse(
    @SerializedName("company") val company: Company,
    @SerializedName("timestamp") val timestamp: String
)

