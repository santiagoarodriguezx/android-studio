package com.example.myapplication.data.network

import com.example.myapplication.data.models.CompanyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * 🏢 API Service para gestión de Companies
 */
interface CompaniesApiService {

    /**
     * Obtener información de una compañía por ID
     * Solo super admins pueden ver cualquier company
     * Admins normales solo pueden ver su propia company
     */
    @GET("api/companies/{company_id}")
    suspend fun getCompanyById(
        @Path("company_id") companyId: String,
        @Header("Authorization") token: String
    ): Response<CompanyResponse>
}

