package id.co.psplauncher.data.network.response

data class ShoppingCartResponse(
    val id: String,
    val cashier: CashierInfo,
    val companyId: String,
    val merchantId: String,
    val shoppingCartItems: List<ShoppingCartItemResponse>
)

data class CashierInfo(
    val id: String,
    val name: String
)

data class ShoppingCartItemResponse(
    val id: String,
    val quantity: Int,
    val product: ShoppingCartProduct
)

data class ShoppingCartProduct(
    val id: String,
    val name: String,
    val productType: String,
    val category: Category,
    val sku: String?,
    val description: String?,
    val images: List<String>,
    val companyId: String,
    val merchantId: String,
    val active: Boolean,
    val sellingPrice: Double,
    val tax: Double
)