package com.example.appbandienthoai.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val TenDangNhap: String,
    val MatKhau: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val accountType:String?,
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



data class AdminOrdersResponse(
    val success: Boolean,
    val orders: List<OrderAdmin>,
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
data class AddCartResponse(
    val success: Boolean,
    val message: String? = null,

    )
data class AddCartRequest(
    val MaKhachHang: Int,
    val MaSanPham: Int,
    val MaMau: Int,
    val BoNho:String

)

data class AddFavoriteRequest(
    val MaKhachHang: Int,
    val MaSanPham: Int
)

data class AddFavoriteReponse(
    val success: Boolean,
    val message: String?=null
)

data class FavoriteItem(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("price") val price: Double,
    @SerializedName("price_formatted") val priceFormatted: String,
    @SerializedName("image") val image: String,
    @SerializedName("added_date") val addedDate: String
)

data class WishlistResponse(
    val success: Boolean,
    val data: List<FavoriteItem>? = null,
    val message: String?=null

)

data class CartItem(
    val MaChiTietSP: Int,
    val MaSanPham: Int,
    val TenSanPham: String,
    val Gia: Int,
    val SoLuong: Int,
    val SoLuongTon:Int,
    val TenMau: String,
    val BoNho: String,
    val HinhAnh: String
)
data class CartItemUI(
    val item: CartItem,
    val isChecked: Boolean = false
)




data class UpdateQuantityRequest(
    val MaKhachHang: Int,
    val MaChiTietSP: Int,
    val SoLuong: Int
)

data class DeleteOrderRequest(
    val MaKhachHang: Int,
    val MaDonHang: Int
)
data class RemoveCartRequest(
    val MaKhachHang: Int,
    val MaChiTietSP: Int
)

data class CheckoutRequest(
    val MaKhachHang: Int,
    val items: List<CheckoutItem>
)


data class CheckoutItem(
    val MaChiTietSP: Int,
    val SoLuong: Int
)
data class PromoResponse(
    val success: Boolean,
    val message: String?,
    val TiLeGiam: Float?
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
    val message: String?,
    val MaDonHang: Int?,
    val TongTienGoc: Long?,
    val GiamGia: Long?,
    val TongThanhToan: Long?
)
data class OrderAdmin(
    val MaDonHang: Int,
    val NgayDatHang: String,
    val TongTien: Double,
    val TrangThai: Int,
    val TrangThaiText: String,
    val HoTen: String,
    val SoDienThoai: String
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
data class ChangePasswordRequest(
    val MaKhachHang: Int,
    val MatKhauCu: String,
    val MatKhauMoi: String
)
data class ChangePasswordResponse(
    val success: Boolean,
    val message: String
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