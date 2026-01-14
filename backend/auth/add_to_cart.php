<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Origin: *");

require_once "../config/db_connect.php"; 

$data = json_decode(file_get_contents("php://input"), true);

// Nhận MaSanPham và MaMau thay vì chỉ nhận MaChiTietSP để đảm bảo tính chính xác
if (!isset($data['MaKhachHang']) || !isset($data['MaSanPham']) || !isset($data['MaMau'])) {
    echo json_encode([
        "success" => false, 
        "message" => "Thiếu mã khách hàng, mã sản phẩm hoặc mã màu"
    ]);
    exit;
}

$maKhachHang = $data['MaKhachHang'];
$maSanPham = $data['MaSanPham'];
$maMau = $data['MaMau'];
$soLuong = isset($data['SoLuong']) ? (int)$data['SoLuong'] : 1;

try {
    // 1. Tìm MaChiTietSP phù hợp với Sản phẩm và Màu sắc đã chọn
    $sql_find_detail = "SELECT MaChiTietSP FROM ChiTietSanPham 
                        WHERE MaSanPham = :maSanPham AND MaMau = :maMau LIMIT 1";
    
    $stmt_find = $conn->prepare($sql_find_detail);
    $stmt_find->execute([':maSanPham' => $maSanPham, ':maMau' => $maMau]);
    $detail = $stmt_find->fetch(PDO::FETCH_ASSOC);

    if (!$detail) {
        echo json_encode([
            "success" => false,
            "message" => "Không tìm thấy biến thể sản phẩm với màu sắc này"
        ]);
        exit;
    }

    $maChiTietSP = $detail['MaChiTietSP'];

    $sql_cart = "INSERT INTO GioHang (MaKhachHang, MaChiTietSP, SoLuong, TrangThai) 
                 VALUES (:maKhachHang, :maChiTietSP, :soLuong, 1) 
                 ON DUPLICATE KEY UPDATE SoLuong = SoLuong + :soLuongUpdate";

    $stmt_cart = $conn->prepare($sql_cart);
    $result = $stmt_cart->execute([
        ':maKhachHang' => $maKhachHang,
        ':maChiTietSP' => $maChiTietSP,
        ':soLuong' => $soLuong,
        ':soLuongUpdate' => $soLuong
    ]);

    if ($result) {
        echo json_encode([
            "success" => true,
            "message" => "Đã thêm sản phẩm màu sắc này vào giỏ hàng",
            "data" => ["MaChiTietSP" => $maChiTietSP] 
        ]);
    }

} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Lỗi database: " . $e->getMessage()
    ]);
}
?>