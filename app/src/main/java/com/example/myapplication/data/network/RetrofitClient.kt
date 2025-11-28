package com.example.myapplication.data.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Cambia esto por la URL de tu API
    private const val BASE_URL = "http://192.168.1.13:8000/" // Para emulador Android
    // Si usas dispositivo físico: "http://TU_IP:8000/"

    private var applicationContext: Context? = null

    /**
     * Inicializar el RetrofitClient con el contexto de la aplicación
     * Llamar esto desde Application.onCreate()
     */
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente HTTP con AuthInterceptor para renovación automática de tokens
     */
    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        // Agregar AuthInterceptor si tenemos contexto
        applicationContext?.let {
            builder.addInterceptor(AuthInterceptor(it))
        }

        builder.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val analyticsApi: AnalyticsApiService by lazy { retrofit.create(AnalyticsApiService::class.java) }
    val messageLogsApi: MessageLogsApiService by lazy { retrofit.create(MessageLogsApiService::class.java) }
    val productsApi: ProductsApiService by lazy { retrofit.create(ProductsApiService::class.java) }
    val companiesApi: CompaniesApiService by lazy { retrofit.create(CompaniesApiService::class.java) }
    val scheduledMessagesApi: ScheduledMessagesApi by lazy { retrofit.create(ScheduledMessagesApi::class.java) }
}
