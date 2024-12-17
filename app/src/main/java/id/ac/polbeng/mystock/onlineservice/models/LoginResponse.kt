package id.ac.polbeng.mystock.onlineservice.models

data class LoginResponse (
    val message: String,
    val error: Boolean,
    val data: User
)