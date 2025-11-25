package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.*
import com.example.myapplication.data.repository.AnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AnalyticsState {
    object Idle : AnalyticsState()
    object Loading : AnalyticsState()
    data class Success(val message: String) : AnalyticsState()
    data class Error(val message: String) : AnalyticsState()
}

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AnalyticsRepository(application)

    private val _analyticsState = MutableStateFlow<AnalyticsState>(AnalyticsState.Idle)
    val analyticsState: StateFlow<AnalyticsState> = _analyticsState.asStateFlow()

    // ==================== DAILY ANALYTICS ====================

    private val _dailyAnalytics = MutableStateFlow<DailyAnalyticsResponse?>(null)
    val dailyAnalytics: StateFlow<DailyAnalyticsResponse?> = _dailyAnalytics.asStateFlow()

    fun getDailyAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        days: Int? = 30,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _analyticsState.value = AnalyticsState.Loading

            repository.getDailyAnalytics(startDate, endDate, days, companyId)
                .onSuccess { response ->
                    _dailyAnalytics.value = response
                    _analyticsState.value = AnalyticsState.Success("Analytics cargados exitosamente")
                }
                .onFailure { error ->
                    _analyticsState.value = AnalyticsState.Error(
                        error.message ?: "Error desconocido al cargar analytics"
                    )
                }
        }
    }

    // ==================== HOURLY ANALYTICS ====================

    private val _hourlyAnalytics = MutableStateFlow<HourlyAnalyticsResponse?>(null)
    val hourlyAnalytics: StateFlow<HourlyAnalyticsResponse?> = _hourlyAnalytics.asStateFlow()

    fun getHourlyAnalytics(
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _analyticsState.value = AnalyticsState.Loading

            repository.getHourlyAnalytics(date, startDate, endDate, companyId)
                .onSuccess { response ->
                    _hourlyAnalytics.value = response
                    _analyticsState.value = AnalyticsState.Success("Analytics por hora cargados")
                }
                .onFailure { error ->
                    _analyticsState.value = AnalyticsState.Error(
                        error.message ?: "Error al cargar analytics por hora"
                    )
                }
        }
    }

    // ==================== INTENT ANALYTICS ====================

    private val _intentAnalytics = MutableStateFlow<IntentAnalyticsResponse?>(null)
    val intentAnalytics: StateFlow<IntentAnalyticsResponse?> = _intentAnalytics.asStateFlow()

    fun getIntentAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        days: Int? = 30,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _analyticsState.value = AnalyticsState.Loading

            repository.getIntentAnalytics(startDate, endDate, days, companyId)
                .onSuccess { response ->
                    _intentAnalytics.value = response
                    _analyticsState.value = AnalyticsState.Success("Analytics de intenciones cargados")
                }
                .onFailure { error ->
                    _analyticsState.value = AnalyticsState.Error(
                        error.message ?: "Error al cargar analytics de intenciones"
                    )
                }
        }
    }

    private val _intentDetails = MutableStateFlow<IntentDetailsResponse?>(null)
    val intentDetails: StateFlow<IntentDetailsResponse?> = _intentDetails.asStateFlow()

    fun getIntentDetails(
        intentName: String,
        days: Int? = 30,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _analyticsState.value = AnalyticsState.Loading

            repository.getIntentDetails(intentName, days, companyId)
                .onSuccess { response ->
                    _intentDetails.value = response
                    _analyticsState.value = AnalyticsState.Success("Detalles cargados")
                }
                .onFailure { error ->
                    _analyticsState.value = AnalyticsState.Error(
                        error.message ?: "Error al cargar detalles"
                    )
                }
        }
    }

    // ==================== DASHBOARD ====================

    private val _dashboardOverview = MutableStateFlow<DashboardOverview?>(null)
    val dashboardOverview: StateFlow<DashboardOverview?> = _dashboardOverview.asStateFlow()

    fun getDashboardOverview(
        days: Int? = 7,
        companyId: String? = null
    ) {
        viewModelScope.launch {
            _analyticsState.value = AnalyticsState.Loading

            repository.getDashboardOverview(days, companyId)
                .onSuccess { response ->
                    _dashboardOverview.value = response
                    _analyticsState.value = AnalyticsState.Success("Dashboard cargado")
                }
                .onFailure { error ->
                    _analyticsState.value = AnalyticsState.Error(
                        error.message ?: "Error al cargar dashboard"
                    )
                }
        }
    }

    // ==================== UTILITY ====================

    fun resetState() {
        _analyticsState.value = AnalyticsState.Idle
    }

    fun clearDailyAnalytics() {
        _dailyAnalytics.value = null
    }

    fun clearHourlyAnalytics() {
        _hourlyAnalytics.value = null
    }

    fun clearIntentAnalytics() {
        _intentAnalytics.value = null
    }

    fun clearDashboard() {
        _dashboardOverview.value = null
    }
}

