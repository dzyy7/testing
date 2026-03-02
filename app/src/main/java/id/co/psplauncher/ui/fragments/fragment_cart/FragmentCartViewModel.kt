package id.co.psplauncher.ui.fragments.fragment_cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.local.CartManager
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.cart.ShoppingCartRepository
import id.co.psplauncher.data.network.request.ShoppingCartItemRequest
import id.co.psplauncher.data.network.request.ShoppingCartRequest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentCartViewModel @Inject constructor(
    private val shoppingCartRepository: ShoppingCartRepository
) : ViewModel() {

    val cartItems = CartManager.cartItems.asLiveData()

    private val _postCartResult = MutableLiveData<Resource<String>>()
    val postCartResult: LiveData<Resource<String>> = _postCartResult

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

    fun postShoppingCart() {
        val items = CartManager.cartItems.value
        if (items.isEmpty()) return

        val request = ShoppingCartRequest(
            shoppingCartItems = items.map { cartItem ->
                ShoppingCartItemRequest(
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity
                )
            }
        )

        viewModelScope.launch {
            _postCartResult.value = Resource.Loading
            val existingCartId = CartManager.currentCartId
            val result = if (existingCartId != null) {
                shoppingCartRepository.putShoppingCart(existingCartId, request)
            } else {
                shoppingCartRepository.postShoppingCart(request)
            }

            if (result is Resource.Success) {
                CartManager.setCurrentCartId(result.value.id)
                _postCartResult.value = Resource.Success(result.value.id)
            } else {
                _postCartResult.value = result as Resource.Failure
            }
        }
    }
}