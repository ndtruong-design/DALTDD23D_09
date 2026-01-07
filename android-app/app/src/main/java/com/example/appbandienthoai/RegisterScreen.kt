package com.example.appbandienthoai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(api: ApiService, onLoginClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tạo tài khoản mới",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Tạo tài khoản để bắt đầu mua sắm ngay!!",
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))
        // Username
        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("Tên người dùng", color = Color(0xFF6A1B9A)) }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        // Số điện thoại
        OutlinedTextField(
            value = phone, onValueChange = { phone = it },
            label = { Text("Số điện thoại", color = Color(0xFF6A1B9A)) }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(Modifier.height(16.dp))
        // Mật khẩu
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Mật khẩu", color = Color(0xFF6A1B9A)) }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = ""
                    )
                }
            }
        )
        Spacer(Modifier.height(24.dp))
        // Button đăng ký
        Button(
            onClick = {
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
            modifier = Modifier.fillMaxWidth(),shape = RoundedCornerShape(12.dp)
        ) {
            Text(if(loading) "Đang đăng ký..." else "Đăng ký")
        }

        if(error.isNotEmpty()){
            Spacer(Modifier.height(8.dp))
            Text(error, color = Color.Red)
        }
        Spacer(Modifier.height( 23.dp))
        Row {
            OutlinedButton(
                onClick = {},shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Gmail")
            }
            Spacer(Modifier.width(12.dp))
            OutlinedButton(
                onClick = {},shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Facebook")
            }
        }

        Spacer(Modifier.height( 50.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Bạn chưa có tài khoản? ",
                color = Color.Gray
            )
            Text(
                text = "Đăng nhập",
                color = Color(0xFF6A1B9A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }


}
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        api = object : ApiService {
            override suspend fun login(request: LoginRequest) = throw NotImplementedError()
            override suspend fun forgotPassword(request: ForgotPasswordRequest) = throw NotImplementedError()
            override suspend fun register(request: RegisterRequest) =
                RegisterResponse(
                    success = true,
                    message = "Đăng ký thành công"
                )
            override suspend fun getAds(): List<Advertise> = emptyList()
        },
        onLoginClick = {}
    )
}