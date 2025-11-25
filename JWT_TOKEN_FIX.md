# 🔐 Solución al Error JWT "Not enough segments"

## ❌ Problema
```
2025-11-22 14:07:43,742 - WARNING - Token inválido: Not enough segments
```

Este error indica que el token JWT está **malformado o incompleto**.

## ✅ Solución Implementada

### 1. TokenManager Actualizado
El `TokenManager.kt` ya está configurado correctamente para:
- ✅ Guardar el token completo (3 partes: header.payload.signature)
- ✅ Recuperar el token desde DataStore
- ✅ Incluir logs detallados para debugging

### 2. AuthRepository - Método getAuthHeader()
```kotlin
private suspend fun getAuthHeader(): String {
    val token = tokenManager.getAccessToken().first()
    if (token.isNullOrEmpty()) {
        Log.e(TAG, "❌ No hay token de acceso disponible")
        throw IllegalStateException("No hay token de acceso disponible")
    }
    Log.d(TAG, "✅ Token recuperado para header: ${token.take(50)}...")
    return "Bearer $token"
}
```

**Importante**: El token se envía con el prefijo `Bearer ` automáticamente.

### 3. Login Flow - Guardado del Token

#### En `AuthRepository.login()`:
```kotlin
// ✅ Guardar tokens si el login fue exitoso
if (!loginResponse.accessToken.isNullOrEmpty() && !loginResponse.requires2FA) {
    loginResponse.accessToken?.let { accessToken ->
        loginResponse.refreshToken?.let { refreshToken ->
            Log.d(TAG, "💾 Guardando tokens después de login exitoso")
            tokenManager.saveTokens(accessToken, refreshToken)
            tokenManager.saveUserEmail(email)
        }
    }
}
```

#### En `AuthRepository.verify2FA()`:
```kotlin
// ✅ Guardar tokens si accessToken existe (verificación exitosa)
if (!loginResponse.accessToken.isNullOrEmpty()) {
    loginResponse.accessToken?.let { accessToken ->
        loginResponse.refreshToken?.let { refreshToken ->
            tokenManager.saveTokens(accessToken, refreshToken)
            tokenManager.saveUserEmail(email)
        }
    }
}
```

## 🧪 Verificar en Logcat

### Logs a buscar:

#### ✅ Login exitoso:
```
TokenManager: ✅ Tokens guardados. AccessToken length: 200, RefreshToken length: 150
TokenManager: AccessToken preview: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
AuthRepository: ✅ Token recuperado para header: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### ❌ Token no guardado:
```
TokenManager: 📤 getAccessToken: null
AuthRepository: ❌ No hay token de acceso disponible
```

## 🔍 Debugging en tu App

### Paso 1: Verificar que el token se guarda
Después de hacer login, revisa Logcat:

```bash
adb logcat | grep "TokenManager"
```

Deberías ver:
```
✅ Tokens guardados. AccessToken length: XXX
```

### Paso 2: Verificar que el token se recupera
Cuando navegas a ProfileScreen, deberías ver:
```
📤 getAccessToken: Token existe (XXX chars)
✅ Token recuperado para header: eyJhbGc...
```

### Paso 3: Verificar formato del token
Un JWT válido tiene **exactamente 3 partes** separadas por puntos:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyX2lkIiwiZXhwIjoxNjAwMDAwMDAwfQ.signature_here
       HEADER                    .         PAYLOAD           .   SIGNATURE
```

## 🛠️ Solucionar Problemas Comunes

### Problema 1: Token no se guarda después del login

**Causa**: `requires2FA` es true o `accessToken` es null

**Solución**: Verificar la respuesta del servidor en Logcat:
```kotlin
Log.d(TAG, "📥 Respuesta login - requires2FA: ${loginResponse.requires2FA}, accessToken: ${loginResponse.accessToken?.take(50)}")
```

### Problema 2: Token se pierde al reiniciar la app

**Causa**: DataStore no está guardando correctamente

**Verificar**: 
1. Busca en Logcat: `✅ Tokens guardados`
2. Revisa que no haya errores de permisos

### Problema 3: Token incompleto (1 o 2 partes)

**Causa**: El servidor está enviando un token malformado

**Verificar en Postman**:
```bash
POST http://192.168.1.13:8000/auth/unified-login
{
  "email": "infoexecorp@gmail.com",
  "password": "tu_password",
  "device_fingerprint": "android-test"
}
```

**Respuesta esperada**:
```json
{
  "access_token": "eyJhbGc....(largo)....xyz",
  "refresh_token": "eyJhbGc....(largo)....abc",
  "user": {...}
}
```

**Verificar**: Cuenta los puntos en el `access_token`. Debe haber **exactamente 2** (para 3 partes).

## 🔧 Fix Temporal para Testing

Si quieres probar rápidamente, puedes hardcodear un token de prueba:

```kotlin
// ⚠️ SOLO PARA TESTING - REMOVER EN PRODUCCIÓN
suspend fun setTestToken() {
    val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkZTY5NDI5Mi01NzZlLTRjYjgtOTVhYy04OTJhODk0NTE0MDYiLCJlbWFpbCI6ImluZm9leGVjb3JwQGdtYWlsLmNvbSIsInJvbGUiOiJhZG1pbiIsImV4cCI6MTczMjI5NjAwMH0.FAKE_SIGNATURE"
    tokenManager.saveTokens(testToken, "refresh_token_fake")
}
```

## 📱 Probar el Token Manualmente

### En tu app Android:

1. **Login exitoso**
2. **Ir a ProfileScreen** (botón flotante)
3. **Verificar Logcat**:
   ```
   TokenManager: 📤 getAccessToken: Token existe
   AuthRepository: ✅ Token recuperado
   ```

### Si ves este error:
```
TokenManager: 📤 getAccessToken: null
```

**Causa**: El login no guardó el token.

**Solución**:
1. Revisar logs de login
2. Verificar que `loginResponse.accessToken` no sea null
3. Verificar que `requires2FA` sea false

## 🎯 Checklist de Verificación

- [ ] El servidor devuelve un `access_token` con formato JWT válido
- [ ] El `access_token` tiene exactamente 3 partes separadas por `.`
- [ ] La app guarda el token después de login exitoso (ver logs)
- [ ] La app recupera el token correctamente (ver logs)
- [ ] El token se envía con el prefijo `Bearer `
- [ ] No hay errores de permisos en DataStore

## 📞 Endpoint para Validar Token

Usa este endpoint para verificar si el token es válido:

```http
GET http://192.168.1.13:8000/auth/me
Authorization: Bearer YOUR_TOKEN_HERE
```

**Respuesta esperada (200 OK)**:
```json
{
  "id": "user-id",
  "email": "infoexecorp@gmail.com",
  "name": "Usuario",
  "role": "admin"
}
```

**Error esperado (401 Unauthorized)**:
```json
{
  "detail": "Token inválido: Not enough segments"
}
```

## 🎉 Confirmación de Fix

Si todo funciona correctamente, verás:

1. **Al hacer login**:
   ```
   ✅ Tokens guardados. AccessToken length: 200
   ```

2. **Al abrir ProfileScreen**:
   ```
   ✅ Token recuperado para header: eyJhbG...
   ```

3. **En la pantalla**:
   - Foto de perfil con inicial
   - Nombre del usuario
   - Email
   - Rol

---

**Estado**: ✅ Solución implementada
**Próximo paso**: Probar el login y verificar logs en Logcat

