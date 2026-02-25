package id.co.psplauncher.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.local.UserPreferences
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.auth.AuthRepository
import id.co.psplauncher.data.network.response.LoginResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    object NotCashier : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>> = _loginResponse

    fun login(username: String, password: String, device: String, address: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val response = authRepository.login(username, password, device, address)
            _loginResponse.value = response

            when (response) {
                is Resource.Success -> {
                    userPreferences.saveAccessToken(response.value.token)
                    checkAuthAndRole()
                }
                is Resource.Failure -> {
                    _loginState.value = LoginState.Error("Login gagal")
                }
                is Resource.Loading -> {
                    _loginState.value = LoginState.Loading
                }
            }
        }
    }

    private fun checkAuthAndRole() {
        viewModelScope.launch {
            when (val authResponse = authRepository.authCheck()) {
                is Resource.Success -> {
                    val data = authResponse.value
                    if (data.roleType == "CASHIER") {
                        saveUserData(data)
                        userPreferences.saveAccessToken(data.token)
                        _loginState.value = LoginState.Success
                    } else {
                        clearToken()
                        _loginState.value = LoginState.NotCashier
                    }
                }
                is Resource.Failure -> {
                    clearToken()
                    _loginState.value = LoginState.Error("Gagal verifikasi user")
                }
                is Resource.Loading -> {
                    _loginState.value = LoginState.Loading
                }
            }
        }
    }

    private suspend fun saveUserData(data: id.co.psplauncher.data.network.response.AuthCheckResponse) {
        userPreferences.saveUserName(data.name)
        userPreferences.saveMerchantName(data.merchantName)
        userPreferences.saveRoleType(data.roleType)
    }

    fun clearToken() {
        viewModelScope.launch {
            userPreferences.clearToken()
        }
    }

    fun printToken() {
        val token = userPreferences.getAccessToken()
        Log.d("TOKEN", token)
    }
}
