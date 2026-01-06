package com.example.appbandienthoai

import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
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
data class Product(
    val MaSanPham:Int,
    val TenSanPham:String,
    val MoTa: String,
    val Hang: String,
    val TrangThai: Int
)
data class ProductDetail(
    val MaChiTietSP:Int,
    val MaSanPham: Int,
    val MaMau: Int,
    val SoLuongTon: Int,
    val Gia: Int,
    val BoNho: String,
    val ManHinh: String,
    val KichThuoc:String,
    val CameraSau:String,
    val CameraTruoc:String,
    val Pin: String,
    val HeDieuHanh: String,
    val CPU: String,
    val GPU: String,
    val RAM: String
)
data class Picture(
    val MaAnh: Int,
    val MaSanPham: Int,
    val DuongLinkAnh: String,
    val LaAnhDaiDien: Int
)

data class Advertisement(
    val MaQuangCao: Int,
    val HinhAnh: String,

)

interface ApiService {

    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("forgotpassword.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse
}

