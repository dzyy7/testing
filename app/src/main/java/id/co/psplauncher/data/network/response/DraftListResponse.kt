package id.co.psplauncher.data.network.response

data class DraftListResponse(
    val content: List<DraftItem>,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val first: Boolean,
    val empty: Boolean
)

data class DraftItem(
    val id: String,
    val createDate: String,
    val itemCount: Int,
    val totalPrice: Double,
    val creatorName: String,
    val items: List<DraftProductItem>
) {
    // Nama draft: trx- + 12 karakter terakhir dari id
    val draftName: String
        get() = "trx-${id.takeLast(12)}"
}

data class DraftProductItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val buyingPrice: Double,
    val tax: Double
)