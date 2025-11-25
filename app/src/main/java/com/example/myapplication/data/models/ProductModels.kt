package com.example.myapplication.data.models

import com.google.gson.annotations.SerializedName

/**
 * 📦 Modelos de Productos
 */

data class Product(
    val id: String,
    @SerializedName("company_id")
    val companyId: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock: Int = 0,
    val category: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val active: Boolean = true,
    val metadata: Map<String, Any>? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class ProductsResponse(
    val products: List<Product>,
    val total: Int,
    val limit: Int,
    val offset: Int,
    val filters: ProductFilters?,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class ProductResponse(
    val product: Product,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class ProductFilters(
    @SerializedName("active_only")
    val activeOnly: Boolean? = null,
    val category: String? = null,
    @SerializedName("min_price")
    val minPrice: Double? = null,
    @SerializedName("max_price")
    val maxPrice: Double? = null
)

data class ProductCreate(
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock: Int = 0,
    val category: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val active: Boolean = true,
    val metadata: Map<String, Any>? = null
)

data class ProductUpdate(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val category: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val active: Boolean? = null,
    val metadata: Map<String, Any>? = null
)

data class ProductSummary(
    @SerializedName("total_products")
    val totalProducts: Int,
    @SerializedName("active_products")
    val activeProducts: Int,
    @SerializedName("inactive_products")
    val inactiveProducts: Int,
    @SerializedName("out_of_stock")
    val outOfStock: Int,
    @SerializedName("total_categories")
    val totalCategories: Int,
    val categories: List<String>,
    @SerializedName("inventory_value")
    val inventoryValue: Double,
    @SerializedName("total_items_in_stock")
    val totalItemsInStock: Int,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

data class StockUpdateResponse(
    val product: Product,
    @SerializedName("stock_change")
    val stockChange: Int,
    @SerializedName("previous_stock")
    val previousStock: Int,
    @SerializedName("new_stock")
    val newStock: Int,
    val message: String,
    @SerializedName("company_id")
    val companyId: String,
    val timestamp: String
)

