package com.example.myapplication.utils

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * 🛠️ Utilidades para manejo de errores del backend
 */
object ErrorUtils {

    private val gson = Gson()

    /**
     * Extrae el mensaje de error del body de respuesta del backend
     *
     * Maneja diferentes formatos:
     * - { "detail": "mensaje" }
     * - { "detail": [{"msg": "mensaje", ...}] }
     * - Otros formatos genéricos
     */
    fun extractErrorMessage(errorBody: String?, defaultCode: Int, defaultMessage: String): String {
        if (errorBody.isNullOrEmpty()) {
            return "Error: $defaultCode - $defaultMessage"
        }

        return try {
            val jsonError = gson.fromJson(errorBody, JsonObject::class.java)

            if (jsonError.has("detail")) {
                val detail = jsonError.get("detail")

                when {
                    // Si detail es un string simple
                    detail.isJsonPrimitive -> {
                        detail.asString
                    }
                    // Si detail es un array de errores
                    detail.isJsonArray -> {
                        val errors = detail.asJsonArray
                        if (errors.size() > 0) {
                            val firstError = errors.get(0)
                            if (firstError.isJsonObject) {
                                val errorObj = firstError.asJsonObject
                                errorObj.get("msg")?.asString
                                    ?: errorObj.get("message")?.asString
                                    ?: detail.toString()
                            } else {
                                firstError.asString
                            }
                        } else {
                            "Error: $defaultCode - $defaultMessage"
                        }
                    }
                    else -> {
                        detail.toString()
                    }
                }
            } else if (jsonError.has("message")) {
                jsonError.get("message").asString
            } else {
                "Error: $defaultCode - $defaultMessage"
            }
        } catch (e: Exception) {
            "Error: $defaultCode - $defaultMessage"
        }
    }
}

