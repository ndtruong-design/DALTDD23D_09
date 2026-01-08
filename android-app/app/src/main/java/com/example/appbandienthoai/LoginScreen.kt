package com.example.appbandienthoai
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lint.kotlin.metadata.Visibility
import com.example.appbandienthoai.ui.theme.AppBanDienThoaiTheme
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Checkbox

@Composable
fun LoginScreen(
    api:  ApiService,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    //var isPasswordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

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
                contentScale = ContentScale.Fit // Đảm bảo ảnh không bị méo
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Đăng nhập tài khoản",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(60.dp))


        InputSection(
            label = "Tên đăng nhập",
            placeholder = "Nhập tên đăng nhập của bạn",
            value = username,
            onValueChange = { username = it }
        )

        Spacer(Modifier.height(16.dp))

        InputSection(
            label = "Mật khẩu",
            placeholder = "Nhập mật khẩu của bạn",
            value = password,
            onValueChange = { password = it },
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
            Text("Ghi nhớ tôi")
            Spacer(Modifier.weight(1f))
            Text("Quên mật khẩu", color = Color(0xFF6A1B9A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onForgotPasswordClick() })
        }



        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                scope.launch {
                    errorMessage = ""

                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Vui lòng nhập đầy đủ thông tin"
                        return@launch
                    }

                    loading = true
                    try {
                        val response = api.login(
                            LoginRequest(username, password)
                        )

                        if (response.success) {
                            response.token?.let {
                                saveToken(context, it)
                                onLoginSuccess()
                            }
                        } else {
                            errorMessage = response.message ?: "Sai tài khoản hoặc mật khẩu"
                        }

                    } catch (e: HttpException) {

                        errorMessage = "Sai tài khoản hoặc lỗi server: ${e.code()}"
                        Log.e("LOGIN_API", "Http Error: ${e.message()}")

                    } catch (e: Exception) {

                        errorMessage = "Lỗi hệ thống: ${e.message}"
                        Log.e("LOGIN_API", "Exception: ", e)
                    } finally {
                        loading = false
                    }
                }
            },

            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp).width(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Đăng nhập", color = Color.White)
            }
        }

        Spacer(Modifier.height(16.dp))

//        Row(
//            modifier = Modifier.padding(bottom = 32.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Quên mật khẩu?",
//                color = Color.Black,
//                fontWeight = FontWeight.Bold,
//                fontSize = 14.sp,
//                textDecoration = TextDecoration.Underline,
//                modifier = Modifier.clickable {
//                    onForgotPasswordClick()
//                }
//            )
//        }

        Spacer(Modifier.height(10.dp))

//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
//        ) {
//
//            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
//
//            Text(
//                text = "Hoặc",
//                modifier = Modifier.padding(horizontal = 8.dp),
//                fontSize = 12.sp,
//                color = Color.Gray
//            )
//
//            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
//        }
//
//        Spacer(Modifier.height(16.dp))

<<<<<<< HEAD
            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)

            Text(
                text = "Hoặc",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )

            HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
        }

        Spacer(Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SocialMediaButton(
                iconRes = R.drawable.logo_google,
                onClick = { }
            )
            SocialMediaButton(
                iconRes = R.drawable.logo_fb,
                onClick = {  }
            )
        }
=======
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            SocialMediaButton(
//                iconRes = R.drawable.logo_google,
//                onClick = { /* Xử lý đăng nhập Google */ }
//            )
//            SocialMediaButton(
//                iconRes = R.drawable.logo_fb,
//                onClick = { /* Xử lý đăng nhập Facebook */ }
//            )
//        }
>>>>>>> 6ca6197cc32c827e382e45e3814aadadb7bce635

        Spacer(modifier = Modifier.weight(1f))


        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chưa có tài khoản? ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "Đăng Ký",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    onRegisterClick()
                }
            )
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Nếu bạn muốn test với theme của app
    AppBanDienThoaiTheme {
        LoginScreen(
            api = object : ApiService { // fake API để Preview
                override suspend fun login(request: LoginRequest) =
                    LoginResponse(success = true, token = "fake_token", message = null)

                override suspend fun register(request: RegisterRequest) =
                    throw NotImplementedError()

                override suspend fun forgotPassword(request: ForgotPasswordRequest) =
                    throw NotImplementedError()
            },
            onRegisterClick = {},
            onForgotPasswordClick = {},
            onLoginSuccess = TODO()
        )
    }
}*/

@Composable
fun InputSection(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {

    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = Color.LightGray)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            // 2. Thay đổi VisualTransformation dựa trên trạng thái passwordVisible
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            // 3. Thêm icon ở cuối ô nhập liệu
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility
                            else
                                Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            },
            keyboardOptions = if (isPassword)
                KeyboardOptions(keyboardType = KeyboardType.Password)
            else
                KeyboardOptions(keyboardType = KeyboardType.Email)
        )
    }
}

//@Composable
//fun SocialMediaButton(
//    iconRes: Int,
//    onClick: () -> Unit
//) {
//    OutlinedButton(
//        onClick = onClick,
//        modifier = Modifier
//            .size(60.dp), // Kích thước nút vuông
//        shape = RoundedCornerShape(12.dp),
//        contentPadding = PaddingValues(12.dp), // Khoảng cách từ viền nút đến icon
//        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
//        border = BorderStroke(1.dp, Color.LightGray)
//    ) {
//        Image(
//            painter = painterResource(id = iconRes),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}