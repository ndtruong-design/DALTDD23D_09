package com.example.appbandienthoai

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onCheckout: () -> Unit
) {
    val cartItems by cartViewModel.items.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Giỏ hàng",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        Spacer(Modifier.height(16.dp))

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
                        item = item,
                        onIncrease = { cartViewModel.increase(item.MaSanPham) },
                        onDecrease = { cartViewModel.decrease(item.MaSanPham) },
                        onRemove = { cartViewModel.remove(item.MaSanPham) }
                    )

                }
            }
            Spacer(Modifier.height(12.dp))

            CartSummary(
                total = cartViewModel.totalPrice(),
                onCheckout = onCheckout
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
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

        AsyncImage(model = item.HinhAnh, contentDescription = item.TenSanPham, modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Fit)

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.TenSanPham, fontWeight = FontWeight.Bold, maxLines = 2)
            Text("Màu: ${item.TenMau} • ${item.BoNho}", fontSize = 12.sp)
            Text("${item.Gia}đ", color = Color(0xFF6A1B9A))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = onDecrease) {
                    Icon(Icons.Default.Remove, contentDescription = "Giảm")
                }

                Text(
                    text = item.SoLuong.toString(),
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onIncrease) {
                    Icon(Icons.Default.Add, contentDescription = "Tăng")
                }
            }

            Text(
                text = "Xóa",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onRemove() }
            )
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
            Text("Tổng cộng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                "$total đ",
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
                .height(48.dp)
        ) {
            Text("THANH TOÁN")
        }

    }
}
