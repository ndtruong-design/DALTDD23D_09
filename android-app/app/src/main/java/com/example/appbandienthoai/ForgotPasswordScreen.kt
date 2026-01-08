package com.example.appbandienthoai

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(onBackToLogin: () -> Unit, api: ApiService) {

    var phone by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var showChangePassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {


            IconButton(
                onClick = { onBackToLogin() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_shop),
                    contentDescription = null,
                    modifier = Modifier
                        .width(180.dp)
                        .height(90.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Quên mật khẩu?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))


                Text(
                    text = "Vui lòng nhập số điện thoại đã đăng ký.\nChúng tôi sẽ xác thực tài khoản của bạn.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- INPUT SỐ ĐIỆN THOẠI ---
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    placeholder = { Text("Nhập số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(16.dp))


                if (!showChangePassword) {
                    Button(
                        onClick = {
                            scope.launch {
                                loading = true
                                error = ""
                                try {
                                    val request = ForgotPasswordRequest(SoDienThoai = phone, MatKhau = "")
                                    val response = api.forgotPassword(request)
                                    if (response.success) {
                                        showChangePassword = true
                                    } else {
                                        error = response.message
                                        showChangePassword = false
                                    }
                                } catch (e: Exception) {
                                    error = "Không kết nối được server: ${e.message}"
                                    showChangePassword = false
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF673AB7),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (loading) "Đang kiểm tra..." else "Gửi mã xác nhận",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }


                if (showChangePassword) {
                    Spacer(Modifier.height(24.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Mật khẩu mới") },
                        placeholder = { Text("Nhập mật khẩu mới") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp)
                    )

<<<<<<< HEAD
            Button(
                onClick = {
                    scope.launch {
                        if (newPassword.isBlank()) {
                        error = "Vui lòng nhập mật khẩu mới"
                        return@launch
                    }
                        try {
                            val request = ForgotPasswordRequest(SoDienThoai = phone, MatKhau = newPassword)
                            val response = api.forgotPassword(request)


                            if (response.success) {
                                onBackToLogin()
                            } else {
                                error = response.message
=======
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val request = ForgotPasswordRequest(SoDienThoai = phone, MatKhau = newPassword)
                                    val response = api.forgotPassword(request)

                                    if (response.success) {
                                        onBackToLogin()
                                    } else {
                                        error = response.message
                                    }
                                } catch (e: Exception) {
                                    error = "Không kết nối được server"
                                }
>>>>>>> 6ca6197cc32c827e382e45e3814aadadb7bce635
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF673AB7), // Màu tím Screen 2
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Đổi Mật Khẩu",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        api = object : ApiService {
            override suspend fun login(request: LoginRequest) = throw NotImplementedError()
            override suspend fun register(request: RegisterRequest) = throw NotImplementedError()
            override suspend fun forgotPassword(request: ForgotPasswordRequest) =
                ForgotPasswordResponse(
                    success = true,
                    message = "Preview message",
                    token = "fake_token"
                )
            override suspend fun getAds(): List<Advertise> = emptyList()
<<<<<<< HEAD
            override suspend fun getProduct(): List<Product> {
                TODO("Not yet implemented")
            }

            override suspend fun filterProducts(
                min: Int?,
                max: Int?,
                hang: String?
            ): FilterResponse {
                TODO("Not yet implemented")
            }

            override suspend fun getProductDetail(id: Int): List<ProductDetail> {
                TODO("Not yet implemented")
            }

=======
            override suspend fun getProduct(): List<Product> = emptyList()
            override suspend fun getProductDetail(id: Int): List<ProductDetail> = emptyList()
>>>>>>> 6ca6197cc32c827e382e45e3814aadadb7bce635
        },
        onBackToLogin = {}
    )
}