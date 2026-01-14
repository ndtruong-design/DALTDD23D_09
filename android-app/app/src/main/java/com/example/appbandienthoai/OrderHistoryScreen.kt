package com.example.appbandienthoai

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    api: ApiService,
    userId: Int,
    navController: NavController
) {
    var orders by remember { mutableStateOf<List<OrderHistoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Gọi API khi màn hình được tạo
    LaunchedEffect(Unit) {
        try {
            val response = api.getOrderHistory(userId)
            if (response.success) {
                orders = response.data
            }
        } catch (e: Exception) {
            Log.e("ORDER_HISTORY", "Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Logic chia danh sách đơn hàng theo trạng thái
            // 0: Chờ duyệt, 1: Đang giao -> Tab Đơn hàng
            val activeOrders = orders.filter { it.statusCode == 0 || it.statusCode == 1 }
            // 2: Thành công, 3: Hủy -> Tab Lịch sử
            val historyOrders = orders.filter { it.statusCode == 2 || it.statusCode == 3 }

            Column(modifier = Modifier.padding(padding)) {
                OrderTabContent(activeOrders, historyOrders)
            }
        }
    }
}

@Composable
fun OrderTabContent(
    activeOrders: List<OrderHistoryItem>,
    historyOrders: List<OrderHistoryItem>
) {
    val tabTitles = listOf("Đơn hàng", "Lịch sử")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Tab Row ---
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color(0xFF6A1B9A),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Color.White
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // --- Nội dung từng Tab (Pager) ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Màu nền xám nhẹ
        ) { page ->
            val currentList = if (page == 0) activeOrders else historyOrders

            if (currentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (page == 0) "Không có đơn hàng nào đang xử lý" else "Chưa có lịch sử đơn hàng",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(currentList) { order ->
                        OrderItemCard(order)
                    }
                }
            }
        }
    }
}

// --- Giữ nguyên các Component con như cũ ---

@Composable
fun OrderItemCard(order: OrderHistoryItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- Header: Mã đơn + Trạng thái ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đơn hàng #${order.orderId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF6A1B9A)
                )
                StatusChip(statusText = order.statusText, statusCode = order.statusCode)
            }

            Text(
                text = "Ngày đặt: ${order.dateOrdered}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            // Hiển thị ngày dự kiến nếu có
            order.dateExpected?.let {
                Text(
                    text = "Dự kiến giao: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1976D2)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // --- List sản phẩm ---
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                order.items.forEach { product ->
                    ProductRowItem(product)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // --- Thông tin bổ sung: Địa chỉ & PTTT ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Địa chỉ: ${order.address}",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 1
                )
                Text(
                    text = "Thanh toán: ${order.paymentMethod ?: "N/A"}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Footer: Tổng tiền ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = order.paymentStatusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = when(order.paymentStatusText) {
                            "Đã thanh toán" -> Color(0xFF2E7D32)
                            "Chưa thanh toán" -> Color(0xFFE65100)
                            else -> Color.Gray
                        }
                    )
                    Text(text = "${order.items.size} sản phẩm", fontSize = 12.sp, color = Color.Gray)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Tổng thanh toán", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = order.totalPriceFormatted,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProductRowItem(product: OrderHistoryProduct) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = product.image,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.productName,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                fontSize = 14.sp
            )
            Text(
                text = "${product.variantInfo} | ${product.color}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("x${product.quantity}", fontSize = 14.sp)
                Text(product.priceFormatted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun StatusChip(statusText: String, statusCode: Int) {
    val (bgColor, textColor) = when (statusCode) {
        0 -> Color(0xFFFFF3E0) to Color(0xFFEF6C00) // Chờ duyệt: Cam
        1 -> Color(0xFFE3F2FD) to Color(0xFF1565C0) // Đang giao: Xanh dương
        2 -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Thành công: Xanh lá
        3 -> Color(0xFFFFEBEE) to Color(0xFFC62828) // Hủy: Đỏ
        else -> Color.LightGray to Color.Black
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = statusText,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}