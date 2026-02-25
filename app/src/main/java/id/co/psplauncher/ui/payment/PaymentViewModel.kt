package id.co.psplauncher.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel() {

    // Selected payment method
    private val _selectedPaymentMethod = MutableLiveData<PaymentMethod>()
    val selectedPaymentMethod: LiveData<PaymentMethod> = _selectedPaymentMethod

    // Cart items
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    // Total amount
    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> = _totalAmount

    init {
        // Set default payment method
        _selectedPaymentMethod.value = PaymentMethod.CASH

        // TODO: Load cart items from repository/shared state
        loadCartItems()
    }

    private fun loadCartItems() {
        // TODO: Implement cart loading logic
        // For now, empty list
        _cartItems.value = emptyList()
        calculateTotal()
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    private fun calculateTotal() {
        val total = _cartItems.value?.sumOf { it.price * it.quantity } ?: 0.0
        _totalAmount.value = total
    }

    fun processPayment() {
        // TODO: Implement payment processing logic
    }
}

// Payment method enum
enum class PaymentMethod {
    CASH,           // Tunai
    BANK_TRANSFER,  // Transfer Bank
    CARD,           // Kartu
    EDC,            // EDC
    QRIS            // QRIS
}

// Cart item data class
data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
)