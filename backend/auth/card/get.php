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
    sp.MaSanPham,
    sp.TenSanPham,
    ct.Gia,
    gh.SoLuong,
    ms.TenMau,
    ct.BoNho,
    (
    SELECT ha.DuongLinkAnh
        FROM hinhanh ha
        WHERE ha.MaSanPham = sp.MaSanPham AND ha.LaAnhDaiDien = TRUE
        LIMIT 1
    ) AS HinhAnh
FROM giohang gh
JOIN chitietsanpham ct ON gh.MaChiTietSP = ct.MaChiTietSP
JOIN sanpham sp ON ct.MaSanPham = sp.MaSanPham
LEFT JOIN mausac ms ON ct.MaMau = ms.MaMau
WHERE gh.MaKhachHang = ?
";

try {
    $stmt = $conn->prepare($sql);
    $stmt->execute([$user_id]);
    $data = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "success" => true,
        "data" => $data
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "success" => false,
        "message" => "Lỗi truy vấn: " . $e->getMessage()
    ]);
}
?>