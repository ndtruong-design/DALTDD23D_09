package com.example.appbandienthoai.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.appbandienthoai.data.api.ApiService
import com.example.appbandienthoai.data.model.UpdateProfileRequest
import com.example.appbandienthoai.data.model.UserProfile
import com.example.appbandienthoai.utils.clearLoginInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int,
    api: ApiService,
    onBack: () -> Unit,
    navController: NavHostController
) {
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var hoTen by remember { mutableStateOf("") }
    var soDienThoai by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ngaySinh by remember { mutableStateOf("") }
    var diaChi by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Error states
    var hoTenError by remember { mutableStateOf("") }
    var soDienThoaiError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var ngaySinhError by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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

    fun validateInputs(): Boolean {
        var isValid = true

        // Reset errors
        hoTenError = ""
        soDienThoaiError = ""
        emailError = ""
        ngaySinhError = ""


        if (hoTen.isBlank()) {
            hoTenError = "Họ tên không được để trống"
            isValid = false
        }


        if (soDienThoai.isBlank()) {
            soDienThoaiError = "Số điện thoại không được để trống"
            isValid = false
        } else if (!soDienThoai.all { it.isDigit() }) {
            soDienThoaiError = "Số điện thoại chỉ được chứa chữ số"
            isValid = false
        } else if (soDienThoai.length != 10) {
            soDienThoaiError = "Số điện thoại phải có 10 chữ số"
            isValid = false
        } else if (!soDienThoai.startsWith("0")) {
            soDienThoaiError = "Số điện thoại phải bắt đầu bằng số 0"
            isValid = false
        }

        if (email.isNotBlank()) {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            if (!email.matches(emailRegex)) {
                emailError = "Email không hợp lệ"
                isValid = false
            }
        }

        if (ngaySinh.isNotBlank()) {
            val dateRegex = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()
            if (!ngaySinh.matches(dateRegex)) {
                ngaySinhError = "Ngày sinh phải theo định dạng YYYY-MM-DD"
                isValid = false
            }
        }

        return isValid
    }

    fun update() {
        if (!validateInputs()) {
            message = " Vui lòng kiểm tra lại thông tin"
            return
        }

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
                    message = " ${res.message ?: "Cập nhật thất bại"}"
                }
            } catch (e: Exception) {
                message = "Lỗi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Đăng xuất") },
            text = { Text("Bạn có chắc chắn muốn đăng xuất?") },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            clearLoginInfo(context)
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text("Đăng xuất", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin cá nhân", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Đăng xuất",
                            tint = Color.Red
                        )
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
            Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), Alignment.Center) {
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

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.startsWith("✓")) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = hoTen,
                onValueChange = {
                    hoTen = it
                    hoTenError = ""
                },
                label = { Text("Họ tên *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = hoTenError.isNotEmpty(),
                supportingText = if (hoTenError.isNotEmpty()) {
                    { Text(hoTenError, color = Color.Red, fontSize = 12.sp) }
                } else null
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = soDienThoai,
                onValueChange = {
                    if (it.length <= 10) {
                        soDienThoai = it.filter { char -> char.isDigit() }
                        soDienThoaiError = ""
                    }
                },
                label = { Text("Số điện thoại *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = soDienThoaiError.isNotEmpty(),
                supportingText = if (soDienThoaiError.isNotEmpty()) {
                    { Text(soDienThoaiError, color = Color.Red, fontSize = 12.sp) }
                } else null
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError.isNotEmpty(),
                supportingText = if (emailError.isNotEmpty()) {
                    { Text(emailError, color = Color.Red, fontSize = 12.sp) }
                } else null
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = ngaySinh,
                onValueChange = {
                    ngaySinh = it
                    ngaySinhError = ""
                },
                label = { Text("Ngày sinh (YYYY-MM-DD)") },
                placeholder = { Text("VD: 2000-01-15") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = ngaySinhError.isNotEmpty(),
                supportingText = if (ngaySinhError.isNotEmpty()) {
                    { Text(ngaySinhError, color = Color.Red, fontSize = 12.sp) }
                } else null
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

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { update() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Cập nhật", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { navController.navigate("change_password") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Lock, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Đổi mật khẩu", fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}