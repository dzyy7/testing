package id.co.psplauncher.data.network.response

data class ChipResponse (
    val content: List<Content>,
    val pageable: Pageable,
    val totalPages: Long,
    val totalElements: Long,
    val last: Boolean,
    val numberOfElements: Long,
    val first: Boolean,
    val size: Long,
    val number: Long,
    val sort: Sort,
    val empty: Boolean
)

data class Content (
    val name: String,
    val id: String
)
