package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.models.Company
import com.example.myapplication.data.network.RetrofitClient
import kotlinx.coroutines.flow.first

/**
 * 🏢 Repositorio para gestión de Companies
 */
class CompanyRepository(private val tokenManager: TokenManager) {

    private val api = RetrofitClient.companiesApi
    private val TAG = "CompanyRepository"

    private suspend fun getAuthHeader(): String {
        val token = tokenManager.getAccessToken().first()
        if (token.isNullOrEmpty()) {
            throw IllegalStateException("No hay token de acceso disponible")
        }
        return "Bearer $token"
    }

    /**
     * Obtener información de una compañía por ID
     */
    suspend fun getCompanyById(companyId: String): Result<Company> {
        return try {
            Log.d(TAG, "🏢 Obteniendo información de compañía: $companyId")
            val response = api.getCompanyById(
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Información de compañía obtenida: ${response.body()!!.company.name}")
                Result.success(response.body()!!.company)
            } else {
                val error = "Error ${response.code()}: ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error obteniendo compañía: ${e.message}", e)
            Result.failure(e)
        }
    }
}

