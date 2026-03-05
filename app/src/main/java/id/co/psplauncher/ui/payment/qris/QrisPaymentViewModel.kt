package id.co.psplauncher.ui.payment.qris

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.cart.ShoppingCartRepository
import id.co.psplauncher.data.network.response.ShoppingCartItemResponse
import id.co.psplauncher.data.network.response.ShoppingCartResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrisPaymentViewModel @Inject constructor(
    private val shoppingCartRepository: ShoppingCartRepository
) : ViewModel() {

    private val _cartState = MutableLiveData<Resource<ShoppingCartResponse>>()
    val cartState: LiveData<Resource<ShoppingCartResponse>> = _cartState

    private val _isFullscreen = MutableLiveData(false)
    val isFullscreen: LiveData<Boolean> = _isFullscreen

    private var cartId: String = ""

    fun loadCart(id: String) {
        cartId = id
        viewModelScope.launch {
            _cartState.value = Resource.Loading
            _cartState.value = shoppingCartRepository.getShoppingCart(id)
        }
    }

    fun getCartId(): String = cartId

    fun getCartTotal(): Double {
        val cart = (_cartState.value as? Resource.Success)?.value ?: return 0.0
        return cart.shoppingCartItems.sumOf { it.product.sellingPrice * it.quantity }
    }

    fun getCartItems(): List<ShoppingCartItemResponse> {
        return (_cartState.value as? Resource.Success)?.value?.shoppingCartItems ?: emptyList()
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !(_isFullscreen.value ?: false)
    }

    fun setFullscreen(value: Boolean) {
        _isFullscreen.value = value
    }
}
