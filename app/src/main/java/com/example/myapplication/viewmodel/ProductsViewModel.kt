package com.example.myapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.models.*
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 📦 Estados para Productos
 */
sealed class ProductsState {
    object Idle : ProductsState()
    object Loading : ProductsState()
    data class Success(val message: String) : ProductsState()
    data class Error(val message: String) : ProductsState()
}

/**
 * 📦 ViewModel para gestión de productos
 */
class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ProductsViewModel"
    private val tokenManager = TokenManager(application)
    private val repository: ProductRepository = ProductRepository(tokenManager)
    private val companyRepository: CompanyRepository = CompanyRepository(tokenManager)

    // Estados
    private val _productsState = MutableStateFlow<ProductsState>(ProductsState.Idle)
    val productsState: StateFlow<ProductsState> = _productsState.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _productsSummary = MutableStateFlow<ProductSummary?>(null)
    val productsSummary: StateFlow<ProductSummary?> = _productsSummary.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Estado de la empresa
    private val _companyInfo = MutableStateFlow<Company?>(null)
    val companyInfo: StateFlow<Company?> = _companyInfo.asStateFlow()

    // Filtros actuales
    private val _currentFilters = MutableStateFlow(FilterState())
    val currentFilters: StateFlow<FilterState> = _currentFilters.asStateFlow()

    data class FilterState(
        val activeOnly: Boolean = true,
        val category: String? = null,
        val minPrice: Double? = null,
        val maxPrice: Double? = null,
        val searchQuery: String? = null
    )

    init {
        loadCompanyInfo()
        loadProducts()
        loadSummary()
    }

    // ==================== OBTENER INFORMACIÓN DE LA EMPRESA ====================

    fun loadCompanyInfo() {
        viewModelScope.launch {
            try {
                val companyId = tokenManager.getCompanyId().first()
                if (companyId.isNullOrEmpty()) {
                    Log.w(TAG, "⚠️ No hay company_id disponible")
                    return@launch
                }

                Log.d(TAG, "🏢 Cargando información de empresa: $companyId")
                val result = companyRepository.getCompanyById(companyId)

                result.onSuccess { company ->
                    _companyInfo.value = company
                    Log.d(TAG, "✅ Información de empresa cargada: ${company.name}")
                }.onFailure { error ->
                    Log.e(TAG, "❌ Error cargando empresa: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error: ${e.message}")
            }
        }
    }

    // ==================== OBTENER PRODUCTOS ====================

    fun loadProducts(
        activeOnly: Boolean? = null,
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        limit: Int = 100,
        offset: Int = 0
    ) {
        viewModelScope.launch {
            try {
                _productsState.value = ProductsState.Loading
                _isRefreshing.value = true

                // Actualizar filtros
                _currentFilters.value = _currentFilters.value.copy(
                    activeOnly = activeOnly ?: _currentFilters.value.activeOnly,
                    category = category,
                    minPrice = minPrice,
                    maxPrice = maxPrice
                )

                val result = repository.getAllProducts(
                    activeOnly = _currentFilters.value.activeOnly,
                    category = _currentFilters.value.category,
                    minPrice = _currentFilters.value.minPrice,
                    maxPrice = _currentFilters.value.maxPrice,
                    limit = limit,
                    offset = offset
                )

                result.onSuccess { response ->
                    _products.value = response.products
                    _totalCount.value = response.total
                    _productsState.value = ProductsState.Success("Productos cargados")
                    Log.d(TAG, "✅ ${response.total} productos cargados")
                }.onFailure { error ->
                    _productsState.value = ProductsState.Error(error.message ?: "Error cargando productos")
                    Log.e(TAG, "❌ Error: ${error.message}")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            loadProducts()
            return
        }

        viewModelScope.launch {
            try {
                _productsState.value = ProductsState.Loading
                _isRefreshing.value = true
                _currentFilters.value = _currentFilters.value.copy(searchQuery = query)

                val result = repository.searchProducts(
                    searchTerm = query,
                    activeOnly = _currentFilters.value.activeOnly
                )

                result.onSuccess { response ->
                    _products.value = response.products
                    _totalCount.value = response.total
                    _productsState.value = ProductsState.Success("Búsqueda completada")
                    Log.d(TAG, "🔍 ${response.total} resultados para '$query'")
                }.onFailure { error ->
                    _productsState.value = ProductsState.Error(error.message ?: "Error en búsqueda")
                    Log.e(TAG, "❌ Error búsqueda: ${error.message}")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadProductsByCategory(category: String) {
        viewModelScope.launch {
            try {
                _productsState.value = ProductsState.Loading
                _isRefreshing.value = true
                _currentFilters.value = _currentFilters.value.copy(category = category)

                val result = repository.getProductsByCategory(
                    category = category,
                    activeOnly = _currentFilters.value.activeOnly
                )

                result.onSuccess { response ->
                    _products.value = response.products
                    _totalCount.value = response.total
                    _productsState.value = ProductsState.Success("Categoría cargada")
                    Log.d(TAG, "📂 ${response.total} productos en '$category'")
                }.onFailure { error ->
                    _productsState.value = ProductsState.Error(error.message ?: "Error cargando categoría")
                    Log.e(TAG, "❌ Error categoría: ${error.message}")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun selectProduct(productId: String) {
        viewModelScope.launch {
            try {
                _productsState.value = ProductsState.Loading

                val result = repository.getProductById(productId)

                result.onSuccess { product ->
                    _selectedProduct.value = product
                    _productsState.value = ProductsState.Success("Producto cargado")
                    Log.d(TAG, "✅ Producto seleccionado: ${product.name}")
                }.onFailure { error ->
                    _productsState.value = ProductsState.Error(error.message ?: "Error cargando producto")
                    Log.e(TAG, "❌ Error: ${error.message}")
                }
            } catch (e: Exception) {
                _productsState.value = ProductsState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // ==================== CREAR/ACTUALIZAR/ELIMINAR ====================

    fun createProduct(product: ProductCreate) {
        viewModelScope.launch {
            try {
                _productsState.value = ProductsState.Loading

                val result = repository.createProduct(product)

                result.onSuccess { createdProduct ->
                    _productsState.value = ProductsState.Success("Producto creado exitosamente")
                    Log.d(TAG, "✅ Producto creado: ${createdProduct.name}")
                    // Recargar lista
                    loadProducts()
                    loadSummary()
                }.onFailure { error ->
                    _productsState.value = ProductsState.Error(error.message ?: "Error creando producto")
                    Log.e(TAG, "❌ Error: ${error.message}")
                }
            } catch (e: Exception) {
                _productsState.value = ProductsState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateProduct(productId: String, product: ProductUpdate) {
        viewModelScope.launch {
            try {
                _productsState.value = ProductsState.Loading

                val result = repository.updateProduct(productId, product)

                result.onSuccess { updatedProduct ->
                    _productsState.value = ProductsState.Success("Producto actualizado")
                    Log.d(TAG, "✅ Producto actualizado: ${updatedProduct.name}")
                    // Actualizar en la lista
                    _products.value = _products.value.map {
                        if (it.id == productId) updatedProduct else it
                    }
                    if (_selectedProduct.value?.id == productId) {
                        _selectedProduct.value = updatedProduct
                    }
                    loadSummary()
                }.onFailure { error ->
                    _productsState.value = ProductsState.Error(error.message ?: "Error actualizando producto")
                    Log.e(TAG, "❌ Error: ${error.message}")
                }
            } catch (e: Exception) {
                _productsState.value = ProductsState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteProduct(productId: String, softDelete: Boolean = true) {
        viewModelScope.launch {
            try {
                _productsState.value = ProductsState.Loading

                val result = repository.deleteProduct(productId, softDelete)

                result.onSuccess {
                    val message = if (softDelete) "Producto desactivado" else "Producto eliminado"
                    _productsState.value = ProductsState.Success(message)
                    Log.d(TAG, "✅ $message: $productId")
                    // Remover de la lista o marcar como inactivo
                    if (softDelete) {
                        loadProducts()
                    } else {
                        _products.value = _products.value.filter { it.id != productId }
                    }
                    loadSummary()
                }.onFailure { error ->
                    _productsState.value = ProductsState.Error(error.message ?: "Error eliminando producto")
                    Log.e(TAG, "❌ Error: ${error.message}")
                }
            } catch (e: Exception) {
                _productsState.value = ProductsState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    // ==================== OPERACIONES ESPECIALES ====================

    fun updateStock(productId: String, stockChange: Int) {
        viewModelScope.launch {
            try {
                _productsState.value = ProductsState.Loading

                val result = repository.updateStock(productId, stockChange)

                result.onSuccess { response ->
                    _productsState.value = ProductsState.Success(response.message)
                    Log.d(TAG, "📊 ${response.message}")
                    // Actualizar en la lista
                    _products.value = _products.value.map {
                        if (it.id == productId) response.product else it
                    }
                    if (_selectedProduct.value?.id == productId) {
                        _selectedProduct.value = response.product
                    }
                    loadSummary()
                }.onFailure { error ->
                    _productsState.value = ProductsState.Error(error.message ?: "Error actualizando stock")
                    Log.e(TAG, "❌ Error: ${error.message}")
                }
            } catch (e: Exception) {
                _productsState.value = ProductsState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun loadSummary() {
        viewModelScope.launch {
            try {
                val result = repository.getProductsSummary()

                result.onSuccess { summary ->
                    _productsSummary.value = summary
                    Log.d(TAG, "📊 Resumen cargado: ${summary.totalProducts} productos")
                }.onFailure { error ->
                    Log.e(TAG, "❌ Error cargando resumen: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error: ${e.message}")
            }
        }
    }

    // ==================== UTILIDADES ====================

    fun clearFilters() {
        _currentFilters.value = FilterState()
        loadProducts()
    }

    fun refreshData() {
        loadProducts()
        loadSummary()
    }

    fun resetState() {
        _productsState.value = ProductsState.Idle
    }

    fun clearSelection() {
        _selectedProduct.value = null
    }
}

