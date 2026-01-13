package com.example.appbandienthoai


import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.collections.listOf

data class BottomNavItem (
    val title: String,
    val icon: ImageVector,
    val route: String
)
val bottomNavItems = listOf(
    BottomNavItem("Trang chủ", Icons.Default.Home,"home"),
    BottomNavItem("Giỏ hàng", Icons.Default.ShoppingCart,"cart"),
    BottomNavItem("Đơn hàng", Icons.Default.LocalShipping,"don_hang"),
    BottomNavItem("Tài khoản", Icons.Default.Person,"profile")
)
