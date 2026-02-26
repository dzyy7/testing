package id.co.psplauncher.data.local

import id.co.psplauncher.data.network.response.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartItem(
    val product: Product,
    val quantity: Int   // <-- UBAH var → val, paksa immutable
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
                // Buat object BARU — bukan mutasi langsung
                currentItems[existingIndex] = existingItem.copy(quantity = newQuantity)
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
        val index = currentItems.indexOfFirst { it.product.id == productId }

        if (index >= 0) {
            if (newQuantity <= 0) {
                currentItems.removeAt(index)
            } else if (newQuantity <= currentItems[index].product.stock) {
                // Buat object BARU dengan copy() — bukan mutasi var langsung
                currentItems[index] = currentItems[index].copy(quantity = newQuantity)
            }
            _cartItems.value = currentItems
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
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