package id.co.psplauncher.data.network.request

data class ShoppingCartRequest(
    val shoppingCartItems: List<ShoppingCartItemRequest>
)

data class ShoppingCartItemRequest(
    val productId: String,
    val quantity: Int
)