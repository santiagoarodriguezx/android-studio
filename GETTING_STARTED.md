## 🚀 Inicio Rápido

### Paso 1: Configurar la URL de la API

1. Abre `RetrofitClient.kt`
2. Cambia la URL:
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8000/" // Para emulador
   // O
   private const val BASE_URL = "http://TU_IP:8000/" // Para dispositivo físico
   ```

### Paso 2: Sincronizar Gradle

1. En Android Studio, haz click en **File > Sync Project with Gradle Files**
2. Espera a que se descarguen todas las dependencias

### Paso 3: Ejecutar la App

1. Conecta un dispositivo o inicia un emulador
2. Click en el botón **Run** (▶️) en Android Studio
3. Selecciona tu dispositivo/emulador

### Paso 4: Probar Funcionalidades

#### Crear una Cuenta
1. En la pantalla de Login, click en **"Crear cuenta nueva"**
2. Llena el formulario:
   - Nombre: Tu nombre
   - Email: test@example.com
   - Teléfono: (opcional)
   - Contraseña: mínimo 8 caracteres
   - Confirmar contraseña
3. Click en **"Crear Cuenta"**
4. Verás un mensaje de éxito

#### Iniciar Sesión
1. Ingresa tu email y contraseña
2. Click en **"Iniciar Sesión"**
3. Si tienes 2FA habilitado, ingresa el código de 6 dígitos
4. Serás redirigido a la pantalla Home

#### Recuperar Contraseña
1. En Login, click en **"¿Olvidaste tu contraseña?"**
2. Ingresa tu email
3. Click en **"Enviar Email"**
4. Revisa tu email para el token de reset

## 🔍 Verificación de Funcionalidad

### ✅ Checklist de Pruebas

- [ ] La app compila sin errores
- [ ] Se puede crear una cuenta nueva
- [ ] Se recibe email de verificación (revisa backend)
- [ ] Se puede hacer login con credenciales válidas
- [ ] Aparece error con credenciales inválidas
- [ ] La verificación 2FA funciona (si está habilitada)
- [ ] Se muestra correctamente la información del usuario
- [ ] El botón de logout funciona
- [ ] Los tokens se guardan correctamente
- [ ] La navegación entre pantallas es fluida

## 🐛 Solución de Problemas Comunes

### La app no se conecta al backend

**Síntoma:** Errores de conexión, "Unable to resolve host"

**Solución:**
```kotlin
// En RetrofitClient.kt, verifica:

// Para EMULADOR:
private const val BASE_URL = "http://10.0.2.2:8000/"

// Para DISPOSITIVO FÍSICO en la misma red WiFi:
// 1. Obtén tu IP local (cmd > ipconfig)
// 2. Usa esa IP:
private const val BASE_URL = "http://192.168.1.X:8000/"
```

### Error "Cleartext HTTP traffic not permitted"

**Solución:** Ya está configurado en `AndroidManifest.xml` con:
```xml
android:usesCleartextTraffic="true"
```

### El backend no acepta las peticiones

**Verifica CORS en tu backend FastAPI:**
```python
from fastapi.middleware.cors import CORSMiddleware

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # En producción, especifica dominios
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

### Los tokens no se guardan

**Verifica en Logcat:**
1. Abre Logcat en Android Studio
2. Filtra por "AuthRepository" o "TokenManager"
3. Verifica que no haya excepciones

### La app se crashea al iniciar

1. **Clean Project:** `Build > Clean Project`
2. **Rebuild:** `Build > Rebuild Project`
3. **Invalidate Caches:** `File > Invalidate Caches > Invalidate and Restart`

## 📊 Logs Útiles

Para ver logs detallados de las peticiones HTTP, revisa Logcat con el filtro "OkHttp".

Verás algo como:
```
--> POST http://10.0.2.2:8000/auth/login
Content-Type: application/json
{"email":"test@example.com","password":"password123"}
<-- 200 OK
{"success":true,"access_token":"eyJ0eXAiOiJKV1QiLCJh..."}
```

## 🎨 Personalización Visual

### Cambiar Tema de Colores

Edita `ui/theme/Color.kt`:

```kotlin
// Colores primarios
val Primary = Color(0xFF6200EE)
val Secondary = Color(0xFF03DAC6)
val Tertiary = Color(0xFF018786)

// Colores de error
val Error = Color(0xFFB00020)
```

### Agregar Logo

1. Coloca tu logo en `res/drawable/logo.png`
2. En `LoginScreen.kt`, agrega:

```kotlin
Image(
    painter = painterResource(id = R.drawable.logo),
    contentDescription = "Logo",
    modifier = Modifier.size(120.dp)
)
```

## 📱 Configuración para Producción

### 1. Cambiar a HTTPS

```kotlin
// RetrofitClient.kt
private const val BASE_URL = "https://api.tudominio.com/"
```

### 2. Remover Cleartext Traffic

```xml
<!-- AndroidManifest.xml - ELIMINAR en producción -->
android:usesCleartextTraffic="true"
```

### 3. Ofuscar Código

```kotlin
// app/build.gradle.kts
buildTypes {
    release {
        isMinifyEnabled = true  // Cambiar a true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### 4. Firmar la APK

1. `Build > Generate Signed Bundle / APK`
2. Selecciona **APK** o **Android App Bundle**
3. Crea un nuevo keystore o usa uno existente
4. Completa la información de firma

## 🔐 Mejores Prácticas de Seguridad

### ✅ Implementado
- ✅ Tokens almacenados de forma segura con DataStore
- ✅ HTTPS recomendado para producción
- ✅ Validación de contraseñas
- ✅ Manejo seguro de errores

### 🔜 Recomendaciones Adicionales
- [ ] Implementar Certificate Pinning
- [ ] Agregar ProGuard rules personalizadas
- [ ] Implementar detección de root/jailbreak
- [ ] Agregar rate limiting en cliente
- [ ] Implementar biometría como 2FA adicional

## 📚 Recursos Adicionales

- [Documentación de Retrofit](https://square.github.io/retrofit/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)

---

**Creado con ❤️ para simplificar la autenticación en Android**

