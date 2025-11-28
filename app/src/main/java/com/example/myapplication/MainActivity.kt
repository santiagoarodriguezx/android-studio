package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.ThemeManager
import com.example.myapplication.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar dependencias
        val tokenManager = TokenManager(applicationContext)
        val authRepository = AuthRepository(tokenManager, applicationContext)
        authViewModel = AuthViewModel(authRepository)
        themeManager = ThemeManager(applicationContext)

        enableEdgeToEdge()
        setContent {
            // Observar el tema
            val isDarkMode by themeManager.isDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    AppNavigation(
                        navController = navController,
                        viewModel = authViewModel,
                        isDarkMode = isDarkMode,
                        onThemeChange = { newValue ->
                            scope.launch {
                                themeManager.setDarkMode(newValue)
                            }
                        }
                    )
                }
            }
        }
    }

    // ✅ Eliminado onResume() para evitar loop infinito de peticiones al backend
    // El estado de login ya se verifica en el init{} del AuthViewModel
}
