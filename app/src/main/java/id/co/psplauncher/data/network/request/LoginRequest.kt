package id.co.psplauncher.data.network.request

data class LoginRequest (
    val username: String,
    val password: String,
    val device: String?,
    val address: String
)