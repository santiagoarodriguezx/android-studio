package com.example.myapplication.test

import android.content.Context
import android.util.Log
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.utils.DeviceUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Clase de utilidad para probar el Device Fingerprint
 *
 * Uso en MainActivity onCreate():
 * ```kotlin
 * DeviceFingerprintTest.testFingerprint(applicationContext)
 * ```
 */
object DeviceFingerprintTest {

    private const val TAG = "DeviceFingerprintTest"

    fun testFingerprint(context: Context) {
        runBlocking {
            val tokenManager = TokenManager(context)

            Log.d(TAG, "=== INICIO TEST DEVICE FINGERPRINT ===")

            // 1. Generar fingerprint
            val fingerprint = DeviceUtils.generateDeviceFingerprint(context)
            Log.d(TAG, "✅ Fingerprint generado: $fingerprint")

            // 2. Información del dispositivo
            val deviceName = DeviceUtils.getDeviceName()
            Log.d(TAG, "📱 Nombre del dispositivo: $deviceName")

            val deviceInfo = DeviceUtils.getDeviceInfo()
            Log.d(TAG, "ℹ️ Info del dispositivo:")
            deviceInfo.forEach { (key, value) ->
                Log.d(TAG, "   - $key: $value")
            }

            // 3. Verificar si ya hay uno guardado
            val existingFingerprint = tokenManager.getDeviceFingerprint().first()
            if (existingFingerprint != null) {
                Log.d(TAG, "💾 Fingerprint guardado anteriormente: $existingFingerprint")

                // Verificar que sea el mismo
                if (fingerprint == existingFingerprint) {
                    Log.d(TAG, "✅ CORRECTO: El fingerprint es consistente")
                } else {
                    Log.e(TAG, "❌ ERROR: El fingerprint cambió!")
                    Log.e(TAG, "   Anterior: $existingFingerprint")
                    Log.e(TAG, "   Nuevo: $fingerprint")
                }
            } else {
                Log.d(TAG, "📝 No hay fingerprint guardado (primera vez)")

                // Guardarlo
                tokenManager.saveDeviceFingerprint(fingerprint)
                Log.d(TAG, "💾 Fingerprint guardado correctamente")

                // Verificar que se guardó
                val saved = tokenManager.getDeviceFingerprint().first()
                if (saved == fingerprint) {
                    Log.d(TAG, "✅ VERIFICADO: El fingerprint se guardó correctamente")
                } else {
                    Log.e(TAG, "❌ ERROR: El fingerprint no se guardó correctamente")
                }
            }

            // 4. Test de persistencia - generar 5 veces y verificar que sea el mismo
            Log.d(TAG, "\n--- Test de Consistencia (5 generaciones) ---")
            val fingerprints = mutableListOf<String>()
            repeat(5) { i ->
                val fp = DeviceUtils.generateDeviceFingerprint(context)
                fingerprints.add(fp)
                Log.d(TAG, "Generación ${i+1}: $fp")
            }

            if (fingerprints.all { it == fingerprints.first() }) {
                Log.d(TAG, "✅ PERFECTO: Todas las generaciones producen el mismo fingerprint")
            } else {
                Log.e(TAG, "❌ ERROR: Las generaciones producen fingerprints diferentes!")
            }

            Log.d(TAG, "=== FIN TEST DEVICE FINGERPRINT ===\n")
        }
    }

    /**
     * Limpia el fingerprint guardado (útil para testing)
     */
    fun clearFingerprint(context: Context) {
        runBlocking {
            val tokenManager = TokenManager(context)
            tokenManager.clearDeviceFingerprint()
            Log.d(TAG, "🗑️ Fingerprint eliminado")
        }
    }
}

