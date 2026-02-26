package id.co.psplauncher.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.cart.ShoppingCartRepository
import id.co.psplauncher.data.network.request.ShoppingCartItemRequest
import id.co.psplauncher.data.network.request.ShoppingCartRequest
import id.co.psplauncher.data.network.response.ShoppingCartResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val shoppingCartRepository: ShoppingCartRepository
) : ViewModel() {

    private val _cartState = MutableLiveData<Resource<ShoppingCartResponse>>()
    val cartState: LiveData<Resource<ShoppingCartResponse>> = _cartState

    private val _updateState = MutableLiveData<Resource<ShoppingCartResponse>>()
    val updateState: LiveData<Resource<ShoppingCartResponse>> = _updateState

    private val _selectedPaymentMethod = MutableLiveData(PaymentMethod.CASH)
    val selectedPaymentMethod: LiveData<PaymentMethod> = _selectedPaymentMethod

    // Simpan cartId agar PUT bisa pakai tanpa kirim ulang dari Fragment
    private var cartId: String = ""

    fun loadCart(id: String) {
        cartId = id
        viewModelScope.launch {
            _cartState.value = Resource.Loading
            _cartState.value = shoppingCartRepository.getShoppingCart(id)
        }
    }

    fun updateItemQuantity(productId: String, newQuantity: Int) {
        val currentCart = (_cartState.value as? Resource.Success)?.value ?: return

        // Buat request baru dengan quantity yang sudah diupdate
        val updatedItems = currentCart.shoppingCartItems.map { item ->
            ShoppingCartItemRequest(
                productId = item.product.id,
                quantity = if (item.product.id == productId) newQuantity else item.quantity
            )
        }.filter { it.quantity > 0 } // hapus item yang qty-nya 0

        val request = ShoppingCartRequest(shoppingCartItems = updatedItems)

        viewModelScope.launch {
            _updateState.value = Resource.Loading
            val result = shoppingCartRepository.putShoppingCart(cartId, request)
            if (result is Resource.Success) {
                // Sync _cartState dengan data terbaru dari server
                _cartState.value = result
            }
            _updateState.value = result
        }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun getCartTotal(): Double {
        val cart = (_cartState.value as? Resource.Success)?.value ?: return 0.0
        return cart.shoppingCartItems.sumOf { it.product.sellingPrice * it.quantity }
    }
}

enum class PaymentMethod {
    CASH,
    BANK_TRANSFER,
    CARD,
    EDC,
    QRIS
}