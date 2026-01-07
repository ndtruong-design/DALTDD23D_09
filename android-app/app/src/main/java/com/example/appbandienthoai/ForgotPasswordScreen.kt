package com.example.appbandienthoai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "QUÊN MẬT KHẨU",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(16.dp))

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
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
        ) {
            Text("KIỂM TRA SỐ ĐIỆN THOẠI", color = Color.White)
        }


        if (error.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.Red
            )
        }

        if (showChangePassword) {
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Mật khẩu mới") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp)
            )

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
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
            ) {
                Text("ĐỔI MẬT KHẨU", color = Color.White)
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Quay lại đăng nhập",
            color = Color(0xFF6A1B9A),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBackToLogin() },
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        api = object : ApiService {
            override suspend fun login(request: LoginRequest) = throw NotImplementedError()
            override suspend fun register(request: RegisterRequest) =throw NotImplementedError()
            override suspend fun forgotPassword(request: ForgotPasswordRequest) =
                ForgotPasswordResponse(
                    success = true,
                    message = "Preview message",
                    token = "fake_token"
                )
            override suspend fun getAds(): List<Advertise> = emptyList()
        },
        onBackToLogin = {}
    )
}
