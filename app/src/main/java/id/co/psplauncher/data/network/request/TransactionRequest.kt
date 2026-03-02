package id.co.psplauncher.data.network.request

data class TransactionRequest(
    val transactionId: String,
    val paymentMethod: String,
    val customer: CustomerRequest,
    val shoppingCartId: String,
    val pin: String = "",
    val isLimitProcess: Boolean = false
)

data class CustomerRequest(
    val pspUserId: String = "-",
    val pspAccountId: String = "-",
    val pspCallerId: String = "-",
    val pspCallerName: String,
    val pspNfcId: String = "-",
    val pspCompanyId: String
)