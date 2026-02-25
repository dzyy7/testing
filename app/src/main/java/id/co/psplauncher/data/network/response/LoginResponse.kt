package id.co.psplauncher.data.network.response

data class LoginResponse (
    val status: Boolean,
    val message: String,
    val token: String,
    val id: String
)
