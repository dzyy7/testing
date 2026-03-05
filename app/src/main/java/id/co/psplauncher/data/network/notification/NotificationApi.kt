package id.co.psplauncher.data.network.notification

import id.co.psplauncher.data.network.response.NotificationDetailResponse
import id.co.psplauncher.data.network.response.NotificationListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {

    @GET("cashier/notification")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "date,desc"
    ): Response<NotificationListResponse>

    @GET("cashier/notification/{id}")
    suspend fun getNotificationDetail(
        @Path("id") id: String
    ): Response<NotificationDetailResponse>
}
