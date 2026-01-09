package com.example.appbandienthoai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentScreen(
    onPlaceOrder: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("COD") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text("Địa chỉ giao hàng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ tên") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Địa chỉ") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text("Phương thức thanh toán", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            PaymentOption("COD", "Thanh toán khi nhận hàng", paymentMethod) {
                paymentMethod = it
            }
            PaymentOption("BANK", "Chuyển khoản ngân hàng", paymentMethod) {
                paymentMethod = it
            }
            PaymentOption("WALLET", "Ví điện tử", paymentMethod) {
                paymentMethod = it
            }
        }

        item {
            Text("Tóm tắt đơn hàng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            SummaryRow("Tạm tính", "15.000.000đ")
            SummaryRow("Phí vận chuyển", "30.000đ")
            Divider()
            SummaryRow("Tổng cộng", "15.030.000đ", true)
        }

        item {
            Button(
                onClick = onPlaceOrder,
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()
            ) {
                Text("ĐẶT HÀNG")
            }
        }
    }
}


@Composable
fun PaymentOption(
    value: String,
    label: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = value == selected,
            onClick = { onSelect(value) }
        )
        Text(label)
    }
}

@Composable
fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            value,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
