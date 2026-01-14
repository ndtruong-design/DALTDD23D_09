    -- Tạo Database với bảng mã hỗ trợ tiếng Việt đầy đủ
    CREATE DATABASE IF NOT EXISTS MobileShopDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    USE MobileShopDB;

    -- 1. Bảng Sản phẩm
    CREATE TABLE SanPham (
        MaSanPham INT AUTO_INCREMENT PRIMARY KEY,
        TenSanPham VARCHAR(255) NOT NULL,
        MoTa TEXT, -- Đã đổi từ NVARCHAR(MAX) sang TEXT
        Hang VARCHAR(100),
        TrangThai INT DEFAULT 1 -- 1: Hoạt động, 0: Ngừng bán
    );
    -- 2. Bảng Màu sắc
    CREATE TABLE MauSac (
        MaMau INT AUTO_INCREMENT PRIMARY KEY,
        TenMau VARCHAR(50) NOT NULL,
        MaHex VARCHAR(7), -- Thêm cột mã màu (ví dụ: #FFFFFF)
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
        ManHinh VARCHAR(100),
        KichThuoc VARCHAR(50),
        CameraSau VARCHAR(100),
        CameraTruoc VARCHAR(100),
        Pin VARCHAR(50),
        HeDieuHanh VARCHAR(50),
        CPU VARCHAR(100),
        GPU VARCHAR(100),
        RAM VARCHAR(50),
        FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
        FOREIGN KEY (MaMau) REFERENCES MauSac(MaMau)
    );
    -- 3. Bảng Hình ảnh
    CREATE TABLE HinhAnh (
        MaAnh INT AUTO_INCREMENT PRIMARY KEY,
        MaChiTietSP INT NOT NULL,
        MaMau INT NOT NULL, -- Đã thêm cột này (Có thể để NULL nếu ảnh đó không thuộc màu nào cụ thể)
        DuongLinkAnh TEXT NOT NULL,
        LaAnhDaiDien BOOLEAN DEFAULT FALSE,
        FOREIGN KEY (MaChiTietSP) REFERENCES ChiTietSanPham(MaChiTietSP),
        FOREIGN KEY (MaMau) REFERENCES MauSac(MaMau) -- Thêm dòng này nếu đã có bảng MauSac
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
        HoTen VARCHAR(100),
        MatKhau VARCHAR(255) NOT NULL,
        NgaySinh DATE,
        SoDienThoai VARCHAR(15),
        Email VARCHAR(100),
        DiaChi TEXT,
        AnhDaiDien TEXT,
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

    -- 10. Bảng Giỏ hàng (Đã gộp với Chi tiết giỏ hàng)
    CREATE TABLE GioHang (
        MaKhachHang INT NOT NULL,
        MaChiTietSP INT NOT NULL,
        SoLuong INT DEFAULT 1,
        TrangThai INT DEFAULT 1, -- 1: Đang mua, 0: Đã đặt hàng
        FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
        FOREIGN KEY (MaChiTietSP) REFERENCES ChiTietSanPham(MaChiTietSP),
        UNIQUE KEY unique_cart_item (MaKhachHang, MaChiTietSP)
    );

    -- 11. Bảng Đơn hàng
    CREATE TABLE DonHang (
        MaDonHang INT AUTO_INCREMENT PRIMARY KEY,
        MaKhachHang INT NOT NULL,
        NgayDatHang DATETIME DEFAULT CURRENT_TIMESTAMP, -- Đã đổi GETDATE() thành CURRENT_TIMESTAMP
        NgayDuKien DATETIME,
        TrangThai INT DEFAULT 0, -- 0: Chờ duyệt, 1: Đang giao, 2: Thành công, 3: Hủy
        TrangThaiThanhToan INT DEFAULT 0, -- 0: Chưa thanh toán, 1: Đã thanh toán, 2: Hoàn tiền
        TongTien DECIMAL(18, 2),
        MaPTTT INT,
        DiaChiGiaoHang TEXT,
        MaKhuyenMai VARCHAR(20),
        FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
        FOREIGN KEY (MaPTTT) REFERENCES PhuongThucThanhToan(MaPTTT),
        FOREIGN KEY (MaKhuyenMai) REFERENCES KhuyenMai(MaKhuyenMai)
    );

    -- 12. Bảng Chi tiết đơn hàng
    CREATE TABLE ChiTietDonHang (
        MaCTDH INT AUTO_INCREMENT PRIMARY KEY,
        MaDonHang INT NOT NULL,
        MaChiTietSP INT NOT NULL,
        SoLuong INT NOT NULL,
        DonGia DECIMAL(18, 2) NOT NULL,
        TrangThai INT DEFAULT 1,
        FOREIGN KEY (MaDonHang) REFERENCES DonHang(MaDonHang),
        FOREIGN KEY (MaChiTietSP) REFERENCES ChiTietSanPham(MaChiTietSP)
    );

    -- 13. Bảng Đánh giá
    CREATE TABLE DanhGia (
        MaDanhGia INT AUTO_INCREMENT PRIMARY KEY,
        NoiDung TEXT,
        MaDonHang INT NOT NULL,
        TrangThai INT DEFAULT 1, -- 1: Hiện, 0: Ẩn
        FOREIGN KEY (MaDonHang) REFERENCES DonHang(MaDonHang)
    );


    -- Thêm dữ liệu mẫu vào các bảng

    -- 1. Thêm dữ liệu bảng Sản phẩm
    INSERT INTO SanPham (TenSanPham, MoTa, Hang, TrangThai) VALUES 
    ('iPhone 15 Pro Max', 'Thiết kế khung titan chuẩn hàng không vũ trụ, bền bỉ và nhẹ.', 'Apple', 1),
    ('Samsung Galaxy S24 Ultra', 'Quyền năng Galaxy AI, Zoom mắt thần bóng đêm.', 'Samsung', 1),
    ('Xiaomi Redmi Note 13 Pro', 'Siêu phẩm tầm trung, camera 200MP.', 'Xiaomi', 1);
    -- 2. Thêm dữ liệu bảng Màu sắc
    INSERT INTO MauSac (TenMau,MaHex) VALUES 
    ('Titan Tự Nhiên', '#878479'),   
    ('Đen Phantom', '#1C1C1C'),
    ('Đen Huyền Bí', '#0A0A0A'),
    ('Vàng Hoàng Kim', '#FFD700'),
    ('Xanh Xa Lánh', '#008080');
    


    -- 4. Thêm dữ liệu Chi tiết sản phẩm (Kho và Giá)
    -- iPhone 15 Pro Max (ID=1) màu Titan (ID=1)
    INSERT INTO ChiTietSanPham (MaSanPham, MaMau, SoLuongTon, Gia, BoNho, ManHinh, KichThuoc, CameraSau, CameraTruoc, Pin, HeDieuHanh, CPU, GPU, RAM) VALUES 
    (1, 1, 50, 34990000, '256GB', 'LTPO Super Retina XDR OLED', '6.7 inch', '48MP + 12MP + 12MP', '12MP', '4441 mAh', 'iOS 17', 'Apple A17 Pro', 'Apple GPU (6-core graphics)', '8GB'),
    (1, 1, 20, 40990000, '512GB', 'LTPO Super Retina XDR OLED', '6.7 inch', '48MP + 12MP + 12MP', '12MP', '4441 mAh', 'iOS 17', 'Apple A17 Pro', 'Apple GPU (6-core graphics)', '8GB'); 

    -- Samsung S24 Ultra (ID=2) đen phantom (ID=3)
    INSERT INTO ChiTietSanPham (MaSanPham, MaMau, SoLuongTon, Gia, BoNho, ManHinh, KichThuoc, CameraSau, CameraTruoc, Pin, HeDieuHanh, CPU, GPU, RAM) VALUES 
    (2, 2, 100, 31990000, '256GB', 'Dynamic AMOLED 2X', '6.8 inch', '200MP + 50MP + 12MP + 10MP', '12MP', '5000 mAh', 'Android 14', 'Snapdragon 8 Gen 3', 'Adreno 750', '12GB'),
    (2, 2, 50, 35990000, '512GB', 'Dynamic AMOLED 2X', '6.8 inch', '200MP + 50MP + 12MP + 10MP', '12MP', '5000 mAh', 'Android 14', 'Snapdragon 8 Gen 3', 'Adreno 750', '12GB');

    -- Xiaomi Redmi Note 13 Pro (ID=3) đen huyền bí (ID=3)
    INSERT INTO ChiTietSanPham (
        MaSanPham, MaMau, SoLuongTon, Gia, BoNho, ManHinh, 
        KichThuoc, CameraSau, CameraTruoc, Pin, HeDieuHanh, 
        CPU, GPU, RAM
    ) VALUES 
    (
        3, 3, 60, 7290000, '256GB', 'AMOLED, 120Hz', 
        '6.67 inch', '200MP (Chính) + 8MP (Góc siêu rộng) + 2MP (Cận cảnh)', 
        '16MP', '5000 mAh', 'Android 13', 
        'MediaTek Helio G99-Ultra', 'Mali-G57 MC2', '8GB'
    ),
    (
        3, 5, 60, 7290000, '256GB', 'AMOLED, 120Hz', 
        '6.67 inch', '200MP (Chính) + 8MP (Góc siêu rộng) + 2MP (Cận cảnh)', 
        '16MP', '5000 mAh', 'Android 13', 
        'MediaTek Helio G99-Ultra', 'Mali-G57 MC2', '8GB'
    ),
    (
        3, 3, 35, 8990000, '512GB', 'AMOLED, 120Hz', 
        '6.67 inch', '200MP (Chính) + 8MP (Góc siêu rộng) + 2MP (Cận cảnh)', 
        '16MP', '5000 mAh', 'Android 13', 
        'MediaTek Helio G99-Ultra', 'Mali-G57 MC2', '12GB'
    );

    -- 5. Thêm dữ liệu bảng Hình ảnh
    INSERT INTO HinhAnh (MaChiTietSP, MaMau, DuongLinkAnh, LaAnhDaiDien) VALUES 
    (1, 1, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_2__5_2_1_1.jpg', TRUE),  -- Ảnh đại diện iPhone
    (1, 1, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_4__1.jpg', FALSE), -- Ảnh chi tiết iPhone
    (1, 1, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_5__1.jpg', FALSE), -- Ảnh chi tiết iPhone
    (1, 1, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_6__1.jpg', FALSE), -- Ảnh chi tiết iPhone
    (2, 1, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:358:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_2__5_2_1_1.jpg', TRUE),  -- Ảnh đại diện iPhone
    (2, 1, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_4__1.jpg', FALSE), -- Ảnh chi tiết iPhone
    (2, 1, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_5__1.jpg', FALSE), -- Ảnh chi tiết iPhone
    (2, 1, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/i/p/iphone-15-pro-max_6__1.jpg', FALSE), -- Ảnh chi tiết iPhone
    (3, 2, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-s24-ultra-den-600.png', TRUE),   -- Ảnh đại diện Samsung
    (3, 2, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-s24-ultra_5_.png', FALSE),
    (3, 2, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-s24-ultra_6_.png', FALSE),
    (3, 2, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-s24-ultra_7_.png', FALSE), -- Ảnh chi tiết Samsung
    (4, 2, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/s/ss-s24-ultra-den-600.png', TRUE),   -- Ảnh đại diện Samsung
    (4, 2, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-s24-ultra_5_.png', FALSE),
    (4, 2, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-s24-ultra_6_.png', FALSE),
    (4, 2, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/s/a/samsung-galaxy-s24-ultra_7_.png', FALSE), -- Ảnh chi tiết Samsung
    (5, 3, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-redmi-note-13-pro-4g_13__1_3.png', TRUE),    -- Ảnh đại diện Xiaomi
    (5,3, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-redmi-note-13-pro-4g_6__1_3.png', FALSE),
    (5,3, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi_redmi_note_13_pro_4g_3.png', FALSE),
    (5,3,'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-redmi-note-13-pro-4g_4__1_3.png', FALSE), -- Ảnh chi tiết Xiaomi
    (6,5, 'https://cdn2.cellphones.com.vn/358x/media/catalog/product/2/0/20241135_3.png', TRUE),    -- Ảnh đại diện Xiaomi màu khác
    (6,5, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/p/h/photo_2024-12-23_10-19-43_-_copy.jpg', FALSE),
    (6,5, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/p/h/photo_2024-12-23_10-19-33_-_copy.jpg', FALSE), -- Ảnh chi tiết Xiaomi màu khác
    (7, 3, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-redmi-note-13-pro-4g_13__1_3.png', TRUE),    -- Ảnh đại diện Xiaomi
    (7,3, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-redmi-note-13-pro-4g_6__1_3.png', FALSE),
    (7,3, 'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi_redmi_note_13_pro_4g_3.png', FALSE),
    (7,3,'https://cdn2.cellphones.com.vn/insecure/rs:fill:0:358/q:90/plain/https://cellphones.com.vn/media/catalog/product/x/i/xiaomi-redmi-note-13-pro-4g_4__1_3.png', FALSE); -- Ảnh chi tiết Xiaomi
    -- 6. Thêm dữ liệu Khách hàng
    INSERT INTO KhachHang (TenDangNhap, HoTen, MatKhau, NgaySinh, SoDienThoai, Email, DiaChi, AnhDaiDien) VALUES 
    ('nguyenvanan', 'Nguyễn Văn An', '123456', '1999-01-01', '0909123456', 'nguyenvanan@example.com', '123 Đường Lê Lợi, Q1, TP.HCM', 'https://cdn11.dienmaycholon.vn/filewebdmclnew/public/userupload/files/Image%20FP_2024/avatar-dep-1.jpg'),
    ('tranthib', 'Trần Thị B', 'password', '2000-05-20', '0912345678', 'tranthib@example.com', '456 Đường Nguyễn Huệ, Q1, TP.HCM', 'https://example.com/avatar-b.jpg');

    -- 6.1. Thêm dữ liệu Giỏ hàng
    INSERT INTO GioHang (MaKhachHang, MaChiTietSP, SoLuong, TrangThai) VALUES 
    (1, 1, 1, 1), -- Khách hàng 1 thêm 1 iPhone 15 Pro Max (Chi tiết SP ID=1) vào giỏ
    (1, 4, 2, 1); -- Khách hàng 1 thêm 2 Samsung S24 Ultra (Chi tiết SP ID=4) vào giỏ

    -- 8. Thêm dữ liệu Admin
    INSERT INTO Admin (TenDangNhap, MatKhau) VALUES 
    ('admin', 'admin123'),
    ('mod_support', 'mod123');

    -- 9. Thêm Phương thức thanh toán
    INSERT INTO PhuongThucThanhToan (TenPhuongThuc) VALUES 
    ('Thanh toán khi nhận hàng (COD)'),
    ('Chuyển khoản ngân hàng (QR Code)'),
    ('Ví MoMo');

    -- 10. Thêm Khuyến mãi
    INSERT INTO KhuyenMai (MaKhuyenMai, GiaToiThieu, SoLanNhap, SoLuong, TiLeGiam, TrangThai) VALUES 
    ('SUMMER2025', 5000000, 0, 100, 5.0, 1), -- Giảm 5%
    ('WELCOME', 0, 0, 1000, 2.0, 1);       -- Giảm 2%

    -- 11. Thêm Đơn hàng mẫu (Đã đặt hàng)
    INSERT INTO DonHang (MaKhachHang, NgayDatHang, TrangThai, TrangThaiThanhToan, TongTien, MaPTTT, DiaChiGiaoHang) VALUES 
    (1, NOW(), 1, 1, 34990000, 1, '123 Đường Lê Lợi, Q1, TP.HCM'); -- Đang giao, đã thanh toán

    -- 12. Thêm Chi tiết đơn hàng (Sản phẩm trong đơn hàng trên)
    INSERT INTO ChiTietDonHang (MaDonHang, MaChiTietSP, SoLuong, DonGia) VALUES 
    (1, 1, 1, 34990000); -- Mua 1 cái iPhone 15 Pro Max (Chi tiết SP ID=1)

    -- 13. Thêm Đánh giá mẫu
    INSERT INTO DanhGia (NoiDung, MaDonHang, TrangThai) VALUES 
    ('Điện thoại rất đẹp, giao hàng nhanh!', 1, 1);

    -- 14. Thêm dữ liệu Quảng cáo (Banner)
    INSERT INTO QuangCao (HinhAnh, TrangThai) VALUES 
    ('https://cellphones.com.vn/sforum/wp-content/uploads/2019/05/Honor-20-Pro-lo-anh-quang-cao-1.jpg', 1),
    ('https://s3.cloud.cmctelecom.vn/tinhte2/2019/07/4706909_Cover_Samsung_chong_nuoc.jpg', 1),
    ('https://img.global.news.samsung.com/vn/wp-content/uploads/2021/05/KV-final-12-1024x666.jpg',1),
    ('https://cdn.tgdd.vn/Files/2018/08/01/1105915/oppo_f9_1_800x450.jpg',1)