package id.co.psplauncher.data.network.auth

import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.BaseRepository
import id.co.psplauncher.data.network.model.ModelLogin
import id.co.psplauncher.data.network.request.LoginRequest
import id.co.psplauncher.data.network.response.AuthCheckResponse
import javax.inject.Inject

class AuthRepository @Inject constructor (
    private val api: AuthApi,
    private val userPreferences: UserPreferences
): BaseRepository(){
    suspend fun login(
        username: String,
        password: String,
        device: String,
        address: String
    ) = safeApiCall({
        api.login(LoginRequest(username, password, device, address))
    },
        userPreferences
    )

    suspend fun authCheck() = safeApiCall<AuthCheckResponse>(
        { api.authCheck() },
        userPreferences
    )
}