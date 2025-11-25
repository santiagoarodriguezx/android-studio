package com.example.myapplication.data.repository

import com.example.myapplication.data.models.*
import com.example.myapplication.data.network.RetrofitClient
import com.example.myapplication.data.local.TokenManager
import android.content.Context

class AnalyticsRepository(private val context: Context) {

    private val api = RetrofitClient.analyticsApi
    private val tokenManager = TokenManager(context)

    private fun getAuthHeader(): String {
        val token = tokenManager.getAccessToken()
        return "Bearer $token"
    }

    // ==================== DAILY ANALYTICS ====================

    suspend fun getDailyAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        days: Int? = 30,
        companyId: String? = null
    ): Result<DailyAnalyticsResponse> {
        return try {
            val response = api.getDailyAnalytics(
                startDate = startDate,
                endDate = endDate,
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

    suspend fun getDailyAnalyticsByDate(
        date: String,
        companyId: String? = null
    ): Result<DailyAnalytics> {
        return try {
            val response = api.getDailyAnalyticsByDate(
                date = date,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HOURLY ANALYTICS ====================

    suspend fun getHourlyAnalytics(
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        companyId: String? = null
    ): Result<HourlyAnalyticsResponse> {
        return try {
            val response = api.getHourlyAnalytics(
                date = date,
                startDate = startDate,
                endDate = endDate,
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

    // ==================== INTENT ANALYTICS ====================

    suspend fun getIntentAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        days: Int? = 30,
        companyId: String? = null
    ): Result<IntentAnalyticsResponse> {
        return try {
            val response = api.getIntentAnalytics(
                startDate = startDate,
                endDate = endDate,
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

    suspend fun getIntentDetails(
        intentName: String,
        days: Int? = 30,
        companyId: String? = null
    ): Result<IntentDetailsResponse> {
        return try {
            val response = api.getIntentDetails(
                intentName = intentName,
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

    // ==================== DASHBOARD ====================

    suspend fun getDashboardOverview(
        days: Int? = 7,
        companyId: String? = null
    ): Result<DashboardOverview> {
        return try {
            val response = api.getDashboardOverview(
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

