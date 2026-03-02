package id.co.psplauncher.ui.fragments.dialog_paymentconfirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.response.TransactionResponse
import id.co.psplauncher.data.network.transaction.TransactionRepository
import id.co.psplauncher.ui.payment.PaymentMethod
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialogPaymentConfirmationViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _transactionResult = MutableLiveData<Resource<TransactionResponse>>()
    val transactionResult: LiveData<Resource<TransactionResponse>> = _transactionResult

    fun submitTransaction(cartId: String, paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            _transactionResult.value = Resource.Loading
            val companyId = userPreferences.getCompanyId()
            val request = transactionRepository.buildRequest(
                cartId = cartId,
                paymentMethod = paymentMethod,
                companyId = companyId
            )
            _transactionResult.value = transactionRepository.postTransaction(request)
        }
    }
}