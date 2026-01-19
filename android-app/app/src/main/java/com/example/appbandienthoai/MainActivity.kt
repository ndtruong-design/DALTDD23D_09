package com.example.appbandienthoai
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appbandienthoai.Screens.AdminOrderDetailScreen
import com.example.appbandienthoai.Screens.AdminOrderListScreen
import com.example.appbandienthoai.Screens.CartScreen
import com.example.appbandienthoai.Screens.ChangePasswordScreen
import com.example.appbandienthoai.Screens.FilterScreen
import com.example.appbandienthoai.Screens.ForgotPasswordScreen
import com.example.appbandienthoai.Screens.LoginScreen
import com.example.appbandienthoai.Screens.MainScreen
import com.example.appbandienthoai.Screens.OrderHistoryScreen
import com.example.appbandienthoai.Screens.PaymentScreen
import com.example.appbandienthoai.Screens.ProductDetailScreen
import com.example.appbandienthoai.Screens.ProfileScreen
import com.example.appbandienthoai.Screens.RegisterScreen
import com.example.appbandienthoai.Screens.SplashScreen
import com.example.appbandienthoai.Screens.WishListScreen
import com.example.appbandienthoai.components.CartViewModel
import com.example.appbandienthoai.data.api.RetrofitClient
import com.example.appbandienthoai.ui.theme.AppBanDienThoaiTheme
import com.example.appbandienthoai.utils.getUserId

class MainActivity : ComponentActivity() {
    val cartViewModel: CartViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(RetrofitClient.api) as  T
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


        startDestination = "login"

    ) {
        composable("splash") { SplashScreen(navController) }

        composable("login") {
            LoginScreen(
                api = RetrofitClient.api,
                onRegisterClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgot") },
                onLoginSuccess = { type ->
                    if (type == "admin") {
                        navController.navigate("adminOrderlistscreen") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
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



        composable(
            route = "detail/{MaSanPham}/{BoNho}",
            arguments = listOf(
                navArgument("MaSanPham") { type = NavType.IntType },
                navArgument("BoNho"){type= NavType.StringType
                nullable=true}
            )
        ) { backStackEntry ->

            val maSanPham = backStackEntry.arguments?.getInt("MaSanPham") ?: 0
            val boNho=backStackEntry.arguments?.getString("BoNho")?:""


            ProductDetailScreen(
                maSanPham = maSanPham,
                boNho = boNho,
                onBackClick = { navController.popBackStack() }
            )
        }


        composable("profile") {
            val context = LocalContext.current
            var userId by remember { mutableStateOf(-1) }

            LaunchedEffect(Unit) {
                userId = getUserId(context)
            }

            if (userId > 0) {
                ProfileScreen(
                    userId = userId,
                    api = RetrofitClient.api,
                    onBack = { navController.popBackStack() },
                    navController = navController
                )
            } else {

                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        composable("change_password") {
            val context = LocalContext.current
            var userId by remember { mutableStateOf(-1) }

            LaunchedEffect(Unit) {
                userId = getUserId(context)
            }

            ChangePasswordScreen(
                userId = userId,
                api = RetrofitClient.api,
                onBack = { navController.popBackStack() }
            )
        }
        composable("adminOrderdetailscreen/{MaDonHang}",
            arguments = listOf(navArgument("MaDonHang"){type = NavType.IntType}))
        { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("MaDonHang") ?: 0
            AdminOrderDetailScreen(
                orderId = orderId,
                api = RetrofitClient.api,
                onBack = { navController.popBackStack() }
            )
        }
        composable("adminOrderlistscreen")
        {
            AdminOrderListScreen(
                navController = navController,
                api = RetrofitClient.api,

                )
        }


        composable("don_hang") {
            val context = LocalContext.current
            var userId by remember { mutableStateOf(-1) }

            LaunchedEffect(Unit) {
                userId = getUserId(context)
            }
            if (userId > 0) {
                OrderHistoryScreen(
                    api = RetrofitClient.api,
                    userId = userId,
                    navController = navController
                )
            }
        }
        composable("yeu_thich"){
            val context = LocalContext.current
            var userId by remember { mutableStateOf(-1) }

            LaunchedEffect(Unit) {
                userId = getUserId(context)
            }
            if (userId > 0) {
                WishListScreen(
                    api = RetrofitClient.api,
                    userId = userId,
                    navController = navController
                )
            }
        }
    }
}