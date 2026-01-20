package com.example.appbandienthoai.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun PaymentScreen(
    totalAmount: Long,
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
    val finalTotal = (totalAmount + shippingFee - discountAmount).coerceAtLeast(0) // Không để âm

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
                Log.e("PaymentScreen", "Lỗi lấy thông tin user: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },

        bottomBar = {
            ContainerBottomBar(
                total = finalTotal,
                isValid = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                onCheckout = {
                    coroutineScope.launch {
                        try {

                            val selectedItems = cartItems
                                .filter { it.isChecked }
                                .map { CheckoutItem(it.item.MaChiTietSP, it.item.SoLuong) }

                            if (selectedItems.isEmpty()) {
                                Toast.makeText(context, "Không có sản phẩm nào được chọn!", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            val request = PlaceOrderRequest(
                                MaKhachHang = userId,
                                HoTen = name,
                                SoDienThoai = phone,
                                DiaChi = address,
                                MaPTTT = paymentMethod,
                                MaKhuyenMai = if (isCouponApplied) couponCode else null,
                                TongTien = finalTotal,
                                items = selectedItems
                            )

                            val response: PlaceOrderResponse = api.placeOrder(request)

                            if (response.success) {
                                Toast.makeText(context, "Đặt hàng thành công! Mã: ${response.MaDonHang}", Toast.LENGTH_LONG).show()
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Lỗi: ${response.message}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("PAYMENT_DEBUG", "Error: ", e)
                            Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
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
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Họ tên") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    label = { Text("Số điện thoại") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address, onValueChange = { address = it },
                    label = { Text("Địa chỉ") }, modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text("Khuyến mãi", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = {

                            if (!isCouponApplied) {
                                couponCode = it
                                promoMessage = ""
                            }
                        },
                        label = { Text("Mã giảm giá") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        readOnly = isCouponApplied,
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            disabledTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (isCouponApplied) {

                                isCouponApplied = false
                                discountRate = 0f
                                couponCode = ""
                                promoMessage = ""
                            } else {

                                if (couponCode.isBlank()) {
                                    promoMessage = "Vui lòng nhập mã"
                                    return@Button
                                }
                                coroutineScope.launch {
                                    try {
                                        val response = api.checkPromoCode(couponCode, totalAmount)
                                        if (response.success) {
                                            discountRate = response.TiLeGiam ?: 0f
                                            isCouponApplied = true
                                            promoMessage = response.message ?: "Thành công"
                                        } else {
                                            isCouponApplied = false
                                            discountRate = 0f
                                            promoMessage = response.message ?: "Mã lỗi"
                                        }
                                    } catch (e: Exception) {
                                        promoMessage = "Lỗi mạng"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.height(56.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCouponApplied) Color(0xFFD32F2F) else Color(0xFF673AB7)
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(text = if (isCouponApplied) "XÓA" else "ÁP DỤNG")
                    }
                }


                if (promoMessage.isNotBlank()) {
                    Text(
                        text = promoMessage,
                        color = if (isCouponApplied) Color(0xFF4CAF50) else Color.Red,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                        fontSize = 13.sp
                    )
                }
            }

            item {
                Text("Phương thức thanh toán", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                PaymentOption("COD", "Thanh toán khi nhận hàng", paymentMethod) { paymentMethod = it }
                PaymentOption("BANK", "Chuyển khoản ngân hàng", paymentMethod) { paymentMethod = it }
                PaymentOption("WALLET", "Ví điện tử", paymentMethod) { paymentMethod = it }
            }


            item {
                Text("Tóm tắt đơn hàng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SummaryRow("Tạm tính", "${formatPrice(totalAmount)}đ")
                        SummaryRow("Phí vận chuyển", "+${formatPrice(shippingFee)}đ")
                        if (isCouponApplied && discountAmount > 0) {
                            SummaryRow(
                                "Giảm giá ($discountRate%)",
                                "-${formatPrice(discountAmount)}đ",
                                color = Color(0xFF4CAF50)
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        SummaryRow("Tổng thanh toán", "${formatPrice(finalTotal)}đ", true, fontSize = 20)
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}


@Composable
fun ContainerBottomBar(total: Long, isValid: Boolean, onCheckout: () -> Unit) {
    Surface(
        shadowElevation = 10.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tổng thanh toán", fontSize = 14.sp, color = Color.Gray)
                    Text("${formatPrice(total)}đ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
                }

                Button(
                    onClick = onCheckout,
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("THANH TOÁN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, bold: Boolean = false, color: Color = Color.Black, fontSize: Int = 14) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = fontSize.sp)
        Text(
            value,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = color,
            fontSize = fontSize.sp
        )
    }
}


fun formatPrice(amount: Long): String {
    return String.format("%,d", amount).replace(',', '.')
}


@Composable
fun PaymentOption(value: String, label: String, selected: String, onSelect: (String) -> Unit) {
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
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}