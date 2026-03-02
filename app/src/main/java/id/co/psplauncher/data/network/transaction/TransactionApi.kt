package id.co.psplauncher.data.network.transaction

import id.co.psplauncher.data.network.request.TransactionRequest
import id.co.psplauncher.data.network.response.TransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionApi {

    @POST("cashier/transaction")
    suspend fun postTransaction(
        @Body request: TransactionRequest
    ): Response<TransactionResponse>
}