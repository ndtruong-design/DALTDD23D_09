<?php
header("Content-Type: application/json; charset=UTF-8");
require_once "../../config/db_connect.php";

$user_id = intval($_GET['user_id'] ?? 0);

$sql = "
SELECT
    gh.MaChiTietSP,
    sp.MaSanPham,
    sp.TenSanPham,
    ct.Gia,
    gh.SoLuong,
    ct.MaMau,
    ct.BoNho,
    (
        SELECT ha.DuongLinkAnh
        FROM hinhanh ha
        WHERE ha.MaSanPham = sp.MaSanPham
        LIMIT 1
    ) AS HinhAnh
FROM giohang gh
JOIN chitietsanpham ct 
    ON gh.MaChiTietSP = ct.MaChiTietSP
JOIN sanpham sp 
    ON ct.MaSanPham = sp.MaSanPham
WHERE gh.MaKhachHang = ?
";

$stmt = $conn->prepare($sql);
$stmt->execute([$user_id]);

echo json_encode([
    "success" => true,
    "data" => $stmt->fetchAll(PDO::FETCH_ASSOC)
]);
