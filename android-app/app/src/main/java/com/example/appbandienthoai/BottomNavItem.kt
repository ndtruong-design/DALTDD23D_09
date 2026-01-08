package com.example.appbandienthoai

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem (
    val title: String,
    val icon: ImageVector,
    val route: String
)
val bottomNavItems=listOf(
    BottomNavItem("Trang chủ", Icons.Default.Home,"home"),
    BottomNavItem("Giỏ hàng", Icons.Default.ShoppingCart,"cart"),
    BottomNavItem("Đơn hàng", Icons.Default.DeliveryDining,"donhang"),
    BottomNavItem("Tài khoản", Icons.Default.Person,"profile")
)
