package id.co.psplauncher.data.network.card

import id.co.psplauncher.data.network.response.CardInquiryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface CardInquiryApi {

    @Headers("Content-Type: application/json")
    @GET("cashier/transaction/inquiry/{nfcId}")
    suspend fun inquiryByNfc(
        @Path("nfcId") nfcId: String
    ): Response<CardInquiryResponse>
}