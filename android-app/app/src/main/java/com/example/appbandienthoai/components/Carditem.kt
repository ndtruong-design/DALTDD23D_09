package com.example.appbandienthoai.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appbandienthoai.data.api.ApiService
import com.example.appbandienthoai.data.model.CartItem
import com.example.appbandienthoai.data.model.CartItemUI
import com.example.appbandienthoai.data.model.CheckoutItem
import com.example.appbandienthoai.data.model.CheckoutRequest
import com.example.appbandienthoai.data.model.RemoveCartRequest
import com.example.appbandienthoai.data.model.UpdateQuantityRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val api: ApiService) : ViewModel() {
    private val _items = MutableStateFlow<List<CartItemUI>>(emptyList())
    val items: StateFlow<List<CartItemUI>> = _items

    private var currentUserId: Int = -1

    val total: StateFlow<Int> = items
        .map { list ->
            list
                .filter { it.isChecked }
                .sumOf { it.item.Gia * it.item.SoLuong }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    fun loadCart(userId: Int) {
        currentUserId = userId
        viewModelScope.launch {
            try {
                val res = api.getCard(userId)
                if (res.success) {
                    _items.value = res.data.orEmpty().map {
                        CartItemUI(item = it, isChecked = true)
                    }
                }
            } catch (e: Exception) {
                Log.e("API_CHECK", "Lỗi Parse dữ liệu: ${e.message}")
            }
        }
    }

    fun updateQuantity(item: CartItem, newQty: Int) {
        if (newQty < 1) return
        viewModelScope.launch {
            try {
                val res = api.updateQuantity(
                    UpdateQuantityRequest(
                        currentUserId,
                        item.MaChiTietSP,
                        newQty
                    )
                )
                if (res.success) {
                    loadCart(currentUserId)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun increase(maChiTietSP: Int) {
        val item = _items.value.find { it.item.MaChiTietSP == maChiTietSP }
        item?.let {
            updateQuantity(it.item, it.item.SoLuong + 1)
        }
    }

    fun decrease(maChiTietSP: Int) {
        val item = _items.value.find { it.item.MaChiTietSP == maChiTietSP }
        item?.let {
            if (it.item.SoLuong > 1) {
                updateQuantity(it.item, it.item.SoLuong - 1)
            }
        }
    }
    fun remove(maChiTietSP: Int) {
        viewModelScope.launch {
            try {
                val res = api.removeItem(RemoveCartRequest(currentUserId, maChiTietSP))
                if (res.success) loadCart(currentUserId)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    fun processCheckout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val selectedItems = _items.value.filter { it.isChecked }

        if (selectedItems.isEmpty()) {
            onError("Vui lòng chọn ít nhất một sản phẩm để thanh toán")
            return
        }

        viewModelScope.launch {
            try {

                val checkoutItems = selectedItems.map {
                    CheckoutItem(it.item.MaChiTietSP, it.item.SoLuong)
                }

                val request = CheckoutRequest(
                    MaKhachHang = currentUserId,
                    items = checkoutItems
                )

                val res = api.check(request)
                if (res.success) {

                    loadCart(currentUserId)
                    onSuccess()
                } else {
                    Log.e("CHECKOUT_DEBUG", "Server message: ${res.message}")
                    onError(res.message ?: "Thanh toán thất bại")
                }
            } catch (e: Exception) {
                onError("Lỗi kết nối: ${e.message}")
            }
        }
    }
    fun toggleCheck(maChiTietSP: Int) {
        _items.value = _items.value.map {
            if (it.item.MaChiTietSP == maChiTietSP)
                it.copy(isChecked = !it.isChecked)
            else it
        }
    }
}
