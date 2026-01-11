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
    val RAM: String
)

data class Advertise(
    val MaHinhAnh: Int,
    val HinhAnh: String
)

data class FilterResponse(
    val success: Boolean,
    val data: List<Product>
)

data class Order(
    val MaDonHang: Int,
    val NgayDatHang: String,
    val TongTien: Double,
    val TrangThai: Int,
    val TrangThaiText:String,
    val HoTen: String,
    val SoDienThoai: String
)

data class AdminOrdersResponse(
    val success: Boolean,
    val orders: List<Order>,
    val total :Int,
)

data class OrderDetailData(
    val MaDonHang: Int,
    val NgayDat: String,
    val TongTien: Double,
    val TrangThai: Int,
    val DiaChiGiaoHang: String?,
    val KhachHang: CustomerData
)

data class CustomerData(
    val HoTen: String,
    val SoDienThoai: String
)

data class OrderItemData(
    val TenSanPham: String,
    val SoLuong: Int,
    val DonGia: Double,
    val DuongLinkAnh: String?,
    val BoNho: String,
    val TenMau: String
)

data class OrderDetailResponse(
    val success: Boolean,
    val order: OrderDetailData,
    val items: List<OrderItemData>,
    val message: String? = null
)

data class UpdateStatusRequest(
    val MaDonHang: Int,
    val TrangThai: Int
)

data class UpdateStatusResponse(
    val success: Boolean,
    val message: String
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
    suspend fun getProductDetail(@Query("MaSanPham") id: Int): List<ProductDetail>

    @GET("admin/orders/get_orders.php")
    suspend fun getAdminOrders(
        @Query("status") status: Int? = null
    ): AdminOrdersResponse

    @GET("admin/orders/get_order_detail.php")
    suspend fun getOrderDetail(
        @Query("MaDonHang") orderId: Int
    ): OrderDetailResponse

    @POST("admin/orders/update_status.php")
    suspend fun updateOrderStatus(
        @Body request: UpdateStatusRequest
    ): UpdateStatusResponse
}