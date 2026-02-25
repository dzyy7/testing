package id.co.psplauncher.data.network.dashboard

import id.co.psplauncher.data.network.response.DashboardResponse
import id.co.psplauncher.data.network.response.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DashboardApi {
    @Headers("Content-Type: application/json")
    @GET("cashier/product?page=0&size=18&sort=name%2CASC&showEmptyStock=false&tags=")
    suspend fun dashboard(): Response<DashboardResponse>

    @Headers("Content-Type: application/json")
    @GET("cashier/product/{productId}")
    suspend fun getProductById(@Path("productId") productId: String): Response<Product>
}