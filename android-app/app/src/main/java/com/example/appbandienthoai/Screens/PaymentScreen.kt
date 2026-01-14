package com.example.appbandienthoai.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appbandienthoai.components.CartViewModel
import com.example.appbandienthoai.data.api.ApiService
import com.example.appbandienthoai.data.model.CheckoutItem
import com.example.appbandienthoai.data.model.PlaceOrderRequest
import com.example.appbandienthoai.data.model.PlaceOrderResponse
import com.example.appbandienthoai.utils.getUserId
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(totalAmount: Long,
                  api: ApiService,
                  navController: NavHostController,
                  cartViewModel: CartViewModel

) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("COD") }

    val coroutineScope = rememberCoroutineScope()
    var couponCode by remember { mutableStateOf("") }
    var discountRate by remember { mutableStateOf(0f) }
    var isCouponApplied by remember { mutableStateOf(false) }
    var promoMessage by remember { mutableStateOf("") }

    val shippingFee = 30000L
    val discountAmount = (totalAmount * (discountRate / 100)).toLong()
    val total = (totalAmount + shippingFee - discountAmount)

    val context = LocalContext.current
    val cartItems by cartViewModel.items.collectAsState()
    val userId by produceState(initialValue = -1) {
        value = getUserId(context)
    }
    LaunchedEffect(userId) {
        if (userId != -1) {
            try {
                val response = api.getUserInfo(userId)
                if (response.success && response.data != null) {

                    name = response.data.HoTen ?: ""
                    phone = response.data.SoDienThoai ?: ""
                    address = response.data.DiaChi ?: ""
                }
            } catch (e: Exception) {

            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Thanh toán")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {
        item {
            Text("Địa chỉ giao hàng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ tên") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Địa chỉ") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Text("Khuyến mãi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = couponCode,
                    onValueChange = {
                        couponCode = it
                        promoMessage = ""
                    },
                    label = { Text("Nhập mã giảm giá") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isCouponApplied
                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val response = api.checkPromoCode(couponCode, totalAmount)
                                if (response.success) {
                                    discountRate = response.TiLeGiam ?: 0f
                                    isCouponApplied = true
                                    promoMessage = response.message ?: "Áp dụng thành công"
                                } else {
                                    isCouponApplied = false
                                    promoMessage = response.message ?: "Mã giảm giá không hợp lệ"
                                }
                            } catch (e: Exception) {
                                promoMessage = "Lỗi kết nối: ${e.localizedMessage}"
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp).height(56.dp),
                    enabled = couponCode.isNotBlank() && !isCouponApplied,
                ) {
                    Text(if (isCouponApplied) "ĐÃ ÁP DỤNG" else "ÁP DỤNG")
                }
            }


            if (promoMessage.isNotBlank()) {
                Text(
                    text = promoMessage,
                    color = if (isCouponApplied) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 13.sp
                )
            }

            if (isCouponApplied) {
                Text("Đã giảm $discountRate% đơn hàng", color = Color(0xFF4CAF50), fontSize = 12.sp)
            }
        }
        item {
            Text("Phương thức thanh toán", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            PaymentOption("COD", "Thanh toán khi nhận hàng", paymentMethod) {
                paymentMethod = it
            }
            PaymentOption("BANK", "Chuyển khoản ngân hàng", paymentMethod) {
                paymentMethod = it
            }
            PaymentOption("WALLET", "Ví điện tử", paymentMethod) {
                paymentMethod = it
            }
        }

        item {
            Text("Tóm tắt đơn hàng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            SummaryRow("Tạm tính", "${formatPrice(totalAmount)}đ")
            SummaryRow("Phí vận chuyển","+${formatPrice(shippingFee)}đ" )
            if (discountAmount > 0) {
                SummaryRow("Giảm giá ($discountRate%)", "-${formatPrice(discountAmount)}đ")
            }
            Divider()
            SummaryRow("Tổng cộng", "${formatPrice(total)}đ", true)
        }

        item {
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val selectedItems = cartItems
                                .filter { it.isChecked }
                                .map { CheckoutItem(it.item.MaChiTietSP, it.item.SoLuong) }
                            Log.d("PAYMENT_DEBUG", "UserId: $userId, Items count: ${selectedItems.size}, Total: $total")
                            val request = PlaceOrderRequest(
                                MaKhachHang = userId,
                                HoTen = name,
                                SoDienThoai = phone,
                                DiaChi = address,
                                MaPTTT = paymentMethod,
                                MaKhuyenMai = if (isCouponApplied) couponCode else null,
                                TongTien = total,
                                items = selectedItems
                            )

                            val response: PlaceOrderResponse = api.placeOrder(request)

                            if (response.success) {

                                Toast.makeText(context, "Đặt hàng thành công! Mã đơn: ${response.MaDonHang}", Toast.LENGTH_LONG).show()
                                navController.navigate("home") {
                                    popUpTo("cart") { inclusive = true }
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("PAYMENT_DEBUG", "Full Error: ", e)
                            Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text("THANH TOÁN")
            }
        }
    }
}
}

fun formatPrice(amount: Long): String {
    return String.format("%,d", amount).replace(',', '.')
}
@Composable
fun PaymentOption(
    value: String,
    label: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = value == selected,
            onClick = { onSelect(value) }
        )
        Text(label)
    }
}

@Composable
fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            value,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
