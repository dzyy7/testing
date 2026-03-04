package id.co.psplauncher.data.network.response

data class TransactionResponse(
    val merchantName: String,
    val cashierName: String,
    val posName: String,
    val transactionId: String,
    val transactionTime: String,
    val paymentMethod: String,
    val orderDetails: List<OrderDetail>,
    val totalAmount: Double,
    val success: Boolean,
    val message: String
)

data class OrderDetail(
    val id: String,
    val productName: String,
    val price: Double,
    val buyingPrice: Double,
    val category: String,
    val tax: Double,
    val quantity: Int,
    val productType: String,
    val sku: String?,
    val description: String?
)