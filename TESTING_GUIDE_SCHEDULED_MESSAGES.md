# 🚀 GUÍA DE COMPILACIÓN Y TESTING - MENSAJES PROGRAMADOS

## ✅ Verificación Pre-Compilación

Antes de compilar, asegúrate de que:

1. ✅ Todos los archivos fueron creados correctamente
2. ✅ No hay errores de compilación (solo warnings esperados)
3. ✅ El backend FastAPI está corriendo

---

## 📋 Archivos Verificados

### ✅ Sin Errores de Compilación
```
✅ data/models/ScheduledMessage.kt
✅ data/network/ScheduledMessagesApi.kt
✅ data/repository/ScheduledMessagesRepository.kt
✅ viewmodel/ScheduledMessagesViewModel.kt
✅ ui/screens/ScheduledMessagesScreen.kt
✅ ui/screens/CreateScheduledMessageScreen.kt
✅ ui/screens/ScheduledMessageDetailScreen.kt
✅ navigation/Screen.kt
✅ navigation/AppNavigation.kt
✅ ui/screens/HomeScreen.kt
✅ data/network/RetrofitClient.kt
```

### ⚠️ Warnings Esperados (No son Errores)
```
- "Function is never used" → Normal, se usarán en runtime
- "Property is never used" → Normal, se usarán en runtime
- "Deprecated icons" → No afecta funcionalidad
- "Unused imports" → Se pueden limpiar opcionalmente
```

---

## 🔧 Compilar el Proyecto

### Opción 1: Desde Android Studio
```
1. Abre Android Studio
2. Sync Project with Gradle Files (🔄)
3. Build > Rebuild Project
4. Espera a que termine
5. Build > Make Project
```

### Opción 2: Desde Terminal (Windows)
```powershell
cd C:\Users\xrz\AndroidStudioProjects\MyApplication2
.\gradlew clean assembleDebug
```

### Opción 3: Desde Terminal (Mac/Linux)
```bash
cd /path/to/MyApplication2
./gradlew clean assembleDebug
```

---

## 📱 Instalar en Dispositivo/Emulador

### Usando Android Studio
```
1. Conecta tu dispositivo o inicia el emulador
2. Run > Run 'app' (▶️)
3. Selecciona el dispositivo
4. Espera a que se instale
```

### Usando ADB
```powershell
# En Windows
adb install app\build\outputs\apk\debug\app-debug.apk

# En Mac/Linux
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Testing Manual

### Test 1: Navegación Básica ⏱️ 2 min

1. **Iniciar App**
   ```
   ✅ Login exitoso
   ✅ HomeScreen se carga
   ✅ Botón "📅 Programados" visible en segunda fila
   ```

2. **Acceder a Mensajes Programados**
   ```
   ✅ Toca "📅 Programados"
   ✅ Se abre ScheduledMessagesScreen
   ✅ Se muestra lista o empty state
   ```

---

### Test 2: Programar Mensaje Manual ⏱️ 3 min

1. **Abrir Formulario**
   ```
   ✅ Toca botón flotante "Programar Mensaje"
   ✅ Se abre CreateScheduledMessageScreen
   ✅ Por defecto está en modo "Manual"
   ```

2. **Llenar Formulario**
   ```
   Destinatarios: 573001234567, 573007654321
   Mensaje: "🎉 Promoción especial de prueba"
   Fecha: Mañana
   Hora: 10:00
   Zona: America/Bogota
   ```

3. **Programar**
   ```
   ✅ Toca "Programar Mensaje"
   ✅ Muestra loading
   ✅ Regresa a la lista
   ✅ Mensaje aparece con estado ⏳ PENDING
   ```

**Logs Esperados:**
```
ScheduledMessagesRepo: ✅ Token recuperado
ScheduledMessagesRepo: 📅 Programando mensaje para 2 destinatarios
ScheduledMessagesRepo: ✅ Mensaje programado exitosamente
ScheduledMessagesVM: ✅ Mensaje programado
```

---

### Test 3: Generar con IA ⏱️ 4 min

1. **Seleccionar Modo IA**
   ```
   ✅ Toca "Programar Mensaje"
   ✅ Selecciona "🤖 Con IA"
   ```

2. **Generar Preview**
   ```
   Destinatarios: 573001234567
   Prompt: "Mensaje de bienvenida para nuevos clientes"
   Contexto: "Tono amigable y profesional"
   
   ✅ Toca "Generar Mensaje"
   ✅ Muestra loading
   ✅ Se genera el mensaje
   ✅ Aparece en el área de texto editable
   ```

3. **Programar**
   ```
   Fecha: Pasado mañana
   Hora: 14:00
   
   ✅ Toca "Programar Mensaje"
   ✅ Mensaje aparece con badge "🤖 IA"
   ```

**Logs Esperados:**
```
ScheduledMessagesRepo: 🤖 Generando preview
ScheduledMessagesRepo: ✅ Mensaje generado: Bienvenido a...
ScheduledMessagesRepo: 📅 Programando mensaje
```

---

### Test 4: Ver Detalle ⏱️ 2 min

1. **Abrir Detalle**
   ```
   ✅ Toca cualquier mensaje de la lista
   ✅ Se abre ScheduledMessageDetailScreen
   ```

2. **Verificar Información**
   ```
   ✅ Estado visible con color
   ✅ Contenido completo del mensaje
   ✅ Fecha y hora programada
   ✅ Lista de destinatarios
   ✅ Botones de acción (si es PENDING)
   ```

---

### Test 5: Filtros ⏱️ 1 min

1. **Filtrar por Estado**
   ```
   ✅ Toca ícono de filtro (🔍)
   ✅ Selecciona "⏳ Pendientes"
   ✅ Lista se actualiza
   ✅ Solo muestra pendientes
   ```

2. **Probar Otros Filtros**
   ```
   ✅ "Todos" → Muestra todos
   ✅ "✅ Enviados" → Muestra enviados
   ✅ "❌ Fallidos" → Muestra fallidos
   ✅ "🚫 Cancelados" → Muestra cancelados
   ```

---

### Test 6: Enviar Ahora ⏱️ 2 min

1. **Abrir Mensaje Pendiente**
   ```
   ✅ Selecciona un mensaje PENDING
   ✅ Toca "Enviar Ahora"
   ✅ Aparece diálogo de confirmación
   ```

2. **Confirmar Envío**
   ```
   ✅ Toca "Enviar Ahora" en el diálogo
   ✅ Muestra loading
   ✅ Regresa a la lista
   ✅ Estado cambia a ✅ SENT (si exitoso)
   ```

**Logs Esperados:**
```
ScheduledMessagesRepo: 🚀 Enviando mensaje ahora: abc123
ScheduledMessagesRepo: ✅ Mensaje enviado exitosamente
```

---

### Test 7: Cancelar Mensaje ⏱️ 2 min

1. **Abrir Mensaje Pendiente**
   ```
   ✅ Selecciona un mensaje PENDING
   ✅ Toca "Cancelar Mensaje"
   ✅ Aparece diálogo de confirmación
   ```

2. **Confirmar Cancelación**
   ```
   ✅ Toca "Cancelar Mensaje" en el diálogo
   ✅ Muestra loading
   ✅ Regresa a la lista
   ✅ Estado cambia a 🚫 CANCELLED
   ```

---

### Test 8: Validaciones ⏱️ 3 min

1. **Sin Destinatarios**
   ```
   ✅ Intenta programar sin destinatarios
   ✅ Botón está deshabilitado
   ```

2. **Sin Mensaje**
   ```
   ✅ Intenta programar sin mensaje/prompt
   ✅ Botón está deshabilitado
   ```

3. **Sin Fecha**
   ```
   ✅ Intenta programar sin seleccionar fecha
   ✅ Botón está deshabilitado
   ```

4. **Mensaje Muy Largo**
   ```
   ✅ Escribe más de 1000 caracteres
   ✅ Contador se pone rojo
   ✅ Botón se deshabilita
   ```

5. **Prompt Muy Corto**
   ```
   ✅ Escribe menos de 10 caracteres en IA
   ✅ Botón "Generar" está deshabilitado
   ```

---

## 🐛 Troubleshooting

### ⚠️ Bug Crítico Resuelto: Loop Infinito de Peticiones

**Síntomas**:
```
- App hace peticiones infinitas a /auth/me
- Backend responde "Token expirado" continuamente
- App se reinicia sola
- Logs muestran cientos de peticiones GET /auth/me → 1.1
```

**Causa Raíz**:
1. `MainActivity.onResume()` llamaba a `checkLoginStatus()` cada vez que la app se reanudaba
2. `checkLoginStatus()` llamaba a `loadCurrentUser()` que hace petición a `/auth/me`
3. Token expirado → 401 → AuthInterceptor intenta renovar
4. Si el refresh token también expiró, falla y vuelve a intentar infinitamente

**Solución Implementada** ✅:
1. ✅ **Eliminado `onResume()`** en `MainActivity.kt` - el estado solo se verifica en el `init{}` del ViewModel
2. ✅ **Flag `isCheckingLoginStatus`** para evitar llamadas concurrentes
3. ✅ **Flag `isRefreshing`** en `AuthInterceptor` para evitar múltiples renovaciones simultáneas
4. ✅ **Limpieza automática de tokens** cuando el refresh token también expira
5. ✅ **Manejo de errores mejorado** en `loadCurrentUser()` para limpiar sesión cuando falla

**Archivos Modificados**:
- ✅ `MainActivity.kt` - Eliminado onResume()
- ✅ `AuthViewModel.kt` - Agregado flag isCheckingLoginStatus y mejor manejo de errores
- ✅ `AuthRepository.kt` - Agregado método clearTokens()
- ✅ `AuthInterceptor.kt` - Agregado flag isRefreshing y limpieza de tokens cuando expira

**Verificar que el bug está resuelto**:
```bash
# 1. Limpiar datos de la app
adb shell pm clear com.example.myapplication

# 2. Instalar la versión actualizada
.\gradlew installDebug

# 3. Monitorear logs
adb logcat | grep -E "AuthViewModel|AuthInterceptor|MainActivity"

# ✅ NO deberías ver loops infinitos de peticiones
# ✅ Si el token expira, debería limpiar la sesión y pedir login
```

---

### Error: "No hay token de acceso disponible"

**Causa**: Usuario no está logueado

**Solución**:
```
1. Cierra la app completamente
2. Abre la app
3. Haz login nuevamente
4. Intenta de nuevo
```

**Verificar**:
```bash
adb logcat | grep TokenManager
# Deberías ver: "✅ Token guardado exitosamente"
```

---

### Error: "Error: 401 - Unauthorized"

**Causa**: Token expirado o inválido

**Solución**:
```
1. El AuthInterceptor debería renovar automáticamente
2. Si persiste, cierra sesión y vuelve a iniciar
```

**Verificar**:
```bash
adb logcat | grep AuthInterceptor
# Deberías ver: "🔄 Token renovado automáticamente"
```

---

### Error: "Error: 500 - Internal Server Error"

**Causa**: Problema en el backend

**Solución**:
```
1. Verifica que el backend esté corriendo
2. Revisa los logs del backend
3. Asegúrate de que la base de datos esté activa
```

**Verificar Backend**:
```bash
curl http://TU_IP:8000/api/scheduled-messages/ \
  -H "Authorization: Bearer TU_TOKEN"
```

---

### La lista está vacía

**Posibles Causas**:
1. No hay mensajes programados
2. Filtro activo que no tiene resultados
3. Error de red

**Solución**:
```
1. Verifica el filtro (debe estar en "Todos")
2. Programa un mensaje de prueba
3. Revisa los logs
```

**Verificar**:
```bash
adb logcat | grep ScheduledMessages
```

---

### El mensaje generado con IA está vacío

**Causa**: Error en la API de Gemini o backend

**Solución**:
```
1. Verifica que el backend tenga configurada la API key de Gemini
2. Revisa los logs del backend
3. Intenta con un prompt diferente
```

---

## 📊 Logs de Éxito

### Flujo Completo Exitoso

```
// Login
TokenManager: ✅ Token guardado exitosamente
AuthViewModel: ✅ Login exitoso

// Cargar mensajes
ScheduledMessagesRepo: ✅ Token recuperado para ScheduledMessages: eyJhbGc...
ScheduledMessagesRepo: 📋 Obteniendo mensajes programados (limit: 50, offset: 0)
ScheduledMessagesRepo: ✅ 3 mensajes obtenidos
ScheduledMessagesVM: ✅ 3 mensajes cargados

// Generar con IA
ScheduledMessagesRepo: 🤖 Generando preview de mensaje con IA
ScheduledMessagesRepo: ✅ Mensaje generado: ¡Bienvenido a nuestra empresa...
ScheduledMessagesVM: ✅ Mensaje generado

// Programar mensaje
ScheduledMessagesRepo: 📅 Programando mensaje para 2 destinatarios
ScheduledMessagesRepo: ✅ Mensaje programado exitosamente: abc123-def456
ScheduledMessagesVM: ✅ Mensaje programado: abc123-def456

// Ver detalle
ScheduledMessagesRepo: 📄 Obteniendo detalle del mensaje: abc123-def456
ScheduledMessagesRepo: ✅ Mensaje obtenido: abc123-def456

// Enviar ahora
ScheduledMessagesRepo: 🚀 Enviando mensaje ahora: abc123-def456
ScheduledMessagesRepo: ✅ Mensaje enviado exitosamente
ScheduledMessagesVM: ✅ Mensaje enviado
```

---

## ✅ Checklist de Verificación Final

Antes de considerar la implementación completa, verifica:

### Backend
- [ ] Backend FastAPI corriendo en puerto 8000
- [ ] Endpoint `/api/scheduled-messages/` accesible
- [ ] API key de Gemini configurada
- [ ] Base de datos activa

### App
- [ ] Compilación exitosa sin errores
- [ ] App instalada en dispositivo/emulador
- [ ] Login funcional
- [ ] Navegación a "Programados" funciona

### Funcionalidad
- [ ] Puede programar mensaje manual
- [ ] Puede generar mensaje con IA
- [ ] Puede ver lista de mensajes
- [ ] Puede filtrar por estado
- [ ] Puede ver detalle
- [ ] Puede enviar ahora
- [ ] Puede cancelar
- [ ] Validaciones funcionan

### UI/UX
- [ ] Colores y badges correctos
- [ ] Loading states visibles
- [ ] Diálogos de confirmación
- [ ] Feedback de éxito/error
- [ ] Animaciones suaves

---

## 🎉 Resultado Esperado

Si todo está correcto, deberías poder:

1. ✅ Abrir la app y ver el botón "📅 Programados"
2. ✅ Acceder a la pantalla de mensajes programados
3. ✅ Programar un mensaje manual
4. ✅ Generar un mensaje con IA
5. ✅ Ver la lista de mensajes con sus estados
6. ✅ Filtrar por estado
7. ✅ Ver detalles completos
8. ✅ Enviar un mensaje inmediatamente
9. ✅ Cancelar un mensaje pendiente

**Todo sin errores de compilación ni crashes** 🚀

---

## 📞 Soporte

Si encuentras problemas:

1. **Revisa los logs**:
   ```bash
   adb logcat | grep -E "ScheduledMessages|TokenManager|AuthInterceptor"
   ```

2. **Verifica la configuración**:
   - IP del backend en `RetrofitClient.kt`
   - Tokens en SharedPreferences
   - Conectividad de red

3. **Consulta la documentación**:
   - `SCHEDULED_MESSAGES_README.md`
   - `IMPLEMENTATION_SUMMARY_SCHEDULED_MESSAGES.md`

---

**Tiempo estimado total de testing**: 20-25 minutos
**Última actualización**: 27/11/2025
**Estado**: ✅ LISTO PARA TESTING

