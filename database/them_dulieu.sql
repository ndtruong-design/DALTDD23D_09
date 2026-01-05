USE MobileShopDB;

-- 1. Thêm dữ liệu bảng Thông số kỹ thuật
INSERT INTO ThongSoKyThuat (ManHinh, KichThuoc, CameraSau, CameraTruoc, Pin, HeDieuHanh, CPU, GPU, RAM) VALUES 
('LTPO Super Retina XDR OLED', '6.7 inch', '48MP + 12MP + 12MP', '12MP', '4441 mAh', 'iOS 17', 'Apple A17 Pro', 'Apple GPU (6-core graphics)', '8GB'),
('Dynamic AMOLED 2X', '6.8 inch', '200MP + 50MP + 12MP + 10MP', '12MP', '5000 mAh', 'Android 14', 'Snapdragon 8 Gen 3', 'Adreno 750', '12GB'),
('AMOLED', '6.67 inch', '50MP + 8MP + 2MP', '16MP', '5000 mAh', 'Android 13', 'Snapdragon 7+ Gen 2', 'Adreno 725', '8GB');

-- 2. Thêm dữ liệu bảng Sản phẩm (Liên kết với ThongSoKyThuat qua ID tự tăng 1, 2, 3)
INSERT INTO SanPham (TenSanPham, MaThongSo, MoTa, Hang, TrangThai) VALUES 
('iPhone 15 Pro Max', 1, 'Thiết kế khung titan chuẩn hàng không vũ trụ, bền bỉ và nhẹ.', 'Apple', 1),
('Samsung Galaxy S24 Ultra', 2, 'Quyền năng Galaxy AI, Zoom mắt thần bóng đêm.', 'Samsung', 1),
('Xiaomi Redmi Note 13 Pro', 3, 'Siêu phẩm tầm trung, camera 200MP.', 'Xiaomi', 1);

-- 3. Thêm dữ liệu bảng Màu sắc
INSERT INTO MauSac (TenMau) VALUES 
('Titan Tự Nhiên'), 
('Đen Phantom'), 
('Vàng Hổ Phách'),
('Trắng Ngọc Trai');

-- 4. Thêm dữ liệu Chi tiết sản phẩm (Kho và Giá)
-- iPhone 15 Pro Max (ID=1) màu Titan (ID=1)
INSERT INTO ChiTietSanPham (MaSanPham, MaMau, SoLuongTon, Gia, BoNho) VALUES 
(1, 1, 50, 34990000, '256GB'),
(1, 1, 20, 40990000, '512GB'),
(1, 2, 30, 34990000, '256GB'); -- Màu Đen

-- Samsung S24 Ultra (ID=2) màu Vàng (ID=3)
INSERT INTO ChiTietSanPham (MaSanPham, MaMau, SoLuongTon, Gia, BoNho) VALUES 
(2, 3, 100, 31990000, '256GB'),
(2, 2, 50, 35990000, '512GB');

-- 5. Thêm dữ liệu Khách hàng
INSERT INTO KhachHang (TenDangNhap, MatKhau, NgaySinh, SoDienThoai, Email, DiaChi) VALUES 
('nguyenvanan', '123456', '1999-01-01', '0909123456', 'nguyenvanan@example.com', '123 Đường Lê Lợi, Q1, TP.HCM'),
('tranthib', 'password', '2000-05-20', '0912345678', 'tranthib@example.com', '456 Đường Nguyễn Huệ, Q1, TP.HCM');

-- 6. Thêm dữ liệu Admin
INSERT INTO Admin (TenDangNhap, MatKhau) VALUES 
('admin', 'admin123'),
('mod_support', 'mod123');

-- 7. Thêm Phương thức thanh toán
INSERT INTO PhuongThucThanhToan (TenPhuongThuc) VALUES 
('Thanh toán khi nhận hàng (COD)'),
('Chuyển khoản ngân hàng (QR Code)'),
('Ví MoMo');

-- 8. Thêm Khuyến mãi
INSERT INTO KhuyenMai (MaKhuyenMai, GiaToiThieu, SoLanNhap, SoLuong, TiLeGiam, TrangThai) VALUES 
('SUMMER2025', 5000000, 0, 100, 5.0, 1), -- Giảm 5%
('WELCOME', 0, 0, 1000, 2.0, 1);       -- Giảm 2%

-- 9. Thêm Đơn hàng mẫu (Đã đặt hàng)
INSERT INTO DonHang (MaKhachHang, NgayDatHang, TrangThai, TongTien, MaPTTT, DiaChiGiaoHang) VALUES 
(1, NOW(), 1, 34990000, 1, '123 Đường Lê Lợi, Q1, TP.HCM'); -- Đang giao

-- 10. Thêm Chi tiết đơn hàng (Sản phẩm trong đơn hàng trên)
INSERT INTO ChiTietDonHang (MaDonHang, MaSanPham, SoLuong, DonGia) VALUES 
(1, 1, 1, 34990000); -- Mua 1 cái iPhone 15 Pro Max

-- 11. Thêm Đánh giá mẫu
INSERT INTO DanhGia (NoiDung, MaDonHang, TrangThai) VALUES 
('Điện thoại rất đẹp, giao hàng nhanh!', 1, 1);

-- 12. Thêm dữ liệu Quảng cáo (Banner)
INSERT INTO QuangCao (HinhAnh, TrangThai) VALUES 
('https://example.com/banner-iphone15.jpg', 1),
('https://example.com/banner-s24ultra.jpg', 1);