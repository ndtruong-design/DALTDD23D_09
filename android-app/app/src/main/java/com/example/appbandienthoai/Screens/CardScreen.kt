package com.example.appbandienthoai.Screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.appbandienthoai.components.CartViewModel
import com.example.appbandienthoai.data.model.CartItemUI
import com.example.appbandienthoai.utils.getUserId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    navController: NavHostController
) { val cartItems by cartViewModel.items.collectAsState()

    val context = LocalContext.current
    val total by cartViewModel.total.collectAsState()
    val canPay by remember(cartItems) {
        derivedStateOf {
            val selectedItems = cartItems.filter { it.isChecked }
            val isAnyChecked = selectedItems.isNotEmpty()
            val isAllInStock = selectedItems.all { it.item.SoLuong <= it.item.SoLuongTon }
            isAnyChecked && isAllInStock
        }
    }
    LaunchedEffect(Unit) {
        val userId = getUserId(context)
        if (userId != -1) {
            cartViewModel.loadCart(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Giỏ hàng",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        }
                    },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Giỏ hàng trống")
                }
            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item =item,
                            onCheckedChange = {
                                cartViewModel.toggleCheck(item.item.MaChiTietSP)
                            },
                            onIncrease = { cartViewModel.increase(item.item.MaChiTietSP) } ,
                                    onDecrease = { cartViewModel.decrease(item.item.MaChiTietSP) },
                                    onRemove   = { cartViewModel.remove(item.item.MaChiTietSP) },
                            onClickDetail = {
                                navController.navigate(
                                    "detail/${item.item.MaSanPham}/${item.item.BoNho}"
                                )
                            }
                        )

                    }
                }
                Spacer(Modifier.height(12.dp))

                CartSummary(
                    total = total,
                    enabled = canPay,
                    onCheckout = {
                        cartViewModel.processCheckout(
                            onSuccess = {

                                Toast.makeText(context, "Đã chọn hàng cần mua", Toast.LENGTH_SHORT).show()
                                navController.navigate("payment/${total.toLong()}")
                            },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemUI,
    onCheckedChange: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit, onClickDetail: () -> Unit
) {
    val isOutOfStock = item.item.SoLuong > item.item.SoLuongTon

    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.isChecked, onCheckedChange = { onCheckedChange() })


        AsyncImage(
            model = item.item.HinhAnh,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClickDetail() },
            contentScale = ContentScale.Fit, contentDescription = ""
        )

        Spacer(Modifier.width(12.dp))


        Column(modifier = Modifier.weight(1f).clickable {
            onClickDetail()
        },) {
            Text(item.item.TenSanPham, fontWeight = FontWeight.Bold, maxLines = 2)
            Text("Màu: ${item.item.TenMau} • ${item.item.BoNho}", fontSize = 12.sp)
            Text("%,d đ".format(item.item.Gia).replace(',', '.'), color = Color(0xFF6A1B9A))

            if (isOutOfStock) {
                Text(
                    text = "Kho không đủ (Còn: ${item.item.SoLuongTon})",
                    color = Color.Red,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                }

                Text(
                    text = item.item.SoLuong.toString(),
                    fontWeight = FontWeight.Bold,
                    color = if (isOutOfStock) Color.Red else Color.Black
                )

                IconButton(
                    onClick = onIncrease,
                    enabled = item.item.SoLuong < item.item.SoLuongTon // Chặn tăng nếu hết kho
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}
@Composable
fun CartSummary(
    total: Int,
    enabled: Boolean,
    onCheckout: () -> Unit
) {
    Column {
     Divider()

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tổng cộng", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
            Text(
                " %,d đ".format(total).replace(',', '.'),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A)
            )
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onCheckout,enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6A1B9A),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.LightGray
            )
        ) {
            Text("ĐẶT HÀNG")
        }

    }
}
