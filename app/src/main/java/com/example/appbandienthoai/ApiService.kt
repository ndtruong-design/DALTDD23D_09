package com.example.appbandienthoai

import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST
data class LoginRequest(
    val TenDangNhap: String,
    val MatKhau: String
)
data class LoginResponse(
    val success: Boolean? = false,
    val token: String? = null,
    val error: String? = null
)

data class RegisterRequest(
    val TenDangNhap: String,
    val SoDienThoai: String,
    val MatKhau: String
)


data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val error: String? = null
)

data class ForgotPasswordRequest(
    val SoDienThoai: String,
    val MatKhau: String
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String,
    val token: String?
)
interface ApiService {

    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
    @POST("forgotpassword.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse
}

