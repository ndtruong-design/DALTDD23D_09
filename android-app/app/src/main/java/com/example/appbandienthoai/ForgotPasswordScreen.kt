package com.example.appbandienthoai

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    api: ApiService
) {
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

            // Nút quay lại
            IconButton(
                onClick = { onBackToLogin() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

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
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Vui lòng nhập số điện thoại đã đăng ký.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!showChangePassword) {
                    Button(
                        onClick = {
                            scope.launch {
                                loading = true
                                error = ""
                                try {
                                    val response = api.forgotPassword(
                                        ForgotPasswordRequest(phone, "")
                                    )
                                    if (response.success) {
                                        showChangePassword = true
                                    } else {
                                        error = response.message
                                    }
                                } catch (e: Exception) {
                                    error = "Không kết nối được server"
                                }
                                loading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Kieeerm tra")
                    }
                }

                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = Color.Red)
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
                                if (newPassword.isBlank()) {
                                    error = "Vui lòng nhập mật khẩu mới"
                                    return@launch
                                }
                                try {
                                    val response = api.forgotPassword(
                                        ForgotPasswordRequest(phone, newPassword)
                                    )
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
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Đổi mật khẩu")
                    }
                }
            }
        }
    }
}


