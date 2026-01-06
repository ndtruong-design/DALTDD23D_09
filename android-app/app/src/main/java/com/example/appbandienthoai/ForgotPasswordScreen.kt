import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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


@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToResetPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // --- NÚT BACK (Đã căn chỉnh vị trí đẹp) ---
            IconButton(
                onClick = { onNavigateToLogin() },
                modifier = Modifier
                    .statusBarsPadding() // Tránh tai thỏ/nốt ruồi
                    .padding(top = 8.dp, start = 8.dp) // Cách lề thêm một chút
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_shop),
                    contentDescription = null,
                    modifier = Modifier
                        .width(180.dp)
                        .height(90.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Quên mật khẩu?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Vui lòng nhập địa chỉ email đã đăng ký.\nChúng tôi sẽ gửi mã xác nhận cho bạn.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                InputSection(
                    label = "Email",
                    placeholder = "Nhập email của bạn",
                    value = email,
                    onValueChange = { email = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // Xử lý logic gửi mail reset mật khẩu
                        // Sau đó có thể hiển thị thông báo hoặc chuyển trang
                        onNavigateToResetPassword()
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
                        text = "Gửi mã xác nhận",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}