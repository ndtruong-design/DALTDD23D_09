package com.example.appbandienthoai

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {

    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var adsList by remember { mutableStateOf<List<Advertise>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedItemIndex by remember { mutableIntStateOf(0) }

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
                        Text("Mobile Store", fontWeight = FontWeight.Bold)
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Favorite, contentDescription = null)
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
                                contentDescription = item.title
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
                        value = "",
                        onValueChange = {},
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
                        androidx.compose.foundation.pager.rememberPagerState { adsList.size }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        androidx.compose.foundation.pager.HorizontalPager(
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
            val rows = productList.chunked(2)
            items(rows) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    row.forEach {
                        ProductItem(it, Modifier.weight(1f),
                            onClick={
                                navController.navigate("detail/${it.MaSanPham}/${it.BoNho}")
                            })
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
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
fun ProductItem(product: Product, modifier: Modifier = Modifier,onClick:()-> Unit) {
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
                Text("Giá ${product.Gia}", color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)

            }
        }
    }
}
