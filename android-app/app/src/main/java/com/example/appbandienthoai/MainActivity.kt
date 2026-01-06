package com.example.appbandienthoai

import ForgotPasswordScreen
import LoginScreen
import RegisterScreen
import ResetPasswordScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

                AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // 1. Màn hình Đăng Nhập
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
            )
        }

        // 2. Màn hình Đăng Ký
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // 3. Màn hình Quên Mật Khẩu (Nhập Email)
        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToResetPassword = { navController.navigate("reset_password") } // Chuyển tiếp
            )
        }

        // 4. Màn hình Đặt Lại Mật Khẩu (Nhập OTP & Pass mới) -> MỚI THÊM
        composable("reset_password") {
            ResetPasswordScreen(
                onNavigateToLogin = {
                    // Reset thành công -> Về thẳng màn Login, xóa hết lịch sử back stack
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() } // Quay lại sửa email nếu nhập sai
            )
        }
    }
}


