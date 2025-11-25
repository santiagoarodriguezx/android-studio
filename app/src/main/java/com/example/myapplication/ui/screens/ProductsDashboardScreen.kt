package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.ProductsViewModel
import com.example.myapplication.viewmodel.ProductsState
import com.example.myapplication.data.models.Product
import com.example.myapplication.data.models.ProductSummary
import com.example.myapplication.data.models.Company
import java.text.NumberFormat
import java.util.*

/**
 * 📦 Dashboard de Productos - Pantalla Principal
 *
 * Gestión completa de productos con diseño moderno
 * Incluye estadísticas, acciones rápidas y lista de productos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsDashboardScreen(
    viewModel: ProductsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val productsState by viewModel.productsState.collectAsState()
    val products by viewModel.products.collectAsState()
    val summary by viewModel.productsSummary.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val currentFilters by viewModel.currentFilters.collectAsState()
    val companyInfo by viewModel.companyInfo.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Inventory,
                            contentDescription = null,
                            tint = Primary
                        )
                        Text(
                            "Gestión de Productos",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Filtros activos indicator
                    if (currentFilters.category != null || currentFilters.searchQuery != null) {
                        Badge(
                            containerColor = Warning,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text("!")
                        }
                    }

                    // Filtrar
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Outlined.FilterList, "Filtros")
                    }

                    // Refrescar
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Outlined.Refresh, "Refrescar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddProductDialog = true },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Nuevo Producto") },
                containerColor = Primary,
                contentColor = Color.White
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            when {
                productsState is ProductsState.Loading && products.isEmpty() -> {
                    LoadingView()
                }

                productsState is ProductsState.Error && products.isEmpty() -> {
                    ErrorView(
                        message = (productsState as ProductsState.Error).message,
                        onRetry = { viewModel.refreshData() }
                    )
                }

                else -> {
                    ProductsContent(
                        products = products,
                        summary = summary,
                        companyInfo = companyInfo,
                        isRefreshing = isRefreshing,
                        searchQuery = searchQuery,
                        onSearchQueryChange = {
                            searchQuery = it
                            if (it.isBlank()) {
                                viewModel.loadProducts()
                            } else {
                                viewModel.searchProducts(it)
                            }
                        },
                        onCategoryClick = { category ->
                            selectedCategory = if (selectedCategory == category) null else category
                            selectedCategory?.let { viewModel.loadProductsByCategory(it) }
                                ?: viewModel.loadProducts()
                        },
                        selectedCategory = selectedCategory,
                        onProductClick = { /* TODO: Navegar a detalles */ },
                        onStockUpdate = { productId, change ->
                            viewModel.updateStock(productId, change)
                        },
                        onToggleActive = { productId ->
                            // TODO: Implementar toggle active
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddProductDialog) {
        // TODO: Implementar dialog crear producto
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Nuevo Producto") },
            text = { Text("Funcionalidad próximamente...") },
            confirmButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showFilterDialog) {
        FilterDialog(
            currentFilters = currentFilters,
            onDismiss = { showFilterDialog = false },
            onApplyFilters = { activeOnly, minPrice, maxPrice ->
                viewModel.loadProducts(
                    activeOnly = activeOnly,
                    minPrice = minPrice,
                    maxPrice = maxPrice
                )
                showFilterDialog = false
            },
            onClearFilters = {
                viewModel.clearFilters()
                selectedCategory = null
                searchQuery = ""
                showFilterDialog = false
            }
        )
    }

    // Mostrar mensajes de éxito/error
    LaunchedEffect(productsState) {
        when (val state = productsState) {
            is ProductsState.Success -> {
                // Opcional: Mostrar Snackbar
                viewModel.resetState()
            }
            is ProductsState.Error -> {
                // Error ya se muestra en UI
            }
            else -> {}
        }
    }
}

@Composable
private fun ProductsContent(
    products: List<Product>,
    summary: ProductSummary?,
    companyInfo: Company?,
    isRefreshing: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    selectedCategory: String?,
    onProductClick: (String) -> Unit,
    onStockUpdate: (String, Int) -> Unit,
    onToggleActive: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Indicador de carga mientras refresca
        if (isRefreshing) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Primary
                )
            }
        }

        // Información de la empresa
        if (companyInfo != null) {
            item {
                CompanyInfoCard(company = companyInfo)
            }
        }

        // Barra de búsqueda
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )
        }

        // Estadísticas principales
        if (summary != null) {
            item {
                Text(
                    "📊 Resumen General",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            }

            item {
                SummaryKPIs(summary = summary)
            }
        }

        // Categorías
        if (summary != null && summary.categories.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "📂 Categorías",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
            }

            item {
                CategoriesRow(
                    categories = summary.categories,
                    selectedCategory = selectedCategory,
                    onCategoryClick = onCategoryClick
                )
            }
        }

        // Acciones rápidas
        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "⚡ Acciones Rápidas",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
        }

        item {
            QuickActionsGrid(summary = summary)
        }

        // Lista de productos
        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "📦 Productos (${products.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                if (searchQuery.isNotBlank() || selectedCategory != null) {
                    Text(
                        "Filtrado",
                        fontSize = 14.sp,
                        color = Warning,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (products.isEmpty()) {
            item {
                EmptyProductsView(
                    hasFilters = searchQuery.isNotBlank() || selectedCategory != null
                )
            }
        } else {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) },
                    onStockUpdate = onStockUpdate,
                    onToggleActive = onToggleActive
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar productos...") },
        leadingIcon = {
            Icon(Icons.Outlined.Search, null, tint = OnSurfaceVariant)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Outlined.Clear, "Limpiar", tint = OnSurfaceVariant)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
            focusedBorderColor = Primary,
            unfocusedBorderColor = OnSurfaceVariant.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun SummaryKPIs(summary: ProductSummary) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPICard(
                title = "Total Productos",
                value = summary.totalProducts.toString(),
                icon = Icons.Outlined.Inventory,
                color = Primary,
                modifier = Modifier.weight(1f)
            )
            KPICard(
                title = "Activos",
                value = summary.activeProducts.toString(),
                icon = Icons.Outlined.CheckCircle,
                color = Success,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPICard(
                title = "Sin Stock",
                value = summary.outOfStock.toString(),
                icon = Icons.Outlined.ErrorOutline,
                color = Error,
                modifier = Modifier.weight(1f)
            )
            KPICard(
                title = "Categorías",
                value = summary.totalCategories.toString(),
                icon = Icons.Outlined.Category,
                color = Secondary,
                modifier = Modifier.weight(1f)
            )
        }

        // Valor total del inventario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.AttachMoney,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            "Valor Total Inventario",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "${summary.totalItemsInStock} items en stock",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
                Text(
                    currencyFormat.format(summary.inventoryValue),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Success
                )
            }
        }
    }
}

@Composable
private fun CategoriesRow(
    categories: List<String>,
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategoryClick(category) },
                label = { Text(category) },
                leadingIcon = if (selectedCategory == category) {
                    { Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun QuickActionsGrid(summary: ProductSummary?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            title = "Bajo Stock",
            value = summary?.outOfStock?.toString() ?: "-",
            icon = Icons.Outlined.Warning,
            color = Warning,
            onClick = { /* TODO: Filtrar productos con bajo stock */ },
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Inactivos",
            value = summary?.inactiveProducts?.toString() ?: "-",
            icon = Icons.Outlined.Block,
            color = OnSurfaceVariant,
            onClick = { /* TODO: Mostrar productos inactivos */ },
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Exportar",
            value = "",
            icon = Icons.Outlined.Download,
            color = Secondary,
            onClick = { /* TODO: Exportar datos */ },
            modifier = Modifier.weight(1f),
            showValue = false
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showValue: Boolean = true
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showValue) {
                Text(
                    value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onStockUpdate: (String, Int) -> Unit,
    onToggleActive: (String) -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    var showStockDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera con nombre y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            product.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (product.active) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                "Activo",
                                tint = Success,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (product.category != null) {
                        Text(
                            product.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = Secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Menú de opciones
                IconButton(onClick = { /* TODO: Mostrar menú */ }) {
                    Icon(Icons.Outlined.MoreVert, "Opciones")
                }
            }

            if (product.description != null) {
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Info de precio y stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precio
                Column {
                    Text(
                        "Precio",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        currencyFormat.format(product.price),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }

                // Stock
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Stock",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            product.stock.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                product.stock == 0 -> Error
                                product.stock < 10 -> Warning
                                else -> Success
                            }
                        )
                        Icon(
                            when {
                                product.stock == 0 -> Icons.Outlined.ErrorOutline
                                product.stock < 10 -> Icons.Outlined.Warning
                                else -> Icons.Outlined.Inventory
                            },
                            null,
                            tint = when {
                                product.stock == 0 -> Error
                                product.stock < 10 -> Warning
                                else -> Success
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showStockDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Icon(Icons.Outlined.AddCircleOutline, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Stock")
                }

                OutlinedButton(
                    onClick = { /* TODO: Editar */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Secondary
                    )
                ) {
                    Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Editar")
                }
            }
        }
    }

    // Dialog para actualizar stock
    if (showStockDialog) {
        StockUpdateDialog(
            productName = product.name,
            currentStock = product.stock,
            onDismiss = { showStockDialog = false },
            onUpdateStock = { change ->
                onStockUpdate(product.id, change)
                showStockDialog = false
            }
        )
    }
}

@Composable
private fun StockUpdateDialog(
    productName: String,
    currentStock: Int,
    onDismiss: () -> Unit,
    onUpdateStock: (Int) -> Unit
) {
    var stockChange by remember { mutableStateOf("") }
    var isAddition by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Stock") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    productName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Stock actual: $currentStock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isAddition,
                        onClick = { isAddition = true },
                        label = { Text("Agregar") },
                        leadingIcon = { Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isAddition,
                        onClick = { isAddition = false },
                        label = { Text("Quitar") },
                        leadingIcon = { Icon(Icons.Filled.Remove, null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = stockChange,
                    onValueChange = { stockChange = it.filter { char -> char.isDigit() } },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (stockChange.isNotBlank()) {
                    val change = stockChange.toIntOrNull() ?: 0
                    val newStock = if (isAddition) currentStock + change else currentStock - change
                    Text(
                        "Nuevo stock: $newStock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (newStock >= 0) Success else Error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val change = stockChange.toIntOrNull() ?: 0
                    onUpdateStock(if (isAddition) change else -change)
                },
                enabled = stockChange.isNotBlank() && stockChange.toIntOrNull() != null
            ) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun FilterDialog(
    currentFilters: ProductsViewModel.FilterState,
    onDismiss: () -> Unit,
    onApplyFilters: (Boolean, Double?, Double?) -> Unit,
    onClearFilters: () -> Unit
) {
    var activeOnly by remember { mutableStateOf(currentFilters.activeOnly) }
    var minPrice by remember { mutableStateOf(currentFilters.minPrice?.toString() ?: "") }
    var maxPrice by remember { mutableStateOf(currentFilters.maxPrice?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtros") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Activos/Inactivos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Solo productos activos")
                    Switch(
                        checked = activeOnly,
                        onCheckedChange = { activeOnly = it }
                    )
                }

                // Rango de precios
                Text(
                    "Rango de Precios",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minPrice,
                        onValueChange = { minPrice = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Mínimo") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxPrice,
                        onValueChange = { maxPrice = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Máximo") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters(
                        activeOnly,
                        minPrice.toDoubleOrNull(),
                        maxPrice.toDoubleOrNull()
                    )
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onClearFilters) {
                    Text("Limpiar")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        }
    )
}

@Composable
private fun EmptyProductsView(hasFilters: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                if (hasFilters) Icons.Outlined.SearchOff else Icons.Outlined.Inventory,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                if (hasFilters) "No se encontraron productos" else "No hay productos registrados",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (hasFilters) {
                Text(
                    "Intenta ajustar los filtros de búsqueda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun KPICard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = Primary)
            Text(
                "Cargando productos...",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                "Error",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Error
            )
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Outlined.Refresh, null)
                Spacer(Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

/**
 * Card con información de la empresa
 */
@Composable
private fun CompanyInfoCard(company: Company) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo o ícono de la empresa
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Business,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Primary
                )
            }

            // Información de la empresa
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    company.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )

                company.sector?.let { sector ->
                    Text(
                        sector,
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Plan de suscripción
                    Surface(
                        color = when (company.subscriptionPlan) {
                            "enterprise" -> Success.copy(alpha = 0.1f)
                            "pro" -> Primary.copy(alpha = 0.1f)
                            else -> OnSurfaceVariant.copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            company.subscriptionPlan.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (company.subscriptionPlan) {
                                "enterprise" -> Success
                                "pro" -> Primary
                                else -> OnSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Estado
                    if (company.isActive) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Activo",
                            modifier = Modifier.size(16.dp),
                            tint = Success
                        )
                        Text(
                            "Activo",
                            fontSize = 12.sp,
                            color = Success
                        )
                    }
                }
            }
        }
    }
}
