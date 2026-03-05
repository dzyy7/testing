package id.co.psplauncher.data.network.response

data class NotificationListResponse(
    val content: List<NotificationItem>,
    val pageable: PageableInfo,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val numberOfElements: Int,
    val first: Boolean,
    val size: Int,
    val number: Int,
    val sort: SortInfo,
    val empty: Boolean
)

data class NotificationItem(
    val id: String,
    val userName: String,
    val date: String,
    val type: String
)

data class NotificationDetailResponse(
    val id: String,
    val userName: String,
    val date: String,
    val type: String,
    val message: String
)

data class PageableInfo(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: SortInfo,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)

data class SortInfo(
    val unsorted: Boolean,
    val sorted: Boolean,
    val empty: Boolean
)
