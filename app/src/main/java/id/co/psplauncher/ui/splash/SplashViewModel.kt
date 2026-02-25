package id.co.psplauncher.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.auth.AuthRepository
import id.co.psplauncher.data.network.response.AuthCheckResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object NotCashier : AuthState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun checkAuth() {
        val token = userPreferences.getAccessToken()
        if (token.isNullOrEmpty()) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val response = authRepository.authCheck()) {
                is Resource.Success -> {
                    val data = response.value
                    if (data.roleType == "CASHIER") {
                        saveUserData(data)
                        userPreferences.saveAccessToken(data.token)
                        _authState.value = AuthState.Authenticated
                    } else {
                        userPreferences.clearToken()
                        _authState.value = AuthState.NotCashier
                    }
                }
                is Resource.Failure -> {
                    userPreferences.clearToken()
                    _authState.value = AuthState.Unauthenticated
                }
                is Resource.Loading -> {
                    _authState.value = AuthState.Loading
                }
            }
        }
    }

    private suspend fun saveUserData(data: AuthCheckResponse) {
        userPreferences.saveUserName(data.name)
        userPreferences.saveMerchantName(data.merchantName)
        userPreferences.saveRoleType(data.roleType)
    }
}
