package com.example.appbandienthoai

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class LoginRequest(
    val TenDangNhap: String,
    val MatKhau: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val message: String?
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
    val MaSanPham: Int,
    val TenSanPham: String,
    val MoTa: String,
    val Hang: String,
    val TrangThai: Int,
    val Gia: Int,
    val DuongLinkAnh: String,
    val BoNho:String
)

data class ProductDetail(
    val MaChiTietSP: Int,
    val MaSanPham: Int,
    val MaMau: Int,
    val SoLuongTon: Int,
    val Gia: Int,
    val BoNho: String,
    val ManHinh: String,
    val KichThuoc: String,
    val CameraSau: String,
    val CameraTruoc: String,
    val Pin: String,
    val HeDieuHanh: String,
    val CPU: String,
    val GPU: String,
    val RAM: String,
    val TenSanPham: String  ,
    val Hang: String,
    val MoTa: String
)

data class ImageDetail(
    val MaAnh: Int,
    val MaChiTietSP: Int,
    val MaMau: Int,
    val DuongLinkAnh: String,
    val laAnhDaiDien: Int
)

data class Advertise(

    val MaHinhAnh: Int,
    val HinhAnh: String
)
data class FilterResponse(
    val success: Boolean,
    val data: List<Product>
)

data class ColorPhone(
    val MaMau: Int,
    val TenMau: String,
    val MaHex: String?
)
data class OrderHistoryResponse(
    val success: Boolean,
    val data: List<Order>
)

data class Order(
    @SerializedName("order_id") val MaDonHang: Int,
    @SerializedName("date_ordered") val NgayDatHang: String,
    @SerializedName("total_price_formatted") val TongTienHienThi: String,
    @SerializedName("status_text") val TrangThaiText: String,
    @SerializedName("status_code") val TrangThaiCode: Int,
    @SerializedName("payment_status_text") val TrangThaiThanhToan: String,
    @SerializedName("items") val ChiTiet: List<OrderItem>
)

data class OrderItem(
    @SerializedName("product_name") val TenSanPham: String,
    @SerializedName("variant_info") val ThongTinPhienBan: String,
    @SerializedName("color") val MauSac: String,
    @SerializedName("quantity") val SoLuong: Int,
    @SerializedName("price_formatted") val GiaHienThi: String,
    @SerializedName("image") val HinhAnh: String
)


interface ApiService {
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("forgotpassword.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @GET("get_ads.php")
    suspend fun getAds(): List<Advertise>

    @GET("get_products.php")
    suspend fun getProduct(): List<Product>

    @GET("filter.php")
    suspend fun filterProducts(
        @Query("min") min: Int?,
        @Query("max") max: Int?,
        @Query("hang") hang: String?
    ): FilterResponse

    @GET("get_product_detail.php")
    suspend fun getProductDetail(
        @Query("MaSanPham") maSanPham: Int,
        @Query("BoNho") boNho: String,
        @Query("MaMau") maMau: String? = null
    ): ProductDetail

    @GET("get_image_detail.php")
    suspend fun getImageDetail(
        @Query("MaSanPham") maSanPham: Int,
        @Query("MaMau") maMau: String
    ): List<ImageDetail>

    @GET("get_color.php")
    suspend fun getColor(
        @Query("MaSanPham") MaSanPham: Int,
        @Query("BoNho")BoNho: String
    ): List<ColorPhone>


    @GET("get_order_history.php")
    suspend fun getOrderHistory(@Query("user_id") userId: Int): OrderHistoryResponse
}
