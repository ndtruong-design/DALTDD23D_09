package com.example.appbandienthoai.Screens
import android.util.Log

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.appbandienthoai.components.bottomNavItems
import com.example.appbandienthoai.data.api.RetrofitClient
import com.example.appbandienthoai.data.model.Advertise
import com.example.appbandienthoai.data.model.Product


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {

    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var adsList by remember { mutableStateOf<List<Advertise>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val brands = productList.map { it.Hang }.distinct()

    LaunchedEffect(Unit) {
        try {
            adsList = RetrofitClient.api.getAds()
            productList = RetrofitClient.api.getProduct()
        } catch (e: Exception) {
            Log.e("API_ERROR", e.message ?: "")
        } finally {
            isLoading = false
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
                        Text("T3Q MOBILE STORE", fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            navController.navigate("yeu_thich")
                        }) {
                            Icon(Icons.Default.Favorite, contentDescription = null,tint=Color.Red)
                        }
                    }
                }
            )

        },
        bottomBar = {
            NavigationBar (
                containerColor = MaterialTheme.colorScheme.surface,
            ){
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex== index,
                        onClick = {
                            selectedItemIndex = index
                            navController.navigate(item.route) {
                                popUpTo("home") {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = {
                            Text(text=item.title)
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint=Color(0xFF6A1B9A)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            // 🔍 Search + Filter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        modifier = Modifier.weight(1f),
                        value = searchQuery,
                        onValueChange = {searchQuery=it},
                        placeholder = { Text("Tìm kiếm sản phẩm") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        shape = CircleShape,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    IconButton(onClick = {
                        navController.navigate("filter/${brands.joinToString(",")}")
                    }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = null)
                    }
                }
            }

            // 📢 Banner quảng cáo
            item {
                if (adsList.isNotEmpty()) {
                    val pagerState =
                        rememberPagerState { adsList.size }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            pageSpacing = 12.dp
                        ) { page ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                AsyncImage(
                                    model = adsList[page].HinhAnh,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.Center) {
                            repeat(adsList.size) {
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .size(if (pagerState.currentPage == it) 10.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (pagerState.currentPage == it)
                                                Color.Red else Color.LightGray
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // 📦 Danh sách sản phẩm (2 cột)
            val filteredList = productList.filter {
                it.TenSanPham.contains(searchQuery, ignoreCase = true) ||
                        it.Hang.contains(searchQuery, ignoreCase = true)
            }

            val rows = filteredList.chunked(2)

            if (filteredList.isEmpty() && !isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Không tìm thấy sản phẩm nào", color = Color.Gray)
                    }
                }
            } else {
                items(rows) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { product ->
                            ProductItem(
                                product,
                                Modifier.weight(1f),
                                onClick = {
                                    navController.navigate("detail/${product.MaSanPham}/${product.BoNho}")
                                }
                            )
                        }
                        // Nếu hàng chỉ có 1 sản phẩm, thêm Spacer để căn chỉnh
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }


            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, modifier: Modifier = Modifier, onClick:()-> Unit) {
    Card(
        modifier = modifier.clickable {onClick()},
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            AsyncImage(
                model = product.DuongLinkAnh,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )

            Column(Modifier.padding(8.dp)) {
                Text(
                    text = "${product.TenSanPham} ${product.BoNho}",
                    fontWeight = FontWeight.Bold
                )
                Text(product.Hang, fontSize = 12.sp, color = Color.Gray)
                Text("Giá: %,d đ".format(product.Gia).replace(',', '.'), color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)

            }
        }
    }
}
