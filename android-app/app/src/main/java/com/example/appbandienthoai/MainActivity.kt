package com.example.appbandienthoai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.appbandienthoai.ui.theme.AppBanDienThoaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                api = RetrofitClient.api,
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgot") },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }


        composable("register") {
            RegisterScreen(
                api = RetrofitClient.api,
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }
        composable("main") {
            MainScreen()
        }

        composable("forgot") {
            ForgotPasswordScreen(
                api = RetrofitClient.api,
                onBackToLogin = { navController.popBackStack() }
            )
        }

    }
}





