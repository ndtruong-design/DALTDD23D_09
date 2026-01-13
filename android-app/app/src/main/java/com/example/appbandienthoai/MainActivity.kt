package com.example.appbandienthoai
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
<<<<<<< HEAD
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
=======
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.appbandienthoai.ui.theme.AppBanDienThoaiTheme

class MainActivity : ComponentActivity() {
    val cartViewModel: CartViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(RetrofitClient.api) as T
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBanDienThoaiTheme {
                AppNavigation(cartViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(cartViewModel: CartViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,


        startDestination = "login" // ĐỔI TỪ "login" THÀNH "profile"

    ) {
        composable("splash") { SplashScreen(navController) }

        composable("login") {
            LoginScreen(
                api = RetrofitClient.api,
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgot") },
                onLoginSuccess = {
                    navController.navigate("home") {
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

        composable("home") {
            MainScreen(navController = navController)
        }

        composable("cart") {
            CartScreen(
                cartViewModel = cartViewModel,
                navController = navController
            )
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
<<<<<<< HEAD
        composable(
                route = "payment/{totalAmount}",
        arguments = listOf(navArgument("totalAmount") { type = NavType.IntType })
        ) { backStackEntry ->
        val totalAmount = backStackEntry.arguments?.getInt("totalAmount") ?: 0

        PaymentScreen(
            totalAmount = totalAmount.toLong(),
            api = RetrofitClient.api,
            navController = navController,
            cartViewModel = cartViewModel
        )
    }
//        composable(route="payment")
//        {
//            PaymentScreen(onPlaceOrder = {navController.popBackStack()})
//        }
=======

        composable(route="payment") {
            PaymentScreen(onPlaceOrder = {navController.popBackStack()})
        }
        composable(
            route = "detail/{MaSanPham}/{BoNho}",
            arguments = listOf(
                navArgument("MaSanPham") { type = NavType.IntType },
                navArgument("BoNho"){type= NavType.StringType
                nullable=true}
            )
        ) { backStackEntry ->
            // Lấy giá trị từ arguments
            val maSanPham = backStackEntry.arguments?.getInt("MaSanPham") ?: 0
            val boNho=backStackEntry.arguments?.getString("BoNho")?:""
>>>>>>> cb342957ce8dc201ad23364afa3f3cd981701307

            // Truyền cả hai vào ProductDetailScreen
            ProductDetailScreen(
                maSanPham = maSanPham,
                boNho=boNho,
                onBackClick = { navController.popBackStack() }
            )
        }

        // THÊM ROUTE PROFILE
        composable("profile") {
            ProfileScreen(
                userId = 1, // Thay số 1 bằng ID thật từ login
                api = RetrofitClient.api,
                onBack = { navController.popBackStack() }
            )
        }

        composable("don_hang") {
            // LƯU Ý: Ở đây tôi đang hardcode userId = 1 giống như ProfileScreen của bạn.
            // Thực tế bạn nên lấy userId từ DataStore/Session sau khi login.
            OrderHistoryScreen(
                api = RetrofitClient.api,
                userId = 1,
                navController = navController
            )
        }
    }
}