package id.co.psplauncher.data.network.transaction

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.request.CustomerRequest
import id.co.psplauncher.data.network.request.TransactionRequest
import id.co.psplauncher.data.network.response.TransactionResponse
import id.co.psplauncher.ui.payment.PaymentMethod
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val api: TransactionApi,
    private val userPreferences: UserPreferences
) : BaseRepository() {

    suspend fun postTransaction(request: TransactionRequest): Resource<TransactionResponse> {
        return safeApiCall({ api.postTransaction(request) }, userPreferences)
    }

    // Helper: build request langsung dari sini
    fun buildRequest(
        cartId: String,
        paymentMethod: PaymentMethod,
        companyId: String
    ): TransactionRequest {
        return TransactionRequest(
            transactionId = generateTransactionId(),
            paymentMethod = paymentMethod.toApiString(),
            shoppingCartId = cartId,
            customer = CustomerRequest(
                pspCallerName = paymentMethod.toCallerName(),
                pspCompanyId = companyId
            )
        )
    }

    private fun generateTransactionId(): String = "TM-${System.currentTimeMillis()}"
}

// Extension functions untuk PaymentMethod
fun PaymentMethod.toApiString(): String = when (this) {
    PaymentMethod.CASH -> "CASH"
    PaymentMethod.BANK_TRANSFER -> "BANK_TRANSFER"
    PaymentMethod.CARD -> "CARD"
    PaymentMethod.EDC -> "EDC"
    PaymentMethod.QRIS -> "QRIS"
}

fun PaymentMethod.toCallerName(): String = when (this) {
    PaymentMethod.CASH -> "CASH"
    PaymentMethod.BANK_TRANSFER -> "TRANSFER"
    PaymentMethod.CARD -> "CARD"
    PaymentMethod.EDC -> "EDC"
    PaymentMethod.QRIS -> "QRIS"
}

fun PaymentMethod.showKembalian(): Boolean = this == PaymentMethod.CASH
fun PaymentMethod.needsInputNominal(): Boolean = this == PaymentMethod.CASH