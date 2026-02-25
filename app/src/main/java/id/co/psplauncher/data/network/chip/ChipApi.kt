package id.co.psplauncher.data.network.chip

import id.co.psplauncher.data.network.response.ChipResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ChipApi {
    @Headers("Content-Type: application/json")
    @GET("cashier/categories/chip")
    suspend fun fetchChip(): Response<ChipResponse>
}