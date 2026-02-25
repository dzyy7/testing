package id.co.psplauncher.data.network.chip

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import javax.inject.Inject

class ChipRepository @Inject constructor (
    private val api: ChipApi,
    private val userPreferences: UserPreferences
): BaseRepository() {
    suspend fun fetchData() = safeApiCall({
        api.fetchChip()
    },
        userPreferences)
}