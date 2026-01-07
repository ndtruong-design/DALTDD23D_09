package com.example.appbandienthoai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import coil.compose.AsyncImage           // Để dùng hàm hiển thị ảnh từ link
import com.example.appbandienthoai.RetrofitClient.api
import androidx.compose.runtime.mutableStateListOf

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier= Modifier,api: ApiService = RetrofitClient.api){

    val hinhquangcao1 = listOf(
        "https://cdnv2.tgdd.vn/mwg-static/common/News/1576531/dien-thoai-1.jpg",
        "https://cdn.hoanghamobile.vn/Uploads/2024/04/24/dien-thoai-di-dong-29.jpg"
    )
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
        Column (modifier = Modifier.padding(it))
        {
            Row(modifier= Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    modifier= Modifier.weight(1f),
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

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Khoảng cách giữa các ảnh
            ) {
                items(hinhquangcao1) { item ->
                    AsyncImage(
                        model = item,
                        contentDescription = "Banner",
                        modifier = Modifier
                            .width(320.dp) // Độ rộng của mỗi banner
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp)), // Bo góc cho đẹp
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}