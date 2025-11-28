# 📅 Módulo de Mensajes Programados - Android

## ✅ Implementación Completa

Este módulo permite programar mensajes de WhatsApp para envío futuro, con soporte para generación de contenido mediante IA.

---

## 📁 Archivos Creados

### 1. Modelos de Datos
- **`ScheduledMessage.kt`**: Modelos para requests y responses del API
  - `ScheduleMessageRequest`
  - `GenerateMessageRequest`
  - `UpdateScheduledMessageRequest`
  - `ScheduledMessage`
  - `ScheduledMessageStatus` (enum)
  - Todos los response models

### 2. API
- **`ScheduledMessagesApi.kt`**: Interfaz Retrofit con todos los endpoints
  - `POST /api/scheduled-messages/` - Programar mensaje
  - `POST /api/scheduled-messages/generate-message` - Preview con IA
  - `GET /api/scheduled-messages/` - Listar mensajes
  - `GET /api/scheduled-messages/{id}` - Detalle
  - `PATCH /api/scheduled-messages/{id}` - Actualizar
  - `DELETE /api/scheduled-messages/{id}` - Cancelar
  - `POST /api/scheduled-messages/{id}/send-now` - Enviar ahora

### 3. Repositorio
- **`ScheduledMessagesRepository.kt`**: Capa de datos
  - Manejo correcto de tokens con `.first()`
  - Logging detallado
  - Result types para manejo de errores

### 4. ViewModel
- **`ScheduledMessagesViewModel.kt`**: Lógica de negocio
  - Estados: Idle, Loading, Success, Error, MessageScheduled, MessageGenerated
  - Funciones para todas las operaciones CRUD
  - Paginación automática
  - Preview de mensajes IA

### 5. Pantallas UI
- **`ScheduledMessagesScreen.kt`**: Lista de mensajes programados
  - Filtros por estado (Pending, Sent, Failed, Cancelled)
  - Cards con información completa
  - Navegación a detalle
  
- **`CreateScheduledMessageScreen.kt`**: Crear mensaje programado
  - Modo Manual: Escribir mensaje manualmente
  - Modo IA: Generar con prompt
  - Selector de fecha y hora
  - Validación de formularios
  
- **`ScheduledMessageDetailScreen.kt`**: Detalle del mensaje
  - Visualización completa de información
  - Estadísticas de envío
  - Acciones: Enviar Ahora, Cancelar

### 6. Navegación
- **Actualizaciones en `Screen.kt`**: Rutas agregadas
- **Actualizaciones en `AppNavigation.kt`**: Composables configurados
- **Actualizaciones en `HomeScreen.kt`**: Botón de acceso agregado
- **Actualizaciones en `RetrofitClient.kt`**: API registrada

---

## 🎨 Características Implementadas

### ✅ Programar Mensajes
- **Manual**: Escribe tu propio mensaje
- **Con IA**: Genera contenido automáticamente usando Gemini
- Soporte para múltiples destinatarios (hasta 100)
- Selector de fecha y hora
- Configuración de zona horaria

### ✅ Gestión de Mensajes
- Ver lista de mensajes programados
- Filtrar por estado (Pending, Sent, Failed, Cancelled)
- Ver detalles completos
- Actualizar mensajes pendientes
- Cancelar mensajes
- Enviar inmediatamente (sin esperar)

### ✅ Preview de IA
- Generar mensaje sin programar (preview)
- Editar mensaje generado antes de programar
- Contexto adicional opcional

### ✅ UI/UX Moderna
- Diseño Material 3
- Animaciones fluidas
- Cards con información clara
- Badges de estado con colores
- Iconos descriptivos
- Estadísticas visuales

---

## 🚀 Cómo Usar

### 1. Acceder al Módulo
Desde el **HomeScreen**, toca el botón **"📅 Programados"** en las acciones rápidas.

### 2. Programar un Mensaje Manual

```kotlin
// La UI maneja esto automáticamente, solo necesitas:
1. Tocar el botón "Programar Mensaje"
2. Seleccionar "Manual"
3. Ingresar destinatarios (separados por comas)
4. Escribir el mensaje
5. Seleccionar fecha y hora
6. Presionar "Programar Mensaje"
```

### 3. Programar con IA

```kotlin
// La UI maneja esto automáticamente:
1. Tocar el botón "Programar Mensaje"
2. Seleccionar "Con IA"
3. Ingresar destinatarios
4. Escribir el prompt (ej: "Mensaje promocional para Black Friday")
5. (Opcional) Agregar contexto
6. Presionar "Generar Mensaje"
7. Editar si es necesario
8. Seleccionar fecha y hora
9. Presionar "Programar Mensaje"
```

### 4. Gestionar Mensajes

```kotlin
// Ver detalles
- Toca cualquier mensaje en la lista

// Filtrar
- Usa el botón de filtro en la barra superior
- Selecciona: Todos, Pendientes, Enviados, Fallidos, Cancelados

// Enviar ahora
- Abre el detalle del mensaje
- Presiona "Enviar Ahora"
- Confirma

// Cancelar
- Abre el detalle del mensaje
- Presiona "Cancelar Mensaje"
- Confirma
```

---

## 🔧 Configuración

### Asegúrate de que el Backend esté Corriendo

El endpoint debe estar disponible en:
```
http://TU_IP:8000/api/scheduled-messages/
```

### Tokens Automáticos

El módulo maneja automáticamente:
- ✅ Obtención de tokens
- ✅ Renovación automática (vía AuthInterceptor)
- ✅ Manejo de errores de autenticación

---

## 📊 Estados de Mensajes

| Estado | Emoji | Descripción |
|--------|-------|-------------|
| **PENDING** | ⏳ | Mensaje programado, esperando hora de envío |
| **SENT** | ✅ | Mensaje enviado exitosamente |
| **FAILED** | ❌ | Falló el envío |
| **CANCELLED** | 🚫 | Cancelado por el usuario |

---

## 🎯 Casos de Uso

### 1. Promoción Programada
```
Destinatarios: Lista de clientes
Mensaje: "🎉 Black Friday: 50% de descuento hoy!"
Fecha: 29/11/2025 09:00
```

### 2. Recordatorio Automático
```
IA Prompt: "Recordatorio amable de pago pendiente"
Contexto: "Tono profesional, para servicios de consultoría"
Fecha: 30/11/2025 10:00
```

### 3. Felicitaciones Masivas
```
IA Prompt: "Mensaje de felicitación para fin de año"
Contexto: "Tono cálido y profesional para clientes VIP"
Fecha: 31/12/2025 18:00
```

---

## 🐛 Troubleshooting

### Error: "Token inválido"
**Solución**: El token se maneja automáticamente. Si persiste, cierra sesión y vuelve a iniciar.

### Error: "No hay token de acceso disponible"
**Solución**: Asegúrate de estar logueado. El sistema debería redirigir a login automáticamente.

### Los mensajes no aparecen
**Solución**: 
1. Verifica que el backend esté corriendo
2. Revisa los logs con `adb logcat | grep ScheduledMessages`
3. Asegúrate de tener conexión a internet

### No puedo editar un mensaje
**Solución**: Solo los mensajes en estado **PENDING** pueden editarse.

---

## 📝 Ejemplo de Logs Exitosos

```
ScheduledMessagesRepo: ✅ Token recuperado para ScheduledMessages: eyJhbGciOiJIUzI1NiIs...
ScheduledMessagesRepo: 📅 Programando mensaje para 5 destinatarios
ScheduledMessagesRepo: ✅ Mensaje programado exitosamente: abc123-def456-ghi789
ScheduledMessagesVM: ✅ Mensaje programado: abc123-def456-ghi789
```

---

## 🎨 Capturas de Funcionalidad

### HomeScreen
- Botón "📅 Programados" agregado en la segunda fila de acciones rápidas

### ScheduledMessagesScreen
- Lista con cards mostrando:
  - Estado con badge de color
  - Contenido del mensaje (preview)
  - Número de destinatarios
  - Fecha programada
  - Badge "IA" si fue generado automáticamente

### CreateScheduledMessageScreen
- Tabs para seleccionar Manual/IA
- Campos de destinatarios
- Editor de mensaje o generador IA
- Selector de fecha con DatePicker
- Campo de zona horaria

### ScheduledMessageDetailScreen
- Card de estado
- Contenido completo del mensaje
- Información de programación
- Lista de destinatarios
- Estadísticas (si está enviado)
- Botones de acción (Enviar Ahora, Cancelar)

---

## ✅ Testing

### Prueba Básica

1. **Crear mensaje manual**
   - Abre la app
   - Ve a "Programados"
   - Crea un mensaje manual para mañana
   - Verifica que aparezca en la lista con estado ⏳ PENDING

2. **Generar con IA**
   - Crea un nuevo mensaje
   - Selecciona "Con IA"
   - Escribe: "Mensaje de bienvenida para nuevos clientes"
   - Presiona "Generar Mensaje"
   - Verifica que se genere el contenido

3. **Enviar ahora**
   - Abre el detalle de un mensaje pendiente
   - Presiona "Enviar Ahora"
   - Confirma
   - Verifica que cambie a estado ✅ SENT

4. **Cancelar**
   - Abre el detalle de un mensaje pendiente
   - Presiona "Cancelar Mensaje"
   - Confirma
   - Verifica que cambie a estado 🚫 CANCELLED

---

## 🎉 Conclusión

El módulo de **Mensajes Programados** está **100% funcional** con todas las características solicitadas:

✅ Programación de mensajes manuales
✅ Generación de contenido con IA
✅ Preview antes de programar
✅ Gestión completa (ver, editar, cancelar)
✅ Envío inmediato
✅ Filtros y paginación
✅ UI moderna y responsive
✅ Manejo correcto de tokens
✅ Logging detallado
✅ Navegación integrada

¡Listo para usar! 🚀

