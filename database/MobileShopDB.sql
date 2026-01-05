-- Tạo Database với bảng mã hỗ trợ tiếng Việt đầy đủ
CREATE DATABASE IF NOT EXISTS MobileShopDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE MobileShopDB;

-- 1. Bảng Thông số kỹ thuật
CREATE TABLE ThongSoKyThuat (
    MaThongSo INT AUTO_INCREMENT PRIMARY KEY,
    ManHinh VARCHAR(100),
    KichThuoc VARCHAR(50),
    CameraSau VARCHAR(100),
    CameraTruoc VARCHAR(100),
    Pin VARCHAR(50),
    HeDieuHanh VARCHAR(50),
    CPU VARCHAR(100),
    GPU VARCHAR(100),
    RAM VARCHAR(50)
);

-- 2. Bảng Sản phẩm
CREATE TABLE SanPham (
    MaSanPham INT AUTO_INCREMENT PRIMARY KEY,
    TenSanPham VARCHAR(255) NOT NULL,
    MaThongSo INT,
    MoTa TEXT, -- Đã đổi từ NVARCHAR(MAX) sang TEXT
    AnhDienThoai TEXT,
    AnhChiTiet TEXT,
    Hang VARCHAR(100),
    TrangThai INT DEFAULT 1, -- 1: Hoạt động, 0: Ngừng bán
    FOREIGN KEY (MaThongSo) REFERENCES ThongSoKyThuat(MaThongSo)
);

-- 3. Bảng Màu sắc
CREATE TABLE MauSac (
    MaMau INT AUTO_INCREMENT PRIMARY KEY,
    TenMau VARCHAR(50) NOT NULL,
    TrangThai INT DEFAULT 1
);

-- 4. Bảng Chi tiết sản phẩm
CREATE TABLE ChiTietSanPham (
    MaChiTietSP INT AUTO_INCREMENT PRIMARY KEY,
    MaSanPham INT NOT NULL,
    MaMau INT NOT NULL,
    SoLuongTon INT DEFAULT 0,
    Gia DECIMAL(18, 2) NOT NULL,
    BoNho VARCHAR(50),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    FOREIGN KEY (MaMau) REFERENCES MauSac(MaMau)
);

-- 5. Bảng Quảng cáo
CREATE TABLE QuangCao (
    MaQuangCao INT AUTO_INCREMENT PRIMARY KEY,
    HinhAnh TEXT NOT NULL,
    TrangThai INT DEFAULT 1
);

-- 6. Bảng Khách hàng
CREATE TABLE KhachHang (
    MaKhachHang INT AUTO_INCREMENT PRIMARY KEY,
    TenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    MatKhau VARCHAR(255) NOT NULL,
    NgaySinh DATE,
    SoDienThoai VARCHAR(15),
    Email VARCHAR(100),
    DiaChi TEXT,
    TrangThai INT DEFAULT 1 -- 1: Hoạt động, 0: Khóa
);

-- 7. Bảng Admin
CREATE TABLE Admin (
    MaAdmin INT AUTO_INCREMENT PRIMARY KEY,
    TenDangNhap VARCHAR(50) UNIQUE NOT NULL,
    MatKhau VARCHAR(255) NOT NULL
);

-- 8. Bảng Phương thức thanh toán
CREATE TABLE PhuongThucThanhToan (
    MaPTTT INT AUTO_INCREMENT PRIMARY KEY,
    TenPhuongThuc VARCHAR(100) NOT NULL
);

-- 9. Bảng Khuyến mãi
CREATE TABLE KhuyenMai (
    MaKhuyenMai VARCHAR(20) PRIMARY KEY,
    GiaToiThieu DECIMAL(18, 2) DEFAULT 0,
    SoLanNhap INT DEFAULT 0,
    SoLuong INT DEFAULT 0,
    TiLeGiam FLOAT,
    TrangThai INT DEFAULT 1 -- 1: Còn hạn, 0: Hết hạn
);

-- 10. Bảng Giỏ hàng
CREATE TABLE GioHang (
    MaGioHang INT AUTO_INCREMENT PRIMARY KEY,
    MaKhachHang INT NOT NULL,
    TrangThai INT DEFAULT 1, -- 1: Đang mua, 0: Đã đặt hàng
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang)
);

-- 11. Bảng Chi tiết giỏ hàng
CREATE TABLE ChiTietGioHang (
    MaCTGH INT AUTO_INCREMENT PRIMARY KEY,
    MaGioHang INT NOT NULL,
    MaSanPham INT NOT NULL,
    SoLuong INT DEFAULT 1,
    TrangThai INT DEFAULT 1,
    FOREIGN KEY (MaGioHang) REFERENCES GioHang(MaGioHang),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);

-- 12. Bảng Đơn hàng
CREATE TABLE DonHang (
    MaDonHang INT AUTO_INCREMENT PRIMARY KEY,
    MaKhachHang INT NOT NULL,
    NgayDatHang DATETIME DEFAULT CURRENT_TIMESTAMP, -- Đã đổi GETDATE() thành CURRENT_TIMESTAMP
    NgayDuKien DATETIME,
    TrangThai INT DEFAULT 0, -- 0: Chờ duyệt, 1: Đang giao, 2: Thành công, 3: Hủy
    TongTien DECIMAL(18, 2),
    MaPTTT INT,
    DiaChiGiaoHang TEXT,
    MaKhuyenMai VARCHAR(20),
    FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT),
    FOREIGN KEY (MaKhuyenMai) REFERENCES KhuyenMai(MaKhuyenMai)
);

-- 13. Bảng Chi tiết đơn hàng
CREATE TABLE ChiTietDonHang (
    MaCTDH INT AUTO_INCREMENT PRIMARY KEY,
    MaDonHang INT NOT NULL,
    MaSanPham INT NOT NULL,
    SoLuong INT NOT NULL,
    DonGia DECIMAL(18, 2) NOT NULL,
    TrangThai INT DEFAULT 1,
    FOREIGN KEY (MaDonHang) REFERENCES DonHang(MaDonHang),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham)
);

-- 14. Bảng Đánh giá
CREATE TABLE DanhGia (
    MaDanhGia INT AUTO_INCREMENT PRIMARY KEY,
    NoiDung TEXT,
    MaDonHang INT NOT NULL,
    TrangThai INT DEFAULT 1, -- 1: Hiện, 0: Ẩn
    FOREIGN KEY (MaDonHang) REFERENCES DonHang(MaDonHang)
);