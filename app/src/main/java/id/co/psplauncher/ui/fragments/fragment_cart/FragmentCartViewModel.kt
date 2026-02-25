package id.co.psplauncher.ui.fragments.fragment_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import id.co.psplauncher.data.local.CartManager

class FragmentCartViewModel : ViewModel() {

    val cartItems = CartManager.cartItems.asLiveData()

    fun updateQuantity(productId: String, newQuantity: Int) {
        CartManager.updateQuantity(productId, newQuantity)
    }

    fun removeItem(productId: String) {
        CartManager.removeFromCart(productId)
    }

    fun clearCart() {
        CartManager.clearCart()
    }

    fun getTotal(): Double {
        return CartManager.getCartTotal()
    }

    fun getItemCount(): Int {
        return CartManager.getCartItemCount()
    }
}
