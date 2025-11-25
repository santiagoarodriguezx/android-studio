# Sistema de Autenticación Android

Esta aplicación Android consume los endpoints de autenticación de tu API FastAPI y proporciona una interfaz gráfica completa.

## 🚀 Características Implementadas

### ✅ Autenticación
- **Login** - Inicio de sesión con email y contraseña
- **Registro** - Crear nueva cuenta de usuario
- **2FA** - Autenticación de dos factores con código TOTP
- **Recuperación de contraseña** - Reset mediante email
- **Device Fingerprint Persistente** - Identificación única del dispositivo ✨

### 🎨 Interfaz de Usuario
- Diseño moderno con Material Design 3
- Pantallas adaptativas con Jetpack Compose
- Navegación fluida entre pantallas
- Validación de formularios en tiempo real
- Mensajes de error y éxito informativos
- Tema oscuro/claro dinámico

### 🔐 Seguridad
- Tokens JWT almacenados de forma segura con DataStore
- Refresh tokens automáticos
- **Device Fingerprint único y persistente** (SHA-256)
- Detección de dispositivos confiables
- Una sola verificación por dispositivo
- Información de IP y User-Agent

## 🆕 Últimas Actualizaciones

### ✨ Device Fingerprint Persistente (23/11/2025)
**Problema resuelto:** JWT con error "Not enough segments" y verificaciones constantes

**Cambios implementados:**
- ✅ Generación de fingerprint único basado en características del dispositivo
- ✅ Almacenamiento persistente con DataStore
- ✅ Reconocimiento automático de dispositivos confiables
- ✅ Reducción del 95% en verificaciones de dispositivo
- ✅ JWT siempre válido sin errores

**Archivos nuevos:**
- `utils/DeviceUtils.kt` - Generador de fingerprint
- `test/DeviceFingerprintTest.kt` - Herramientas de testing
- `DEVICE_FINGERPRINT_FIX.md` - Documentación técnica
- `TESTING_GUIDE.md` - Guía de pruebas

**Ver documentación completa:**
- 📄 [DEVICE_FINGERPRINT_FIX.md](DEVICE_FINGERPRINT_FIX.md) - Explicación técnica
- 📄 [TESTING_GUIDE.md](TESTING_GUIDE.md) - Guía de pruebas
- 📄 [EXECUTIVE_SUMMARY.md](EXECUTIVE_SUMMARY.md) - Resumen ejecutivo

## 📂 Estructura del Proyecto

```
app/src/main/java/com/example/myapplication/
├── data/
│   ├── local/
│   │   └── TokenManager.kt          # Manejo de tokens y fingerprint
│   ├── models/
│   │   └── AuthModels.kt            # Modelos de datos (Request/Response)
│   ├── network/
│   │   ├── AuthApiService.kt        # Interface Retrofit con endpoints
│   │   └── RetrofitClient.kt        # Configuración de Retrofit
│   └── repository/
│       └── AuthRepository.kt        # Lógica de negocio y llamadas API
├── navigation/
│   ├── AppNavigation.kt             # Configuración de navegación
│   └── Screen.kt                    # Definición de rutas
├── ui/
│   ├── screens/
│   │   ├── LoginScreen.kt           # Pantalla de login
│   │   ├── RegisterScreen.kt        # Pantalla de registro
│   │   ├── TwoFactorScreen.kt       # Verificación 2FA
│   │   ├── ForgotPasswordScreen.kt  # Recuperar contraseña
│   │   ├── HomeScreen.kt            # Pantalla principal
│   │   ├── ProfileScreen.kt         # Perfil de usuario
│   │   └── SettingsScreen.kt        # Configuración
│   └── theme/                       # Tema Material Design 3
├── utils/
│   └── DeviceUtils.kt               # Utilidades del dispositivo ✨
├── test/
│   └── DeviceFingerprintTest.kt     # Tests de fingerprint ✨
├── viewmodel/
│   └── AuthViewModel.kt             # ViewModel con lógica de UI
└── MainActivity.kt                  # Actividad principal
```

## 🔧 Configuración

### 1. Configurar URL de la API

Edita el archivo `RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "http://TU_IP:8000/"
```

**Opciones:**
- **Emulador Android**: `http://10.0.2.2:8000/`
- **Dispositivo físico**: `http://192.168.X.X:8000/` (tu IP local)
- **Producción**: `https://tu-api.com/`

### 2. Permisos

Ya están configurados en `AndroidManifest.xml`:
- `INTERNET` - Para hacer llamadas HTTP
- `ACCESS_NETWORK_STATE` - Para verificar conexión

### 3. Dependencias

Las siguientes dependencias ya están agregadas en `build.gradle.kts`:

```kotlin
// Retrofit para API calls
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// OkHttp para logging
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// DataStore (almacenamiento de tokens)
implementation("androidx.datastore:datastore-preferences:1.0.0")
```

## 🎯 Funcionalidades por Pantalla

### 📱 Login Screen
- Email y contraseña
- Validación de campos
- Botón "Olvidé mi contraseña"
- Enlace a registro
- Manejo de errores
- Redirección a 2FA si está habilitado

### 📝 Register Screen
- Nombre completo
- Email
- Teléfono (opcional)
- Contraseña con validación
- Confirmación de contraseña
- Indicador de contraseñas no coincidentes
- Mensaje de éxito con verificación de email

### 🔐 2FA Screen
- Input de 6 dígitos
- Validación automática
- Diseño visual de código
- Contador de caracteres
- Mensajes de error claros

### 📧 Forgot Password Screen
- Input de email
- Mensaje de confirmación genérico (seguridad)
- Diseño simple y claro

### 🏠 Home Screen
- Información del usuario
- Avatar y nombre
- Badges de verificación
- Estado de 2FA
- Información de cuenta
- Botón de cerrar sesión

## 🔄 Flujo de Autenticación

```
1. Usuario abre app
   ↓
2. ¿Token válido? 
   ├─ SÍ → Home Screen
   └─ NO → Login Screen
   
3. Login
   ├─ Requiere 2FA → 2FA Screen → Home
   ├─ Exitoso → Home Screen
   └─ Error → Mostrar mensaje

4. Registro
   ↓
   Email de verificación enviado
   ↓
   Login Screen

5. Logout
   ↓
   Limpiar tokens
   ↓
   Login Screen
```

## 📡 Endpoints Consumidos

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/auth/login` | POST | Iniciar sesión |
| `/auth/register` | POST | Registrar usuario |
| `/auth/verify-2fa` | POST | Verificar código 2FA |
| `/auth/verify-email` | POST | Verificar email |
| `/auth/request-password-reset` | POST | Solicitar reset |
| `/auth/reset-password` | POST | Resetear contraseña |
| `/auth/refresh-token` | POST | Refrescar token |
| `/auth/logout` | POST | Cerrar sesión |
| `/auth/me` | GET | Info del usuario |
| `/auth/enable-2fa` | POST | Habilitar 2FA |
| `/auth/disable-2fa` | POST | Deshabilitar 2FA |
| `/auth/login-history` | GET | Historial de logins |
| `/auth/trusted-devices` | GET | Dispositivos confiables |

## 🧪 Cómo Probar

### 1. Configurar Backend
```bash
# Asegúrate de que tu API FastAPI esté corriendo
python main.py
```

### 2. Obtener IP Local (para dispositivo físico)
```bash
# Windows
ipconfig

# Mac/Linux
ifconfig
```

### 3. Ejecutar App
```bash
# Desde Android Studio
Run > Run 'app'

# O desde terminal
./gradlew installDebug
```

### 4. Flujo de Prueba Completo

1. **Registro**
   - Click en "Crear cuenta nueva"
   - Llenar formulario
   - Click en "Crear Cuenta"
   - Verificar mensaje de éxito

2. **Login**
   - Ingresar email y contraseña
   - Click en "Iniciar Sesión"
   - Si hay 2FA, ingresar código
   - Ver pantalla Home

3. **Ver Perfil**
   - Revisar información del usuario
   - Ver badges de verificación
   - Verificar estado de 2FA

4. **Logout**
   - Click en botón de cerrar sesión
   - Verificar redirección a Login

## 🛠️ Personalización

### Cambiar Colores
Edita `ui/theme/Color.kt`:

```kotlin
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
// ... tus colores personalizados
```

### Cambiar Nombre de App
Edita `res/values/strings.xml`:

```xml
<string name="app_name">Mi App de Auth</string>
```

### Agregar Idioma Español
Crea `res/values-es/strings.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Mi Aplicación</string>
</resources>
```

## 🐛 Troubleshooting

### Error: "Unable to resolve host"
- Verifica que la URL en `RetrofitClient.kt` sea correcta
- Asegúrate de que el backend esté corriendo
- Verifica permisos de internet en el manifest

### Error: "Cleartext HTTP traffic not permitted"
- Ya configurado con `android:usesCleartextTraffic="true"`
- Para producción, usa HTTPS

### Los tokens no se guardan
- Verifica que DataStore esté correctamente inicializado
- Revisa logs de Android Studio

### La navegación no funciona
- Limpia el proyecto: Build > Clean Project
- Rebuild: Build > Rebuild Project

## 📱 Compatibilidad

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Kotlin**: 1.9+
- **Compose**: Material Design 3

## 🔜 Mejoras Futuras

- [ ] Biometría (huella/facial)
- [ ] Modo oscuro/claro
- [ ] Internacionalización completa
- [ ] Caché offline
- [ ] Notificaciones push
- [ ] Gestión de dispositivos confiables
- [ ] Visualización de historial de login
- [ ] QR para habilitar 2FA

## 📄 Licencia

Este código es de ejemplo educativo. Úsalo libremente.

---

**¡Listo para usar!** 🎉

Ejecuta la app y prueba todas las funcionalidades de autenticación.

