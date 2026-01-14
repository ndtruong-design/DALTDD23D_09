package com.example.appbandienthoai.Screens
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appbandienthoai.data.api.ApiService
import com.example.appbandienthoai.data.model.OrderDetailData
import com.example.appbandienthoai.data.model.OrderItemData
import com.example.appbandienthoai.data.model.UpdateStatusRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    orderId: Int,
    api: ApiService,
    onBack: () -> Unit
) {
    var orderDetail by remember { mutableStateOf<OrderDetailData?>(null) }
    var orderItems by remember { mutableStateOf<List<OrderItemData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    fun loadData() {
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = api.getOrderDetail(orderId)
                if (response.success) {
                    orderDetail = response.order
                    orderItems = response.items
                    selectedStatus = response.order.TrangThai
                } else {
                    errorMessage = response.message ?: "Không thể tải đơn hàng"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi: ${e.message}"
                Log.e("OrderDetail", "Error", e)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(orderId) {
        loadData()
    }

    fun updateStatus() {
        scope.launch {
            isLoading = true
            try {
                val response = api.updateOrderStatus(
                    UpdateStatusRequest(orderId, selectedStatus)
                )
                if (response.success) {
                    loadData()
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi: ${e.message}"
            } finally {
                isLoading = false
                showStatusDialog = false
            }
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Chọn trạng thái mới") },
            text = {
                Column {
                    RadioOption(
                        text = "Chờ duyệt",
                        selected = selectedStatus == 0,
                        onClick = { selectedStatus = 0 }
                    )
                    RadioOption(
                        text = "Đang giao",
                        selected = selectedStatus == 1,
                        onClick = { selectedStatus = 1 }
                    )
                    RadioOption(
                        text = "Đã giao",
                        selected = selectedStatus == 2,
                        onClick = { selectedStatus = 2 }
                    )
                    RadioOption(
                        text = "Đã hủy",
                        selected = selectedStatus == 3,
                        onClick = { selectedStatus = 3 }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { updateStatus() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text("Xác nhận")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Order Detail", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("#DH$orderId", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                }
            )
        }
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage.isNotEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage, color = Color.Red)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("Quay lại")
                        }
                    }
                }
            }

            orderDetail != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    item {
                        Box(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                            orderDetail?.let { StatusChip(status = it.TrangThai) }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            orderDetail?.let {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        "Khách hàng",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    InfoRow("Khách hàng", it.KhachHang.HoTen)
                                    InfoRow("SĐT", it.KhachHang.SoDienThoai)
                                    it.DiaChiGiaoHang?.let { diachi ->
                                        InfoRow("Địa chỉ", diachi)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Sản phẩm đã đặt (${orderItems.size})",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                orderItems.forEachIndexed { index, item ->
                                    ProductItemRow(item)
                                    if (index < orderItems.size - 1) {
                                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            orderDetail?.let {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Tổng thanh toán:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        String.format("%,.0f đ", it.TongTien),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFFE91E63)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { showStatusDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("CẬP NHẬT TRẠNG THÁI", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontSize = 14.sp)
    }
}

@Composable
fun ProductItemRow(item: OrderItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = item.DuongLinkAnh,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .padding(end = 12.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.TenSanPham,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                "${item.BoNho} - ${item.TenMau}",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("x${item.SoLuong}", fontSize = 14.sp)
                Text(
                    String.format("%,.0f đ", item.DonGia),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun RadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 16.sp)
    }
}