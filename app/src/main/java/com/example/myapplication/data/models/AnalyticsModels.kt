package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

// ==================== DAILY ANALYTICS MODELS ====================

data class DailyAnalytics(
    val date: String,
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("total_messages")
    val totalMessages: Int,
    @SerializedName("unique_users")
    val uniqueUsers: Int,
    @SerializedName("total_sessions")
    val totalSessions: Int,
    @SerializedName("avg_response_time_ms")
    val avgResponseTimeMs: Double
)

data class DailyAnalyticsResponse(
    val analytics: List<DailyAnalytics>,
    val totals: AnalyticsTotals,
    val period: PeriodInfo,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class AnalyticsTotals(
    @SerializedName("total_messages")
    val totalMessages: Int,
    @SerializedName("total_users")
    val totalUsers: Int,
    @SerializedName("total_sessions")
    val totalSessions: Int,
    @SerializedName("avg_response_time")
    val avgResponseTime: Double
)

data class PeriodInfo(
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    val days: Int
)

// ==================== HOURLY ANALYTICS MODELS ====================

data class HourlyAnalytics(
    val date: String,
    val hour: Int,
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("message_count")
    val messageCount: Int,
    @SerializedName("unique_users")
    val uniqueUsers: Int,
    @SerializedName("avg_response_time_ms")
    val avgResponseTimeMs: Double?,
    @SerializedName("successful_messages")
    val successfulMessages: Int
)

data class HourlyAnalyticsResponse(
    @SerializedName("hourly_activity")
    val hourlyActivity: List<HourlyActivity>,
    @SerializedName("peak_hour")
    val peakHour: HourlyActivity?,
    @SerializedName("raw_data")
    val rawData: List<HourlyAnalytics>,
    @SerializedName("company_id")
    val companyId: String,
    val source: String,
    val timestamp: String
)

data class HourlyActivity(
    val hour: Int,
    @SerializedName("total_messages")
    val totalMessages: Int,
    @SerializedName("total_users")
    val totalUsers: Int,
    @SerializedName("avg_response_time_ms")
    val avgResponseTimeMs: Double,
    @SerializedName("successful_messages")
    val successfulMessages: Int,
    @SerializedName("days_count")
    val daysCount: Int
)

// ==================== INTENT ANALYTICS MODELS ====================

data class IntentStats(
    val date: String,
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("intent_detected")
    val intentDetected: String,
    val count: Int,
    @SerializedName("avg_confidence")
    val avgConfidence: Double?,
    @SerializedName("success_rate")
    val successRate: Double?
)

data class IntentAnalyticsResponse(
    @SerializedName("intent_summary")
    val intentSummary: List<IntentSummary>,
    @SerializedName("top_intents")
    val topIntents: List<IntentSummary>,
    @SerializedName("raw_data")
    val rawData: List<IntentStats>,
    val period: PeriodInfo,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class IntentSummary(
    @SerializedName("intent_name")
    val intentName: String,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("avg_confidence")
    val avgConfidence: Double,
    @SerializedName("avg_success_rate")
    val avgSuccessRate: Double,
    val dates: List<IntentDateBreakdown>
)

data class IntentDateBreakdown(
    val date: String,
    val count: Int,
    @SerializedName("avg_confidence")
    val avgConfidence: Double?,
    @SerializedName("success_rate")
    val successRate: Double?
)

data class IntentDetailsResponse(
    @SerializedName("intent_name")
    val intentName: String,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("avg_confidence")
    val avgConfidence: Double,
    @SerializedName("avg_success_rate")
    val avgSuccessRate: Double,
    @SerializedName("daily_breakdown")
    val dailyBreakdown: List<IntentStats>,
    @SerializedName("period_days")
    val periodDays: Int,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

// ==================== DASHBOARD MODELS ====================

data class DashboardOverview(
    val kpis: DashboardKPIs,
    @SerializedName("daily_trend")
    val dailyTrend: List<DailyAnalytics>,
    @SerializedName("top_intents")
    val topIntents: List<IntentStats>,
    @SerializedName("period_days")
    val periodDays: Int,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class DashboardKPIs(
    @SerializedName("total_messages")
    val totalMessages: Int,
    @SerializedName("unique_users")
    val uniqueUsers: Int,
    @SerializedName("total_sessions")
    val totalSessions: Int,
    @SerializedName("avg_response_time_ms")
    val avgResponseTimeMs: Double,
    @SerializedName("messages_per_user")
    val messagesPerUser: Double
)

