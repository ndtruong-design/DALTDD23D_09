package com.example.appbandienthoai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.appbandienthoai.ui.theme.AppBanDienThoaiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBanDienThoaiTheme {
<<<<<<< HEAD
                AppNavigation()

=======
               // AppNavigation()
                MainScreen()
>>>>>>> 6ca6197cc32c827e382e45e3814aadadb7bce635

            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
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
            MainScreen(navController = navController)

        }

        composable("forgot") {
            ForgotPasswordScreen(
                api = RetrofitClient.api,
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(
            route = "filter/{brands}"
        ) { backStackEntry ->
            val brandsParam = backStackEntry.arguments?.getString("brands") ?: ""
            val brands = brandsParam.split(",").filter { it.isNotBlank() }

            FilterScreen(
                api = RetrofitClient.api,
                navController = navController,
                brands = brands
            )
        }

    }
}
