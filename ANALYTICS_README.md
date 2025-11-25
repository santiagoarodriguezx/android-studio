# 📱 MyApplication2 - Sistema Completo de Analytics y Message Logs

## 🎯 Características Implementadas

### ✅ Sistema de Autenticación Completo
- **Login/Register** con validación
- **Verificación de Email**
- **Autenticación 2FA** (TOTP)
- **Verificación de Dispositivos Nuevos**
- **Recuperación de Contraseña**
- **Refresh Tokens**
- **Gestión de Dispositivos Confiables**

### 📊 Analytics Dashboard
Pantalla moderna con:
- **KPIs Principales**: Mensajes totales, usuarios únicos, sesiones, tiempo de respuesta
- **Tendencia Diaria**: Visualización de métricas por día
- **Top Intenciones**: Las intenciones más detectadas con confianza
- **Selector de Período**: 7, 14, 30, 60, 90 días
- **Auto-refresh**: Actualización automática de datos

### 💬 Message Logs en Tiempo Real
Pantalla interactiva con:
- **Logs en Tiempo Real**: Auto-refresh cada 10 segundos
- **Estadísticas en Vivo**: Mensajes, usuarios, tasa de éxito, tiempo de respuesta
- **Filtros Avanzados**: Por ventana de tiempo (5m, 15m, 30m, 1h, 2h)
- **Detalles de Logs**: Vista completa con metadata
- **Indicadores de Estado**: Success/Error/Pending con colores
- **Eliminación de Logs**: Con confirmación

## 📁 Estructura del Proyecto

```
app/src/main/java/com/example/myapplication/
│
├── data/
│   ├── models/
│   │   ├── AuthModels.kt              # Modelos de autenticación
│   │   ├── AnalyticsModels.kt         # ✨ Modelos de analytics
│   │   └── MessageLogModels.kt        # ✨ Modelos de message logs
│   │
│   ├── network/
│   │   ├── RetrofitClient.kt          # Cliente HTTP (actualizado)
│   │   ├── AuthApiService.kt          # API de auth
│   │   ├── AnalyticsApiService.kt     # ✨ API de analytics
│   │   └── MessageLogsApiService.kt   # ✨ API de message logs
│   │
│   ├── repository/
│   │   ├── AuthRepository.kt          # Repositorio de auth
│   │   ├── AnalyticsRepository.kt     # ✨ Repositorio de analytics
│   │   └── MessageLogsRepository.kt   # ✨ Repositorio de message logs
│   │
│   └── local/
│       └── TokenManager.kt            # Gestión de tokens
│
├── viewmodel/
│   ├── AuthViewModel.kt               # ViewModel de auth
│   ├── AnalyticsViewModel.kt          # ✨ ViewModel de analytics
│   └── MessageLogsViewModel.kt        # ✨ ViewModel de message logs
│
├── ui/
│   ├── screens/
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── TwoFactorScreen.kt
│   │   ├── DeviceVerificationScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   ├── HomeScreen.kt              # Actualizado con navegación
│   │   ├── AnalyticsDashboardScreen.kt # ✨ Dashboard de analytics
│   │   └── MessageLogsScreen.kt        # ✨ Logs en tiempo real
│   │
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
└── navigation/
    ├── Screen.kt                      # Rutas (actualizado)
    └── AppNavigation.kt               # Navegación (actualizado)
```

## 🔧 Configuración

### 1. Actualizar Base URL
En `RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "http://TU_IP:8000/"
```

### 2. Endpoints del Backend

#### Analytics Endpoints
```
GET  /api/analytics/daily              # Analytics diarios
GET  /api/analytics/daily/{date}       # Analytics de un día específico
GET  /api/analytics/hourly             # Analytics por hora
GET  /api/analytics/intents            # Analytics de intenciones
GET  /api/analytics/intents/{intent}   # Detalles de intención
GET  /api/analytics/dashboard          # Dashboard overview
```

#### Message Logs Endpoints
```
POST   /api/message-logs/              # Crear log
GET    /api/message-logs/              # Obtener logs (con filtros)
GET    /api/message-logs/recent        # Logs recientes
GET    /api/message-logs/{id}          # Log por ID
PATCH  /api/message-logs/{id}          # Actualizar log
DELETE /api/message-logs/{id}          # Eliminar log
GET    /api/message-logs/stats/realtime # Stats en tiempo real
GET    /api/message-logs/stats/by-user/{id} # Stats por usuario
```

#### Auth Endpoints
```
POST /auth/login                       # Login
POST /auth/register                    # Registro
POST /auth/verify-email                # Verificar email
POST /auth/verify-2fa                  # Verificar código 2FA
POST /auth/resend-2fa                  # Reenviar código 2FA
POST /auth/verify-device               # Verificar dispositivo
POST /auth/enable-2fa                  # Habilitar 2FA
POST /auth/disable-2fa                 # Deshabilitar 2FA
POST /auth/request-password-reset      # Solicitar reset de contraseña
POST /auth/reset-password              # Resetear contraseña
POST /auth/refresh-token               # Refrescar token
POST /auth/logout                      # Cerrar sesión
GET  /auth/me                          # Usuario actual
GET  /auth/login-history               # Historial de logins
GET  /auth/trusted-devices             # Dispositivos confiables
```

## 🎨 Diseño y UI/UX

### Paleta de Colores
```kotlin
Primary = Color(0xFF6366F1)      // Indigo-500
Secondary = Color(0xFF8B5CF6)    // Violet-500
Success = Color(0xFF10B981)      // Green-500
Warning = Color(0xFFF59E0B)      // Amber-500
Error = Color(0xFFEF4444)        // Red-500
Background = Color(0xFFF8FAFC)   // Slate-50
```

### Componentes Reutilizables
- **KPICard**: Tarjeta de KPI con icono, título y valor
- **MessageLogCard**: Tarjeta de log con metadata
- **RealtimeStatsBar**: Barra de estadísticas en tiempo real
- **FilterChip**: Chip de filtro seleccionable
- **LoadingView**: Vista de carga con spinner
- **ErrorView**: Vista de error con retry
- **EmptyView**: Vista cuando no hay datos

## 📱 Flujo de Navegación

```
Login
  ├─> 2FA (si está habilitado)
  ├─> Device Verification (si es dispositivo nuevo)
  └─> Home
       ├─> Analytics Dashboard
       │    ├─> Daily Analytics
       │    ├─> Hourly Analytics
       │    ├─> Intent Analytics
       │    └─> KPIs
       │
       ├─> Message Logs
       │    ├─> Recent Logs
       │    ├─> Realtime Stats
       │    ├─> Log Details
       │    └─> Filters
       │
       └─> Logout
```

## 🚀 Características Avanzadas

### Auto-Refresh en Message Logs
```kotlin
LaunchedEffect(autoRefresh, selectedMinutes) {
    while (autoRefresh) {
        viewModel.getRecentMessageLogs(minutes = selectedMinutes)
        viewModel.getRealtimeStats(minutes = selectedMinutes)
        kotlinx.coroutines.delay(10000) // 10 segundos
    }
}
```

### StateFlows para Reactividad
```kotlin
val messageLogs by viewModel.recentLogs.collectAsState()
val realtimeStats by viewModel.realtimeStats.collectAsState()
```

### Filtros Dinámicos
- **Por tiempo**: 5m, 15m, 30m, 1h, 2h
- **Por tipo**: user, bot, system, image, audio
- **Por estado**: success, error, pending
- **Por intent**: Intenciones detectadas

## 🔒 Seguridad

### Tokens
- **Access Token**: JWT válido por 30 minutos
- **Refresh Token**: Válido por 30 días
- **Auto-refresh**: Antes de expiración

### Headers
```kotlin
private fun getAuthHeader(): String {
    val token = tokenManager.getAccessToken()
    return "Bearer $token"
}
```

## 📊 Analytics Features

### KPIs Mostrados
1. **Total Mensajes**: Suma de todos los mensajes en el período
2. **Usuarios Únicos**: Cantidad de usuarios diferentes
3. **Sesiones Totales**: Total de sesiones iniciadas
4. **Tiempo Promedio**: Tiempo de respuesta promedio en ms
5. **Mensajes por Usuario**: Ratio messages/users

### Tendencias
- **Gráficos diarios**: Últimos 7 días por defecto
- **Período seleccionable**: 7, 14, 30, 60, 90 días
- **Top Intenciones**: Top 5 intenciones más usadas
- **Confianza por Intent**: Score de confianza promedio

## 💡 Tips de Uso

### Analytics Dashboard
1. Selecciona el período deseado (botón superior derecho)
2. Toca el botón refresh para actualizar manualmente
3. Desliza hacia abajo para ver toda la información
4. Las tarjetas de tendencia muestran datos día por día

### Message Logs
1. Usa el botón Play/Pause para controlar auto-refresh
2. Filtra por ventana de tiempo con los chips
3. Toca un log para ver detalles completos
4. Los colores indican el estado (verde=success, rojo=error)
5. Elimina logs directamente desde los detalles

## 🐛 Troubleshooting

### No se cargan los datos
1. Verifica la conexión al backend
2. Revisa que la BASE_URL sea correcta
3. Chequea que el token no haya expirado
4. Mira los logs en Logcat

### Error 401 Unauthorized
- El token expiró, cierra sesión y vuelve a iniciar

### Error 500 Server Error
- Verifica que el backend esté corriendo
- Revisa los logs del servidor

## 📝 TODO / Mejoras Futuras

- [ ] WebSocket para logs en tiempo real real (actualmente polling)
- [ ] Gráficos con MPAndroidChart
- [ ] Export de analytics a CSV/PDF
- [ ] Notificaciones push para eventos críticos
- [ ] Filtros más avanzados (rango de fechas custom)
- [ ] Dark mode completo
- [ ] Caché local con Room
- [ ] Offline mode

## 🎯 Buenas Prácticas Implementadas

✅ **Clean Architecture**: Separación de capas (Data, Domain, Presentation)
✅ **MVVM Pattern**: ViewModels + StateFlows
✅ **Repository Pattern**: Abstracción de fuentes de datos
✅ **Single Source of Truth**: StateFlows como fuente única
✅ **Error Handling**: Try-catch + Result type
✅ **Loading States**: Estados idle, loading, success, error
✅ **Reactive UI**: Compose + StateFlows
✅ **Material Design 3**: Componentes modernos
✅ **Type Safety**: Modelos con data classes
✅ **Null Safety**: Kotlin null safety

## 📚 Dependencias Utilizadas

```gradle
// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

// Retrofit
implementation("com.squareup.retrofit2:retrofit")
implementation("com.squareup.retrofit2:converter-gson")

// OkHttp
implementation("com.squareup.okhttp3:logging-interceptor")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
```

## 🤝 Contribuir

Para agregar nuevas funcionalidades:
1. Crea los modelos en `data/models/`
2. Define la API en `data/network/`
3. Implementa el repositorio en `data/repository/`
4. Crea el ViewModel en `viewmodel/`
5. Diseña la pantalla en `ui/screens/`
6. Agrega la ruta en `navigation/`

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

**Desarrollado con ❤️ usando Jetpack Compose + Material Design 3**

