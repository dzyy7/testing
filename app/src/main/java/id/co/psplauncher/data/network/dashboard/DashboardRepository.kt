package id.co.psplauncher.data.network.dashboard

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import id.co.psplauncher.data.network.response.Product
import javax.inject.Inject

class DashboardRepository @Inject constructor (
    private val api: DashboardApi,
    private val userPreferences: UserPreferences
): BaseRepository() {
    suspend fun fetchData() = safeApiCall({
     api.dashboard()
    },
    userPreferences)

    suspend fun fetchProductById(productId: String) = safeApiCall<Product>(
        { api.getProductById(productId) },
        userPreferences
    )
}