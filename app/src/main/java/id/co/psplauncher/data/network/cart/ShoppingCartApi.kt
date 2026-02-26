package id.co.psplauncher.data.network.cart

import id.co.psplauncher.data.network.request.ShoppingCartRequest
import id.co.psplauncher.data.network.response.DraftListResponse
import id.co.psplauncher.data.network.response.ShoppingCartResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ShoppingCartApi {

    @POST("cashier/shopping-cart")
    suspend fun postShoppingCart(
        @Body request: ShoppingCartRequest
    ): Response<ShoppingCartResponse>

    @GET("cashier/shopping-cart/{id}")
    suspend fun getShoppingCart(
        @Path("id") id: String
    ): Response<ShoppingCartResponse>

    @PUT("cashier/shopping-cart/{id}")
    suspend fun putShoppingCart(
        @Path("id") id: String,
        @Body request: ShoppingCartRequest
    ): Response<ShoppingCartResponse>

    @GET("cashier/shopping-cart")
    suspend fun getDraftList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
        @Query("sort") sort: String = "createdDate,ASC",
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<DraftListResponse>

    @DELETE("cashier/shopping-cart/{id}")
    suspend fun deleteShoppingCart(
        @Path("id") id: String
    ): Response<Unit>
}