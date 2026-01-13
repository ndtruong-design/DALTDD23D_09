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
    val MaKhachHang: Int?,
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

<<<<<<< HEAD

=======
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307
data class CartItem(

    val MaChiTietSP: Int,
    val MaSanPham: Int,
    val TenSanPham: String,
    val Gia: Int,
    val SoLuong: Int,
    val TenMau: String,
    val BoNho: String,
    val HinhAnh: String
)
data class CartItemUI(
    val item: CartItem,
    val isChecked: Boolean = true
)




data class UpdateQuantityRequest(
    val MaKhachHang: Int,
    val MaChiTietSP: Int,
    val SoLuong: Int
)

data class RemoveCartRequest(
    val MaKhachHang: Int,
    val MaChiTietSP: Int
)

data class CheckoutRequest(
    val MaKhachHang: Int,
    val items: List<CheckoutItem>
)
<<<<<<< HEAD

data class CheckoutItem(
    val MaChiTietSP: Int,
    val SoLuong: Int
)
data class PromoResponse(
    val success: Boolean,
    val message: String?,
  val tiLeGiam: Float?
)

data class PlaceOrderRequest(
    val MaKhachHang: Int,
    val HoTen: String,
    val SoDienThoai: String,
    val DiaChi: String,
    val MaPTTT: String,
    val MaKhuyenMai: String?,
    val TongTien: Long,
    val items: List<CheckoutItem>
)
data class PlaceOrderResponse(
    val success: Boolean,
    val message: String,
    val MaDonHang: Int?
)

data class Order(
    @SerializedName("order_id") val MaDonHang: Int,
    @SerializedName("date_ordered") val NgayDatHang: String,
    @SerializedName("total_price_formatted") val TongTienHienThi: String,
    @SerializedName("status_text") val TrangThaiText: String,
    @SerializedName("status_code") val TrangThaiCode: Int,
    @SerializedName("payment_status_text") val TrangThaiThanhToan: String,
    @SerializedName("items") val ChiTiet: List<OrderItem>
=======
data class UpdateProfileRequest(
    val MaKhachHang:  Int,
    val HoTen: String?  = null,
    val Email:  String? = null,
    val SoDienThoai: String? = null,
    val NgaySinh: String?  = null,
    val DiaChi: String? = null,
    val AnhDaiDien: String? = null
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307
)

data class UpdateProfileResponse(
    val success: Boolean,
    val message: String,
    val user: UserProfile? = null
)

data class OrderHistoryResponse(
    val success: Boolean,
    val data: List<OrderHistoryItem>
)

data class OrderHistoryItem(
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("date_ordered") val dateOrdered: String,
    @SerializedName("date_expected") val dateExpected: String?,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("total_price_formatted") val totalPriceFormatted: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_text") val statusText: String,
    @SerializedName("payment_status_text") val paymentStatusText: String,
    @SerializedName("payment_method") val paymentMethod: String?,
    @SerializedName("address") val address: String,
    @SerializedName("items") val items: List<OrderHistoryProduct>
)

data class OrderHistoryProduct(
    @SerializedName("product_name") val productName: String,
    @SerializedName("variant_info") val variantInfo: String,
    @SerializedName("color") val color: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("price_formatted") val priceFormatted: String,
    @SerializedName("image") val image: String
)

<<<<<<< HEAD

=======
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307

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

<<<<<<< HEAD

    @GET("card/get.php")
    suspend fun getCard(
        @Query("MaKhachHang") MaKhachHang: Int
    ): cartResponse<List<CartItem>>
    @POST("card/update.php")
=======
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
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307
    suspend fun updateQuantity(
        @Body body: UpdateQuantityRequest
    ): cartResponse<Unit>

<<<<<<< HEAD

    @POST("card/remove.php")
=======
    @POST("cart/remove.php")
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307
    suspend fun removeItem(
        @Body body: RemoveCartRequest
    ): cartResponse<Unit>

<<<<<<< HEAD

    @POST("card/check.php")
=======
    @POST("cart/checkout.php")
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307
    suspend fun checkout(
        @Body body: CheckoutRequest
    ): cartResponse<Unit>

<<<<<<< HEAD
    @GET("get_khuyen_mai.php")
    suspend fun checkPromoCode(
        @Query("code") code: String,
        @Query("total") total: Long
    ):PromoResponse
    @POST("place_order.php")
    suspend fun placeOrder(
        @Body request: PlaceOrderRequest
    ): PlaceOrderResponse
    @GET("get_user_info.php")
    suspend fun getUserInfo(
        @Query("user_id") userId: Int
    ): cartResponse<PlaceOrderRequest>
    @GET("get_order_history.php")
    suspend fun getOrderHistory(@Query("user_id") userId: Int): OrderHistoryResponse

}
=======
    @GET("get_order_history.php")
    suspend fun getOrderHistory(
        @Query("user_id") userId: Int
    ): OrderHistoryResponse
}
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307
