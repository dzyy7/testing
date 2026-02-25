package id.co.psplauncher.data.network.balance

import id.co.psplauncher.data.network.response.BalanceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface BalanceApi {
    @Headers("Content-Type: application/json")
    @GET("cashier/report/balance")
    suspend fun getBalance(
        @Query("currentDate") currentDate: String
    ): Response<BalanceResponse>
}
