package com.example.appbandienthoai.Screens

import android.widget.Toast
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
) {
    val context = LocalContext.current
    val total by cartViewModel.total.collectAsState()
    val cartItems by cartViewModel.items.collectAsState()
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
                                    onRemove   = { cartViewModel.remove(item.item.MaChiTietSP) }

                        )

                    }
                }
                Spacer(Modifier.height(12.dp))

                CartSummary(
                    total = total,
                    onCheckout = {
                        cartViewModel.processCheckout(
                            onSuccess = {

                                Toast.makeText(context, "Đã chọn hàng cần mua", Toast.LENGTH_SHORT).show()
                                navController.navigate("payment/$total")
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
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = { onCheckedChange() }
        )
        AsyncImage(model = item.item.HinhAnh, contentDescription = item.item.TenSanPham, modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Fit)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.item.TenSanPham, fontWeight = FontWeight.Bold, maxLines = 2)
            Text("Màu: ${item.item.TenMau} • ${item.item.BoNho}", fontSize = 12.sp)
            Text("%,d đ".format(item.item.Gia).replace(',', '.'), color = Color(0xFF6A1B9A))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = onDecrease) {
                    Icon(Icons.Default.Remove, contentDescription = "Giảm")
                }

                Text(
                    text = item.item.SoLuong.toString(),
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onIncrease) {
                    Icon(Icons.Default.Add, contentDescription = "Tăng")
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = Color.Red
                )
            }
        }
    }
}
@Composable
fun CartSummary(
    total: Int,
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
            onClick = onCheckout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonColors(Color(0xFF6A1B9A), contentColor = Color.White, disabledContentColor = Color.Gray, disabledContainerColor = Color.Gray)
        ) {
            Text("THANH TOÁN")
        }

    }
}
