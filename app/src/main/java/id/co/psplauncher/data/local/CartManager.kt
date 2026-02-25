package id.co.psplauncher.data.local

import id.co.psplauncher.data.network.response.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartItem(
    val product: Product,
    var quantity: Int
) {
    val totalPrice: Double
        get() = product.sellingPrice * quantity
}

object CartManager {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(product: Product, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.product.id == product.id }

        if (existingIndex >= 0) {
            val existingItem = currentItems[existingIndex]
            val newQuantity = existingItem.quantity + quantity
            if (newQuantity <= product.stock) {
                existingItem.quantity = newQuantity
            }
        } else {
            val qtyToAdd = if (quantity > product.stock) product.stock else quantity
            if (qtyToAdd > 0) {
                currentItems.add(CartItem(product, qtyToAdd))
            }
        }

        _cartItems.value = currentItems
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val item = currentItems.find { it.product.id == productId }

        if (item != null) {
            if (newQuantity <= 0) {
                currentItems.remove(item)
            } else if (newQuantity <= item.product.stock) {
                item.quantity = newQuantity
            }
            _cartItems.value = currentItems
        }
    }

    fun removeFromCart(productId: String) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.product.id == productId }
        _cartItems.value = currentItems
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getCartTotal(): Double {
        return _cartItems.value.sumOf { it.totalPrice }
    }

    fun getCartItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }

    fun getItemQuantity(productId: String): Int {
        return _cartItems.value.find { it.product.id == productId }?.quantity ?: 0
    }
}
