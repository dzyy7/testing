package id.co.psplauncher.ui.fragments.fragment_draft

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.local.CartItem
import id.co.psplauncher.data.local.CartManager
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.cart.ShoppingCartRepository
import id.co.psplauncher.data.network.response.DraftItem
import id.co.psplauncher.data.network.response.ImageItem
import id.co.psplauncher.data.network.response.Product
import id.co.psplauncher.data.network.response.ShoppingCartResponse
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DraftViewModel @Inject constructor(
    private val repository: ShoppingCartRepository
) : ViewModel() {

    private val _drafts = MutableLiveData<Resource<List<DraftItem>>>()
    val drafts: LiveData<Resource<List<DraftItem>>> = _drafts

    private val _loadDraftResult = MutableLiveData<Resource<ShoppingCartResponse>>()
    val loadDraftResult: LiveData<Resource<ShoppingCartResponse>> = _loadDraftResult

    fun fetchDrafts() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            _drafts.value = Resource.Loading
            val result = repository.getDraftList(startDate = today, endDate = today)
            if (result is Resource.Success) {
                _drafts.value = Resource.Success(result.value.content)
            } else {
                _drafts.value = result as Resource.Failure
            }
        }
    }

    fun loadDraftIntoCart(draftId: String) {
        viewModelScope.launch {
            _loadDraftResult.value = Resource.Loading
            val result = repository.getShoppingCart(draftId)
            if (result is Resource.Success) {
                val cart = result.value
                CartManager.clearCart()
                CartManager.setCurrentCartId(cart.id)
                cart.shoppingCartItems.forEach { item ->
                    val product = Product(
                        id = item.product.id,
                        name = item.product.name,
                        productType = item.product.productType,
                        stock = Int.MAX_VALUE,
                        category = item.product.category,
                        sku = item.product.sku,
                        description = item.product.description,
                        images = item.product.images.map { url ->
                            ImageItem(images = url, thumbnail = url)
                        },
                        companyId = item.product.companyId,
                        merchantId = item.product.merchantId,
                        active = item.product.active,
                        buyingPrice = 0.0,
                        sellingPrice = item.product.sellingPrice,
                        tax = item.product.tax,
                        totalPrice = item.product.sellingPrice * item.quantity,
                        orderCount = 0.0,
                        createdBy = "",
                        createdDate = "",
                        lastModifiedBy = null,
                        lastModifiedDate = "",
                        quantity = item.quantity
                    )
                    CartManager.addToCart(product, item.quantity)
                }
                _loadDraftResult.value = Resource.Success(cart)
            } else {
                _loadDraftResult.value = result as Resource.Failure
            }
        }
    }

    private val _deleteResult = MutableLiveData<Resource<Unit>>()
    val deleteResult: LiveData<Resource<Unit>> = _deleteResult

    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            _deleteResult.value = Resource.Loading
            val result = repository.deleteDraft(draftId)
            _deleteResult.value = result
            if (result is Resource.Success) {
                fetchDrafts()
            }
        }
    }

    fun isCartEmpty(): Boolean = CartManager.cartItems.value.isEmpty()
}