package id.co.psplauncher.data.network.cart

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.request.ShoppingCartRequest
import id.co.psplauncher.data.network.response.DraftListResponse
import id.co.psplauncher.data.network.response.ShoppingCartResponse
import javax.inject.Inject

class ShoppingCartRepository @Inject constructor(
    private val api: ShoppingCartApi,
    private val userPreferences: UserPreferences
) : BaseRepository() {

    suspend fun postShoppingCart(request: ShoppingCartRequest): Resource<ShoppingCartResponse> {
        return safeApiCall({ api.postShoppingCart(request) }, userPreferences)
    }

    suspend fun getShoppingCart(id: String): Resource<ShoppingCartResponse> {
        return safeApiCall({ api.getShoppingCart(id) }, userPreferences)
    }

    suspend fun putShoppingCart(id: String, request: ShoppingCartRequest): Resource<ShoppingCartResponse> {
        return safeApiCall({ api.putShoppingCart(id, request) }, userPreferences)
    }

    suspend fun getDraftList(startDate: String, endDate: String): Resource<DraftListResponse> {
        return safeApiCall({ api.getDraftList(startDate = startDate, endDate = endDate) }, userPreferences)
    }

    suspend fun deleteDraft(id: String): Resource<Unit> {
        return safeApiCall({ api.deleteShoppingCart(id) }, userPreferences)
    }
}