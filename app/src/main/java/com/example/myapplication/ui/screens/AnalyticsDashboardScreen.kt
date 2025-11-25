package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.AnalyticsViewModel
import com.example.myapplication.viewmodel.AnalyticsState
import com.example.myapplication.data.models.DashboardKPIs
import com.example.myapplication.data.models.DailyAnalytics

/**
 * 📊 Dashboard de Analytics - Pantalla Principal
 *
 * Muestra métricas clave, tendencias diarias y top intenciones
 * con diseño moderno y animaciones fluidas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboardScreen(
    viewModel: AnalyticsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val analyticsState by viewModel.analyticsState.collectAsState()
    val dashboardData by viewModel.dashboardOverview.collectAsState()

    var selectedPeriod by remember { mutableStateOf(7) }
    var showPeriodDialog by remember { mutableStateOf(false) }

    // Cargar datos al inicio
    LaunchedEffect(selectedPeriod) {
        viewModel.getDashboardOverview(days = selectedPeriod)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Analytics,
                            contentDescription = null,
                            tint = Primary
                        )
                        Text(
                            "Dashboard Analytics",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Selector de período
                    TextButton(onClick = { showPeriodDialog = true }) {
                        Icon(Icons.Outlined.CalendarToday, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("$selectedPeriod días", fontSize = 14.sp)
                    }

                    // Refrescar
                    IconButton(onClick = {
                        viewModel.getDashboardOverview(days = selectedPeriod)
                    }) {
                        Icon(Icons.Outlined.Refresh, "Refrescar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            when {
                analyticsState is AnalyticsState.Loading && dashboardData == null -> {
                    LoadingView()
                }

                analyticsState is AnalyticsState.Error && dashboardData == null -> {
                    ErrorView(
                        message = (analyticsState as AnalyticsState.Error).message,
                        onRetry = {
                            viewModel.getDashboardOverview(days = selectedPeriod)
                        }
                    )
                }

                dashboardData != null -> {
                    DashboardContent(
                        data = dashboardData!!,
                        isRefreshing = analyticsState is AnalyticsState.Loading
                    )
                }
            }
        }
    }

    // Dialog selector de período
    if (showPeriodDialog) {
        AlertDialog(
            onDismissRequest = { showPeriodDialog = false },
            title = { Text("Seleccionar Período") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(7, 14, 30, 60, 90).forEach { days ->
                        FilterChip(
                            selected = selectedPeriod == days,
                            onClick = {
                                selectedPeriod = days
                                showPeriodDialog = false
                            },
                            label = { Text("Últimos $days días") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPeriodDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun DashboardContent(
    data: com.example.myapplication.data.models.DashboardOverview,
    isRefreshing: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Indicador de carga mientras refresca
        if (isRefreshing) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary
                )
            }
        }

        // KPIs principales
        item {
            Text(
                "📈 Métricas Clave",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
        }

        item {
            KPIsGrid(kpis = data.kpis)
        }

        // Tendencia diaria
        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "📊 Tendencia Diaria",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
        }

        items(data.dailyTrend.take(7)) { dailyData ->
            DailyTrendCard(dailyData)
        }

        // Top intenciones
        if (data.topIntents.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "🎯 Top Intenciones",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            }

            items(data.topIntents.take(5)) { intent ->
                IntentCard(intent)
            }
        }
    }
}

@Composable
private fun KPIsGrid(kpis: DashboardKPIs) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPICard(
                title = "Mensajes",
                value = kpis.totalMessages.toString(),
                icon = Icons.Outlined.Message,
                color = Primary,
                modifier = Modifier.weight(1f)
            )
            KPICard(
                title = "Usuarios",
                value = kpis.uniqueUsers.toString(),
                icon = Icons.Outlined.People,
                color = Secondary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPICard(
                title = "Sesiones",
                value = kpis.totalSessions.toString(),
                icon = Icons.Outlined.DeviceHub,
                color = Success,
                modifier = Modifier.weight(1f)
            )
            KPICard(
                title = "Resp. Prom.",
                value = "${kpis.avgResponseTimeMs.toInt()}ms",
                icon = Icons.Outlined.Speed,
                color = Warning,
                modifier = Modifier.weight(1f)
            )
        }

        // Mensajes por usuario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Mensajes por Usuario",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    String.format("%.1f", kpis.messagesPerUser),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }
    }
}

@Composable
private fun KPICard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun DailyTrendCard(data: DailyAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    data.date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${data.totalMessages} mensajes • ${data.uniqueUsers} usuarios",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${data.totalSessions}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    "sesiones",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun IntentCard(intent: com.example.myapplication.data.models.IntentStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Psychology,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        intent.intentDetected,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    "${intent.count}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Secondary
                )
            }

            if (intent.avgConfidence != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = intent.avgConfidence.toFloat(),
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (intent.avgConfidence > 0.7) Success else Warning
                    )
                    Text(
                        "${(intent.avgConfidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = Primary)
            Text(
                "Cargando analytics...",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                "Error al cargar datos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Icon(Icons.Outlined.Refresh, null)
                Spacer(Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

