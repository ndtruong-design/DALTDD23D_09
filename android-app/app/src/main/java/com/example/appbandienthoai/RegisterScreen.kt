package com.example.appbandienthoai

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
// Đảm bảo bạn đã import R đúng package của dự án
// import com.example.appbandienthoai.R

@Composable
fun RegisterScreen(api: ApiService, onLoginClick: () -> Unit) {
    // --- GIỮ NGUYÊN LOGIC CŨ ---
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") } // Logic cũ dùng Phone
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    // ---------------------------

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Nút Back (Giao diện mới)
            IconButton(
                onClick = { onLoginClick() }, // Dùng onLoginClick để quay lại như logic điều hướng
                modifier = Modifier
                    .padding(top = 48.dp, start = 16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(65.dp))

            //logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_shop),
                    contentDescription = null,
                    modifier = Modifier.size(400.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Tạo tài khoản mới",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- USERNAME ---
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên người dùng") },
                placeholder = { Text("Nhập tên đăng nhập") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PHONE (Giữ logic Phone nhưng style theo giao diện mới) ---
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                placeholder = { Text("Nhập số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PASSWORD ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                placeholder = { Text("Nhập mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = ""
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- BUTTON ĐĂNG KÝ (Style mới, Logic cũ) ---
            Button(
                onClick = {
                    // Logic cũ được giữ nguyên
                    scope.launch {
                        if(username.isBlank() || phone.isBlank() || password.isBlank()){
                            error = "Vui lòng nhập đầy đủ thông tin"
                            return@launch
                        }
                        loading = true
                        error = ""
                        try {
                            val request = RegisterRequest(username, phone, password)
                            val response = api.register(request)
                            if(response.success){
                                error = ""
                                onLoginClick()
                            } else {
                                error = response.message
                            }
                        } catch(e: Exception){
                            error = "Không kết nối được server"
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
                    text = if(loading) "Đang đăng ký..." else "Đăng Ký",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Hiển thị lỗi (Logic cũ)
            if(error.isNotEmpty()){
                Spacer(Modifier.height(8.dp))
                Text(error, color = Color.Red)
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- FOOTER (Giống RegisterScreen2) ---
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đã có tài khoản? ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Đăng Nhập",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        onLoginClick()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    // Mock API cho Preview
    RegisterScreen(
        api = object : ApiService {
            override suspend fun login(request: LoginRequest) = throw NotImplementedError()
            override suspend fun forgotPassword(request: ForgotPasswordRequest) = throw NotImplementedError()
            override suspend fun register(request: RegisterRequest) =
                RegisterResponse(success = true, message = "Đăng ký thành công")
            override suspend fun getAds(): List<Advertise> = emptyList()
            override suspend fun getProduct(): List<Product> = emptyList()
            override suspend fun getProductDetail(id: Int): List<ProductDetail> = emptyList()
        },
        onLoginClick = {}
    )
}