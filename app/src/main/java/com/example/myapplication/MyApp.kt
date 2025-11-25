package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.network.RetrofitClient

/**
 * 🚀 Clase Application principal
 * Se ejecuta cuando la app se inicia
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar RetrofitClient con el contexto de la aplicación
        // Esto permite que el AuthInterceptor funcione correctamente
        RetrofitClient.initialize(this)
    }
}

