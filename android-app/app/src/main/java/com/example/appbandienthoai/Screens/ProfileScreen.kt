package com.example.appbandienthoai.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.appbandienthoai.data.api.ApiService
import com.example.appbandienthoai.data.model.LoginRequest
import com.example.appbandienthoai.data.model.UpdateProfileRequest
import com.example.appbandienthoai.data.model.UserProfile
import com.example.appbandienthoai.utils.clearLoginInfo
import com.example.appbandienthoai.utils.savePassword
import com.example.appbandienthoai.utils.saveRememberMe
import com.example.appbandienthoai.utils.saveToken
import com.example.appbandienthoai.utils.saveUserId
import com.example.appbandienthoai.utils.saveUsername
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int,
    api: ApiService,
    onBack: () -> Unit,
    navController: NavHostController
) { var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var hoTen by remember { mutableStateOf("") }
    var soDienThoai by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ngaySinh by remember { mutableStateOf("") }
    var diaChi by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val res = api.getProfile(userId)
            if (res.success && res.user != null) {
                profile = res.user
                hoTen = res.user.HoTen
                soDienThoai = res.user.SoDienThoai
                email = res.user.Email.orEmpty()
                ngaySinh = res.user.NgaySinh.orEmpty()
                diaChi = res.user.DiaChi.orEmpty()
            } else {
                message = res.message ?: "Lỗi tải thông tin"
            }
        } catch (e: Exception) {
            message = "Lỗi: ${e.message}"
            Log.e("Profile", e.toString())
        } finally {
            isLoading = false
        }
    }

    fun update() {
        scope.launch {
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
                    diaChi = res.user.DiaChi.orEmpty()
                    message = "✓ Cập nhật thành công"
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
                title = { Text("Thông tin cá nhân", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        if (isLoading && profile == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Avatar
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                Alignment.Center
            ) {
                if (!profile?.AnhDaiDien.isNullOrEmpty()) {
                    AsyncImage(
                        model = profile?.AnhDaiDien,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                } else {
                    Box(
                        Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFEEEEEE)),
                        Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, Modifier.size(60.dp), Color.Gray)
                    }
                }
            }

            Text(
                "@${profile?.TenDangNhap ?: ""}",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            // Thông báo
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.startsWith("✓")) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Form
            OutlinedTextField(
                value = hoTen,
                onValueChange = { hoTen = it },
                label = { Text("Họ tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = soDienThoai,
                onValueChange = { soDienThoai = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = ngaySinh,
                onValueChange = { ngaySinh = it },
                label = { Text("Ngày sinh (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = diaChi,
                onValueChange = { diaChi = it },
                label = { Text("Địa chỉ") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))


            Button(
                onClick = { update() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("CẬP NHẬT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { update() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("CẬP NHẬT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))


            OutlinedButton(
                onClick = { navController.navigate("change_password") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Lock, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Đổi mật khẩu", fontSize = 15.sp)
            }

            Spacer(Modifier.height(32.dp))
            OutlinedButton(
                onClick = {   scope.launch {

                    clearLoginInfo(context)
                    navController.navigate("login")
                }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Spacer(Modifier.width(8.dp))
                Text("Đăng xuất", fontSize = 15.sp)
            }
        }
    }
}