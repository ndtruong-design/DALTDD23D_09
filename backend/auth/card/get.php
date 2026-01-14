<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../../config/db_connect.php"; 


$user_id = intval($_GET['MaKhachHang'] ?? 0);

if ($user_id <= 0) {
    echo json_encode(["success" => false, "message" => "User ID không hợp lệ"]);
    exit;
}

$sql = "
SELECT 
    gh.MaChiTietSP, 
    ct.MaSanPham,
    sp.TenSanPham, 
    ct.Gia, 
    gh.SoLuong,
    ct.SoLuongTon,
    ms.TenMau, 
    ct.BoNho,
    ha.DuongLinkAnh AS HinhAnh
FROM GioHang gh
JOIN ChiTietSanPham ct ON gh.MaChiTietSP = ct.MaChiTietSP
JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham
LEFT JOIN MauSac ms ON ct.MaMau = ms.MaMau
LEFT JOIN HinhAnh ha 
    ON ha.MaChiTietSP = ct.MaChiTietSP 
    AND ha.LaAnhDaiDien = 1
WHERE gh.MaKhachHang = ?
ORDER BY (ct.Gia * gh.SoLuong) DESC
";

try {
    $stmt = $conn->prepare($sql);
    $stmt->execute([$user_id]);
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);

   
    foreach ($data as &$item) {
        $item['Gia'] = floatval($item['Gia']);
        $item['SoLuong'] = intval($item['SoLuong']);
    }

    echo json_encode([
        "success" => true,
        "count" => count($data),
        "data" => $data
    ], JSON_UNESCAPED_UNICODE);
} catch (PDOException $e) {
    echo json_encode([
        "success" => false, 
        "message" => "Lỗi: " . $e->getMessage()
    ]);
}
?>

