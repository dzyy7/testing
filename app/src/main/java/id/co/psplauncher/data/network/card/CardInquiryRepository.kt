package id.co.psplauncher.data.network.card

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.response.CardInquiryResponse
import javax.inject.Inject

class CardInquiryRepository @Inject constructor(
    private val api: CardInquiryApi,
    private val userPreferences: UserPreferences
) : BaseRepository() {

    suspend fun inquiryByNfc(nfcId: String): Resource<CardInquiryResponse> {
        return safeApiCall({ api.inquiryByNfc(nfcId) }, userPreferences)
    }
}