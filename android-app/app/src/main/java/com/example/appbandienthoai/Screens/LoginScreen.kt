package com.example.appbandienthoai.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appbandienthoai.R
import com.example.appbandienthoai.data.api.ApiService
import com.example.appbandienthoai.data.model.LoginRequest
import com.example.appbandienthoai.utils.clearLoginInfo
import com.example.appbandienthoai.utils.getPassword
import com.example.appbandienthoai.utils.getRememberMe
import com.example.appbandienthoai.utils.getToken
import com.example.appbandienthoai.utils.getUsername
import com.example.appbandienthoai.utils.savePassword
import com.example.appbandienthoai.utils.saveRememberMe
import com.example.appbandienthoai.utils.saveToken
import com.example.appbandienthoai.utils.saveUserId
import com.example.appbandienthoai.utils.saveUsername
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun LoginScreen(
    api: ApiService,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        val savedUsername = getUsername(context)
        val savepassword= getPassword(context)
        if (!savedUsername.isNullOrEmpty()) {
            username = savedUsername
        }
        if(!savepassword.isNullOrEmpty()){
            password=savepassword
        }

        val savedRememberMe = getRememberMe(context)
        if (savedRememberMe) {
            rememberMe = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_shop),
                contentDescription = null,
                modifier = Modifier.size(300.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Đăng nhập tài khoản",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        InputSection(
            label = "Tên đăng nhập",
            placeholder = "Nhập tên đăng nhập",
            value = username,
            onValueChange = { username = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputSection(
            label = "Mật khẩu",
            placeholder = "Nhập mật khẩu",
            value = password,
            onValueChange = { password = it },
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text("Ghi nhớ tôi")

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Quên mật khẩu",
                color = Color(0xFF6A1B9A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onForgotPasswordClick() }
            )
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                        val response = api.login(LoginRequest(username, password))
                        if (response.success) {
                            val userId = response.MaKhachHang ?: -1
                            val type = response.accountType ?: ""

                            response.token?.let { tokenFromApi ->

                                if (rememberMe) {
                                    saveUsername(context, username)
                                    savePassword(context, password)
                                    saveRememberMe(context, true)
                                } else {
                                    clearLoginInfo(context)
                                }

                                saveToken(context, tokenFromApi)
                                saveUserId(context, userId)

                                onLoginSuccess(type)
                            }
                        }
                        else {
                            errorMessage = response.message ?: "Sai tài khoản hoặc mật khẩu"
                        }
                    } catch (e: HttpException) {
                        errorMessage = "Sai tài khoản hoặc mật khẩu"
                        Log.e("LOGIN", e.message())
                    } catch (e: Exception) {
                        errorMessage = "Lỗi hệ thống"
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
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Đăng nhập", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "Hoặc",
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SocialMediaButton(R.drawable.logo_google) {}
            SocialMediaButton(R.drawable.logo_fb) {}
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Chưa có tài khoản? ", color = Color.Gray)
            Text(
                text = "Đăng ký",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }
    }
}

@Composable
fun InputSection(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.Medium)

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation =
                if (isPassword && !passwordVisible)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector =
                                if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            },
            keyboardOptions =
                if (isPassword)
                    KeyboardOptions(keyboardType = KeyboardType.Password)
                else
                    KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}

@Composable
fun SocialMediaButton(iconRes: Int, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp), // Tăng từ 56.dp lên 64.dp
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(12.dp) // Điều chỉnh padding để logo to hơn
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val remember = getRememberMe(context.applicationContext)
        val token = getToken(context.applicationContext)

        if (remember && !token.isNullOrEmpty()) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}



