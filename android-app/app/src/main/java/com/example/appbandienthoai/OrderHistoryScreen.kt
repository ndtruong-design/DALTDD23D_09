package com.example.appbandienthoai

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavHostController) {
    var orderList by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // TODO: Trong thực tế, bạn cần lấy userId từ Token hoặc SharedPreferences
    // Ở đây mình giả định userId = 1 để test api
    val currentUserId = 1

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getOrderHistory(currentUserId)
            if (response.success) {
                orderList = response.data
            }
        } catch (e: Exception) {
            Log.e("OrderHistory", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Tái sử dụng NavigationBar để giữ đồng bộ giao diện
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                bottomNavItems.forEach { item ->
                    val selected = item.route == "lich_su"
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (item.route != "lich_su") {
                                navController.navigate(item.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (orderList.isEmpty()) {
                Text(
                    text = "Bạn chưa có đơn hàng nào",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(orderList) { order ->
                        OrderItemCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Header: Mã đơn + Trạng thái
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đơn hàng #${order.MaDonHang}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OrderStatusChip(order.TrangThaiCode, order.TrangThaiText)
            }

            Text(
                text = order.NgayDatHang,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // 2. Danh sách sản phẩm trong đơn
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                order.ChiTiet.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = item.HinhAnh,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.TenSanPham,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Text(
                                text = "${item.MauSac} | ${item.ThongTinPhienBan}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "x${item.SoLuong}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = item.GiaHienThi,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // 3. Tổng tiền
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Thanh toán: ${order.TrangThaiThanhToan}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if(order.TrangThaiThanhToan.contains("Đã thanh toán")) Color(0xFF4CAF50) else Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Tổng tiền", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = order.TongTienHienThi,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OrderStatusChip(statusCode: Int, statusText: String) {
    val (bgColor, textColor) = when (statusCode) {
        0 -> Color(0xFFFFF3E0) to Color(0xFFEF6C00) // Chờ duyệt (Cam nhạt)
        1 -> Color(0xFFE3F2FD) to Color(0xFF1976D2) // Đang giao (Xanh dương)
        2 -> Color(0xFFE8F5E9) to Color(0xFF388E3C) // Thành công (Xanh lá)
        3 -> Color(0xFFFFEBEE) to Color(0xFFD32F2F) // Hủy (Đỏ)
        else -> Color.LightGray to Color.Black
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = statusText,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold
        )
    }
}