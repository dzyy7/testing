package id.co.psplauncher.ui.payment.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.card.CardInquiryRepository
import id.co.psplauncher.data.network.cart.ShoppingCartRepository
import id.co.psplauncher.data.network.request.CustomerRequest
import id.co.psplauncher.data.network.request.TransactionRequest
import id.co.psplauncher.data.network.response.CardInquiryResponse
import id.co.psplauncher.data.network.response.ShoppingCartItemResponse
import id.co.psplauncher.data.network.response.ShoppingCartResponse
import id.co.psplauncher.data.network.response.TransactionResponse
import id.co.psplauncher.data.network.transaction.TransactionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardPaymentViewModel @Inject constructor(
    private val cardInquiryRepository: CardInquiryRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
    private val transactionRepository: TransactionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Cart state
    private val _cartState = MutableLiveData<Resource<ShoppingCartResponse>>()
    val cartState: LiveData<Resource<ShoppingCartResponse>> = _cartState

    // NFC Inquiry state
    private val _inquiryState = MutableLiveData<Resource<CardInquiryResponse>?>()
    val inquiryState: LiveData<Resource<CardInquiryResponse>?> = _inquiryState

    // Transaction state
    private val _transactionState = MutableLiveData<Resource<TransactionResponse>>()
    val transactionState: LiveData<Resource<TransactionResponse>> = _transactionState

    // Hold card data after inquiry
    private var cardData: CardInquiryResponse? = null
    private var cartId: String = ""
    private var scannedNfcId: String = ""

    fun loadCart(id: String) {
        cartId = id
        viewModelScope.launch {
            _cartState.value = Resource.Loading
            _cartState.value = shoppingCartRepository.getShoppingCart(id)
        }
    }

    fun onNfcTagRead(nfcId: String) {
        if (scannedNfcId == nfcId && _inquiryState.value is Resource.Success) {
            // Same card already scanned, skip
            return
        }
        scannedNfcId = nfcId
        viewModelScope.launch {
            _inquiryState.value = Resource.Loading
            val result = cardInquiryRepository.inquiryByNfc(nfcId)
            if (result is Resource.Success) {
                cardData = result.value
            }
            _inquiryState.value = result
        }
    }

    fun resetInquiry() {
        _inquiryState.value = null
        cardData = null
        scannedNfcId = ""
    }

    fun submitTransaction() {
        val card = cardData ?: return
        viewModelScope.launch {
            _transactionState.value = Resource.Loading
            val companyId = userPreferences.getCompanyId()
            val request = TransactionRequest(
                transactionId = "TN-${System.currentTimeMillis()}",
                paymentMethod = "CARD",
                shoppingCartId = cartId,
                pin = "",
                isLimitProcess = false,
                customer = CustomerRequest(
                    pspUserId = card.userId,
                    pspAccountId = card.accountId,
                    pspCallerId = card.callerId,
                    pspCallerName = card.callerName,
                    pspNfcId = card.nfcId,
                    pspCompanyId = card.companyId
                )
            )
            _transactionState.value = transactionRepository.postTransaction(request)
        }
    }

    fun getCartTotal(): Double {
        val cart = (_cartState.value as? Resource.Success)?.value ?: return 0.0
        return cart.shoppingCartItems.sumOf { it.product.sellingPrice * it.quantity }
    }

    fun getCartItems(): List<ShoppingCartItemResponse> {
        return (_cartState.value as? Resource.Success)?.value?.shoppingCartItems ?: emptyList()
    }

    fun getCardData(): CardInquiryResponse? = cardData
}