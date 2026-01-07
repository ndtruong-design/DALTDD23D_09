package com.example.appbandienthoai

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items // Quan trọng để dùng items trong LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage           // Để dùng hàm hiển thị ảnh từ link


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier= Modifier){

    var adsList by remember { mutableStateOf<List<Advertise>>(emptyList()) }
    LaunchedEffect(Unit) {
        try {
            // Gọi hàm suspend từ RetrofitClient
            val result = RetrofitClient.api.getAds()
            adsList = result
            Log.d("API_ads", "Đã lấy được: ${result.size} quảng cáo")
        } catch (e: Exception) {
            Log.e("API_ads", "Lỗi: ${e.message}")
        }
    }
    Scaffold (
       topBar = {
           TopAppBar(
               title = {
                        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(

                                text = "Phone-Shop",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Left
                            )

                            IconButton(
                                onClick = {},


                                ){
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Yeu thich"
                                )
                            }

                        }
               }

           )
       }
    )
    {
        LazyColumn (modifier = Modifier.padding(it))
        {
            item{
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = "",
                    onValueChange = {},
                    placeholder = {


                        Text(text = "Tim kiem san pham")

                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Tim kiem"
                        )
                    },
                    shape = CircleShape,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Loc"
                    )
                }
            }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                if (adsList.isEmpty()) {
                    // Hiển thị loading hoặc placeholder nếu chưa có dữ liệu
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator() // Vòng xoay loading
                    }
                } else {
                    // Hiển thị danh sách ảnh khi đã có dữ liệu
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp), // Padding 2 đầu
                        horizontalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa các ảnh
                    ) {
                        items(adsList) { ad ->
                            // Ghép đường dẫn đầy đủ
                            val fullImageUrl = ad.HinhAnh

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .width(300.dp) // Chiều rộng cố định cho mỗi banner
                                    .height(160.dp)
                            ) {
                                AsyncImage(
                                    model = fullImageUrl,
                                    contentDescription = "Quang cao ${ad.MaHinhAnh}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop // Cắt ảnh cho vừa khung
                                )
                            }

                        }
                    }
                }
            }
            item{

            }

        }
    }
}