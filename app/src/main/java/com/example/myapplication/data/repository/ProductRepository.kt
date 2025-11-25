package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.models.*
import com.example.myapplication.data.network.RetrofitClient
import kotlinx.coroutines.flow.first

/**
 * 📦 Repositorio para gestión de productos
 */
class ProductRepository(private val tokenManager: TokenManager) {

    private val api = RetrofitClient.productsApi
    private val TAG = "ProductRepository"

    private suspend fun getAuthHeader(): String {
        val token = tokenManager.getAccessToken().first()
        if (token.isNullOrEmpty()) {
            throw IllegalStateException("No hay token de acceso disponible")
        }
        return "Bearer $token"
    }

    // ==================== OBTENER PRODUCTOS ====================

    suspend fun getAllProducts(
        activeOnly: Boolean = true,
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        limit: Int = 100,
        offset: Int = 0,
        companyId: String? = null
    ): Result<ProductsResponse> {
        return try {
            Log.d(TAG, "📦 Obteniendo productos (limit: $limit, offset: $offset)")
            val response = api.getAllProducts(
                activeOnly = activeOnly,
                category = category,
                minPrice = minPrice,
                maxPrice = maxPrice,
                limit = limit,
                offset = offset,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Productos obtenidos: ${response.body()!!.total}")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "❌ Error obteniendo productos: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción obteniendo productos: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getProductById(productId: String, companyId: String? = null): Result<Product> {
        return try {
            Log.d(TAG, "📦 Obteniendo producto: $productId")
            val response = api.getProductById(
                productId = productId,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Producto obtenido: ${response.body()!!.product.name}")
                Result.success(response.body()!!.product)
            } else {
                Log.e(TAG, "❌ Error obteniendo producto: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción obteniendo producto: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun searchProducts(
        searchTerm: String,
        activeOnly: Boolean = true,
        limit: Int = 50,
        companyId: String? = null
    ): Result<ProductsResponse> {
        return try {
            Log.d(TAG, "🔍 Buscando productos: '$searchTerm'")
            val response = api.searchProducts(
                searchTerm = searchTerm,
                activeOnly = activeOnly,
                limit = limit,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Búsqueda completada: ${response.body()!!.total} resultados")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "❌ Error buscando productos: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción buscando productos: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getProductsByCategory(
        category: String,
        activeOnly: Boolean = true,
        limit: Int = 100,
        companyId: String? = null
    ): Result<ProductsResponse> {
        return try {
            Log.d(TAG, "📂 Obteniendo productos de categoría: $category")
            val response = api.getProductsByCategory(
                category = category,
                activeOnly = activeOnly,
                limit = limit,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Productos de categoría obtenidos: ${response.body()!!.total}")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "❌ Error obteniendo productos por categoría: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción obteniendo productos por categoría: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==================== CREAR/ACTUALIZAR/ELIMINAR ====================

    suspend fun createProduct(product: ProductCreate, companyId: String? = null): Result<Product> {
        return try {
            Log.d(TAG, "➕ Creando producto: ${product.name}")
            val response = api.createProduct(
                product = product,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Producto creado: ${response.body()!!.product.name}")
                Result.success(response.body()!!.product)
            } else {
                Log.e(TAG, "❌ Error creando producto: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción creando producto: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateProduct(
        productId: String,
        product: ProductUpdate,
        companyId: String? = null
    ): Result<Product> {
        return try {
            Log.d(TAG, "✏️ Actualizando producto: $productId")
            val response = api.updateProduct(
                productId = productId,
                product = product,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Producto actualizado: ${response.body()!!.product.name}")
                Result.success(response.body()!!.product)
            } else {
                Log.e(TAG, "❌ Error actualizando producto: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción actualizando producto: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(
        productId: String,
        softDelete: Boolean = true,
        companyId: String? = null
    ): Result<Boolean> {
        return try {
            Log.d(TAG, "🗑️ Eliminando producto: $productId (soft: $softDelete)")
            val response = api.deleteProduct(
                productId = productId,
                companyId = companyId,
                softDelete = softDelete,
                token = getAuthHeader()
            )

            if (response.isSuccessful) {
                Log.d(TAG, "✅ Producto eliminado correctamente")
                Result.success(true)
            } else {
                Log.e(TAG, "❌ Error eliminando producto: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción eliminando producto: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ==================== OPERACIONES ESPECIALES ====================

    suspend fun updateStock(
        productId: String,
        stockChange: Int,
        companyId: String? = null
    ): Result<StockUpdateResponse> {
        return try {
            Log.d(TAG, "📊 Actualizando stock del producto: $productId (cambio: $stockChange)")
            val response = api.updateProductStock(
                productId = productId,
                stockChange = stockChange,
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Stock actualizado: ${response.body()!!.message}")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "❌ Error actualizando stock: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción actualizando stock: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getProductsSummary(companyId: String? = null): Result<ProductSummary> {
        return try {
            Log.d(TAG, "📊 Obteniendo resumen de productos")
            val response = api.getProductsSummary(
                companyId = companyId,
                token = getAuthHeader()
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "✅ Resumen obtenido: ${response.body()!!.totalProducts} productos")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "❌ Error obteniendo resumen: ${response.errorBody()?.string()}")
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción obteniendo resumen: ${e.message}", e)
            Result.failure(e)
        }
    }
}

