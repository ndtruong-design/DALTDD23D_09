import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appbandienthoai.R
import com.google.firebase.appdistribution.gradle.ApiService


@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp), // Khoảng cách lề 2 bên
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(120.dp))

            //logo
            Image(
                painter = painterResource(id = R.drawable.logo_shop),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp) // Điều chỉnh kích thước logo tùy ý
                    .height(100.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Đăng nhập tài khoản",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )


            Spacer(modifier = Modifier.height(60.dp))

            // nhập tên dn
            InputSection(
                label = "Tên đăng nhập",
                placeholder = "Nhập tên đăng nhập của bạn",
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // mật khẩu
            InputSection(
                label = "Mật khẩu",
                placeholder = "Nhập mật khẩu của bạn",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // nút đnhập
            Button(
                onClick = { /* Xử lý sự kiện click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp), // Bo góc nút
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF673AB7), // Màu nền nút
                    contentColor = Color.White // Màu chữ
                )
            ) {
                Text(
                    text = "Đăng Nhập",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quên mật khẩu?",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        onNavigateToForgotPassword()
                    }
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {

                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)

                Text(
                    text = "Hoặc",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialMediaButton(
                    iconRes = R.drawable.logo_google,
                    onClick = { /* Xử lý đăng nhập Google */ }
                )
                SocialMediaButton(
                    iconRes = R.drawable.logo_fb,
                    onClick = { /* Xử lý đăng nhập Facebook */ }
                )
            }


            Spacer(modifier = Modifier.weight(1f)) // Đẩy phần footer xuống dưới cùng

            // từ đ nhập qua đăng ký
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
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
                        onNavigateToRegister()
                    }
                )
            }
        }
    }
}


// Composable tái sử dụng cho các ô nhập liệu (Label nằm trên Input)
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
                androidx.compose.ui.text.input.VisualTransformation.None
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

@Composable
fun SocialMediaButton(
    iconRes: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .size(60.dp), // Kích thước nút vuông
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(12.dp), // Khoảng cách từ viền nút đến icon
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}