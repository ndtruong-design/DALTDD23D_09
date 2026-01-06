import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appbandienthoai.R


@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            // nút back
            IconButton(
                onClick = { onNavigateToLogin() },
                modifier = Modifier
                    .padding(top = 48.dp, start = 16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_shop),
                contentDescription = null,
                modifier = Modifier
                    .width(150.dp)
                    .height(80.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Tạo tài khoản mới",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))


            InputSection(
                label = "Tên đăng nhập",
                placeholder = "Nhập tên đăng nhập",
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(16.dp))


            InputSection(
                label = "Email",
                placeholder = "example@gmail.com",
                value = email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(16.dp))


            InputSection(
                label = "Mật khẩu",
                placeholder = "Nhập mật khẩu",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = {
                    onNavigateToLogin()
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
                    text = "Đăng Ký",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer: Chuyển về Đăng nhập
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đã có tài khoản? ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Đăng Nhập",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        onNavigateToLogin()
                    }
                )
            }
        }
    }
}