package com.example.appbandienthoai

import android.util.Log
import androidx. compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation. shape.RoundedCornerShape
import androidx.compose.foundation. verticalScroll
import androidx. compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons. filled.Person
import androidx.compose.material3.*
import androidx. compose.runtime.*
import androidx. compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui. draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout. ContentScale
import androidx.compose. ui.text.font.FontWeight
import androidx.compose.ui. unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines. launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int,
    api: ApiService,
    onBack: () -> Unit
) {
    var profile by remember { mutableStateOf<UserProfile? >(null) }
    var hoTen by remember { mutableStateOf("") }
    var soDienThoai by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ngaySinh by remember { mutableStateOf("") }
    var diaChi by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val hasChanges by remember(profile, hoTen, soDienThoai, email, ngaySinh, diaChi) {
        derivedStateOf {
            profile?. let {
                hoTen != it.HoTen ||
                        soDienThoai != it.SoDienThoai ||
                        email != (it.Email ?: "") ||
                        ngaySinh != (it.NgaySinh ?: "") ||
                        diaChi != (it.DiaChi ?: "")
            } ?: false
        }
    }
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val res = api.getProfile(userId)
            if (res. success && res.user != null) {
                profile = res.user
                hoTen = res. user.HoTen
                soDienThoai = res.user.SoDienThoai
                email = res.user.Email.orEmpty()
                ngaySinh = res. user.NgaySinh. orEmpty()
                diaChi = res.user.DiaChi.orEmpty()
            } else {
                message = res.message ?: "Lỗi tải thông tin"
            }
        } catch (e:  Exception) {
            message = "Lỗi kết nối: ${e.message}"
            Log.e("Profile", e.toString())
        } finally {
            isLoading = false
        }
    }

    fun update() {
        if (!hasChanges) return

        scope. launch {
            isLoading = true
            message = ""

            try {
                val request = UpdateProfileRequest(
                    MaKhachHang = userId,
                    HoTen = hoTen.takeIf { it.isNotBlank() },
                    SoDienThoai = soDienThoai.takeIf { it.isNotBlank() },
                    Email = email.takeIf { it.isNotBlank() },
                    NgaySinh = ngaySinh.takeIf { it.isNotBlank() },
                    DiaChi = diaChi.takeIf { it.isNotBlank() }
                )

                val res = api.updateProfile(request)
                if (res.success && res.user != null) {
                    profile = res.user
                    hoTen = res.user.HoTen
                    soDienThoai = res.user.SoDienThoai
                    email = res.user.Email.orEmpty()
                    ngaySinh = res.user.NgaySinh.orEmpty()
                    diaChi = res. user.DiaChi.orEmpty()
                    message = "✓ Đã cập nhật thành công"
                } else {
                    message = res.message ?: "Cập nhật thất bại"
                }
            } catch (e: Exception) {
                message = "Lỗi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin cá nhân") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        if (isLoading && profile == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 90.dp)
            ) {
                // Header với Avatar
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1976D2))
                        .padding(32.dp),
                    Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment. CenterHorizontally) {
                        if (!profile?.AnhDaiDien.isNullOrEmpty()) {
                            // Có ảnh → Load từ URL
                            AsyncImage(
                                model = profile?.AnhDaiDien,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                Modifier
                                    . size(96.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    null,
                                    Modifier.size(56.dp),
                                    Color(0xFF757575)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            "@${profile?.TenDangNhap ?: "user"}",
                            color = Color.White,
                            fontSize = 17.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Thông báo
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (message.startsWith("✓")) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(
                                if (message.startsWith("✓")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                RoundedCornerShape(8.dp)
                            )
                            . padding(12.dp),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults. cardElevation(2.dp)
                ) {
                    Column(
                        Modifier. padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ProfileField("Họ tên", hoTen, { hoTen = it })
                        ProfileField("Số điện thoại", soDienThoai, { soDienThoai = it }, "0909123456")
                        ProfileField("Email", email, { email = it }, "example@gmail.com")
                        ProfileField("Ngày sinh", ngaySinh, { ngaySinh = it }, "YYYY-MM-DD")
                        ProfileField("Địa chỉ", diaChi, { diaChi = it }, maxLines = 3)
                    }
                }

                Spacer(Modifier.height(100.dp))
            }

            Button(
                onClick = { update() },
                modifier = Modifier
                    . fillMaxWidth()
                    . padding(16.dp)
                    .align(Alignment. BottomCenter)
                    . height(52.dp),
                enabled = hasChanges && !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        "CẬP NHẬT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileField(
    label:  String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    maxLines: Int = 1
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            singleLine = maxLines == 1,
            maxLines = maxLines,
            shape = RoundedCornerShape(10.dp)
        )
    }
}