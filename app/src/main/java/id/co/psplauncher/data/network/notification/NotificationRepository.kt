package id.co.psplauncher.data.network.notification

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val api: NotificationApi,
    private val userPreferences: UserPreferences
) : BaseRepository() {

    suspend fun getNotifications(page: Int = 0, size: Int = 10, sort: String = "date,desc") = safeApiCall(
        { api.getNotifications(page, size, sort) },
        userPreferences
    )

    suspend fun getNotificationDetail(id: String) = safeApiCall(
        { api.getNotificationDetail(id) },
        userPreferences
    )
}
