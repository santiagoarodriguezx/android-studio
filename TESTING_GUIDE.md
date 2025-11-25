# 🧪 Guía de Pruebas - Device Fingerprint Persistente

## 📋 Checklist de Verificación

### ✅ Archivos Creados/Modificados

- [x] `utils/DeviceUtils.kt` - Generador de fingerprint único
- [x] `data/local/TokenManager.kt` - Métodos de almacenamiento agregados
- [x] `data/repository/AuthRepository.kt` - Uso de fingerprint persistente
- [x] `MainActivity.kt` - Pasa contexto al repository
- [x] `test/DeviceFingerprintTest.kt` - Utilidad de testing
- [x] `DEVICE_FINGERPRINT_FIX.md` - Documentación completa

---

## 🧪 Pruebas Manuales

### Test 1: Primera Instalación (Dispositivo Nuevo)

**Pasos:**
1. Desinstalar la app completamente
2. Instalar la app nuevamente
3. Abrir la app
4. Ir a Login
5. Ingresar credenciales: `infoexecorp@gmail.com`
6. Click en "Iniciar Sesión"

**Resultado Esperado:**
```
📱 Logs de Logcat:
📱 Nuevo fingerprint generado y guardado: a1b2c3d4...
🔐 Intentando login para: infoexecorp@gmail.com
📥 Respuesta login - requiresDeviceVerification: true
```

**Acción del Backend:**
- Envía email de verificación ✅
- UI muestra pantalla de verificación ✅

7. Revisar email
8. Ingresar código de verificación
9. Dispositivo verificado ✅

---

### Test 2: Segundo Login (Dispositivo Confiable)

**Pasos:**
1. Cerrar sesión
2. Volver a hacer login con las mismas credenciales

**Resultado Esperado:**
```
📱 Logs de Logcat:
📱 Usando fingerprint existente: a1b2c3d4...
🔐 Intentando login para: infoexecorp@gmail.com
📥 Respuesta login - success: true ✅
💾 Guardando tokens después de login exitoso
```

**Resultado:**
- ✅ NO pide verificación
- ✅ Login exitoso inmediato
- ✅ JWT válido sin errores

---

### Test 3: Persistencia del Fingerprint

**Pasos:**
1. Hacer login
2. Cerrar la app completamente (swipe desde recientes)
3. Abrir la app nuevamente
4. Hacer login

**Resultado Esperado:**
```
📱 Usando fingerprint existente: a1b2c3d4...
```

**Verificación:**
- ✅ El fingerprint NO cambia
- ✅ No se genera uno nuevo
- ✅ Se usa el mismo del paso 1

---

## 🔧 Pruebas de Desarrollo

### Opción A: Test Automático (Recomendado)

Agregar en `MainActivity.onCreate()` después de inicializar el `authViewModel`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // ... código existente ...
    
    // 🧪 TEST: Descomentar para probar el fingerprint
    // DeviceFingerprintTest.testFingerprint(applicationContext)
}
```

**Ejecutar:**
1. Descomentar la línea de test
2. Ejecutar la app
3. Revisar Logcat con filtro: `DeviceFingerprintTest`

**Logs esperados:**
```
=== INICIO TEST DEVICE FINGERPRINT ===
✅ Fingerprint generado: a1b2c3d4e5f6789...
📱 Nombre del dispositivo: Samsung Galaxy S21
ℹ️ Info del dispositivo:
   - manufacturer: Samsung
   - model: SM-G991B
   - brand: samsung
   - device: o1s
   - androidVersion: 14
   - sdkVersion: 34
💾 Fingerprint guardado correctamente
✅ VERIFICADO: El fingerprint se guardó correctamente

--- Test de Consistencia (5 generaciones) ---
Generación 1: a1b2c3d4e5f6789...
Generación 2: a1b2c3d4e5f6789...
Generación 3: a1b2c3d4e5f6789...
Generación 4: a1b2c3d4e5f6789...
Generación 5: a1b2c3d4e5f6789...
✅ PERFECTO: Todas las generaciones producen el mismo fingerprint
=== FIN TEST DEVICE FINGERPRINT ===
```

---

### Opción B: Verificación Manual con Logcat

**Filtro de Logcat:**
```
AuthRepository|TokenManager|DeviceUtils
```

**Durante el login, deberías ver:**

**Primera vez:**
```
I/TokenManager: 📤 getAccessToken: null
I/AuthRepository: 📱 Nuevo fingerprint generado y guardado: abc123...
I/TokenManager: 📱 Device fingerprint guardado: abc123...
I/AuthRepository: 🔐 Intentando login para: infoexecorp@gmail.com
I/AuthRepository: 📥 Respuesta login - requires2FA: false, accessToken: eyJhbG...
I/TokenManager: ✅ Tokens guardados. AccessToken length: 234, RefreshToken length: 156
```

**Segunda vez:**
```
I/TokenManager: 📤 getAccessToken: Token existe (234 chars)
I/AuthRepository: 📱 Usando fingerprint existente: abc123...
I/AuthRepository: 🔐 Intentando login para: infoexecorp@gmail.com
I/AuthRepository: 📥 Respuesta login - success: true
```

---

## 🐛 Solución de Problemas

### Problema: "El fingerprint sigue cambiando"

**Diagnóstico:**
```kotlin
// En AuthRepository, verificar que se llama a tokenManager.saveDeviceFingerprint()
Log.d(TAG, "📱 Guardando fingerprint: $newFingerprint")
tokenManager.saveDeviceFingerprint(newFingerprint)
```

**Solución:**
- Verificar que `getDeviceFingerprint()` es `suspend fun`
- Verificar que se llama con `await` o desde una corrutina

---

### Problema: "Still getting 'Not enough segments'"

**Posibles causas:**
1. Token vacío o null
2. Token mal formado del backend
3. Header Authorization no se envía

**Verificación:**
```kotlin
// En RetrofitClient, agregar logging interceptor
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.HEADERS
}
```

**Revisar en Logcat:**
```
Authorization: Bearer eyJhbGc...  <- Debe tener 3 partes separadas por punto
```

---

### Problema: "Backend sigue pidiendo verificación"

**Posibles causas:**
1. El fingerprint del backend no coincide con el de la app
2. El dispositivo no fue verificado correctamente

**Verificación:**
1. Revisar los logs del backend
2. Comparar el `device_fingerprint` enviado vs el almacenado
3. Verificar que el endpoint `/auth/verify-device` se completó exitosamente

---

## 📊 Métricas de Éxito

### ✅ Todo funciona correctamente si:

1. **Primera instalación:**
   - Se genera un fingerprint único ✅
   - Se guarda en DataStore ✅
   - Backend envía verificación ✅
   - Usuario verifica el dispositivo ✅

2. **Logins posteriores:**
   - Se usa el mismo fingerprint ✅
   - No se genera uno nuevo ✅
   - Backend reconoce el dispositivo ✅
   - No pide verificación ✅
   - JWT válido sin errores ✅

3. **Persistencia:**
   - Sobrevive al cierre de la app ✅
   - Sobrevive al reinicio del dispositivo ✅
   - Solo cambia si se desinstala la app ✅

---

## 🔒 Consideraciones de Seguridad

### ¿Qué pasa si roban el fingerprint?

El fingerprint por sí solo **NO ES SUFICIENTE** para acceder:
- Se requiere email + password ✅
- El fingerprint solo evita la verificación por email ✅
- Si hay actividad sospechosa, el backend puede requerir re-verificación ✅

### ¿Cómo revocar un dispositivo?

**Desde el backend:**
```http
POST /auth/revoke-device
Authorization: Bearer {token}
{
  "device_id": "abc123..."
}
```

**Desde la app:**
```kotlin
// En SettingsScreen o ProfileScreen
authViewModel.revokeDevice(deviceId)
```

---

## 📝 Notas Finales

- El fingerprint es único por dispositivo y por app
- NO se sincroniza entre dispositivos (cada uno tiene el suyo)
- Si el usuario hace factory reset, se genera uno nuevo
- Compatible con Android 6.0+ (API 24+)
- Cumple con las políticas de privacidad de Google Play

---

## 🎯 Próximos Pasos

1. ✅ Verificar que todo compila sin errores
2. ✅ Probar en un dispositivo real o emulador
3. ✅ Verificar los logs de Logcat
4. ✅ Hacer login múltiples veces
5. ✅ Confirmar que no pide verificación después de la primera vez

**Una vez confirmado que funciona:**
- Comentar o eliminar `DeviceFingerprintTest.testFingerprint()` de MainActivity
- Hacer commit de los cambios
- Celebrar 🎉

