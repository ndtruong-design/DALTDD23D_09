package com.example.appbandienthoai.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import coil.compose.AsyncImage
import com.example.appbandienthoai.data.model.AddCartRequest
import com.example.appbandienthoai.data.model.AddFavoriteRequest
import com.example.appbandienthoai.data.model.ColorPhone
import com.example.appbandienthoai.data.model.ImageDetail
import com.example.appbandienthoai.data.model.ProductDetail
import com.example.appbandienthoai.data.api.RetrofitClient
import com.example.appbandienthoai.utils.getUserId
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    maSanPham: Int,
    boNho: String?,
    onBackClick: () -> Unit
) {
    var product by remember { mutableStateOf<ProductDetail?>(null) }
    var productImage by remember { mutableStateOf<List<ImageDetail>>(emptyList()) }
    var colorList by remember { mutableStateOf<List<ColorPhone>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var selectedMemory by remember { 
        mutableStateOf(if (boNho.isNullOrBlank() || boNho == "none") "256GB" else boNho) 
    }
    
    val memoryOptions = listOf("256GB", "512GB")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedColorId by remember { mutableStateOf("") }


    LaunchedEffect(maSanPham, selectedMemory, selectedColorId) {
        try {
            isLoading = true
            errorMsg = null
            val response = RetrofitClient.api.getProductDetail(
                maSanPham,
                selectedMemory,
                selectedColorId.ifEmpty { null }
            )
            product = response
            if (response != null) {
                try {
                    val colors = RetrofitClient.api.getColor(response.MaSanPham, response.BoNho)
                    colorList = colors.filter { !it.MaHex.isNullOrEmpty() }

                    if (selectedColorId.isEmpty() && colorList.isNotEmpty()) {
                        selectedColorId = response.MaMau.toString()
                    }
                } catch (e: Exception) {
                    Log.e("PRODUCT_ERROR", "Không lấy màu được ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("PRODUCT_ERROR", "Detail fetch failed: ${e.message}")
            errorMsg = "Không thể tải thông tin sản phẩm"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(selectedColorId, product?.MaChiTietSP) {
        val currentMaSanPham = product?.MaSanPham
        if (selectedColorId.isNotEmpty() && currentMaSanPham != null) {
            try {
                val imgRes = RetrofitClient.api.getImageDetail(currentMaSanPham, selectedColorId)
                productImage = imgRes
            } catch (e: Exception) {
                Log.e("PRODUCT_ERROR", "Image fetch failed: ${e.message}")
                productImage = emptyList()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && product == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMsg != null) {
                Text(errorMsg!!, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else if (product != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {

                    if (productImage.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(Color.White),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(productImage) { imgDetail ->
                                AsyncImage(
                                    model = imgDetail.DuongLinkAnh,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(350.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Đang tải ảnh...")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Text(text = product?.Hang ?: "", color = Color.Gray, fontSize = 14.sp)
                    Text(text = product?.TenSanPham ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(
                            "Giá: %,d đ".format(product?.Gia).replace(',', '.'),
                            fontSize = 22.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        IconButton(onClick = {
                            coroutineScope.launch {
                                try {
                                    val userId = getUserId(context)
                                    if (userId != -1 && product != null) {
                                        val response = RetrofitClient.api.addFavorite(
                                            AddFavoriteRequest(
                                                MaKhachHang = userId,
                                                MaSanPham = product!!.MaSanPham
                                            )
                                        )
                                        if (response.success) {
                                            snackbarHostState.showSnackbar(response.message ?: "Đã thêm vào yêu thích!")
                                        } else {
                                            snackbarHostState.showSnackbar(response.message ?: "Thêm thất bại")
                                        }
                                    } else {
                                        snackbarHostState.showSnackbar("Vui lòng đăng nhập để thêm vào yêu thích")
                                    }
                                } catch (e: Exception) {
                                    Log.e("FAVORITE_ERROR", "Lỗi: ${e.message}")
                                    snackbarHostState.showSnackbar("Lỗi kết nối server")
                                }
                            }
                        }) {
                            Icon(Icons.Default.Favorite, contentDescription = "Yêu thích", tint = Color.Red)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


                    Text("Phiên bản bộ nhớ:", fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        memoryOptions.forEach { mem ->
                            FilterChip(
                                selected = selectedMemory == mem,
                                onClick = {
                                    selectedMemory = mem
                                    selectedColorId = ""
                                },
                                label = { Text(mem) },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor =Color.White,
                                    labelColor=Color.Black,
                                    selectedContainerColor = Color(0xFF673AB7),
                                    selectedLabelColor = Color.White
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = true,
                                    borderColor = Color.Gray,
                                    disabledBorderColor = Color.Gray,
                                    selectedBorderColor = Color.Gray,
                                    disabledSelectedBorderColor = Color.Gray

                                )
                            )
                        }
                    }


                    if (colorList.isNotEmpty()) {
                        Text("Chọn màu sắc:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        LazyRow(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(colorList) { colorPhone ->
                                val isSelected = selectedColorId == colorPhone.MaMau.toString()

                                val hex = colorPhone.MaHex ?: return@items
                                val hexColor = if (hex.startsWith("#")) hex else "#$hex"
                                val composeColor = try {
                                    Color(android.graphics.Color.parseColor(hexColor))
                                } catch (e: Exception) {
                                    Color.Gray
                                }

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(composeColor)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColorId = colorPhone.MaMau.toString() }
                                )
                            }
                        }
                        val selectedColor = colorList.find { it.MaMau.toString() == selectedColorId }
                        Text(
                            text = "Màu đang chọn: ${selectedColor?.TenMau ?: "Chưa chọn"}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Spacer(modifier = Modifier.height(16.dp))


                    Text("Mô tả sản phẩm:", fontWeight = FontWeight.Bold)
                    Text(
                        text = product?.MoTa ?: "Đang cập nhật...",
                        modifier = Modifier.padding(top = 8.dp),
                        lineHeight = 22.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    //6. Thông tin chi tiết
                    Text(text="Thông tin chi tiết:", fontWeight = FontWeight.Bold)
                    Text(text="Bộ nhớ: "+product?.BoNho.toString())
                    Text(text="CPU: "+product?.CPU.toString())
                    Text(text="Camera sau: "+product?.CameraSau.toString())
                    Text(text="Camera trước: "+product?.CameraTruoc.toString())
                    Text(text="GPU: "+product?.GPU.toString())
                    Text(text="Hệ điều hành "+product?.HeDieuHanh.toString())
                    Text(text="Kích thước: "+product?.KichThuoc.toString())
                    Text(text="Màn hình: "+product?.ManHinh.toString())
                    Text(text="Pin: "+product?.Pin.toString())
                    Text(text="Ram: "+product?.RAM.toString())

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val userId = getUserId(context)
                                    if (userId != -1 && product != null) {
                                        // Sử dụng AddCartRequest để gửi dữ liệu qua @Body
                                        val response = RetrofitClient.api.addCart(
                                            AddCartRequest(
                                                MaKhachHang = userId,
                                                MaSanPham = product!!.MaSanPham,
                                                MaMau = selectedColorId.toInt(),
                                                BoNho = product!!.BoNho
                                            )
                                        )
                                        if (response.success) {
                                            snackbarHostState.showSnackbar("Đã thêm vào giỏ hàng!")
                                        } else {
                                            snackbarHostState.showSnackbar(response.message ?: "Thêm thất bại")
                                        }
                                    } else {
                                        snackbarHostState.showSnackbar("Vui lòng đăng nhập để mua hàng")
                                    }
                                } catch (e: Exception) {
                                    Log.e("ADD_CART_ERROR", e.message ?: "Lỗi không xác định")
                                    snackbarHostState.showSnackbar("Lỗi kết nối server")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("THÊM VÀO GIỎ HÀNG", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
