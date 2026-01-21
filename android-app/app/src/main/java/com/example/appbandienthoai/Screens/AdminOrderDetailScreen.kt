package com.example.appbandienthoai.Screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var orderDetail by remember { mutableStateOf<OrderDetailData?>(null) }
    var orderItems by remember { mutableStateOf<List<OrderItemData>>(emptyList()) }

    var isInitialLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(0) }

    fun loadData() {
        scope.launch {
            if (orderDetail == null) isInitialLoading = true
            errorMessage = null

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
                errorMessage = "Lỗi kết nối: ${e.localizedMessage ?: "Không xác định"}"
            } finally {
                isInitialLoading = false
            }
        }
    }

    LaunchedEffect(orderId) {
        loadData()
    }

    fun updateStatus() {
        val currentStatus = orderDetail?.TrangThai ?: return

        scope.launch {
            isUpdating = true
            try {
                val response = api.updateOrderStatus(
                    UpdateStatusRequest(orderId, selectedStatus)
                )

                if (response.success) {
                    Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    loadData()
                    showStatusDialog = false
                } else {
                    val msg = response.message ?: "Cập nhật thất bại"
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isUpdating = false
            }
        }
    }

    if (showStatusDialog && orderDetail != null) {
        val currentStatus = orderDetail!!.TrangThai

        LaunchedEffect(showStatusDialog) {
            selectedStatus = currentStatus
        }

        AlertDialog(
            onDismissRequest = { if (!isUpdating) showStatusDialog = false },
            title = { Text("Cập nhật trạng thái") },
            text = {
                Column {
                    listOf(
                        0 to "Chờ duyệt",
                        1 to "Đang giao",
                        2 to "Đã giao",
                        3 to "Đã hủy"
                    ).forEach { (value, label) ->
                        val disabled = currentStatus == 2 ||
                                currentStatus == 3 ||
                                (value < currentStatus && value != 3) ||
                                value == currentStatus

                        RadioOption(
                            text = label,
                            selected = selectedStatus == value,
                            enabled = !disabled,
                            onClick = { if (!disabled) selectedStatus = value }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { updateStatus() },
                    enabled = !isUpdating && selectedStatus != currentStatus,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
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
                        Text("Chi tiết đơn hàng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
            isInitialLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage!!, color = Color.Red)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { loadData() }) { Text("Thử lại") }
                    }
                }
            }

            orderDetail != null -> {
                val detail = orderDetail!!
                val isFinalStatus = detail.TrangThai == 2 || detail.TrangThai == 3

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Box(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                            StatusChip(status = detail.TrangThai)
                        }
                    }

                    item {
                        CardSection(title = "Thông tin khách hàng") {
                            InfoRow("Họ tên", detail.KhachHang.HoTen)
                            InfoRow("SĐT", detail.KhachHang.SoDienThoai)
                            detail.DiaChiGiaoHang?.let { InfoRow("Địa chỉ", it) }
                        }
                    }

                    item {
                        Text(
                            "Sản phẩm (${orderItems.size})",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
                        CardSection(title = null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tổng thanh toán:", fontWeight = FontWeight.Bold)
                                Text(
                                    "%,d đ".format(detail.TongTien.toLong()),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFFE91E63)
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { showStatusDialog = true },
                            enabled = !isFinalStatus && !isUpdating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFinalStatus) Color.Gray else Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isFinalStatus) "ĐƠN HÀNG ĐÃ KẾT THÚC" else "CẬP NHẬT TRẠNG THÁI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}
@Composable
fun CardSection(title: String?, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            if (title != null) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
            }
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProductItemRow(item: OrderItemData) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        AsyncImage(
            model = item.DuongLinkAnh,
            contentDescription = null,
            modifier = Modifier.size(60.dp).padding(end = 12.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(item.TenSanPham, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${item.BoNho} - ${item.TenMau}", color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("x${item.SoLuong}", fontSize = 14.sp)
                Text("%,.0f đ".format(item.DonGia), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RadioOption(
    text: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF2196F3),
                unselectedColor = if (enabled) Color.Gray else Color.LightGray,
                disabledSelectedColor = Color.Gray,
                disabledUnselectedColor = Color.LightGray
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = if (enabled) Color.Black else Color.Gray
        )
    }
}

