package com.example.myapplication.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.security.MessageDigest
import java.util.*

object DeviceUtils {

    private const val TAG = "DeviceUtils"

    /**
     * Genera un fingerprint único para el dispositivo.
     * Combina varios identificadores del dispositivo para crear un hash único.
     */
    fun generateDeviceFingerprint(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        // Combinar información del dispositivo
        val deviceInfo = StringBuilder()
            .append(androidId ?: "unknown")
            .append(Build.MANUFACTURER)
            .append(Build.MODEL)
            .append(Build.BRAND)
            .append(Build.DEVICE)
            .toString()

        // Generar hash SHA-256
        return try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val hashBytes = messageDigest.digest(deviceInfo.toByteArray())
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            // Fallback: UUID basado en la información del dispositivo
            Log.w(TAG, "SHA-256 no disponible, usando fallback UUID: ${e.message}")
            UUID.nameUUIDFromBytes(deviceInfo.toByteArray()).toString()
        }
    }

    /**
     * Obtiene el nombre del dispositivo en formato legible
     */
    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase().startsWith(manufacturer.lowercase())) {
            model.replaceFirstChar { it.uppercase() }
        } else {
            "${manufacturer.replaceFirstChar { it.uppercase() }} $model"
        }
    }

    /**
     * Obtiene información adicional del dispositivo
     */
    fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "manufacturer" to Build.MANUFACTURER,
            "model" to Build.MODEL,
            "brand" to Build.BRAND,
            "device" to Build.DEVICE,
            "androidVersion" to Build.VERSION.RELEASE,
            "sdkVersion" to Build.VERSION.SDK_INT.toString()
        )
    }
}
