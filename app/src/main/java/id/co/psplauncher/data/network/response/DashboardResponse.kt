package id.co.psplauncher.data.network.response

data class DashboardResponse(
    val content: List<Product>,
    val pageable: Pageable,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val numberOfElements: Int,
    val first: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val empty: Boolean
)

data class Product(
    val id: String,
    val name: String,
    val productType: String,
    val stock: Int,
    val category: Category,
    val sku: String?,
    val description: String?,
    val images: List<ImageItem>,
    val companyId: String,
    val merchantId: String,
    val active: Boolean,
    val buyingPrice: Double,
    val sellingPrice: Double,
    val tax: Double,
    val totalPrice: Double,
    val orderCount: Double,
    val createdBy: String,
    val createdDate: String,
    val lastModifiedBy: String?,
    val lastModifiedDate: String,
    var quantity: Int
) {
    val sumPrice: Double
        get() = sellingPrice * quantity
}

data class Category(
    val id: String,
    val name: String,
    val description: String?
)

data class ImageItem(
    val images: String,
    val thumbnail: String
)

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: Sort,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)

data class Sort(
    val unsorted: Boolean,
    val sorted: Boolean,
    val empty: Boolean
)
