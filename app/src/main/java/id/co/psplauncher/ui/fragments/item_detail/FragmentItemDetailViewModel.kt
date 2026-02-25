package id.co.psplauncher.ui.fragments.item_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.local.CartManager
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.dashboard.DashboardRepository
import id.co.psplauncher.data.network.response.Product
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentItemDetailViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _productDetail = MutableLiveData<Resource<Product>>()
    val productDetail: LiveData<Resource<Product>> = _productDetail

    private val _quantity = MutableLiveData(1)
    val quantity: LiveData<Int> = _quantity

    private val _addToCartSuccess = MutableLiveData<Boolean>()
    val addToCartSuccess: LiveData<Boolean> = _addToCartSuccess

    private var currentProduct: Product? = null

    fun fetchProductDetail(productId: String) {
        _productDetail.value = Resource.Loading
        viewModelScope.launch {
            _productDetail.value = dashboardRepository.fetchProductById(productId)
            if (_productDetail.value is Resource.Success) {
                currentProduct = (_productDetail.value as Resource.Success<Product>).value
            }
        }
    }

    fun increaseQuantity() {
        val product = currentProduct ?: return
        val currentQty = _quantity.value ?: 1
        val cartQty = CartManager.getItemQuantity(product.id)
        val maxAllowed = product.stock - cartQty

        if (currentQty < maxAllowed) {
            _quantity.value = currentQty + 1
        }
    }

    fun decreaseQuantity() {
        val currentQty = _quantity.value ?: 1
        if (currentQty > 1) {
            _quantity.value = currentQty - 1
        }
    }

    fun addToCart(): Boolean {
        val product = currentProduct ?: return false
        val qty = _quantity.value ?: 1

        if (product.stock <= 0) return false

        val cartQty = CartManager.getItemQuantity(product.id)
        if (cartQty + qty > product.stock) return false

        CartManager.addToCart(product, qty)
        _addToCartSuccess.value = true
        return true
    }

    fun getMaxQuantity(): Int {
        val product = currentProduct ?: return 0
        val cartQty = CartManager.getItemQuantity(product.id)
        return maxOf(0, product.stock - cartQty)
    }
}
