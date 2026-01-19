<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

require_once "../config/db_connect.php"; 

// 1. Kiểm tra đầu vào
if (!isset($_GET['MaKhachHang'])) {
    echo json_encode(["success" => false, "message" => "Thiếu MaKhachHang"]);
    exit();
}

$user_id = intval($_GET['MaKhachHang']);

try {
    // 2. Câu truy vấn SQL lấy sản phẩm yêu thích
    // Logic: Lấy thông tin SP, lấy giá thấp nhất trong các biến thể, lấy 1 ảnh đại diện
    $sql = "
        SELECT 
            sp.MaSanPham, 
            sp.TenSanPham, 
            yt.NgayThem,
            -- Lấy giá thấp nhất (Min Price) của sản phẩm này
            (SELECT MIN(ct.Gia) 
             FROM ChiTietSanPham ct 
             WHERE ct.MaSanPham = sp.MaSanPham) AS GiaThapNhat,
             
            -- Lấy ảnh đại diện (JOIN qua bảng ChiTietSanPham vì HinhAnh nối với ChiTietSP)
            (SELECT ha.DuongLinkAnh 
             FROM HinhAnh ha
             JOIN ChiTietSanPham ct2 ON ha.MaChiTietSP = ct2.MaChiTietSP
             WHERE ct2.MaSanPham = sp.MaSanPham AND ha.LaAnhDaiDien = 1
             LIMIT 1) AS HinhAnh
             
        FROM DanhSachYeuThich yt
        JOIN SanPham sp ON yt.MaSanPham = sp.MaSanPham
        WHERE yt.MaKhachHang = ?
        ORDER BY yt.NgayThem DESC
    ";

    $stmt = $conn->prepare($sql);
    $stmt->execute([$user_id]);
    
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 3. Xử lý dữ liệu trả về
    $wishlist = [];

    foreach ($rows as $row) {
        $wishlist[] = [
            'product_id' => $row['MaSanPham'],
            'product_name' => $row['TenSanPham'],
            'price' => (float)$row['GiaThapNhat'],
            'price_formatted' => number_format($row['GiaThapNhat'], 0, ',', '.') . ' đ',
            'image' => $row['HinhAnh'] ?? 'https://via.placeholder.com/150', // Ảnh mặc định nếu null
            'added_date' => $row['NgayThem']
        ];
    }

    echo json_encode([
        "success" => true,
        "data" => $wishlist
    ]);

} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Lỗi truy vấn: " . $e->getMessage()
    ]);
}
?>