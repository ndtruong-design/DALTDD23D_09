package com.example.appbandienthoai


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    fun addItem(item: CartItem) {
        val current = _items.value.toMutableList()
        val index = current.indexOfFirst { it.MaSanPham == item.MaSanPham }

        if (index >= 0) {
            current[index] = current[index].copy(
                SoLuong = current[index].SoLuong + 1
            )
        } else {
            current.add(item)
        }
        _items.value = current
    }

    fun increase(maSanPham: Int) {
        _items.value = _items.value.map {
            if (it.MaSanPham == maSanPham)
                it.copy(SoLuong = it.SoLuong + 1)
            else it
        }
    }

    fun decrease(maSanPham: Int) {
        _items.value = _items.value.mapNotNull {
            when {
                it.MaSanPham != maSanPham -> it
                it.SoLuong > 1 -> it.copy(SoLuong = it.SoLuong - 1)
                else -> null
            }
        }
    }

    fun clearCart() {
        _items.value = emptyList()
    }
    fun remove(maSanPham: Int) {
        _items.value = _items.value.filter {
            it.MaSanPham != maSanPham
        }
    }
    fun totalPrice(): Int {
        return _items.value.sumOf { it.Gia * it.SoLuong }
    }
}
