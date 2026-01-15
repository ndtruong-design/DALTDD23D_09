package com.example.appbandienthoai.data.api


import com.example.appbandienthoai.data.model.AddCartRequest
import com.example.appbandienthoai.data.model.AddCartResponse
import com.example.appbandienthoai.data.model.AdminOrdersResponse
import com.example.appbandienthoai.data.model.Advertise
import com.example.appbandienthoai.data.model.CartItem
import com.example.appbandienthoai.data.model.ChangePasswordRequest
import com.example.appbandienthoai.data.model.ChangePasswordResponse
import com.example.appbandienthoai.data.model.CheckoutRequest
import com.example.appbandienthoai.data.model.ColorPhone
import com.example.appbandienthoai.data.model.DeleteOrderRequest
import com.example.appbandienthoai.data.model.FilterResponse
import com.example.appbandienthoai.data.model.ForgotPasswordRequest
import com.example.appbandienthoai.data.model.ForgotPasswordResponse
import com.example.appbandienthoai.data.model.ImageDetail
import com.example.appbandienthoai.data.model.LoginRequest
import com.example.appbandienthoai.data.model.LoginResponse
import com.example.appbandienthoai.data.model.OrderDetailResponse
import com.example.appbandienthoai.data.model.OrderHistoryResponse
import com.example.appbandienthoai.data.model.PlaceOrderRequest
import com.example.appbandienthoai.data.model.PlaceOrderResponse
import com.example.appbandienthoai.data.model.Product
import com.example.appbandienthoai.data.model.ProductDetail
import com.example.appbandienthoai.data.model.ProfileResponse
import com.example.appbandienthoai.data.model.PromoResponse
import com.example.appbandienthoai.data.model.RegisterRequest
import com.example.appbandienthoai.data.model.RegisterResponse
import com.example.appbandienthoai.data.model.RemoveCartRequest
import com.example.appbandienthoai.data.model.UpdateProfileRequest
import com.example.appbandienthoai.data.model.UpdateProfileResponse
import com.example.appbandienthoai.data.model.UpdateQuantityRequest
import com.example.appbandienthoai.data.model.UpdateStatusRequest
import com.example.appbandienthoai.data.model.UpdateStatusResponse
import com.example.appbandienthoai.data.model.cartResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query




interface ApiService {
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/forgotpassword.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse

    @GET("auth/get_ads.php")
    suspend fun getAds(): List<Advertise>

    @GET("auth/get_products.php")
    suspend fun getProduct(): List<Product>

    @GET("auth/filter.php")
    suspend fun filterProducts(
        @Query("min") min: Int?,
        @Query("max") max: Int?,
        @Query("hang") hang: String?
    ): FilterResponse


    @GET("auth/card/get.php")
    suspend fun getCard(
        @Query("MaKhachHang") MaKhachHang: Int
    ): cartResponse<List<CartItem>>

    @GET("auth/get_product_detail.php")
    suspend fun getProductDetail(
        @Query("MaSanPham") maSanPham: Int,
        @Query("BoNho") boNho: String,
        @Query("MaMau") maMau: String? = null
    ): ProductDetail

    @GET("auth/get_image_detail.php")
    suspend fun getImageDetail(
        @Query("MaSanPham") maSanPham: Int,
        @Query("MaMau") maMau: String
    ): List<ImageDetail>

    @GET("auth/get_color.php")
    suspend fun getColor(
        @Query("MaSanPham") MaSanPham: Int,
        @Query("BoNho") BoNho: String
    ): List<ColorPhone>

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

    @GET("auth/get_profile.php")
    suspend fun getProfile(
        @Query("MaKhachHang") userId: Int
    ): ProfileResponse

    @POST("auth/update_profile.php")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): UpdateProfileResponse

    @POST("auth/change_password.php")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): ChangePasswordResponse

    @POST("auth/card/update.php")

    suspend fun updateQuantity(
        @Body body: UpdateQuantityRequest
    ): cartResponse<Unit>


    @POST("auth/card/remove.php")
    suspend fun removeItem(
        @Body body: RemoveCartRequest
    ): cartResponse<Unit>


    @POST("auth/card/check.php")
    suspend fun check(
        @Body body: CheckoutRequest
    ): cartResponse<Unit>


    @GET("auth/get_khuyen_mai.php")
    suspend fun checkPromoCode(
        @Query("MaKhuyenMai") MaKhuyenMai: String?,
        @Query("total") total: Long
    ): PromoResponse

    @POST("auth/place_order.php")
    suspend fun placeOrder(
        @Body request: PlaceOrderRequest
    ): PlaceOrderResponse

    @POST("auth/delete_order.php")
    suspend fun deleteOrder(
        @Body request: DeleteOrderRequest
    ): cartResponse<Unit>

    @GET("auth/get_user_info.php")
    suspend fun getUserInfo(
        @Query("user_id") userId: Int
    ): cartResponse<PlaceOrderRequest>


    @GET("auth/get_order_history.php")
    suspend fun getOrderHistory(
        @Query("MaKhachHang") userId: Int
    ): OrderHistoryResponse


    @POST("auth/add_to_cart.php")
    suspend fun addCart(
        @Body request: AddCartRequest
    ): AddCartResponse


    @GET("auth/search_products.php")
    suspend fun searchProducts(
        @Query("query") query: String
    ): FilterResponse
}