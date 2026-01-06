import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appbandienthoai.R
import kotlin.text.isNotEmpty

@Composable
fun ResetPasswordScreen(
    onNavigateToLogin: () -> Unit, // Đổi xong thì về Login
    onBack: () -> Unit // Quay lại màn hình nhập Email
) {
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // --- NÚT BACK ---
            IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            // --- NỘI DUNG CHÍNH ---
            // Dùng scrollState để tránh bị che phím khi nhập liệu nhiều ô
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()), // Cho phép cuộn
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Đẩy nội dung xuống một chút nếu màn hình dài
                Spacer(modifier = Modifier.height(80.dp))

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_shop),
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .height(80.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Đặt lại mật khẩu",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Nhập mã OTP đã được gửi đến email và thiết lập mật khẩu mới.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 1. Nhập Mã OTP
                InputSection(
                    label = "Mã xác thực (OTP)",
                    placeholder = "Nhập mã 6 số",
                    value = otpCode,
                    onValueChange = { otpCode = it }
                    // Lưu ý: InputSection hiện tại đang dùng bàn phím Email
                    // Nếu muốn bàn phím số, cần sửa component InputSection một chút (xem lưu ý dưới cùng)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Nhập Mật khẩu mới
                InputSection(
                    label = "Mật khẩu mới",
                    placeholder = "Nhập mật khẩu mới",
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Nhập lại mật khẩu mới
                InputSection(
                    label = "Xác nhận mật khẩu",
                    placeholder = "Nhập lại mật khẩu mới",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Nút Xác nhận
                Button(
                    onClick = {
                        // 1. Kiểm tra mật khẩu trùng khớp
                        if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                            // 2. Gọi API xác thực OTP và đổi mật khẩu
                            // 3. Thành công -> Chuyển về trang đăng nhập
                            onNavigateToLogin()
                        } else {
                            // Hiển thị thông báo lỗi (Toast hoặc Text báo lỗi)
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
                        text = "Xác nhận",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(50.dp)) // Khoảng trống dưới cùng
            }
        }
    }
}