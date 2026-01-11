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
    val BoNho: String
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


data class cartResponse<T>(

    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

data class Order(
    val MaDonHang: Int,
    val NgayDatHang: String,
    val TongTien: Double,
    val TrangThai: Int,
    val TrangThaiText: String,
    val HoTen: String,
    val SoDienThoai: String
)

data class AdminOrdersResponse(
    val success: Boolean,
    val orders: List<Order>,
    val total: Int,
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

data class UserProfile(
    val MaKhachHang: Int,
    val TenDangNhap: String,
    val HoTen: String,
    val SoDienThoai: String,
    val Email: String? = null,
    val NgaySinh: String? = null,
    val DiaChi: String? = null,
    val AnhDaiDien: String? = null,
    val TrangThai: Int = 1
)

data class ProfileResponse(
    val success: Boolean,
    val user: UserProfile? = null,
    val message: String? = null
)

data class CartItem(
    val MaSanPham: Int,
    val TenSanPham: String,
    val Gia: Int,
    val SoLuong: Int = 1,
    val HinhAnh: String,
    val TenMau: String,
    val BoNho: String
)

data class AddCartRequest(
    val MaKhachHang: Int,
    val MaSanPham: Int,
    val TenSanPham: String,
    val Gia: Int,
    val DuongLinkAnh: String,
    val TenMau: String,
    val BoNho: String
)

data class UpdateQuantityRequest(
    val MaKhachHang: Int,
    val MaSanPham: Int,
    val SoLuong: Int
)

data class RemoveCartRequest(
    val MaKhachHang: Int,
    val MaSanPham: Int
)

data class CheckoutRequest(
    val MaKhachHang: Int
)
data class UpdateProfileRequest(
    val MaKhachHang:  Int,
    val HoTen: String?  = null,
    val Email:  String? = null,
    val SoDienThoai: String? = null,
    val NgaySinh: String?  = null,
    val DiaChi: String? = null,
    val AnhDaiDien: String? = null
)

data class UpdateProfileResponse(
    val success: Boolean,
    val message: String,
    val user: UserProfile? = null
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

    @GET("get_profile.php")
    suspend fun getProfile(
        @Query("MaKhachHang") userId: Int
    ): ProfileResponse
    @POST("update_profile.php")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): UpdateProfileResponse
    @GET("cart/get.php")
    suspend fun getCart(
        @Query("user_id") userId: Int
    ): cartResponse<List<CartItem>>

    @POST("cart/add.php")
    suspend fun addToCart(
        @Body body: AddCartRequest
    ): cartResponse<Unit>

    @POST("cart/update.php")
    suspend fun updateQuantity(
        @Body body: UpdateQuantityRequest
    ): cartResponse<Unit>

    @POST("cart/remove.php")
    suspend fun removeItem(
        @Body body: RemoveCartRequest
    ): cartResponse<Unit>

    @POST("cart/checkout.php")
    suspend fun checkout(
        @Body body: CheckoutRequest
    ): cartResponse<Unit>
}