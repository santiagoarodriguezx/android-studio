package com.example.myapplication.data.network

import com.example.myapplication.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 📦 API Service para Productos
 * Todos los endpoints requieren autenticación
 */
interface ProductsApiService {

    // ==================== OBTENER PRODUCTOS ====================

    @GET("api/products/")
    suspend fun getAllProducts(
        @Query("active_only") activeOnly: Boolean? = true,
        @Query("category") category: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("limit") limit: Int? = 100,
        @Query("offset") offset: Int? = 0,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<ProductsResponse>

    @GET("api/products/{product_id}")
    suspend fun getProductById(
        @Path("product_id") productId: String,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<ProductResponse>

    @GET("api/products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String,
        @Query("active_only") activeOnly: Boolean? = true,
        @Query("limit") limit: Int? = 100,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<ProductsResponse>

    @GET("api/products/search/{search_term}")
    suspend fun searchProducts(
        @Path("search_term") searchTerm: String,
        @Query("active_only") activeOnly: Boolean? = true,
        @Query("limit") limit: Int? = 50,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<ProductsResponse>

    // ==================== CREAR/ACTUALIZAR/ELIMINAR ====================

    @POST("api/products/")
    suspend fun createProduct(
        @Body product: ProductCreate,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<ProductResponse>

    @PUT("api/products/{product_id}")
    suspend fun updateProduct(
        @Path("product_id") productId: String,
        @Body product: ProductUpdate,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<ProductResponse>

    @DELETE("api/products/{product_id}")
    suspend fun deleteProduct(
        @Path("product_id") productId: String,
        @Query("company_id") companyId: String? = null,
        @Query("soft_delete") softDelete: Boolean? = true,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Map<String, Any>>>

    // ==================== OPERACIONES ESPECIALES ====================

    @PATCH("api/products/{product_id}/stock")
    suspend fun updateProductStock(
        @Path("product_id") productId: String,
        @Query("stock_change") stockChange: Int,
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<StockUpdateResponse>

    @GET("api/products/stats/summary")
    suspend fun getProductsSummary(
        @Query("company_id") companyId: String? = null,
        @Header("Authorization") token: String
    ): Response<ProductSummary>
}

