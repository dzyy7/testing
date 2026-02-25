package id.co.psplauncher.data.network.balance

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import id.co.psplauncher.data.network.response.BalanceResponse
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    private val api: BalanceApi,
    private val userPreferences: UserPreferences
) : BaseRepository() {
    suspend fun fetchBalance(currentDate: String) = safeApiCall(
        { api.getBalance(currentDate) },
        userPreferences
    )
}
