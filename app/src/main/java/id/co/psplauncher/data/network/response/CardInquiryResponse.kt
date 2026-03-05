package id.co.psplauncher.data.network.response

data class CardInquiryResponse(
    val id: String,
    val accountId: String,
    val name: String,
    val userId: String,
    val nfcId: String,
    val companyId: String,
    val callerId: String,
    val callerName: String,
    val photoUrl: String,
    val balance: Double,
    val usePin: Boolean,
    val isUnlimited: Boolean
)