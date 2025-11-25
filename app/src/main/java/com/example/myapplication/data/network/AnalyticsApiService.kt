package com.example.myapplication.data.network

import com.example.myapplication.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface AnalyticsApiService {

    // ==================== DAILY ANALYTICS ====================

    @GET("api/analytics/daily")
    suspend fun getDailyAnalytics(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("days") days: Int? = 30,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<DailyAnalyticsResponse>

    @GET("api/analytics/daily/{date}")
    suspend fun getDailyAnalyticsByDate(
        @Path("date") date: String,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<ApiResponse<DailyAnalytics>>

    // ==================== HOURLY ANALYTICS ====================

    @GET("api/analytics/hourly")
    suspend fun getHourlyAnalytics(
        @Query("date") date: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<HourlyAnalyticsResponse>

    // ==================== INTENT ANALYTICS ====================

    @GET("api/analytics/intents")
    suspend fun getIntentAnalytics(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("days") days: Int? = 30,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<IntentAnalyticsResponse>

    @GET("api/analytics/intents/{intent_name}")
    suspend fun getIntentDetails(
        @Path("intent_name") intentName: String,
        @Query("days") days: Int? = 30,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<IntentDetailsResponse>

    // ==================== DASHBOARD ====================

    @GET("api/analytics/dashboard")
    suspend fun getDashboardOverview(
        @Query("days") days: Int? = 7,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<DashboardOverview>
}

