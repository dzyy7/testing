package id.co.psplauncher.data.network.response

data class AuthCheckResponse(
    val id: String,
    val name: String,
    val username: String,
    val email: String?,
    val phone: String?,
    val companyId: String,
    val companyCode: String,
    val companyInitial: String,
    val companyName: String,
    val active: Boolean,
    val roleType: String,
    val merchantId: String,
    val merchantName: String,
    val createdBy: String?,
    val createdDate: String?,
    val lastModifiedBy: String?,
    val lastModifiedDate: String?,
    val paymentMethods: List<String>?,
    val isSaveModeEnabled: Boolean?,
    val saveModePaymentMethods: List<String>?,
    val token: String
)
