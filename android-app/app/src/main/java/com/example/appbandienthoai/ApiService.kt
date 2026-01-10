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
data class cartResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

data class OrderHistoryResponse(
    val success: Boolean,
    val data: List<Order>
)

<<<<<<< HEAD
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
=======
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

>>>>>>> 39ac91798287cfd6852db976bb681d41760efec8

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
    suspend fun getProductDetail(@Query("MaSanPham") id: Int):List<ProductDetail>
    @GET("cart/get.php")
    suspend fun getCart(
        @Query("user_id") userId: Int
    ): cartResponse<List<CartItem>>


<<<<<<< HEAD
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
=======
    @GET("get_order_history.php")
    suspend fun getOrderHistory(@Query("user_id") userId: Int): OrderHistoryResponse
>>>>>>> 39ac91798287cfd6852db976bb681d41760efec8
}
